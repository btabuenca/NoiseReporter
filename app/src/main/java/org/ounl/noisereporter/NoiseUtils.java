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

import java.util.concurrent.TimeUnit;

import org.ounl.noisereporter.feeback.FeedbackColor;
import org.ounl.noisereporter.feeback.FeedbackColorFactory;

import android.graphics.Color;


public class NoiseUtils {

	private FeedbackColorFactory fcf = null;
	
	public NoiseUtils(){
		fcf = new FeedbackColorFactory();
	}
	
	/**
	 * Difference in minutes between check-in and check-out
	 * 
	 * @return
	 */
	public String duration(long in, long out) {

		long ldiff = Long.valueOf(out).longValue() - Long.valueOf(in).longValue();
		return duration(ldiff);
	}
	
	/**
	 * Returns string hh:mm:ss for the mills given as param
	 * 
	 * @param mills
	 * @return
	 */
	public String duration(long mills) {
		
		int seconds = (int) (mills / 1000) % 60 ;
		int minutes = (int) ((mills / (1000*60)) % 60);
		int hours   = (int) ((mills / (1000*60*60)) % 24);
		
		String sSecs = ""+seconds;
		if (seconds < 10) {
			sSecs = "0"+seconds;
		}

		String sMins = ""+minutes;
		if (minutes < 10) {
			sMins = "0"+minutes;
		}
		
		String sHours = ""+hours;
		if (hours < 10) {
			sHours = "0"+hours;
		}		

		
		return sHours+":"+sMins+":"+sSecs;
		
	}	
	
	/**
	 * Returns minutes for given milliseconds overflowing positively
	 * 
	 * @param lMills
	 * @return
	 */
	public long toMins(long lMills){
		
		long minutes = TimeUnit.MILLISECONDS.toMinutes(lMills);
		
		return minutes ;
	}
	
	/**
	 * Returns hours for given milliseconds in double format
	 * 
	 * @param lMills
	 * @return
	 */
	public double toHours(long lMills){
		
		double hours = 0;
		double mins = 0;
		double seconds = 0;
		
		seconds = lMills / 1000;
		mins = seconds / 60;
		hours = mins / 60;
		
		return hours;
	}	
	
	
	/**
	 * Returns milliseconds for giving hours and mins
	 * 
	 * @param iHours
	 * @param iMins
	 * @return
	 */
	public long toMills (int iHours, int iMins){
		
		long lMillsMins = iMins * 60 * 1000;
		long lMillsHours = iHours * 60 * 60 * 1000;				
		
		return (lMillsMins + lMillsHours);		
	}
	
	/**
	 * Rounds given double value with the number of places. 
	 * Truncates rounding.
	 *  
	 * @param value
	 * @param places
	 * @return
	 */
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}		
	
	
	
	/**
	 * Returns noise level for a given noise deciebels
	 * 
	 * @param dNoise
	 * @return
	 */
	public static int getNoiseLevel(double dNoise, double dMinThres, double dMaxThres) {
		
		try {
			
			double dDiff = dMaxThres - dMinThres;
			double dEscalon = dDiff / Constants.NOISE_LEVELS;
			
			
			if (dNoise < (dMinThres + (dEscalon*1)) ){
				// Level noise level 1
				return 1;
			}else if (dNoise < (dMinThres + (dEscalon*2))){
				// Level noise level 2
				return 2;
			}else if (dNoise < (dMinThres + (dEscalon*3))){
				// Level noise level 3
				return 3;
			}else if (dNoise < (dMinThres + (dEscalon*4))){
				// Level noise level 4
				return 4;
			}else if (dNoise < (dMinThres + (dEscalon*5))){
				// Level noise level 5
				return 5;
			}else if (dNoise < (dMinThres + (dEscalon*6))){
				// Level noise level 6
				return 6;
			}else{
				// Level noise level 7
				return 7;
			}
			
			
		} catch (Exception e) {
			return -1;
		}

	}	
	
	/**
	 * Returns image icon for a given noise level and thresholds
	 * 
	 * @param dNoise
	 * @param dMinThres
	 * @param dMaxThres
	 * @return
	 */
	public static int getFruitImage(double dNoise, double dMinThres, double dMaxThres) {
		
		int iNL = getNoiseLevel(dNoise, dMinThres, dMaxThres);
		return getFruitImage(iNL);				
		
	}
	
	/**
	 * Return image identifier for a given NoiseLevel
	 * @param aNoiseLevel
	 * @return
	 */
	public static int getFruitImage(int aNoiseLevel){
		
		switch (aNoiseLevel) {
		case 1:
			return R.drawable.level1_175x;
		case 2:			
			return R.drawable.level2_175x;
		case 3:
			return R.drawable.level3_175x;			
		case 4:
			return R.drawable.level4_175x;
		case 5:
			return R.drawable.level5_175x;
		case 6:
			return R.drawable.level6_175x;
		case 7:
			return R.drawable.level7_175x;			
		default:
			return R.drawable.levelunknown_175x;
		}
		
		
	}
	

	
	
	/**
	 * Returns feedback color for given noise and level
	 * 
	 * @param dAVGBufferNoise
	 * @param mThresholdMax
	 * @param mThresholdMin
	 * @return
	 */
	public FeedbackColor getFeedbackColor(double dAVGBufferNoise, double mThresholdMax, double mThresholdMin){
		
		int iPos = 0;
		if(dAVGBufferNoise > mThresholdMax){
			// Supera 
			iPos = FeedbackColorFactory.COLOR_GRADIENT_SIZE -1;
		}else if(dAVGBufferNoise < mThresholdMin){
			// Esta por debajo
			iPos = 0;
		}else{
			// Esta en el rango
			double dRange = mThresholdMax - mThresholdMin;
			double dStep = dRange / FeedbackColorFactory.COLOR_GRADIENT_SIZE; 
			double dPos = ((dAVGBufferNoise - mThresholdMin) / dStep);
			iPos = (int)dPos;

			//dBufferPos = ((FeedbackCubeColorFactory.COLOR_GRADIENT_SIZE * (dAVGBufferNoise - mThresholdMin)) / (mThresholdMax - mThresholdMin));	
		}	
		

		
		return fcf.getColor(iPos);		
	}
	
	
	/**
	 * Returns Hex color for given level
	 * @param mLevel
	 * @return
	 */
	public String getHexColor(int mLevel){
		
		int iPos = 0;
		double dStep = FeedbackColorFactory.COLOR_GRADIENT_SIZE / Constants.NOISE_LEVELS;
		
		iPos = (int)dStep * mLevel;
		
//		
//		FeedbackColor fbc =  fcf.getColor(iPos);
//		
//		String sHexColor = fbc.getHexColor();
//		
//		Color.parseColor(sHexColor);
//		
		return fcf.getColor(iPos).getHexColor();
		
	}
	


}
