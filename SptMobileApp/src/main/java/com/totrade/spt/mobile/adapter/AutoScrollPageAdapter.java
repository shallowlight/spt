package com.totrade.spt.mobile.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.totrade.spt.mobile.app.SptApplication;

/**
 * 轮播图适配器
 *
 * @author huangxy
 * @date 2017/4/18
 */
public abstract class AutoScrollPageAdapter<T> extends PagerAdapter {

    public T[] ids;
    protected ViewPager viewPager;
    protected LinearLayout llPoint;

    public AutoScrollPageAdapter(T[] ids, ViewPager viewPager, LinearLayout llPoint) {

        this.ids = ids;
        this.viewPager = viewPager;
        this.llPoint = llPoint;
        autoTask = new AutoTask();

        for (T imgId : ids) {
            ImageView ivPoint = new ImageView(viewPager.getContext());
            ivPoint.setBackgroundResource(getUnSelectedPoint());
            llPoint.addView(ivPoint);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ivPoint.getLayoutParams();
            lp.leftMargin = getMargin();
            lp.rightMargin = getMargin();
            ivPoint.setLayoutParams(lp);
        }

        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下，停止轮播
                        autoTask.stop();
                        break;
                    case MotionEvent.ACTION_UP://抬起，重新轮播
                    case MotionEvent.ACTION_CANCEL://取消事件，也重新轮播
                        autoTask.start();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

    }

    public void selectPoint(int position) {
        for (int i = 0; i < llPoint.getChildCount(); i++) {
            if (i == position) {
                llPoint.getChildAt(i).setBackgroundResource(getSelectedPoint());
            } else {
                llPoint.getChildAt(i).setBackgroundResource(getUnSelectedPoint());
            }
        }
    }

    public abstract int getMargin();                                        //设置指示点间距

    public abstract int getUnSelectedPoint();                               //用来设置未选择时的圆点

    public abstract int getSelectedPoint();                                 //用来设置已选择时的圆点

    public abstract ImageView getImageView(ViewGroup container, T id);      //创建imageView对象

    public abstract long getTime();                                         //轮播图间隔时间

    @Override
    public abstract int getCount();

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv = getImageView(container, ids[position % ids.length]);
        container.addView(iv);
        return iv;
    }


    public AutoTask autoTask;

    /**
     * 首次进入时调用，开始滚动
     */
    public void startRoll() {
        autoTask.start();
    }

    /**
     * 最后一张图片时调用，停止滚动
     */
    public void stopRoll() {
        autoTask.stop();
    }

    class AutoTask implements Runnable {

        @Override
        public void run() {
            //切换Viewpager
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            SptApplication.appHandler.postDelayed(this, getTime());
        }

        public void start() {
            if (!isStart) {
                isStart = true;
                SptApplication.appHandler.postDelayed(this, getTime());
            }
        }

        private boolean isStart = false;

        public void stop() {
            if (isStart) {
                isStart = false;
                SptApplication.appHandler.removeCallbacks(this);//移除Runnable任务快
            }
        }

    }

}
