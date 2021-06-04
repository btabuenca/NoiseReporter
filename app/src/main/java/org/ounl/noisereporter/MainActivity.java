package org.ounl.noisereporter;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.ounl.noisereporter.database.DatabaseHandler;
import org.ounl.noisereporter.database.SubjectsActivity;
import org.ounl.noisereporter.database.tables.NoiseSampleTable;
import org.ounl.noisereporter.database.tables.TagTable;
import org.ounl.noisereporter.prisma.commands.ColorCommand;
import org.ounl.noisereporter.prisma.commands.OffCommand;
import org.ounl.noisereporter.prisma.commands.OnCommand;
import org.ounl.noisereporter.prisma.config.ConfigManager;
import org.ounl.noisereporter.prisma.config.Constants;
import org.ounl.noisereporter.prisma.RequestManagerAsyncTask;
import org.ounl.noisereporter.prisma.config.Color;
import org.ounl.noisereporter.sensors.NoiseSensor;
import org.ounl.noisereporter.sensors.NoiseUtils;
import org.ounl.noisereporter.thingsboard.RequestManagerThingsboardAsyncTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    private String DEBUG_TAG = "btb";

    private boolean mRunning = false;

    private PowerManager.WakeLock mWakeLock;
    private Handler mHandler = new Handler();

    private DatabaseHandler db;

    private NoiseUtils nu = new NoiseUtils();
    private NoiseSensor mSensor;


    private TextView mStatusView, tv_noice;
    ProgressBar bar;
    EditText etIP;
    EditText etTAG;
    EditText etThresMin;
    EditText etThresMax;
    LinearLayout llTecla;
    ImageView ivFruit;
    ToggleButton mToggleButton;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToggleButton = findViewById(R.id.tbFeedback);
        mStatusView = findViewById(R.id.status);
        tv_noice = findViewById(R.id.tv_noice);
        bar = findViewById(R.id.progressBar1);
        etIP = findViewById(R.id.editTextIP);
        etTAG = findViewById(R.id.editTextTAG);
        etTAG.setText(Calendar.getInstance().get(Calendar.YEAR) + "_"
                + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "_"
                + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "_"
                + Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        llTecla = findViewById(R.id.llTeclas);
        etThresMin = findViewById(R.id.etMimThreshold);
        etThresMax = findViewById(R.id.etMaxThreshold);
        ivFruit = findViewById(R.id.imageViewFruit);

        readThreshold(); // Get min and max thresholds
        mSensor = new NoiseSensor();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MainActivity"); // btb commented for deprecated
        mWakeLock = pm.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "MainActivity");

        db = new DatabaseHandler(getApplicationContext());

    }

    private void readThreshold() {
        try {
            ConfigManager.getSingleInstance().setmThresholdMin(
                    new Double(etThresMin.getText().toString()));

            ConfigManager.getSingleInstance().setmThresholdMax(
                    new Double(etThresMax.getText().toString()));
        } catch (Exception e) {
            updateCurrentNoiseAndProgressbar("Threshold have invalid values", 0.0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Log.i("Noise", "==== onResume ===");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.i("Noise", "==== onStop ===");
        // Stop noise monitoring
        stopMonitoring();
    }

    private void startMonitoring() {
        try {
            Log.i("Noise", "==== Start Noise Monitoring===");
            isRecordingPermissionGranted();
            mSensor.start();

            if (!mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }
            // Noise monitoring start
            // Runnable(mPollTask) will execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, ConfigManager.POLL_INTERVAL);
        } catch (Exception e) {
            updateCurrentNoiseAndProgressbar("Error starting activity", 0.0);
            e.printStackTrace();
        }
    }

    private void stopMonitoring() {
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

    }

    private void updateAvgTextAndBackground(double signalAVG, double signal, Color co) {


        // Show alert when noise thersold crossed
        Log.d("PULSE",
                "Color [" + co.getR() + ", " + co.getG() + ", " + co.getB()
                        + "] dB:[" + String.valueOf(signal) + "] dB_AVG:["
                        + String.valueOf(signalAVG) + "]");
        tv_noice.setText("[" + co.getR() + ", " + co.getG() + ", " + co.getB()
                + "] RGB \n[" + String.valueOf(signal) + "] dB \n["
                + String.valueOf(signalAVG) + "] dbAVG");

        llTecla.setBackgroundColor(android.graphics.Color.rgb(co.getR(), co.getG(), co.getB()));
        ivFruit.setImageResource(getNoiseBadge(signalAVG));
    }


    /**
     * Clicked ON button
     *
     * @param v
     */
    public void onOnButton(View v) {
        // make button visible
        ivFruit.setVisibility(View.VISIBLE);
        startMonitoring();
    }

    /**
     * Clicked OFF button
     *
     * @param v
     */
    public void onOffButton(View v) {
        stopMonitoring();
        // make fruit invisible
        ivFruit.setVisibility(View.INVISIBLE);
    }


    public void onToggleButton(View v) {

        ToggleButton tb = (ToggleButton) v;

        String sIp = etIP.getText().toString();

        if (tb.isChecked()) {
            System.out.println("CHECKED " + tb.isChecked());

            // Boot cube on
            Log.i("FC", "Starting feedback cube ..." + sIp);
            OnCommand f = new OnCommand(sIp);
            new RequestManagerAsyncTask().execute(f);


        } else {
            System.out.println("NOT CHECKED" + tb.isChecked());

            // Switch cube off
            Log.i("FC", "Stoping feedback cube ..." + sIp);
            OffCommand f = new OffCommand(sIp);
            new RequestManagerAsyncTask().execute(f);
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
        try {
            Double dMinThres = new Double(etThresMin.getText().toString());
            Double dMaxThres = new Double(etThresMax.getText().toString());
            double dDiff = dMaxThres - dMinThres;
            return NoiseUtils.getFruitImage(dNoise, dMinThres, dMaxThres);

        } catch (Exception e) {
            return R.drawable.levelunknown_175x;
        }

    }


    public void isRecordingPermissionGranted() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {Manifest.permission.RECORD_AUDIO}, 0);

        }
    }

    // Runnable thread to start monitoring
    private Runnable mSleepTask = new Runnable() {
        public void run() {
            // save one record in tags as new session
            String sTag = etTAG.getText().toString();
            Double dThresMin = new Double(etThresMin.getText().toString());
            Double dThresMax = new Double(etThresMax.getText().toString());
            db.addTag(new TagTable(sTag, dThresMin, dThresMax));

            // Start recording samples of noise
            startMonitoring();
        }
    };

    // Runnable thread to poll sampled data
    private Runnable mPollTask = new Runnable() {
        public void run() {

            try {
                // Read input data
                String sIp = etIP.getText().toString();
                ConfigManager.getSingleInstance().setIp(sIp);
                String sTag = etTAG.getText().toString();

                Date dNow = new Date();

                // Current value returned by the sensor
                double amp = mSensor.getAmplitude();

                if (!Double.isInfinite(amp)) {

                    // Average value for the last NUM_POLLS meaures
                    double ampAVG = 0;
                    int iLevel = Constants.NOISE_LEVEL_INIT;

                    // Add noise item to buffer and return position where it was
                    // inserted
                    int iNum = ConfigManager.getSingleInstance()
                            .addNoiseItem(amp);

                    // Return color for average values in buffer
                    // Commented for calibration
                    readThreshold();
                    Color color = ConfigManager.getSingleInstance().getBufferColor();

                    // Send color whenever the cube is activated
                    if (mToggleButton.isChecked()) {
                        // Launch color in the cube
                        ColorCommand fcc = new ColorCommand(ConfigManager
                                .getSingleInstance().getIp(), "" + color.getR(), ""
                                + color.getG(), "" + color.getB());
                        new RequestManagerAsyncTask().execute(fcc);

                        updateCurrentNoiseAndProgressbar("Monitoring on..." + sIp, amp);
                    }



                    // Prepare log
                    ampAVG = ConfigManager.getSingleInstance().getAverageNoise();

                    // Show average color in mobile display
                    updateAvgTextAndBackground(ampAVG, amp, color);

                    // Get thresholds
                    Double dThresMin = new Double(etThresMin.getText().toString());
                    Double dThresMax = new Double(etThresMax.getText().toString());

                    // Insert noise item into database
                    db.addNoiseSample(new NoiseSampleTable(dNow.getTime(), amp, ampAVG, dThresMin, dThresMax, sTag));
                    Log.i(DEBUG_TAG, "timestamp["+dNow.getTime()+"] amplitude["+mSensor.getAmplitude()+"] amplitudeEMA["+mSensor.getAmplitudeEMA()+"] amplitudeAVG["+ampAVG+"]");

                    // Push thingsboard always
                    JSONObject jsonObject = new JSONObject();
                    //jsonObject.put("amplitude", mSensor.getAmplitude());
                    jsonObject.put("amplitudeAVG", ampAVG);


                    new RequestManagerThingsboardAsyncTask().execute(jsonObject);





                    if (iNum == ConfigManager.NUM_POLLS) {
                        ConfigManager.getSingleInstance().resetPollIndex();
                    }

                }

                // Runnable(mPollTask) will again execute after POLL_INTERVAL
                mHandler.postDelayed(mPollTask,
                        ConfigManager.POLL_INTERVAL);

            } catch (Exception e) {
                updateCurrentNoiseAndProgressbar("" + e.getMessage(), 0.0);
                e.printStackTrace();
            }
        }
    };


};
