package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Timothy on 2017/3/31.
 */

public class SmartViewpager extends ViewPager {

    private PointF touchDownPoint = new PointF();//触摸按下的点
    private PointF touchCurPoint = new PointF();//触摸当前点

    private OnSingleTouchListener onSingleTouchListener;

    public SmartViewpager(Context context) {
        super(context);
    }

    public SmartViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        touchCurPoint.x = ev.getX();
        touchCurPoint.y = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            touchDownPoint.x = ev.getX();
            touchDownPoint.y = ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);//通知父窗体不要拦截这次触摸
        }

        int curItem = getCurrentItem();
        if (curItem == 0) {
            getParent().requestDisallowInterceptTouchEvent(!(touchDownPoint.x <= touchCurPoint.x));
        } else if (curItem == getAdapter().getCount() - 1) {
            getParent().requestDisallowInterceptTouchEvent(!(touchDownPoint.x >= touchCurPoint.x));
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        if (ev.getAction() == MotionEvent.ACTION_UP){
            if (ev.getX() == touchDownPoint.x && ev.getY() == touchDownPoint.y){
                if (this.onSingleTouchListener != null){
                    onSingleTouchListener.onTouchListener(curItem);
                }
            }
        }
        return super.onTouchEvent(ev);
    }

    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener){
        this.onSingleTouchListener = onSingleTouchListener;
    }

    public interface OnSingleTouchListener {
        void onTouchListener(int position);
    }

}
