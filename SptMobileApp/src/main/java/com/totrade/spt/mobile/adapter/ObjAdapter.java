package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.TextView;

import com.totrade.spt.mobile.entity.GTDeliveryPlaceEntity;
import com.totrade.spt.mobile.utility.FormatUtil;

import java.util.List;

public class ObjAdapter<T> extends SptMobileAdapterBase<T>
{
	private static AbsListView.LayoutParams params;
	private static int paddingleft;
	private T t;
	private int gravity=Gravity.CENTER_VERTICAL;

	public void setSelectItem(T t)
	{
		this.t = t;
		notifyDataSetChanged();
	}

	public ObjAdapter(Context context, List<T> dataList)
	{
		super(context, dataList);
		params=new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,FormatUtil.dip2px(context, 40));
		paddingleft=FormatUtil.dip2px(context, 10);
	}
	public void setGravity(int gravity)
	{
		this.gravity = gravity;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView=new TextView(getContext());
		convertView.setLayoutParams(params);
		((TextView)convertView).setGravity(gravity);
		((TextView)convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
		convertView.setPadding(paddingleft, 0, paddingleft, 0);

		@SuppressWarnings("unchecked")
		final T t = (T) getItem(position);
		if (null != t)
		{
			if(t instanceof GTDeliveryPlaceEntity)
			{
				((TextView)convertView).setText(((GTDeliveryPlaceEntity) t).getName());
			}
			else
			{
				((TextView)convertView).setText(t.toString());
			}
			if(this.t!=null && this.t.equals(t))
				((TextView)convertView).setTextColor(COLOR_BLUE);
			else
				((TextView)convertView).setTextColor(COLOR_BLACK);
		}
		return convertView;
	}
}
