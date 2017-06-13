package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Timothy on 2017/2/28.
 */

public class FocusScrollView extends ScrollView {
    public FocusScrollView(Context context) {
        super(context);
    }

    public FocusScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
