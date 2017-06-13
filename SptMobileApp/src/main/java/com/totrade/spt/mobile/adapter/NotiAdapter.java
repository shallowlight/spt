package com.totrade.spt.mobile.adapter;

import java.util.Date;
import java.util.List;

import com.autrade.spt.common.constants.SptConstant;
import com.totrade.spt.mobile.entity.NotiEntity;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class NotiAdapter extends SptMobileAdapterBase<NotiEntity>
{
	private ViewHolder vh;
	private boolean isEdit;
	private List<NotiEntity> checkList;

	public boolean isEdit()
	{
		return isEdit;
	}

	public void setEdit(boolean isEdit)
	{
		this.isEdit = isEdit;
	}

	public List<NotiEntity> getCheckList()
	{
		return checkList;
	}

	public void setCheckList(List<NotiEntity> checkList)
	{
		this.checkList = checkList;
	}

	public NotiAdapter(Context context, List<NotiEntity> dataList)
	{
		super(context, dataList);
		setEdit(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = View.inflate(getContext(), R.layout.item_notify, null);
			vh = new ViewHolder();
			vh.tvNotiType = (TextView) convertView.findViewById(R.id.tv_noti_type);
			vh.tvNotiTime = (TextView) convertView.findViewById(R.id.tv_noti_time);
			vh.tvNotiContent = (TextView) convertView.findViewById(R.id.tv_noti_content);
			vh.ivRedPoint = (ImageView) convertView.findViewById(R.id.iv_red_point);
			vh.ivCheck = (CheckBox) convertView.findViewById(R.id.iv_check);
			convertView.setTag(vh);
		} else
		{
			vh = (ViewHolder) convertView.getTag();
		}

		final NotiEntity entity = getDataList().get(position);
		String title = entity.getMsgTitle();
		String content = entity.getMsgContent();
		Date date = entity.getDate();
		String time = FormatUtil.date2String(date, "yyyy-MM-dd HH:mm");
		if (entity.getMsgType().equals(SptConstant.USER_PUSH_MSG_TAG_SUBMITBS))
		{
			title = "实盘询价";
		}
		vh.tvNotiType.setText(title);
		vh.ivRedPoint.setVisibility("0".equals(entity.getReadFlag()) ? View.VISIBLE : View.GONE);	//	"0":未读 "1":已读

		vh.tvNotiContent.setText(content);
		vh.tvNotiTime.setText(time);

		vh.ivCheck.setVisibility(isEdit() ? View.VISIBLE : View.GONE);
		vh.ivCheck.setChecked(checkList.contains(entity));
		vh.ivCheck.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (v.isSelected())
				{
					checkList.remove(entity);
				} else
				{
					checkList.add(entity);
				}
				v.setSelected(!v.isSelected());
			}
		});

		return convertView;
	}

	private static class ViewHolder
	{
		TextView tvNotiType, tvNotiTime, tvNotiContent;
		ImageView ivRedPoint;
		CheckBox ivCheck;
	}

}
