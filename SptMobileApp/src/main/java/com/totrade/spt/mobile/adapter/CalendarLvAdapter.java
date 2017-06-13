package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.totrade.spt.mobile.entity.Day;
import com.totrade.spt.mobile.utility.CalendarUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomGridView;

import java.util.List;

public class CalendarLvAdapter extends BaseAdapter
{

	private Context context;

	public CalendarLvAdapter(Context context)
	{
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return 200;
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			View view = View.inflate(context, R.layout.calendar, null);
			convertView = view;
			holder = new ViewHolder();
			holder.tvYearMonth = (TextView) view.findViewById(R.id.tv_year_month);
			holder.gridview = (CustomGridView) view.findViewById(R.id.gv);
			view.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		final List<Day> dataList = CalendarUtil.getData(position);
		gvAdapter = new CalendarGvAdapter(dataList, context);
		if(chooseDay != null){
			gvAdapter.setChooseDay(chooseDay);
			gvAdapter.notifyDataSetChanged();
			notifyDataSetChanged();
			chooseDay = null;
		}
		holder.tvYearMonth.setText(CalendarUtil.getYear() + "年" + CalendarUtil.getMonth() + "月");
		holder.gridview.setAdapter(gvAdapter);
		holder.gridview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (listener != null)
				{
					listener.onOrder(CalendarUtil.getChooseDate(dataList.get(position)));

					gvAdapter.setChooseDay(dataList.get(position));
					gvAdapter.notifyDataSetChanged();
					notifyDataSetChanged();
				}
			}
		});
		return convertView;
	}

	private ViewHolder holder;
	private CalendarGvAdapter gvAdapter;

	private class ViewHolder
	{
		TextView tvYearMonth;
		CustomGridView gridview;
	}

	private OnCalendarOrderListener listener;

	public void setOnCalendarOrderListener(OnCalendarOrderListener listener)
	{
		this.listener = listener;
	}

	public interface OnCalendarOrderListener
	{
		void onOrder(Day day);
	}

	private static Day chooseDay;

	@SuppressWarnings("static-access")
	public void setChooseDay(Day chooseDay)
	{
		this.chooseDay = chooseDay;
	}
}