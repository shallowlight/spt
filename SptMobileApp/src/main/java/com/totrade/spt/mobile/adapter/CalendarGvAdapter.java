package com.totrade.spt.mobile.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.entity.Day;
import com.totrade.spt.mobile.utility.CalendarUtil;
import com.totrade.spt.mobile.view.R;

public class CalendarGvAdapter extends BaseAdapter {

	List<Day> list;
	Context context;

	public CalendarGvAdapter(List<Day> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LinearLayout view = (LinearLayout) View.inflate(context, R.layout.calendar_item, null);
			convertView = view;
			holder = new ViewHolder();
			holder.tvDay = (TextView) view.findViewById(R.id.tv_calendar_item);
			view.setTag(holder); 
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Day d = list.get(position);
		String day = String.valueOf(d.getDay() == 0 ? "" : d.getDay());
		holder.tvDay.setText(day);
		if(d.getDay() != 0){
			if (d.equals(CalendarUtil.today)) {
				holder.tvDay.setTextColor(Color.RED);
			} else {
				holder.tvDay.setTextColor(context.getResources().getColor(R.color.color_gray_90));
			}
			if (d.equals(chooseDay)) {
				holder.tvDay.setTextColor(Color.WHITE);
				holder.tvDay.setBackgroundResource(R.drawable.red_bg_round);
			}
		}

		return convertView;
	}

	ViewHolder holder;

	private class ViewHolder {
		TextView tvDay;
	}

	private static Day chooseDay;

	@SuppressWarnings("static-access")
	public void setChooseDay(Day chooseDay) {
		this.chooseDay = chooseDay;
	}

}