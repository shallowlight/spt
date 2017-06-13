package com.totrade.spt.mobile.utility;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 专区一级类别选择器
 *
 * @author huangxy
 * @date 2016/11/26
 */
public class ScreenSelection {
    private List<View> views = new ArrayList<>();
    public static ScreenSelection create() {
        return new ScreenSelection();
    }
    public ScreenSelection setView(final LinearLayout linearLayout) {
        linearLayout.setOnTouchListener(new SelcetionTouchListener());
        if (linearLayout.getChildCount() < 1) {
            return this;
        }
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            views.add(linearLayout.getChildAt(i));
        }

//        初始化位置
        linearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onSelcetionListener != null) {
                    linearLayout.getChildAt(0).setSelected(true);
                    onSelcetionListener.selction(linearLayout.getChildAt(0), 0);
                }
            }
        }, 50);

        return this;
    }

//    public void init(LinearLayout linearLayout) {
//        if (onSelcetionListener != null) {
//            linearLayout.getChildAt(0).setSelected(true);
//            onSelcetionListener.selction(linearLayout.getChildAt(0));
//        }
//    }

    class SelcetionTouchListener implements View.OnTouchListener {

        private int rawX;
        private int rawY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    rawX = (int) event.getRawX();
                    rawY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int delX = (int) event.getRawX();
                    int delY = (int) event.getRawY();
                    if (Math.abs(delX - rawX) > 10 || Math.abs(delY - rawY) > 10 ) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    for (int i = 0; i < views.size(); i++) {
                        View child = views.get(i);
                        if (isTouched(child, rawX, rawY)) {
                            child.setSelected(true);
                            if (onSelcetionListener != null) {
                                onSelcetionListener.selction(child, i);
                            }
                        } else {
                            child.setSelected(false);
                        }
                    }
                    break;
            }

            return true;
        }

    }

    /**
     * 是否在View的范围内
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    private static boolean isTouched(View v, int x, int y) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        Rect rect = new Rect(location[0], location[1], location[0] + v.getWidth(), location[1] + v.getHeight());
        return rect.contains(x, y);
    }

    private OnSelcetionListener onSelcetionListener;

    public interface OnSelcetionListener
    {
        void selction(View v, int position);
    }

    public void setOnSelcetionListener(OnSelcetionListener onSelcetionListener)
    {
        this.onSelcetionListener = onSelcetionListener;
    }

}
