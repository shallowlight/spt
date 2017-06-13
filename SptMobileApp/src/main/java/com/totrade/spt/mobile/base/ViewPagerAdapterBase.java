package com.totrade.spt.mobile.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.totrade.spt.mobile.ui.mainhome.LineChatFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager+fragment适配器基础类
 * Created by Timothy on 2017/2/23.
 */

public class ViewPagerAdapterBase extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titles;

    public ViewPagerAdapterBase(FragmentManager fm, List<Fragment> fragmentList,List<String> titles) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList == null ? null : fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

}
