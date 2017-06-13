package com.totrade.spt.mobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.totrade.spt.mobile.view.R;

public class GridViewAdapter extends BaseAdapter
{
	private ViewHolder vh;
	private int[] imgIds;
	private String[] names;
	private Context context;
 
	public GridViewAdapter(Context context, int[] imgIds, String[] names)
	{
		this.imgIds = imgIds;
		this.names = names;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return imgIds.length;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		vh = new ViewHolder();
		View view = View.inflate(context, R.layout.item_gridview_user, null);
		vh.iv = (ImageView) view.findViewById(R.id.iv_gridview);
		vh.tv = (TextView) view.findViewById(R.id.tv_gridview);
		vh.iv.setBackgroundResource(imgIds[position]);
		vh.tv.setText(names[position]);
		return view;
	}

	private class ViewHolder
	{
		ImageView iv;
		TextView tv;
	}

	@Override
	public Object getItem(int position)
	{
		return names[position];
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}
}
