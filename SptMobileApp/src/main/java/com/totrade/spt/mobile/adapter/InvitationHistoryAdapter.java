package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.master.entity.TblCompanyMasterEntity;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

import java.util.List;

public class InvitationHistoryAdapter extends SptMobileAdapterBase<TblCompanyMasterEntity>
{

	public InvitationHistoryAdapter(Context context, List<TblCompanyMasterEntity> dataList)
	{
		super(context, dataList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder vh = null;
		if (convertView == null)
		{
			convertView = View.inflate(getContext(), R.layout.item_invitation_history, null);
			vh = new ViewHolder();
			vh.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			vh.tv_time = (TextView) convertView.findViewById(R.id.tv_dealtime);
			convertView.setTag(vh);
		} else
		{
			vh = (ViewHolder) convertView.getTag();
		}

		vh.tv_name.setText(getDataList().get(position).getCompanyName());
		vh.tv_time.setText(FormatUtil.date2String(getDataList().get(position).getSubmitTime(), "yyyy/MM/dd"));

		return convertView;
	}

	class ViewHolder
	{
		TextView tv_name;
		TextView tv_time;
	}

}
