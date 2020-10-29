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

public class TagDb{
	
	public static final String TABLE_NAME = "tag";
	
	public static final String KEY_TAG = "tag";	
	public static final String KEY_DECIBELSMINTHRESHOLD = "decibelsminthreshold";
	public static final String KEY_DECIBELSMAXTHRESHOLD = "decibelsmaxthreshold";

	private String sTag;
	private double dDecibelsminthreshold;
	private double dDecibelsmaxthreshold;	
	
	
	public TagDb(String sTag, double dDecibelsminthreshold,
			double dDecibelsmaxthreshold) {
		super();
		this.dDecibelsminthreshold = dDecibelsminthreshold;
		this.dDecibelsmaxthreshold = dDecibelsmaxthreshold;		
		this.sTag = sTag;
	}

	
	public TagDb(Cursor c) {
		this.sTag = c.getString(0);
		this.dDecibelsminthreshold = c.getDouble(1);
		this.dDecibelsmaxthreshold = c.getDouble(2);
	}	
	

	
	public static String getCreateTable(){
		
        String sSQL = "CREATE TABLE " + TABLE_NAME + "("
        		+ KEY_TAG + " TEXT PRIMARY KEY," 
        		+ KEY_DECIBELSMINTHRESHOLD + " REAL, "
        		+ KEY_DECIBELSMAXTHRESHOLD + " REAL )";
		
		return sSQL;
		
	}


	public void loadContentValues(ContentValues cv) {

//		 cv.put(KEY_TIMESTAMP, getlTimestamp());
//		 cv.put(KEY_DECIBELS, getdDecibels());
//		 cv.put(KEY_DECIBELSAVG, getdDecibelsavg());
//		 cv.put(KEY_DECIBELSMINTHRESHOLD, getdDecibasdfaelsavg());
//		 cv.put(KEY_DECIBELSMAXTHRESHOLD, getdDecibelasdfassavg());
//		 
//		 cv.put(KEY_TAG, getTag());
		 			
	}


	public String getsTag() {
		return sTag;
	}


	public void setsTag(String sTag) {
		this.sTag = sTag;
	}


	public double getdDecibelsminthreshold() {
		return dDecibelsminthreshold;
	}


	public void setdDecibelsminthreshold(double dDecibelsminthreshold) {
		this.dDecibelsminthreshold = dDecibelsminthreshold;
	}


	public double getdDecibelsmaxthreshold() {
		return dDecibelsmaxthreshold;
	}


	public void setdDecibelsmaxthreshold(double dDecibelsmaxthreshold) {
		this.dDecibelsmaxthreshold = dDecibelsmaxthreshold;
	}



}
