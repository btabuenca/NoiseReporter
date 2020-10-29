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
package org.ounl.noisereporter.db.tables;

/**
 * @author BTB
 *
 */
public class NoiseSamplePJ {
	
	public static final String KEY_TAG = "tag";
	public static final String KEY_COUNT= "count";	
	public static final String KEY_MIN = "min";
	public static final String KEY_MAX = "max";
	public static final String KEY_AVG = "avg";
	
	
	public NoiseSamplePJ(String tag, long count, long min, long max, double avg) {
		super();
		this.tag = tag;
		this.count = count;
		this.min = min;
		this.max = max;
		this.avg = avg;
	}
	public String tag;
	public long count;
	public long min;
	public long max;
	public double avg;


	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getMin() {
		return min;
	}
	public void setMin(long min) {
		this.min = min;
	}
	public long getMax() {
		return max;
	}
	public void setMax(long max) {
		this.max = max;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}

}
