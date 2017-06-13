package com.totrade.spt.mobile.view.customize.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by Timothy on 2017/4/6.
 */

public class IntAxisValueFormatter implements IAxisValueFormatter{
    private List<List<Float>> values;
    public IntAxisValueFormatter(List<List<Float>> values){
        this.values = values;
    }
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int)value);
    }
}
