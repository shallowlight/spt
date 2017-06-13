package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.TblUserNegoMasterEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;

import java.util.List;

public class NegoTemplateAdapter extends SptMobileAdapterBase<TblUserNegoMasterEntity>
{
	private boolean editVisible = false;
	private List<Boolean> selectList;

	public NegoTemplateAdapter(Context context,List<TblUserNegoMasterEntity> dataList, List<Boolean> paramList)
	{
		super(context, dataList);
		this.selectList = paramList;
	}

	public boolean isEditVisible() {
		return editVisible;
	}

	private boolean getItemSelect(int position)
	{
		return selectList!=null && selectList.size()-1 >= position && selectList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = getLayoutInflater().inflate(R.layout.item_nego_template,parent,false);
			AnnotateUtility.bindViewFormId(holder,convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		TblUserNegoMasterEntity entity = (TblUserNegoMasterEntity) getItem(position);

		if (entity != null)
		{
			//设置显示值
			holder.lblProductName.setText(entity.getProductName());
			holder.lblBuySell.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BUY_SELL,entity.getBuySell()));
			holder.lblProductPrice.setText(DispUtility.price2Disp(entity.getProductPrice(),entity.getPriceUnit(),entity.getNumberUnit()));
			holder.lblProductNumber.setText(DispUtility.productNum2Disp(entity.getProductNumber(),null,entity.getNumberUnit()));
			holder.lblDeliveryTime.setText(DispUtility.deliveryTimeToDisp(entity.getDeliveryTime(),entity.getProductType(),entity.getTradeMode()));
			holder.lblDeliveryPlace.setText(!TextUtils.isEmpty(entity.getDeliveryPlaceName())?entity.getDeliveryPlaceName()
					:DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE,entity.getDeliveryPlace()));
			holder.lblBond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND,entity.getBond()));
			boolean isBuyOrder = SptConstant.BUYSELL_BUY.equals(entity.getBuySell());
			//颜色背景
			holder.lblBuySell.setSelected(isBuyOrder);
			holder.lblProductPrice.setSelected(isBuyOrder);
			if(editVisible)
			{
				final boolean select = getItemSelect(position);
				holder.imgSelect.setImageResource(select?R.drawable.select_rect_true:R.drawable.select_rect_false);
				holder.imgSelect.setVisibility(View.VISIBLE);
				final int location = position;
				holder.imgSelect.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						selectList.set(location, !select);
						notifyDataSetChanged();
					}
				});
			}
			else
			{
				holder.imgSelect.setVisibility(View.GONE);
				holder.imgSelect.setOnClickListener(null);
			}
		}
		return convertView;
	}





	public void setEditVisible(boolean paramBoolean)
	{
		this.editVisible = paramBoolean;
		notifyDataSetChanged();
	}


	class ViewHolder
	{
		@BindViewId(R.id.imgSelect) ImageView imgSelect;
		@BindViewId(R.id.lblProductName) TextView lblProductName;
		@BindViewId(R.id.lblBuySell) TextView lblBuySell;
		@BindViewId(R.id.lblProductPrice) TextView lblProductPrice;
		@BindViewId(R.id.lblProductNumber) TextView lblProductNumber;
		@BindViewId(R.id.lblDeliveryTime) TextView lblDeliveryTime;
		@BindViewId(R.id.lblDeliveryPlace) TextView lblDeliveryPlace;
		@BindViewId(R.id.lblBond) TextView lblBond;
	}
}
