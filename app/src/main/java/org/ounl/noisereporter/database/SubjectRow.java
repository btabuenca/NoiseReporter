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
package org.ounl.noisereporter.database;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author BTB
 *
 */
public  class SubjectRow {
	public SubjectRow() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	String sIdSubject;
	ImageView ivBullet;
	TextView tvField0;
	TextView tvField1;
	TextView tvField2;
	TextView tvField3;
	
	ImageView ivPie;
	ImageView ivBar;
	ImageView ivScat;
}
