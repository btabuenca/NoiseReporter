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

import android.database.Cursor;

/**
 * @author BTB
 *
 */
public class NoiseSaladPJ {
	
	public static final String KEY_INGREDIENT = "ingredient";
	public static final String KEY_COUNT= "count";	
	
	public String ingredient;
	public long count;
	
	
	public NoiseSaladPJ(String ingredient, long count) {
		super();
		this.ingredient = ingredient;
		this.count = count;
	}
	
	public NoiseSaladPJ(Cursor c) {

		this.ingredient = c.getString(1);
		this.count = c.getLong(2);

	}


	public String getIngredient() {
		return ingredient;
	}
	public void setIngredient(String ing) {
		this.ingredient = ing;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}

}
