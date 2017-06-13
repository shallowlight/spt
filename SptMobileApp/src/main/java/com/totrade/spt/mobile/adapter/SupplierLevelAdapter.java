package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.common.ConstantArray;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;

public class SupplierLevelAdapter extends SptMobileAdapterBase<String>
{
	private String level;
	String[][] ss = ConstantArray.SUPLEVELS;
	public SupplierLevelAdapter(Context context,String level)
	{
		super(context, null);
		this.level = level;
	}

	@Override
	public String getItem(int position)
	{
		return ss[position][0];
	}

	public String getItemName(int position)
	{
		return ss[position][1];
	}

	@Override
	public int getCount()
	{
		return ss.length;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder ;
		if(convertView == null)
		{
			holder= new ViewHolder();
			convertView = holder.itemView;
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.setObjectPosition(position);
		return convertView;
	}

	class ViewHolder extends ViewHolderBase
	{
		@BindViewId(R.id.lblLevelName)
		TextView lblLevelName;
		@BindViewId(R.id.imgLevel)
		ImageView imgLevel;
		@BindViewId(R.id.imgFocus)
		ImageView imgFocus;
		public ViewHolder()
		{
			super(R.layout.item_supplier_level);
		}

		public void setObjectPosition(int position)
		{
			String[] ss1 = ss[position];
			lblLevelName.setText((position == getCount()-1?"加入":"设为") + ss1[1]);
			imgFocus.setVisibility((level!=null && level.equals(ss1[0]))?View.VISIBLE:View.INVISIBLE);
			imgLevel.setImageResource(DispUtility.supLevel2DispImg(ss1[0]));
		}
	}
}
