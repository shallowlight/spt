package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/12/1.
 */
public class ScroListView extends ListView {
    public ScroListView(Context context) {
        super(context);
    }

    public ScroListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScroListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
