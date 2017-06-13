package com.totrade.spt.mobile.view.customize.chart;

import android.content.Context;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.view.R;

public class MPChartMarkerView extends MarkerView {

    private ArrowTextView tvContent;

    public MPChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (ArrowTextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText(StringUtils.double2String(ce.getHigh(), 2));
        } else {
            tvContent.setText(StringUtils.double2String(e.getY(), 2));
        }
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            mOffset = new MPPointF(getWidth()/10, -getHeight() / 2);
        }
        return mOffset;
    }
}
