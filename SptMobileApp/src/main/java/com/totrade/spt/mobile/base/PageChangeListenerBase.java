package com.totrade.spt.mobile.base;

import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class PageChangeListenerBase implements ViewPager.OnPageChangeListener 
{
	private int[] ints;
	private RadioGroup radioGroup;
	public PageChangeListenerBase(RadioGroup radioGroup,int...ints)
	{
		this.ints = ints;
		this.radioGroup = radioGroup;
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) 
	{
		
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		
	}

	@Override
	public void onPageSelected(int position) 
	{
		if(ints!= null && ints.length>position && radioGroup!=null)
		{
			radioGroup.check(ints[position]);
		}
	}

	private ArrayList<Integer> loopRadioIds()
	{
		if(radioGroup!=null)
		{
			int size = radioGroup.getChildCount();
			ArrayList<Integer> ids = new ArrayList<>();
			for(int i =0;i<size;i++)
			{
				if(radioGroup.getChildAt(i) instanceof RadioButton)
				{
					ids.add(radioGroup.getChildAt(i).getId());
				}
			}
			return ids;
		}
		return null;
	}

}
