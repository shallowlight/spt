package com.totrade.spt.mobile.adapter;

import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.view.R;

/**
 * splash 轮播图适配器
 *
 * @author huangxy
 */
public class SplashPagerAdapter extends AutoScrollPageAdapter {

    public SplashPagerAdapter(Integer[] ids, ViewPager viewpager, LinearLayout llPoint) {
        super(ids, viewpager, llPoint);
    }

    @Override
    public int getMargin() {
        return DensityUtil.dp2px(SptApplication.context, 20);
    }

    @Override
    public int getUnSelectedPoint() {
        return R.drawable.iv_bg_splash_ring;
    }

    @Override
    public int getSelectedPoint() {
        return R.drawable.iv_bg_splash_oval;
    }

    @Override
    public ImageView getImageView(ViewGroup container, Object id) {
        ImageView iv = new ImageView(container.getContext());
        iv.setBackgroundResource((Integer) id);
        return iv;
    }

    @Override
    public long getTime() {
        return 2000;
    }


    @Override
    public int getCount() {
        return ids.length;
    }


}
