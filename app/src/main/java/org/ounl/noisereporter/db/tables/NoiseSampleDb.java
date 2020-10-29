 /*******************************************************************************
 * Copyright (C) 2014 Open University of The Netherlands
 * Author: Bernardo Tabuenca Archilla
 * Lifelong Learning Hub project 
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
package org.ounl.noisereporter.db.tables;

import android.content.ContentValues;
import android.database.Cursor;

public class NoiseSampleDb{
	
	public static final String TABLE_NAME = "noisesample";
	
	public static final String KEY_TIMESTAMP= "timestamp";
	public static final String KEY_DECIBELS = "decibels";
	public static final String KEY_DECIBELSAVG = "decibelsavg";
	public static final String KEY_TAG = "tag";
    
	private long lTimestamp;
	private double dDecibels;
	private double dDecibelsavg;
	private String sTag;
	
	
	
	public NoiseSampleDb(long lTimestamp, double dDecibels,
			double dDecibelsavg, double dDecibelsminthreshold,
			double dDecibelsmaxthreshold, String sTag) {
		super();
		this.lTimestamp = lTimestamp;
		this.dDecibels = dDecibels;
		this.dDecibelsavg = dDecibelsavg;
		
		this.sTag = sTag;
	}

	
	public NoiseSampleDb(Cursor c) {

		this.lTimestamp = c.getInt(0);
		this.dDecibels = c.getDouble(1);
		this.dDecibelsavg = c.getDouble(2);
		this.sTag = c.getString(3);

	}	
	

	
	public static String getCreateTable(){
		
        String sSQL = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_TIMESTAMP + " INTEGER PRIMARY KEY," 
        		+ KEY_DECIBELS + " REAL, "
        		+ KEY_DECIBELSAVG + " REAL, "
                + KEY_TAG + " TEXT" + ")";
		
		return sSQL;
		
	}


	public void loadContentValues(ContentValues cv) {

		 cv.put(KEY_TIMESTAMP, getlTimestamp());
		 cv.put(KEY_DECIBELS, getdDecibels());
		 cv.put(KEY_DECIBELSAVG, getdDecibelsavg());
		 cv.put(KEY_TAG, getsTag());
		 
		 
//		 cv.put(KEY_DECIBELSMINTHRESHOLD, getdDecibasdfaelsavg());
//		 cv.put(KEY_DECIBELSMAXTHRESHOLD, getdDecibelasdfassavg());
//		 
;
		 			
	}



	public long getlTimestamp() {
		return lTimestamp;
	}



	public void setlTimestamp(long lTimestamp) {
		this.lTimestamp = lTimestamp;
	}



	public double getdDecibels() {
		return dDecibels;
	}



	public void setdDecibels(double dDecibels) {
		this.dDecibels = dDecibels;
	}



	public double getdDecibelsavg() {
		return dDecibelsavg;
	}



	public void setdDecibelsavg(double dDecibelsavg) {
		this.dDecibelsavg = dDecibelsavg;
	}



	public String getsTag() {
		return sTag;
	}



	public void setsTag(String sTag) {
		this.sTag = sTag;
	}

}
