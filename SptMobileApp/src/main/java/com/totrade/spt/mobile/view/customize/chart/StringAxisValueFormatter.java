package com.totrade.spt.mobile.view.customize.chart;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.totrade.spt.mobile.utility.LogUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 对字符串类型的坐标轴标记进行格式化
 */
public class StringAxisValueFormatter implements IAxisValueFormatter {

    //区域值
    private List<String> mStrs;

    /**
     * 对字符串类型的坐标轴标记进行格式化
     * @param strs
     */
    public StringAxisValueFormatter(List<String> strs){
        this.mStrs =strs;
    }

    @Override
    public String getFormattedValue(float v, AxisBase axisBase) {
//        LogUtils.i("坐标：","v:"+v+"*****"+Arrays.toString(axisBase.mEntries));
//        LogUtils.i("坐标：",Arrays.toString(axisBase.mCenteredEntries));
//        if (v > axisBase.getAxisMaximum()){
//            v = axisBase.getAxisMaximum();
//        }
//        if ( axisBase.getAxisMinimum() < 0){
//            v = 0;
//        }
        String xValue;
        try{
            xValue = mStrs.get((int) v);
        }catch (Exception e){
            xValue = "";
        }
        return xValue;
    }
}
