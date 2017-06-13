package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.autrade.stage.utility.CollectionUtility;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatAdapter extends SptMobileAdapterBase<IMMessage>
{

	public final static String TAG = "ChatAdapter";
	public ChatAdapter(Context context, List<IMMessage> dataList)
	{
		super(context, dataList);
		timedItems = new HashSet<>();
	}

	@Override
	public IMMessage getItem(int position)
	{
		int size = getCount();
		if(size==0) return null;

		int mPosition = size -1- position;
		return getDataList().get(mPosition);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ChatHolder ch;
		if (convertView == null)
		{
			ch = new ChatHolder(this);
			convertView = ch.initView();
		}
		else
		{
			ch = (ChatHolder) convertView.getTag();
		}
		IMMessage msg = getItem(position);
		if(msg!=null)
		{
			ch.refreshView(getItem(position));
		}
		return convertView;
	}

	public void notifyDataChanged()
	{
		if(!CollectionUtility.isNullOrEmpty(getDataList()))
		{
			updateShowTimeItem();
		}
		notifyDataSetChanged();
	}

	/**
	 * 数据变化时更新时间显示
	 */
	private void updateShowTimeItem()
	{
		timedItems.clear();
		IMMessage msg2 = getItem(getCount()-1);		//最早的消息
		for(IMMessage msg:getDataList())
		{
			if(msg2.getTime() - msg.getTime() >= 5*60*1000)
			{
				timedItems.add(msg2.getUuid());
			}
			msg2 = msg;
		}
		timedItems.add(getItem(0).getUuid());
	}

	private Set<String> timedItems; // 需要显示消息时间的消息ID

	/**
	 * 是否显示时间
	 *
	 * @return
	 */
	public boolean needShowTime(IMMessage immessage)
	{
		return timedItems.contains(immessage.getUuid());
	}

}
