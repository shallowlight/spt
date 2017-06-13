package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class ScrollLinearLayout extends LinearLayout {
    public ScrollLinearLayout(Context context) {
        super(context);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (dispatchTouchEventListener != null) {
            dispatchTouchEventListener.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface DispatchTouchEventListener {
        boolean dispatchTouchEvent(MotionEvent ev);
    }

    private DispatchTouchEventListener dispatchTouchEventListener;

    public void setDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener) {
        this.dispatchTouchEventListener = dispatchTouchEventListener;
    }
}
