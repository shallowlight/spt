package com.totrade.spt.mobile.utility.chart;

import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.chart.IntAxisValueFormatter;
import com.totrade.spt.mobile.view.customize.chart.MPChartMarkerView;
import com.totrade.spt.mobile.view.customize.chart.MyValueFormatter;
import com.totrade.spt.mobile.view.customize.chart.StringAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Timothy on 2017/3/28.
 */

public class MPChartHelper {

    public static final int[] LINE_COLORS = {
            Color.rgb(48, 169, 222), Color.rgb(159, 143, 186), Color.rgb(233, 197, 23)
    };

    public static final int[] LINE_FILL_COLORS = {
            Color.rgb(192, 229, 245), Color.rgb(246, 234, 208), Color.rgb(235, 228, 248)
    };


    private static void setData(BarChart barChart, List<Data> dataList, String title) {

        ArrayList<BarEntry> values = new ArrayList<BarEntry>();
        List<Integer> colors = new ArrayList<Integer>();

        int green = Color.rgb(195, 221, 155);
        int red = Color.rgb(237, 189, 189);

        for (int i = 0; i < dataList.size(); i++) {

            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);
            if (d.yValue >= 0) colors.add(red);
            else colors.add(green);
        }
        BarDataSet set;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, title);
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueFormatter(new PositiveNegativeBarChartValueFormatter());
            data.setBarWidth(0.8f);

            barChart.setData(data);
            barChart.invalidate();
        }
    }

    private static class Data {

        public String xAxisValue;
        public float yValue;
        public float xValue;

        public Data(float xValue, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
        }
    }

    private static class PositiveNegativeBarChartValueFormatter implements IValueFormatter {

        private DecimalFormat mFormattedStringCache;

        public PositiveNegativeBarChartValueFormatter() {
            mFormattedStringCache = new DecimalFormat("######.00");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormattedStringCache.format(value);
        }
    }

    /**
     * 单线单y轴图。
     *
     * @param lineChart
     * @param xAxisValue
     * @param yAxisValue
     * @param title
     * @param showSetValues 是否在折线上显示数据集的值。true为显示，此时y轴上的数值不可见，否则相反。
     */
    public static void setLineChart(LineChart lineChart, List<String> xAxisValue, List<Float> yAxisValue, String title, boolean showSetValues) {


        if (ObjUtils.isEmpty(xAxisValue) && ObjUtils.isEmpty(yAxisValue)) {
            lineChart.setData(null);
        }

        List<List<Float>> entriesList = new ArrayList<>();
        entriesList.add(yAxisValue);

        List<String> titles = new ArrayList<>();
        titles.add(title);

        setLinesChart(lineChart, xAxisValue, entriesList, titles, showSetValues, null);
    }

    /**
     * 绘制线图，默认最多绘制三种颜色。所有线均依赖左侧y轴显示。
     *
     * @param lineChart
     * @param xAxisValue    x轴的轴
     * @param yXAxisValues  y轴的值
     * @param titles        每一个数据系列的标题
     * @param showSetValues 是否在折线上显示数据集的值。true为显示，此时y轴上的数值不可见，否则相反。
     * @param lineColors    线的颜色数组。为null时取默认颜色，此时最多绘制三种颜色。
     */
    public static void setLinesChart(LineChart lineChart, List<String> xAxisValue, List<List<Float>> yXAxisValues, List<String> titles, boolean showSetValues, int[] lineColors) {
        lineChart.getDescription().setEnabled(false);//设置描述
        lineChart.setNoDataText("没有数据");
        lineChart.setPinchZoom(false);//设置按比例放缩柱状图
        MPChartMarkerView markerView = new MPChartMarkerView(lineChart.getContext(), R.layout.custom_marker_view);
        lineChart.setMarker(markerView);
        //x坐标轴设置
        IAxisValueFormatter xAxisFormatter = new StringAxisValueFormatter(xAxisValue);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);//网格线
        xAxis.setGridLineWidth(1f);
        xAxis.setGridColor(Color.parseColor("#ECECEC"));
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisValue.size());
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(xAxisFormatter);

        //y轴设置
        IAxisValueFormatter yAxisFormatter = new IntAxisValueFormatter(yXAxisValues);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridLineWidth(1f);
        leftAxis.setGridColor(Color.parseColor("#ECECEC"));
        leftAxis.setTextColor(Color.parseColor("#999999"));
        leftAxis.setTextSize(10f);
        leftAxis.setLabelCount(xAxisValue.size());
        leftAxis.setValueFormatter(yAxisFormatter);
        leftAxis.setDrawLabels(!showSetValues);//折线上显示值，则不显示坐标轴上的值
//        leftAxis.setDrawZeroLine(true);
        /*leftAxis.setAxisMinimum(0f);*/
        /*leftAxis.setAxisLineWidth(2f);*/

        lineChart.getAxisRight().setEnabled(false);

        //图例设置
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setDrawInside(false);
//        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
//        legend.setForm(Legend.LegendForm.LINE);
//        legend.setTextSize(12f);

        //设置折线图数据
        setLinesChartData(lineChart, yXAxisValues, titles, showSetValues, lineColors);

        lineChart.setExtraOffsets(10, 30, 20, 10);
//        lineChart.animateX(1500);//数据显示动画，从左往右依次显示
    }

    private static void setLinesChartData(LineChart lineChart, List<List<Float>> yXAxisValues, List<String> titles, boolean showSetValues, int[] lineColors) {

        List<List<Entry>> entriesList = new ArrayList<>();
        for (int i = 0; i < yXAxisValues.size(); ++i) {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int j = 0, n = yXAxisValues.get(i).size(); j < n; j++) {
                entries.add(new Entry(j, yXAxisValues.get(i).get(j)));
            }
            entriesList.add(entries);
        }

        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            for (int i = 0; i < lineChart.getData().getDataSetCount(); ++i) {
                LineDataSet set = (LineDataSet) lineChart.getData().getDataSetByIndex(i);
                set.setValues(entriesList.get(i));
                set.setLabel(titles.get(i));
            }
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            for (int i = 0; i < entriesList.size(); ++i) {
                LineDataSet set = new LineDataSet(entriesList.get(i), titles.get(i));
                set.setDrawHighlightIndicators(false);
                if (lineColors != null) {
                    set.setColor(lineColors[i % entriesList.size()]);
                    set.setCircleColor(lineColors[i % entriesList.size()]);
                    set.setCircleColorHole(lineColors[i % entriesList.size()]);
                } else {
                    set.setColor(LINE_COLORS[i % 3]);
                    set.setCircleColor(LINE_COLORS[i % 3]);
                    set.setCircleColorHole(LINE_COLORS[i % 3]);
                }

                if (entriesList.size() == 1) {
                    set.setDrawFilled(true);
                    set.setFillColor(LINE_FILL_COLORS[i % 3]);
                }
                dataSets.add(set);
            }

            LineData data = new LineData(dataSets);
            if (showSetValues) {
                data.setValueTextSize(10f);
                data.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
                        return StringUtils.double2String(value, 1);
                    }
                });
            } else {
                data.setDrawValues(false);
            }

            lineChart.setData(data);
        }
    }


    /**
     * 设置柱线组合图样式，柱图依赖左侧y轴，线图依赖右侧y轴
     */
    public static void setCombineChart(CombinedChart combineChart, final List<String> xAxisValues, List<Float> lineValues, List<Float> barValues, String lineTitle, String barTitle) {
        combineChart.setNoDataText("没有数据");
        combineChart.getDescription().setEnabled(false);//设置描述
//        Description description = new Description();
//        description.setText("单位：月");
//        description.setTextColor(Color.parseColor("#999999"));
//        description.setXOffset(40f);
//        description.setYOffset(40f);
//        combineChart.setDescription(description);

        if (ObjUtils.isEmpty(xAxisValues) && ObjUtils.isEmpty(lineValues) && ObjUtils.isEmpty(barValues)) {
            combineChart.setData(null);
        }


        combineChart.setPinchZoom(false);//设置按比例放缩柱状图

        MPChartMarkerView markerView = new MPChartMarkerView(combineChart.getContext(), R.layout.custom_marker_view);
        combineChart.setMarker(markerView);

//        combineChart.setHighlightFullBarEnabled(true);

        //设置绘制顺序，让线在柱的上层
        combineChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        //x坐标轴设置
        XAxis xAxis = combineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.parseColor("#e5e5e5"));
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisValues.size() + 2);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if (v < 0 || v > (xAxisValues.size() - 1))//使得两侧柱子完全显示
                    return "";
                return xAxisValues.get((int) v);
            }
        });

        //y轴设置
        YAxis leftAxis = combineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#e5e5e5"));
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.parseColor("#999999"));
        leftAxis.setTextSize(10f);
        leftAxis.setAxisMinimum(0f);

        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        try {
            Float yMin = Double.valueOf(Collections.min(barValues) * 0.95).floatValue();
            Float yMax = Double.valueOf(Collections.max(barValues) * 1.1).floatValue();
            leftAxis.setAxisMaximum(yMax);
            leftAxis.setAxisMinimum(yMin);
        } catch (Exception e) {

        }

        YAxis rightAxis = combineChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setTextColor(Color.parseColor("#999999"));
        rightAxis.setTextSize(10f);

        rightAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        //图例设置
        combineChart.getLegend().setEnabled(false);
//        Legend legend = combineChart.getLegend();
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setDrawInside(true);
//        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
//        legend.setForm(Legend.LegendForm.CIRCLE);
//        legend.setTextColor(Color.parseColor("#3b3b3b"));
//        legend.setTextSize(12f);

        //设置组合图数据
        CombinedData data = new CombinedData();
        data.setData(generateLineData(lineValues, lineTitle));
        data.setData(generateBarData(barValues, barTitle));

        combineChart.setData(data);//设置组合图数据源

        //使得两侧柱子完全显示
        xAxis.setAxisMinimum(combineChart.getCombinedData().getXMin() - 0.5f);
        xAxis.setAxisMaximum(combineChart.getCombinedData().getXMax() + 0.5f);

        combineChart.setExtraTopOffset(30);
        combineChart.setExtraBottomOffset(10);
//        combineChart.animateX(1500);//数据显示动画，从左往右依次显示
        combineChart.invalidate();
    }

    /**
     * 生成线图数据
     */
    private static LineData generateLineData(List<Float> lineValues, String lineTitle) {
        ArrayList<Entry> lineEntries = new ArrayList<>();

        for (int i = 0, n = lineValues.size(); i < n; ++i) {
            lineEntries.add(new Entry(i, lineValues.get(i)));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, lineTitle);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setColor(Color.rgb(228, 197, 88));
        lineDataSet.setLineWidth(1.0f);//设置线的宽度
        lineDataSet.setCircleColor(Color.rgb(218, 86, 86));//设置圆圈的颜色
        lineDataSet.setCircleColorHole(Color.rgb(218, 86, 86));//设置圆圈内部洞的颜色
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);//设置线数据依赖于右侧y轴
        lineDataSet.setDrawValues(false);//不绘制线的数据

        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillAlpha(50);
        lineDataSet.setFillColor(Color.rgb(228, 197, 88));

        LineData lineData = new LineData(lineDataSet);
        lineData.setValueTextSize(10f);
        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return StringUtils.double2String(value, 2);
            }
        });

        return lineData;
    }

    /**
     * 生成柱图数据
     *
     * @param barValues
     * @return
     */
    private static BarData generateBarData(List<Float> barValues, String barTitle) {

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0, n = barValues.size(); i < n; ++i) {
            barEntries.add(new BarEntry(i, barValues.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, barTitle);
        barDataSet.setColor(Color.rgb(124, 200, 232));
        barDataSet.setValueTextColor(Color.parseColor("#999999"));
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.9f);
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return StringUtils.double2String(value, 2);
            }
        });

        return barData;
    }
}
