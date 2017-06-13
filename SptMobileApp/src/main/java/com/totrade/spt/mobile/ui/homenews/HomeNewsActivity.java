package com.totrade.spt.mobile.ui.homenews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.HomeScrollPageAdapter;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.view.R;

/**
*
* 新闻列表
* @author huangxy
* @date 2017/4/5
*
*/
public class HomeNewsActivity extends BaseActivity implements XRecyclerView.LoadingListener{

    private ViewPager viewPager;
    private TextView tvDescription;
    private LinearLayout llPoint;
    private XRecyclerView recyclerView;

    private NewsH5Fragment newsH5Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homenews);

        initView();
    }

    private void initView() {
        tvDescription = (TextView) findViewById(R.id.tv_description);
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerView);
        newsH5Fragment = new NewsH5Fragment();
        switchContent(newsH5Fragment);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (newsH5Fragment.onKeyDown(keyCode, event)) return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initData() {
        /*View headView = LayoutInflater.from(this).inflate(R.layout.layout_news_head, null);
        viewPager = (ViewPager) headView.findViewById(R.id.viewPager);
        llPoint = (LinearLayout) headView.findViewById(R.id.ll_point);
        int[] ids = new int[]{R.drawable.home_head_bg1, R.drawable.home_head_bg2, R.drawable.home_head_bg3, };
        final HomeScrollPageAdapter homeScrollPageAdapter = new HomeScrollPageAdapter(ids, viewPager, llPoint);
        viewPager.setAdapter(homeScrollPageAdapter);
        viewPager.setCurrentItem((Integer.MAX_VALUE / 2) % 4);

        homeScrollPageAdapter.selectPoint(0);
        homeScrollPageAdapter.startRoll();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                homeScrollPageAdapter.selectPoint(position % 4);
            }
        });

        recyclerView.addHeaderView(headView);
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);
        recyclerView.setAdapter(new NewsAdapter());
*/
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
    }

}
