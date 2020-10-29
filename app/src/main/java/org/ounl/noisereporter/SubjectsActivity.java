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
package org.ounl.noisereporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.ounl.noisereporter.chart.PieChartActivity;
import org.ounl.noisereporter.db.DatabaseHandler;
import org.ounl.noisereporter.db.ListViewSubjectsAdapter;
import org.ounl.noisereporter.db.tables.NoiseSamplePJ;















//
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * A fragment that launches other parts of the demo application.
 */
public class SubjectsActivity extends Activity {

	private ArrayList<HashMap> list;
	private ListView lview;
	private Intent intent;
	private ListViewSubjectsAdapter adapter;
	private DatabaseHandler db;
	
	private static final int SERIES_NR = 2;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_section_subjects);
		
		db = new DatabaseHandler(getApplicationContext());		
		
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); 
        

		intent = new Intent(this, PieChartActivity.class);
		lview = (ListView) findViewById(R.id.listviewSubjects);
		lview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				intent.putExtra("TAG", (String)view.getTag());
				
                Toast.makeText( SubjectsActivity.this,                            
                		" Position clicked "+position,
                        Toast.LENGTH_LONG).show();
				startActivity(intent);
			}
		});

	}
	
	
	@Override	
	protected void onResume(){
		super.onResume();
		
		// Populate SQLlite list
		populateSubjectsFromLocal();

		adapter = new ListViewSubjectsAdapter(this,list);
		lview.setAdapter(adapter);
		
	}
	
	
	

	/**
	 * Populates list from local database
	 */
	private void populateSubjectsFromLocal() {
		list = new ArrayList<HashMap>();

		List<NoiseSamplePJ> lSubjectDb = db.getSessions();

		for (NoiseSamplePJ t : lSubjectDb) {

			// Records for data
			HashMap temp = new HashMap<String, String>();
			temp.put(NoiseSamplePJ.KEY_TAG, t.getTag());
			temp.put(NoiseSamplePJ.KEY_COUNT, t.getCount());
			temp.put(NoiseSamplePJ.KEY_MIN, t.getMin());
			temp.put(NoiseSamplePJ.KEY_MAX, t.getMax());
			temp.put(NoiseSamplePJ.KEY_AVG, t.getAvg());

		
			list.add(temp);
		}
	}
	
	

	public void onClickPie(View v){
		Intent intent = new Intent(this,PieChartActivity.class);
		intent.putExtra("TAG", "" + (String)v.getTag());
		startActivity(intent);	
	}
	
	
	public void onClickBar(View v){
		
//		de esta view tendras qu recoger el tag para poder hacer la busqueda en bd
//		de sTag, dMin, dSte que te hacen alta para el getBarDataset
//		
//		
//		
//		por alguna razon, no esta mostrando en la lista todos las grabaciones...especialmente las recien hechas
		
	      XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
	      setChartSettings(renderer);
	      intent = ChartFactory.getBarChartIntent(this, getBarDataset(), renderer, Type.DEFAULT);
	      startActivity(intent);
	}
	
	public void onClickScatter(View v){
	      intent = ChartFactory.getScatterChartIntent(this, getDemoDataset(), getDemoRenderer());
	      startActivity(intent);		
	}
	
	
	  private XYMultipleSeriesDataset getDemoDataset() {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    final int nr = 10;
		    Random r = new Random();
		    for (int i = 0; i < SERIES_NR; i++) {
		      XYSeries series = new XYSeries("Demo series " + (i + 1));
		      for (int k = 0; k < nr; k++) {
		        series.add(k, 20 + r.nextInt() % 100);
		      }
		      dataset.addSeries(series);
		    }
		    return dataset;
		  }	
	  
	  private XYMultipleSeriesRenderer getDemoRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setAxisTitleTextSize(16);
		    renderer.setChartTitleTextSize(20);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(15);
		    renderer.setPointSize(5f);
		    renderer.setMargins(new int[] {20, 30, 15, 0});
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.BLUE);
		    r.setPointStyle(PointStyle.SQUARE);
		    r.setFillBelowLine(true);
		    r.setFillBelowLineColor(Color.WHITE);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    r = new XYSeriesRenderer();
		    r.setPointStyle(PointStyle.CIRCLE);
		    r.setColor(Color.GREEN);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    renderer.setAxesColor(Color.DKGRAY);
		    renderer.setLabelsColor(Color.LTGRAY);
		    return renderer;
		  }	  

	
	
	  private XYMultipleSeriesDataset getBarDataset() {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    
		    //db.getSalat(sTag, dMin, dStep)
		    
		    
		    final int nr = 10;
		    Random r = new Random();
		    for (int i = 0; i < SERIES_NR; i++) {
		      CategorySeries series = new CategorySeries("Noise ranking" + (i + 1));
		      for (int k = 0; k < nr; k++) {
		        series.add(100 + r.nextInt() % 100);
		      }
		      dataset.addSeries(series.toXYSeries());
		    }
		    return dataset;
		  }	
	  
	  
	  
	
	  public XYMultipleSeriesRenderer getBarDemoRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setAxisTitleTextSize(16);
		    renderer.setChartTitleTextSize(20);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(15);
		    renderer.setMargins(new int[] {20, 30, 15, 0});
		    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		    r.setColor(Color.BLUE);
		    renderer.addSeriesRenderer(r);
		    r = new SimpleSeriesRenderer();
		    r.setColor(Color.GREEN);
		    renderer.addSeriesRenderer(r);
		    return renderer;
		  }	
	
	
	  private void setChartSettings(XYMultipleSeriesRenderer renderer) {
		    renderer.setChartTitle("Chart demo");
		    renderer.setXTitle("x values");
		    renderer.setYTitle("y values");
		    renderer.setXAxisMin(0.5);
		    renderer.setXAxisMax(10.5);
		    renderer.setYAxisMin(0);
		    renderer.setYAxisMax(210);
		  }	
	
	
	/**
	 * Export query from sqlite to csv and send by email
	 * OR
	 * WS request to insert data on server
	 */
	private void toCSV(){
		// http://stackoverflow.com/questions/14509026/export-sqlite-into-csv		
	}

	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, SubjectsActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
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