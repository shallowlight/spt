package com.totrade.spt.mobile.ui.fundmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 资金管理
 * 入金/出金
 * Created by Timothy on 2017/4/13.
 */

public class OutIntoCurrencyFragment extends BaseSptFragment<FundManagerActivity> {

    private ImageView iv_back;
    private SegmentTabLayout stb_tab;
    private ViewPager view_pager;

    private String[] tabs = new String[]{"出金", "入金"};
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private IntoFundFragment intoFundFragment;
    private OutFundFragment outFundFragment;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_outinto_currency;
    }

    @Override
    protected void initView() {
        iv_back = findView(R.id.iv_back);
        stb_tab = findView(R.id.stb_tab);
        view_pager = findView(R.id.view_pager);

        intoFundFragment = new IntoFundFragment();
        outFundFragment = new OutFundFragment();
        fragments.add(outFundFragment);
        fragments.add(intoFundFragment);
        view_pager.setAdapter(new MyViewPager(getChildFragmentManager(), fragments, Arrays.asList(tabs)));
        stb_tab.setTabData(tabs);
        view_pager.addOnPageChangeListener(mOnPageChangeListener);
        stb_tab.setOnTabSelectListener(mOnTabSelectListener);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });
    }

    private OnTabSelectListener mOnTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            view_pager.setCurrentItem(position);
        }

        @Override
        public void onTabReselect(int position) {

        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            stb_tab.setCurrentTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    class MyViewPager extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public MyViewPager(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList == null ? null : fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }

}
