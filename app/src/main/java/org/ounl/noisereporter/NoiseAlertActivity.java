package org.ounl.noisereporter;

import java.util.Calendar;
import java.util.Date;

import org.ounl.noisereporter.db.DatabaseHandler;
import org.ounl.noisereporter.db.tables.NoiseSampleDb;
import org.ounl.noisereporter.db.tables.TagDb;
import org.ounl.noisereporter.fcube.Constants;
import org.ounl.noisereporter.fcube.commands.FCColor;
import org.ounl.noisereporter.fcube.commands.FCOff;
import org.ounl.noisereporter.fcube.commands.FCOn;
import org.ounl.noisereporter.fcube.config.FeedbackCubeConfig;
import org.ounl.noisereporter.fcube.config.FeedbackCubeManager;
import org.ounl.noisereporter.feeback.FeedbackColor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NoiseAlertActivity extends Activity {
	/* constants */

	/** running state **/
	private boolean mRunning = false;

	/** config state **/
	private PowerManager.WakeLock mWakeLock;
	private Handler mHandler = new Handler();
	private DatabaseHandler db;
	private NoiseUtils nu = new NoiseUtils();

	/* References to view elements */
	private TextView mStatusView, tv_noice;

	/* sound data source */
	private Detect_noise mSensor;
	ProgressBar bar;
	EditText etIP;
	EditText etTAG;
	EditText etThresMin;
	EditText etThresMax;
	LinearLayout llTecla;
	ImageView ivFruit;
	ToggleButton mToggleButton;


	private Runnable mSleepTask = new Runnable() {
		public void run() {
			// Log.i("Noise", "runnable mSleepTask");

			// save one record in tags as new session
			String sTag = etTAG.getText().toString();
			Double dThresMin = new Double(etThresMin.getText().toString());
			Double dThresMax = new Double(etThresMax.getText().toString());
			db.addTag(new TagDb(sTag, dThresMin, dThresMax));
			
			// Start recording samples of noise
			start();
		}
	};

	// Create runnable thread to Monitor Voice
	private Runnable mPollTask = new Runnable() {
		public void run() {

			try {
				// Read input data
				String sIp = etIP.getText().toString();
				FeedbackCubeConfig.getSingleInstance().setIp(sIp);

				String sTag = etTAG.getText().toString();
				

				// Current value returned by the sensor
				double amp = mSensor.getAmplitude();

				if (!Double.isInfinite(amp)) {

					// Average value for the last NUM_POLLS meaures
					double ampAVG = 0;
					// Log.i("Noise", "runnable mPollTask");
					

					Date dNow = new Date();
					int iLevel = Constants.NOISE_LEVEL_INIT;

					// Add noise item to buffer and return position where it was
					// inserted
					int iNum = FeedbackCubeConfig.getSingleInstance()
							.addNoiseItem(amp);

					// Return color for average values in buffer
					// Commented for calibration
					readThreshold();
					FeedbackColor color = FeedbackCubeConfig.getSingleInstance().getBufferColor();

					// Send color whenever the cube is activated
					if(mToggleButton.isChecked()){
						// Launch color in the cube
						FCColor fcc = new FCColor(FeedbackCubeConfig
							.getSingleInstance().getIp(), "" + color.getR(), ""
							+ color.getG(), "" + color.getB());
						new FeedbackCubeManager().execute(fcc);
					}
					// Prepare log
					ampAVG = FeedbackCubeConfig.getSingleInstance().getAverageNoise();
					
					// Show average color in mobile display
					updateCurrentNoiseAndProgressbar("Monitoring on..." + sIp, amp);
					updateAvgTextAndBackground(ampAVG, amp, color);

					// Get thresholds
					Double dThresMin = new Double(etThresMin.getText().toString());
					Double dThresMax = new Double(etThresMax.getText().toString());
					
					// Insert noise item into database
					db.addNoiseSample(new NoiseSampleDb(dNow.getTime(), amp, ampAVG, dThresMin, dThresMax, sTag));
					
					// new Double(etThresMin.getText().toString())

					// Added for callibration
					// FeedbackCubeColor color = new FeedbackCubeColor(0,0,0);
					// callForHelp(ampAVG, amp, color);

					if (iNum == FeedbackCubeConfig.NUM_POLLS) {

						FeedbackCubeConfig.getSingleInstance().resetPollIndex();
					}

				}

				// Runnable(mPollTask) will again execute after POLL_INTERVAL
				mHandler.postDelayed(mPollTask,
						FeedbackCubeConfig.POLL_INTERVAL);

			} catch (Exception e) {
				updateCurrentNoiseAndProgressbar("" + e.getMessage(), 0.0);
				e.printStackTrace();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Defined SoundLevelView in main.xml file
		setContentView(R.layout.main);

		mToggleButton = (ToggleButton) findViewById(R.id.tbFeedback);
		mStatusView = (TextView) findViewById(R.id.status);
		tv_noice = (TextView) findViewById(R.id.tv_noice);
		bar = (ProgressBar) findViewById(R.id.progressBar1);

		etIP = (EditText) findViewById(R.id.editTextIP);
		etTAG = (EditText) findViewById(R.id.editTextTAG);
		etTAG.setText(Calendar.getInstance().get(Calendar.YEAR) + "_"
				+ (Calendar.getInstance().get(Calendar.MONTH) + 1) + "_"
				+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "_"
				+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

		llTecla = (LinearLayout) findViewById(R.id.llTeclas);
		etThresMin = (EditText) findViewById(R.id.etMimThreshold);
		etThresMax = (EditText) findViewById(R.id.etMaxThreshold);
		ivFruit = (ImageView) findViewById(R.id.imageViewFruit);
		readThreshold();

		// Used to record voice
		mSensor = new Detect_noise();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"NoiseAlertActivity");

		db = new DatabaseHandler(getApplicationContext());


	}

	private void readThreshold() {
		// Configure thresholds
		// etThresMin = (EditText) findViewById(R.id.etMimThreshold);
		try {
			FeedbackCubeConfig.getSingleInstance().setmThresholdMin(
					new Double(etThresMin.getText().toString()));

			// etThresMax = (EditText) findViewById(R.id.etMaxThreshold);
			FeedbackCubeConfig.getSingleInstance().setmThresholdMax(
					new Double(etThresMax.getText().toString()));
		} catch (Exception e) {
			updateCurrentNoiseAndProgressbar("Threshold have invalid values", 0.0);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.i("Noise", "==== onResume ===");

// AAAAA esto lo has comentado para que no inicie atizándoles		
//		if (!mRunning) {
//			mRunning = true;
//			start();
//		}
	}

	@Override
	public void onStop() {
		super.onStop();
		// Log.i("Noise", "==== onStop ===");
		// Stop noise monitoring
		stop();
	}

	private void start() {
		try {
            Log.i("Noise", "==== Start Noise Monitoring===");

			mSensor.start();
			if (!mWakeLock.isHeld()) {
				mWakeLock.acquire();
			}
			// Noise monitoring start
			// Runnable(mPollTask) will execute after POLL_INTERVAL
			mHandler.postDelayed(mPollTask, FeedbackCubeConfig.POLL_INTERVAL);
		} catch (Exception e) {
			updateCurrentNoiseAndProgressbar("Error starting activity", 0.0);
			e.printStackTrace();
		}
	}

	private void stop() {
		try {


			Log.i("Noise", "==== Stop Noise Monitoring===");
			if (mWakeLock.isHeld()) {
				mWakeLock.release();
			}
			mHandler.removeCallbacks(mSleepTask);
			mHandler.removeCallbacks(mPollTask);
			mSensor.stop();
			bar.setProgress(0);
			updateCurrentNoiseAndProgressbar("stopped...", 0.0);
			mRunning = false;
		} catch (Exception e) {
			updateCurrentNoiseAndProgressbar("Error stopping activity", 0.0);
			e.printStackTrace();
		}

	}

	private void updateCurrentNoiseAndProgressbar(String status, double signalEMA) {
		mStatusView.setText(status);
		bar.setProgress((int) signalEMA);
		// Log.d("SONUND", String.valueOf(signalEMA));
		// tv_noice.setText(signalEMA + "dB");
	}

	private void updateAvgTextAndBackground(double signalAVG, double signal,
			FeedbackColor co) {

		// stop();

		// Show alert when noise thersold crossed
		// Toast.makeText(getApplicationContext(),
		// "Noise Thersold Crossed!!!!!", Toast.LENGTH_LONG).show();
		Log.d("PULSE",
				"Color [" + co.getR() + ", " + co.getG() + ", " + co.getB()
						+ "] dB:[" + String.valueOf(signal) + "] dB_AVG:["
						+ String.valueOf(signalAVG) + "]");
		tv_noice.setText("[" + co.getR() + ", " + co.getG() + ", " + co.getB()
				+ "] RGB \n[" + String.valueOf(signal) + "] dB \n["
				+ String.valueOf(signalAVG) + "] dbAVG");

		llTecla.setBackgroundColor(Color.rgb(co.getR(), co.getG(), co.getB()));
		
		
		ivFruit.setImageResource(getNoiseBadge(signalAVG));
	}

	
//	/**
//	 * Clicked Power button
//	 * 
//	 * @param v
//	 */
//	public void onPower(View v) {
//		// Boot cube on
//		String sIp = etIP.getText().toString();
//		FeedbackCubeConfig.getSingleInstance().setIp(sIp);
//		Log.i("FC", "Starting feedback cube ..."+sIp);
//		
//		FCOn f = new FCOn(FeedbackCubeConfig.getSingleInstance().getIp());
//		new FeedbackCubeManager().execute(f);
//
//	}
	
	/**
	 * Clicked ON button
	 * 
	 * @param v
	 */
	public void onOn(View v) {
		// make button visible
		ivFruit.setVisibility(View.VISIBLE);
		start();
	}

	/**
	 * Clicked OFF button
	 * 
	 * @param v
	 */
	public void onOff(View v) {
		stop();
		//make fruit invisible
		ivFruit.setVisibility(View.INVISIBLE);
		//ivFruit.setImageResource(R.drawable.noise_175x);
	}
	

	public void onToggle(View v) {
		
		ToggleButton tb = (ToggleButton)v;
		
		String sIp = etIP.getText().toString();
		
		if (tb.isChecked()){
			System.out.println("CHECKED "+tb.isChecked());
			
			// Boot cube on
			Log.i("FC", "Starting feedback cube ..."+sIp);			
			FCOn f = new FCOn(sIp);
			new FeedbackCubeManager().execute(f);

			
			
		}else{
			System.out.println("NOT CHECKED"+tb.isChecked());
			
			// Switch cube off
			Log.i("FC", "Stoping feedback cube ..."+sIp);			
			FCOff f = new FCOff(sIp);
			new FeedbackCubeManager().execute(f);

			
		}
		
	}
	
	
	
	/**
	 * Clicked Chart button
	 * 
	 * @param v
	 */
	public void onChart(View v) {

		Intent intent = new Intent(this, SubjectsActivity.class);
		startActivity(intent);
		
		
	}	
	
	
	/**
	 * Returns image resource for a given noise level taking into account
	 * the max and min thresholds
	 * 
	 * @param dNoise
	 * @return
	 */
	private int getNoiseBadge(double dNoise) {
		// Configure thresholds
		// etThresMin = (EditText) findViewById(R.id.etMimThreshold);
		try {
			
			//int iLevels = 7;
			
			Double dMinThres = new Double(etThresMin.getText().toString());
			Double dMaxThres = new Double(etThresMax.getText().toString());
			
			double dDiff = dMaxThres - dMinThres;
			//double dEscalon = dDiff / iLevels;
			
			
			return NoiseUtils.getFruitImage(dNoise, dMinThres, dMaxThres);

			
//			
//			if (dNoise < (dMinThres + (dEscalon*1)) ){
//				// Level noise level 1
//				return R.drawable.level1_175x;
//			}else if (dNoise < (dMinThres + (dEscalon*2))){
//				// Level noise level 2
//				return R.drawable.level2_175x;
//			}else if (dNoise < (dMinThres + (dEscalon*3))){
//				// Level noise level 3
//				return R.drawable.level3_175x;
//			}else if (dNoise < (dMinThres + (dEscalon*4))){
//				// Level noise level 4
//				return R.drawable.level4_175x;
//			}else if (dNoise < (dMinThres + (dEscalon*5))){
//				// Level noise level 5
//				return R.drawable.level5_175x;				
//			}else if (dNoise < (dMinThres + (dEscalon*6))){
//				// Level noise level 6
//				return R.drawable.level6_175x;
//			}else{
//				// Level noise level 7
//				return R.drawable.level7_175x;
//			}
			
			
		} catch (Exception e) {
			return R.drawable.levelunknown_175x;
		}

	}
	
	

};
