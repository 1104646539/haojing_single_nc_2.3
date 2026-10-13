package com.tnd.multifuction1.activity;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class MyAxisValueFormatter implements IAxisValueFormatter
{
    private static final String TAG = "MyAxisValueFormatter";
    protected String[] data;


    public MyAxisValueFormatter(String[] data) {
        this.data = data;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

//        int days = (int) value;
//        Log.i(TAG, "value=======" + value);
//        Log.i(TAG, "axis=======" + axis.toString());
        int index = (int)value;
        index = index > data.length - 1 ? data.length - 1 : index;
        return data[index];
    }

}
