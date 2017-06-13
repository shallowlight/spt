package com.totrade.spt.mobile.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter
{
	private List<Fragment> lst;
	public SectionsPagerAdapter(FragmentManager fm, List<Fragment> lst)
	{
		super(fm);
		this.lst = lst;
	}


	@Override
	public Fragment getItem(int arg0)
	{
		return lst==null ? null : lst.get(arg0);
	}

	@Override
	public int getCount()
	{
		return lst==null ? 0 : lst.size();
	}
 

}
