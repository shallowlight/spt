package com.totrade.spt.mobile.adapter;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.totrade.spt.mobile.base.BasePager;

import java.util.ArrayList;

public class AccMngAdapter extends PagerAdapter
{
	private ArrayList<BasePager> pagerList;
	
	public AccMngAdapter(ArrayList<BasePager> pagerList)
	{
		super();
		this.pagerList = pagerList;
	}

	@Override
	public int getCount()
	{
		return pagerList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		BasePager bp = pagerList.get(position);
		View containterView = bp.initView();
		bp.prepare();
		container.addView(containterView);
		return containterView;
	}
	
}