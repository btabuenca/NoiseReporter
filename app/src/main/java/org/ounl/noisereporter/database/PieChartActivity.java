/*******************************************************************************
 * Copyright (C) 2014 Open University of The Netherlands
 * Author: Bernardo Tabuenca Archilla
 * LearnTracker project 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.ounl.noisereporter.database;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.ounl.noisereporter.R;
import org.ounl.noisereporter.database.tables.MinStepDO;
import org.ounl.noisereporter.database.tables.NoiseSaladDO;
import org.ounl.noisereporter.prisma.config.Color;
import org.ounl.noisereporter.prisma.config.Constants;
import org.ounl.noisereporter.sensors.NoiseUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class PieChartActivity extends Activity {

	private String CLASSNAME = this.getClass().getName();


	private static double[] VALUES;
	private static String[] NAME_LIST;
	private DatabaseHandler db;
	private CategorySeries mSeries = new CategorySeries("");
	private DefaultRenderer renderer = new DefaultRenderer();
	private GraphicalView mChartView;
	private String mTag;
	private NoiseUtils noiseUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piechart);
		db = new DatabaseHandler(getApplicationContext());
		noiseUtils = new NoiseUtils();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mTag = extras.getString("TAG");
		}

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		//populateChart();
		applySize();

		renderer.setApplyBackgroundColor(true);
		// mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		renderer.setBackgroundColor(android.graphics.Color.WHITE);
		// renderer.setChartTitleTextSize(50f);
		renderer.setChartTitle("Overall distribution of time by assignment");
		// renderer.setLabelsTextSize(30f);
		renderer.setLabelsColor(android.graphics.Color.BLACK);
		// renderer.setLegendTextSize(30f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		renderer.setZoomButtonsVisible(true);
		renderer.setStartAngle(90);

		// Retrieve values from database
		
		MinStepDO ms = db.getMinStepNoiseSamples(mTag, Constants.NOISE_LEVELS);
//		Log.d(CLASSNAME,  " Fruit " + NAME_LIST[i] + " / Count " + VALUES[i]);
		List<NoiseSaladDO> las = db.getSalat(mTag, ms.getMinDecibels(), ms.getStepDecibels());
		
		NAME_LIST = new String[las.size()];
		VALUES = new double[las.size()];

		int iNumRecords = 0;
		for (int i = 0; i < las.size(); i++) {

			NAME_LIST[i] = las.get(i).getIngredient();
			VALUES[i] = las.get(i).getCount();
			mSeries.add(NAME_LIST[i], VALUES[i]);
			SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
			
			Color fbc = noiseUtils.getFeedbackColor(i+1, Constants.NOISE_LEVELS, 1);
			ssr.setColor(android.graphics.Color.parseColor(fbc.getHexColor()));
			
			//ssr.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
			renderer.addSeriesRenderer(ssr);
			Log.d(CLASSNAME, i 
					+ " Fruit " + NAME_LIST[i] 
					+ " / Count "
					+ VALUES[i]);
			iNumRecords++;

		}

		if (iNumRecords == 0) {
			Toast.makeText(
					PieChartActivity.this,
					"The pie chart is empty because you have not recorded any activity yet.",
					Toast.LENGTH_LONG).show();
		}

		if (mChartView != null) {
			mChartView.repaint();
		}

	}

	private void applySize() {
		switch (getResources().getDisplayMetrics().densityDpi) {
		case Constants.DENSITY_XXHIGH:
			renderer.setMargins(new int[] { 50, 130, 30, 10 });
			// renderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XXHDPI);
			renderer.setChartTitleTextSize(Constants.TEXT_SIZE_XXXHDPI);
			renderer.setLabelsTextSize(Constants.TEXT_SIZE_XXHDPI);
			renderer.setLegendTextSize(Constants.TEXT_SIZE_XXHDPI);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			renderer.setMargins(new int[] { 40, 90, 25, 10 });
			// renderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XHDPI);
			renderer.setChartTitleTextSize(Constants.TEXT_SIZE_XHDPI);
			renderer.setLabelsTextSize(Constants.TEXT_SIZE_XHDPI);
			renderer.setLegendTextSize(Constants.TEXT_SIZE_XHDPI);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			renderer.setMargins(new int[] { 30, 50, 20, 10 });
			// renderer.setAxisTitleTextSize(Constants.TEXT_SIZE_HDPI);
			renderer.setChartTitleTextSize(Constants.TEXT_SIZE_HDPI);
			renderer.setLabelsTextSize(Constants.TEXT_SIZE_HDPI);
			renderer.setLegendTextSize(Constants.TEXT_SIZE_HDPI);
			break;
		default:
			renderer.setMargins(new int[] { 30, 50, 20, 10 });
			// renderer.setAxisTitleTextSize(Constants.TEXT_SIZE_LDPI);
			renderer.setChartTitleTextSize(Constants.TEXT_SIZE_LDPI);
			renderer.setLabelsTextSize(Constants.TEXT_SIZE_LDPI);
			renderer.setLegendTextSize(Constants.TEXT_SIZE_LDPI);
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, renderer);
			renderer.setClickEnabled(true);
			renderer.setSelectableBuffer(10);

			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();

					if (seriesSelection == null) {
						// Temporary commented by btb
						 Toast.makeText(PieChartActivity.this,
						 "No chart element was clicked",
						 Toast.LENGTH_SHORT).show();
					} else {
						long lo = (long) seriesSelection.getValue();

						Toast.makeText(
								PieChartActivity.this,
								" You have " + lo
										+ " samples on ''", Toast.LENGTH_LONG).show();
						// Temporary commented by btb
						 Toast.makeText(
						 PieChartActivity.this,
						 "Chart element data point index "
						 + (seriesSelection.getPointIndex() + 1)
						 + " was clicked" + " point value="
						 + lo,
						 Toast.LENGTH_LONG).show();
					}
				}
			});

			mChartView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						// Temporary commented by btb
						 Toast.makeText(PieChartActivity.this,
						 "No chart element was long pressed",
						 Toast.LENGTH_SHORT).show();
						return false;
					} else {
						// Temporary commented by btb
						 Toast.makeText(PieChartActivity.this,
						 "Chart element data point index "
						 + seriesSelection.getPointIndex()
						 + " was long pressed",
						 Toast.LENGTH_SHORT).show();
						return true;
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed in the action
			// bar.
			// Create a simple intent that starts the hierarchical parent
			// activity and
			// use NavUtils in the Support Package to ensure proper handling of
			// Up.
			Intent upIntent = new Intent(this, SubjectsActivity.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is not part of the application's task, so
				// create a new task
				// with a synthesized back stack.
				TaskStackBuilder.from(this)
				// If there are ancestor activities, they should be added here.
						.addNextIntent(upIntent).startActivities();
				finish();
			} else {
				// This activity is part of the application's task, so simply
				// navigate up to the hierarchical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}