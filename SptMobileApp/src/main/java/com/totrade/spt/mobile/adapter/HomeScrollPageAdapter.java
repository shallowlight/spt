package com.totrade.spt.mobile.adapter;

import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.view.R;

/**
 * 首页头部轮播图适配器
 *
 * @author huangxy
 * @date 2017/4/1
 */
public class HomeScrollPageAdapter extends AutoScrollPageAdapter {

    public HomeScrollPageAdapter(String[] ids, ViewPager viewpager, LinearLayout llPoint) {
        super(ids, viewpager, llPoint);
    }

    @Override
    public int getMargin() {
        return DensityUtil.dp2px(SptApplication.context, 3);
    }

    @Override
    public int getUnSelectedPoint() {
        return R.drawable.iv_bg_oval;
    }

    @Override
    public int getSelectedPoint() {
        return R.drawable.iv_bg_oval2;
    }

    @Override
    public ImageView getImageView(ViewGroup container, Object id) {
        ImageView iv = new ImageView(container.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(lp);
        Picasso.with(container.getContext()).load((String) id).fit().centerCrop().into(iv);
        return iv;
    }

    @Override
    public long getTime() {
        return 3000;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

}
