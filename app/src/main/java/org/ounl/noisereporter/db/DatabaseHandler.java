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
package org.ounl.noisereporter.db;

import java.util.ArrayList;
import java.util.List;

import org.ounl.noisereporter.Constants;
import org.ounl.noisereporter.db.tables.MinStepPJ;
import org.ounl.noisereporter.db.tables.NoiseSampleDb;
import org.ounl.noisereporter.db.tables.NoiseSamplePJ;
import org.ounl.noisereporter.db.tables.NoiseSaladPJ;
import org.ounl.noisereporter.db.tables.TagDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private String CLASSNAME = this.getClass().getSimpleName();

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "noise.db";
	private SQLiteDatabase sqliteDB = null;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		sqliteDB = this.getWritableDatabase();
		sqliteDB.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		sqliteDB = db;

		Log.d(CLASSNAME, "Creating table NoiseSample ...");
		db.execSQL(NoiseSampleDb.getCreateTable());
		
		Log.d(CLASSNAME, "Creating table Tag ...");
		db.execSQL(TagDb.getCreateTable());		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + NoiseSampleDb.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TagDb.TABLE_NAME);

		onCreate(db);
	}

	//---------------------------------------------------------------------------
	// Table noisesample
	//---------------------------------------------------------------------------
	
	/**
	 * Insert NoiseSample into db
	 * 
	 * @param o
	 */
	public void addNoiseSample(NoiseSampleDb o) {

		sqliteDB = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		o.loadContentValues(values);
		try {
			sqliteDB.insert(NoiseSampleDb.TABLE_NAME, null, values);
		} catch (Exception e) {
			Log.e(CLASSNAME, "Error inserting NoiseSample " + e.toString() + "");
			e.printStackTrace();
		}
		sqliteDB.close();
	}

	/**
	 * Insert list of NoiseSampless into db
	 * 
	 * @param NoiseSampless
	 */
	public void addNoiseSamples(List<NoiseSampleDb> noiseSamples) {

		for (int i = 0; i < noiseSamples.size(); i++) {
			addNoiseSample(noiseSamples.get(i));
		}

	}

	/**
	 * Get NoiseSampless from database
	 * 
	 * @return
	 */
	public List<NoiseSampleDb> getNoiseSamples() {
		List<NoiseSampleDb> list = new ArrayList<NoiseSampleDb>();
		String selectQuery = "SELECT  * FROM " + NoiseSampleDb.TABLE_NAME;
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				NoiseSampleDb o = new NoiseSampleDb(cursor);
				list.add(o);
			} while (cursor.moveToNext());
		}
		cursor.close();
		sqliteDB.close();
		return list;
	}
	
	/**
	 * Get min and step NoiseSample for a given tag
	 * 
	 * @return
	 */
	public MinStepPJ getMinStepNoiseSamples(String aTag, int aNumSteps) {
		
		MinStepPJ ms = null;
		String selectQuery = "SELECT min(decibels), max(decibels), (max(decibels) - min(decibels))/"+aNumSteps+" as step FROM "+ NoiseSampleDb.TABLE_NAME+" WHERE tag ='"+aTag+"'" ;
		System.out.println(selectQuery);
		
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			ms = new MinStepPJ(cursor);
		}
		cursor.close();
		sqliteDB.close();
		return ms;
	}	
	
	/**
	 * Get subjects from database ordered by order
	 * 
	 * @return
	 */
	public List<NoiseSamplePJ> getSessions() {
		List<NoiseSamplePJ> list = new ArrayList<NoiseSamplePJ>();
		String selectQuery = "SELECT tag, count(*), min(timestamp), max(timestamp), avg(decibels) FROM noisesample GROUP BY tag ORDER by timestamp asc";
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				
				
				String sCursorTag = cursor.getString(0);
				long lCursorCOUNT = Long.parseLong(cursor.getString(1));
				long lCursorMIN = Long.parseLong(cursor.getString(2));
				long lCursorMAX = Long.parseLong(cursor.getString(3));
				double dCursorAVG = Double.parseDouble(cursor.getString(4));
				
				
				NoiseSamplePJ o = new NoiseSamplePJ(sCursorTag, lCursorCOUNT, lCursorMIN, lCursorMAX, dCursorAVG);
				list.add(o);
			} while (cursor.moveToNext());
		}
		cursor.close();
		sqliteDB.close();
		return list;
	}		
	
	
	//---------------------------------------------------------------------------
	// Table tag
	//---------------------------------------------------------------------------	
	
	/**
	 * Insert Tag into db
	 * 
	 * @param o
	 */
	public void addTag(TagDb o) {

		sqliteDB = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		o.loadContentValues(values);
		try {
			sqliteDB.insert(TagDb.TABLE_NAME, null, values);
		} catch (Exception e) {
			Log.e(CLASSNAME, "Error inserting Tag " + e.toString() + "");
			e.printStackTrace();
		}
		sqliteDB.close();
	}

	/**
	 * Insert list of Tags into db
	 * 
	 * @param Tagss
	 */
	public void addTags(List<TagDb> tags) {

		for (int i = 0; i < tags.size(); i++) {
			addTag(tags.get(i));
		}

	}

	/**
	 * Get Tags from database
	 * 
	 * @return
	 */
	public List<TagDb> getTags() {
		List<TagDb> list = new ArrayList<TagDb>();
		String selectQuery = "SELECT  * FROM " + TagDb.TABLE_NAME;
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				TagDb o = new TagDb(cursor);
				list.add(o);
			} while (cursor.moveToNext());
		}
		cursor.close();
		sqliteDB.close();
		return list;
	}
	
	
	/**
	 * Returns Tag for a given tag identifier
	 * 
	 * @return
	 */
	public TagDb getTag(String sTag) {
		List<TagDb> list = new ArrayList<TagDb>();
		String selectQuery = "SELECT  * FROM " + TagDb.TABLE_NAME + " WHERE "+TagDb.KEY_TAG+"='"+sTag+"'";
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		TagDb o = new TagDb(cursor);
		if (cursor.moveToFirst()) {				
				list.add(o);
		}
		cursor.close();
		sqliteDB.close();
		return o;
	}	
	
	

	/**
	 * Get Tags from database
	 * 
	 * WATCH OUT. THIS IS HARD CODED CONFIGURED FOR 7 LEVELS OF NOISE
	 * 
	 * @return
	 */
	public List<NoiseSaladPJ> getSalat(String sTag, double dMin, double dStep) {
		
		List<NoiseSaladPJ> list = new ArrayList<NoiseSaladPJ>();
		String selectQuery = " ";
		selectQuery += " SELECT '1', '"+Constants.ICON_LEVEL_1+"', count(*) ";
		selectQuery += " FROM noisesample ";
		selectQuery += " where ";
		selectQuery += " 	tag = '"+ sTag +"'"; 
		selectQuery += " 	AND";
		selectQuery += " 	decibels BETWEEN ("+ dMin +") AND ("+ dMin +" + ("+ dStep +" * 1))";
		selectQuery += " UNION";
		selectQuery += " SELECT '2', '"+Constants.ICON_LEVEL_2+"', count(*)";
		selectQuery += " FROM noisesample ";
		selectQuery += " where ";
		selectQuery += " 	tag = '"+ sTag +"'"; 
		selectQuery += " 	AND";
		selectQuery += " 	decibels BETWEEN ("+ dMin +" + ("+ dStep +" * 1)) AND ("+ dMin +" + ("+ dStep +" * 2))";
		selectQuery += " UNION";
		selectQuery += " SELECT '3', '"+Constants.ICON_LEVEL_3+"', count(*)";
		selectQuery += " FROM noisesample ";
		selectQuery += " where ";
		selectQuery += " 	tag = '"+ sTag +"'"; 
		selectQuery += " 	AND";
		selectQuery += " 	decibels BETWEEN ("+ dMin +" + ("+ dStep +" * 2)) AND ("+ dMin +" + ("+ dStep +" * 3))";
		selectQuery += " UNION";
		selectQuery += " SELECT '4', '"+Constants.ICON_LEVEL_4+"', count(*)";
		selectQuery += " FROM noisesample ";
		selectQuery += " where ";
		selectQuery += " 	tag = '"+ sTag +"'"; 
		selectQuery += " 	AND";
		selectQuery += " 	decibels BETWEEN ("+ dMin +" + ("+ dStep +" * 3)) AND ("+ dMin +" + ("+ dStep +" * 4))";
		selectQuery += " UNION";
		selectQuery += " SELECT '5', '"+Constants.ICON_LEVEL_5+"', count(*)";
		selectQuery += " FROM noisesample ";
		selectQuery += " where ";
		selectQuery += " 	tag = '"+ sTag +"'"; 
		selectQuery += " 	AND";
		selectQuery += " 	decibels BETWEEN ("+ dMin +" + ("+ dStep +" * 4)) AND ("+ dMin +" + ("+ dStep +" * 5))";
		selectQuery += " UNION";
		selectQuery += " SELECT '6', '"+Constants.ICON_LEVEL_6+"', count(*)";
		selectQuery += " FROM noisesample ";
		selectQuery += " where ";
		selectQuery += " 	tag = '"+ sTag +"'"; 
		selectQuery += " 	AND";
		selectQuery += " 	decibels BETWEEN ("+ dMin +" + ("+ dStep +" * 5)) AND ("+ dMin +" + ("+ dStep +" * 6))";	
		selectQuery += " UNION";
		selectQuery += " SELECT '7', '"+Constants.ICON_LEVEL_7+"', count(*)";
		selectQuery += " FROM noisesample ";
		selectQuery += " where ";
		selectQuery += " 	tag = '"+ sTag +"'"; 
		selectQuery += " 	AND";
		selectQuery += " 	decibels BETWEEN ("+ dMin +" + ("+ dStep +" * 6)) AND ("+ dMin +" + ("+ dStep +" * 7))";
		
		System.out.println(selectQuery);

		
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				NoiseSaladPJ o = new NoiseSaladPJ(cursor);
				list.add(o);
			} while (cursor.moveToNext());
		}
		cursor.close();
		sqliteDB.close();
		return list;
	}


}