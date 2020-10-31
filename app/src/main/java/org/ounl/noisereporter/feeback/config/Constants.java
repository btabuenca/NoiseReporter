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
package org.ounl.noisereporter.feeback.config;

/**
 * @author Bernardo Tabuenca
 *
 */
public class Constants {

	public final static int NOISE_LEVEL_INIT = -1;

	public static final int NOISE_LEVELS = 7;
	
	// Noise headings
	public static final String ICON_LEVEL_1 = "Broccoli";
	public static final String ICON_LEVEL_2 = "Apple";
	public static final String ICON_LEVEL_3 = "Grapes";
	public static final String ICON_LEVEL_4 = "Papaya";
	public static final String ICON_LEVEL_5 = "Banana";
	public static final String ICON_LEVEL_6 = "Onion";
	public static final String ICON_LEVEL_7 = "Pepper";
	
	
	public static long SEM = 604800000;
	public static long HORA = 3600000;
	public static long CUARTOHORA = 900000;
	public static long MEDIAHORA = 1800000;
	public static long TRESCUARTOSHORA = 2700000;
	public static long DIA = 86400000;
	
	// Text sizes for charts
	public static final int TEXT_SIZE_XXXXHDPI = 52;
	public static final int TEXT_SIZE_XXXHDPI = 48;
	public static final int TEXT_SIZE_XXHDPI = 36;
	public static final int TEXT_SIZE_XHDPI = 24;
	public static final int TEXT_SIZE_HDPI = 20;
	public static final int TEXT_SIZE_MDPI = 18;
	public static final int TEXT_SIZE_LDPI = 13;
	
	public static final int DENSITY_XXHIGH = 480;

	// User types and treatemnts
	public final static int USER_TYPE_SOCIAL = 1;
	public final static int USER_TYPE_YARDST = 2;
	
	// Results from operations
	public final static int OPERATION_SUCCESS = 0;
	public final static int OPERATION_FAILED = 1;
	

}
