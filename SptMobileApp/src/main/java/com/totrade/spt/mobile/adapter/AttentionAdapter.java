package com.totrade.spt.mobile.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.master.dto.ProductTypeDto;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.view.R;

public class AttentionAdapter extends SptMobileAdapterBase<ProductTypeDto>
{
	private Set<String> productTypeSet;
	private String productType;
	private static final int COLOR_WHITE=0xffffffff;
	
	public AttentionAdapter(Context context, List<ProductTypeDto> dataList)
	{
		super(context, dataList);
		productTypeSet =SharePreferenceUtility.spGetOutSet(context,SharePreferenceUtility.PUSH_PRODUCTSET, new HashSet<String>());
	}

	public void setProductType(String productType)
	{
		this.productType = productType;
	}

	private static class ViewHolder
	{
		TextView lblAttention;
		ImageView ivRedPoint;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = getLayoutInflater().inflate(R.layout.attention_item, null);
			viewHolder.lblAttention = (TextView) convertView.findViewById(R.id.lblAttention);
			viewHolder.ivRedPoint = (ImageView) convertView.findViewById(R.id.ivRedPoint);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ProductTypeDto entity = (ProductTypeDto) getItem(position);
		if (entity != null)
		{
			boolean selectType = entity.getProductType().equals(productType);
			viewHolder.ivRedPoint.setVisibility(View.INVISIBLE);
			for (String type : productTypeSet)
			{
				if (entity.getProductType().equals(type) && !selectType)
				{
					viewHolder.ivRedPoint.setVisibility(View.VISIBLE);
					break;
				}
			}
			viewHolder.lblAttention.setText(entity.getTypeName());
			if (selectType)
			{
				convertView.setBackgroundColor(0xffd9d9d9);
				viewHolder.lblAttention.setTextColor(COLOR_ORANGE);
			}
			else
			{
				convertView.setBackgroundColor(Color.TRANSPARENT);
				viewHolder.lblAttention.setTextColor(COLOR_WHITE);
			}

		}
		return convertView;
	}
}
