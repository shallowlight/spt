package com.totrade.spt.mobile.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.entity.GetBuySellIntensionDownEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 *
 * Created by iUserName on 16/12/08.
 */

public class UserNegoAdapter extends RecyclerAdapterBase<GetBuySellIntensionDownEntity,UserNegoAdapter.ViewHolder>
{
	protected DecimalFormat format=new DecimalFormat("#0.#######");

	public UserNegoAdapter(List<GetBuySellIntensionDownEntity> list)
	{
		super(list);
	}

	@Override
	public UserNegoAdapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType)
	{
		return new ViewHolder(parent);
	}



	class ViewHolder extends ViewHolderBase<GetBuySellIntensionDownEntity>
	{
		@BindViewId(R.id.lblProductName) TextView lblProductName;
		@BindViewId(R.id.lblBuySell) TextView lblBuySell;
		@BindViewId(R.id.lblProductPrice) TextView lblProductPrice;
		@BindViewId(R.id.lblProductNumber) TextView lblProductNumber;
		@BindViewId(R.id.lblDeliveryTime) TextView lblDeliveryTime;
		@BindViewId(R.id.lblDeliveryPlace) TextView lblDeliveryPlace;
		@BindViewId(R.id.lblBond) TextView lblBond;


		public ViewHolder(ViewGroup parent)
		{
			super(parent, R.layout.item_nego_user);
		}

		@Override
		public void initItemData()
		{
			//显示值
			lblProductName.setText(itemObj.getProductName());
			lblBuySell.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BUY_SELL,itemObj.getBuySell()));
			lblProductPrice.setText(DispUtility.price2Disp(itemObj.getProductPrice(),itemObj.getPriceUnit(),itemObj.getNumberUnit()));

			lblProductNumber.setText(DispUtility.productNum2Disp(itemObj.getProductRemainNumber(),itemObj.getProductNumber(),itemObj.getNumberUnit()));
			lblDeliveryTime.setText(DispUtility.deliveryTimeToDisp(itemObj.getDeliveryTime(),itemObj.getProductType(),itemObj.getTradeMode()));
			lblDeliveryPlace.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE,itemObj.getDeliveryPlace()));
			lblBond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND,itemObj.getBond()));

			//设置颜色
			boolean isBuyOrder = SptConstant.BUYSELL_BUY.equals(itemObj.getBuySell());
			lblBuySell.setSelected(isBuyOrder);
			lblProductPrice.setSelected(isBuyOrder);
		}
	}

}
