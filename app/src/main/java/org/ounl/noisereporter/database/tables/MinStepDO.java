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
package org.ounl.noisereporter.database.tables;

import android.database.Cursor;

/**
 * @author BTB
 *
 */
public class MinStepDO {
	
	public static final String KEY_MINDECIBELS = "min";
	public static final String KEY_STEPDECIBELS= "dec";	
	
	public double minDecibels;
	public double stepDecibels;
	
	
	public MinStepDO(double ingredient, double count) {
		super();
		this.minDecibels = ingredient;
		this.stepDecibels = count;
	}
	
	public MinStepDO(Cursor c) {

		this.minDecibels = c.getDouble(0);
		this.stepDecibels = c.getDouble(2);

	}


	public double getMinDecibels() {
		return minDecibels;
	}
	public void setMinDecibels(double ing) {
		this.minDecibels = ing;
	}
	public double getStepDecibels() {
		return stepDecibels;
	}
	public void setSteDecibels(double count) {
		this.stepDecibels = count;
	}

}
