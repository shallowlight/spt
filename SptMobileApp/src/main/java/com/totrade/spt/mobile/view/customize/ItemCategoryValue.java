package com.totrade.spt.mobile.view.customize;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.view.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemCategoryValue extends LinearLayout
{
	TextView lblCategory;
	TextView lblValue;
	View view;
	public ItemCategoryValue(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initView(context);
	}
	
	private void initView(Context context)
	{
		if(view==null)
		{
			view=LayoutInflater.from(context).inflate(R.layout.item_categoryvalue, null);
			lblCategory=(TextView) view.findViewById(R.id.lblCategory);
			lblValue=(TextView)view.findViewById(R.id.lblValue);
		}
		else
		{
			lblCategory.setText("");
			lblValue.setText("");
		}
		if(this.getTag()!=null && this.getTag() instanceof String)
		{
			lblCategory.setText(this.getTag() +":");
		}
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		this.addView(view);
	}
	
	public void setCategoryName(String categoryName)
	{
		lblCategory.setText(categoryName+":");
	}
	
	public void setValue(String value)
	{
		lblValue.setText(value==null?"":value);
	}

	public void setValue(String key,String category)
	{
		String value = null;
		if(!StringUtility.isNullOrEmpty(key)
				&& !StringUtility.isNullOrEmpty(category))
		{
			value=DictionaryUtility.getValue(category, key);
		}
		lblValue.setText(value==null?"":value);
	}
	
	public void setTextColor(int textColor)
	{
		lblValue.setTextColor(textColor);
		lblCategory.setTextColor(textColor);
	}

	
	public TextView getLblCategory()
	{
		return lblCategory;
	}

	public TextView getLblValue()
	{
		return lblValue;
	}
	
}
