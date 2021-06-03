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
package org.ounl.noisereporter.database;

import java.util.ArrayList;
import java.util.List;

import org.ounl.noisereporter.prisma.config.Constants;
import org.ounl.noisereporter.database.tables.MinStepDO;
import org.ounl.noisereporter.database.tables.NoiseSampleTable;
import org.ounl.noisereporter.database.tables.NoiseSampleDO;
import org.ounl.noisereporter.database.tables.NoiseSaladDO;
import org.ounl.noisereporter.database.tables.TagTable;

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
		db.execSQL(NoiseSampleTable.getCreateTable());
		
		Log.d(CLASSNAME, "Creating table Tag ...");
		db.execSQL(TagTable.getCreateTable());

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + NoiseSampleTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TagTable.TABLE_NAME);

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
	public void addNoiseSample(NoiseSampleTable o) {

		sqliteDB = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		o.loadContentValues(values);
		try {
			sqliteDB.insert(NoiseSampleTable.TABLE_NAME, null, values);
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
	public void addNoiseSamples(List<NoiseSampleTable> noiseSamples) {

		for (int i = 0; i < noiseSamples.size(); i++) {
			addNoiseSample(noiseSamples.get(i));
		}

	}

	/**
	 * Get NoiseSampless from database
	 * 
	 * @return
	 */
	public List<NoiseSampleTable> getNoiseSamples() {
		List<NoiseSampleTable> list = new ArrayList<NoiseSampleTable>();
		String selectQuery = "SELECT  * FROM " + NoiseSampleTable.TABLE_NAME;
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				NoiseSampleTable o = new NoiseSampleTable(cursor);
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
	public MinStepDO getMinStepNoiseSamples(String aTag, int aNumSteps) {
		
		MinStepDO ms = null;
		String selectQuery = "SELECT min(decibels), max(decibels), (max(decibels) - min(decibels))/"+aNumSteps+" as step FROM "+ NoiseSampleTable.TABLE_NAME+" WHERE tag ='"+aTag+"'" ;
		System.out.println(selectQuery);
		
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			ms = new MinStepDO(cursor);
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
	public List<NoiseSampleDO> getSessions() {
		List<NoiseSampleDO> list = new ArrayList<NoiseSampleDO>();
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
				
				
				NoiseSampleDO o = new NoiseSampleDO(sCursorTag, lCursorCOUNT, lCursorMIN, lCursorMAX, dCursorAVG);
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
	public void addTag(TagTable o) {

		sqliteDB = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		o.loadContentValues(values);
		try {
			sqliteDB.insert(TagTable.TABLE_NAME, null, values);
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
	public void addTags(List<TagTable> tags) {

		for (int i = 0; i < tags.size(); i++) {
			addTag(tags.get(i));
		}

	}

	/**
	 * Get Tags from database
	 * 
	 * @return
	 */
	public List<TagTable> getTags() {
		List<TagTable> list = new ArrayList<TagTable>();
		String selectQuery = "SELECT  * FROM " + TagTable.TABLE_NAME;
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				TagTable o = new TagTable(cursor);
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
	public TagTable getTag(String sTag) {
		List<TagTable> list = new ArrayList<TagTable>();
		String selectQuery = "SELECT  * FROM " + TagTable.TABLE_NAME + " WHERE "+ TagTable.KEY_TAG+"='"+sTag+"'";
		sqliteDB = this.getWritableDatabase();
		Cursor cursor = sqliteDB.rawQuery(selectQuery, null);
		TagTable o = new TagTable(cursor);
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
	public List<NoiseSaladDO> getSalat(String sTag, double dMin, double dStep) {
		
		List<NoiseSaladDO> list = new ArrayList<NoiseSaladDO>();
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
				NoiseSaladDO o = new NoiseSaladDO(cursor);
				list.add(o);
			} while (cursor.moveToNext());
		}
		cursor.close();
		sqliteDB.close();
		return list;
	}


}