package com.gidi.bio_console.utils;

import android.util.SparseArray;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


public class TimeAxisValueFormatter implements IAxisValueFormatter {
    private SparseArray<String> times;
    public TimeAxisValueFormatter(SparseArray<String> times){
        this.times = times;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String time = null;
        try {
            time = times.get((int) value);
            String[] strings = time.split(" ");
            return strings[1];
        } catch (Exception e) {
            return "";
        }
    }
}
