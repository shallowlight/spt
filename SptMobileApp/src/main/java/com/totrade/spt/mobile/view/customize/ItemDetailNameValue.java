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

public class ItemDetailNameValue extends LinearLayout
{
	TextView lblName;
	TextView lblValue;
	View view;
	public ItemDetailNameValue(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initView(context);
	}
	
	public ItemDetailNameValue(Context context, String name,String value) 
	{
		super(context);
		initView(context);
		setName(name);
		setValue(value);
	}
	public ItemDetailNameValue(Context context, String name,String value,String cateGory) 
	{
		super(context);
		initView(context);
		setName(name);
		if(StringUtility.isNullOrEmpty(cateGory))
		{
			setValue(value);
		}
		else
		{
			setValue(value,cateGory);
		}
	}
	
	private void initView(Context context)
	{
		view=LayoutInflater.from(context).inflate(R.layout.item_common_detail, null);
		lblName=(TextView) view.findViewById(R.id.lblName);
		lblValue=(TextView)view.findViewById(R.id.lblValue);
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		this.addView(view);
	}
	
	public void setName(String categoryName)
	{
		lblName.setText(categoryName);
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
	
	public TextView getLblName()
	{
		return lblName;
	}

	public TextView getLblValue()
	{
		return lblValue;
	}
	
}
