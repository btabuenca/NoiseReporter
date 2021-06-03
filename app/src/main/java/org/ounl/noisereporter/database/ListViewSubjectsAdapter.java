/*******************************************************************************
 * Copyright (C) 2015 Bernardo Tabuenca Archilla
 * Noise Reporter Project 
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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.ounl.noisereporter.database.tables.NoiseSampleTable;
import org.ounl.noisereporter.database.tables.NoiseSampleDO;
import org.ounl.noisereporter.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewSubjectsAdapter extends BaseAdapter {

    private String CLASSNAME = this.getClass().getName();

    private ArrayList<HashMap> list;

    Activity activity;

    public ListViewSubjectsAdapter(Activity activity, ArrayList<HashMap> list) {
        super();
        this.activity = activity;
        this.list = list;

        Log.d(CLASSNAME, "Creating list adapter with " + getCount() + " items  ");
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0l;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        SubjectRow sr = new SubjectRow();

        HashMap map = list.get(position);

        LayoutInflater inflater = activity.getLayoutInflater();


        v = inflater.inflate(R.layout.listview_row_subject, null);
        sr.ivBullet = (ImageView) v.findViewById(R.id.ivSubjectIcon);
        sr.tvField0 = (TextView) v.findViewById(R.id.tvSubjectLevel0);
        sr.tvField1 = (TextView) v.findViewById(R.id.tvSubjectLevel1);
        sr.tvField2 = (TextView) v.findViewById(R.id.tvSubjectLevel2);
        sr.tvField3 = (TextView) v.findViewById(R.id.tvSubjectLevel3);

        sr.ivPie = (ImageView) v.findViewById(R.id.ivPie);
        sr.ivBar = (ImageView) v.findViewById(R.id.ivBar);
        sr.ivScat = (ImageView) v.findViewById(R.id.ivScatter);


        sr.ivPie.setTag((String) map.get(NoiseSampleTable.KEY_TAG));
        sr.ivBar.setTag((String) map.get(NoiseSampleTable.KEY_TAG));
        sr.ivScat.setTag((String) map.get(NoiseSampleTable.KEY_TAG));


        sr.sIdSubject = (String) map.get(NoiseSampleTable.KEY_TAG);

        // Level 0
        sr.tvField0.setText((String) map.get(NoiseSampleTable.KEY_TAG));

        // Level 1
        sr.tvField1.setText(" Mean[" + (Double) map.get(NoiseSampleDO.KEY_AVG) + "]");

        // Level 2
        long lMin = (Long) map.get(NoiseSampleDO.KEY_MIN);
        long lMax = (Long) map.get(NoiseSampleDO.KEY_MAX);


        long minutes = TimeUnit.MILLISECONDS.toMinutes(lMax - lMin);
        sr.tvField2.setText("  " + minutes + " minutes");

        // Level 3
        Date dDateStart = new Date();
        dDateStart.setTime(lMin);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String reportDate = df.format(dDateStart);
        sr.tvField3.setText("  " + reportDate);


        return v;
    }
}
