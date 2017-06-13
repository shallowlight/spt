package com.totrade.spt.mobile.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.entity.BsMatchListDownEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.view.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by iUserName on 16/12/08.
 */

public class Nego1V1Adapter extends RecyclerAdapterBase<BsMatchListDownEntity, Nego1V1Adapter.ViewHolder> {
    protected DecimalFormat format = new DecimalFormat("#0.#######");

    public Nego1V1Adapter(List<BsMatchListDownEntity> list) {
        super(list);
    }

    @Override
    public Nego1V1Adapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }


    class ViewHolder extends ViewHolderBase<BsMatchListDownEntity> {
        @BindViewId(R.id.imgNext)
        ImageView imgNext;
        @BindViewId(R.id.imgNegoStatus)
        ImageView imgNegoStatus;
        @BindViewId(R.id.imgSupLevel)
        ImageView imgSupLevel;
        @BindViewId(R.id.lblProductName)
        TextView lblProductName;
        @BindViewId(R.id.lblBuySell)
        TextView lblBuySell;
        @BindViewId(R.id.lblSubSide)
        View lblSubSide;
        @BindViewId(R.id.lblPaySystem)
        TextView lblPaySystem;
        @BindViewId(R.id.lblNegoTime)
        TextView lblNegoTime;
        @BindViewId(R.id.lblValidTime)
        TextView lblValidTime;
        @BindViewId(R.id.lblValidTimeSub)
        TextView lblValidTimeSub;
        @BindViewId(R.id.lblProductNumber)
        TextView lblProductNumber;
        @BindViewId(R.id.lblDeliveryTime)
        TextView lblDeliveryTime;
        @BindViewId(R.id.lblDeliveryPlace)
        TextView lblDeliveryPlace;
        @BindViewId(R.id.lblProductPrice)
        TextView lblProductPrice;
        @BindViewId(R.id.lblBond)
        TextView lblBond;
        @BindViewId(R.id.lblLastNegoTime)
        TextView lblLastNegoTime;


        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_nego_main);
            lblPaySystem.setVisibility(View.GONE);
            imgSupLevel.setVisibility(View.GONE);
            lblLastNegoTime.setVisibility(View.VISIBLE);
        }

        @Override
        public void initItemData() {
            //显示值

            boolean isDeal = itemObj.getNegoStatus().equals(SptConstant.NEGOSTATUS_D) || itemObj.getProductRemainNumber().compareTo(BigDecimal.ZERO) <= 0;
            boolean isValid = itemObj.getNegoStatus().equals(SptConstant.NEGOSTATUS_C) || itemObj.getNegoStatus().equals(SptConstant.NEGOSTATUS_U)
                    || itemObj.getValidTime().after(HostTimeUtility.getDate()) && !isDeal;

            lblProductName.setText(itemObj.getProductName());
            lblBuySell.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BUY_SELL, itemObj.getBuySell()));
            lblProductPrice.setText(DispUtility.price2Disp(itemObj.getProductPrice(), itemObj.getPriceUnit(), itemObj.getNumberUnit()));
            lblNegoTime.setText(date2String(sdf, itemObj.getUpdateTime()));

            lblProductNumber.setText(DispUtility.productNum2Disp(itemObj.getProductRemainNumber(), itemObj.getProductNumber(), itemObj.getNumberUnit()));
            lblDeliveryTime.setText(DispUtility.deliveryTimeToDisp(itemObj.getDeliveryTime(), itemObj.getProductType(), itemObj.getTradeMode()));
            lblDeliveryPlace.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, itemObj.getDeliveryPlace()));
            lblBond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, itemObj.getBond()));
            lblLastNegoTime.setText(date2String(sdf2, itemObj.getUploadTime()));

            //设置颜色
            boolean isBuyOrder = SptConstant.BUYSELL_BUY.equals(itemObj.getBuySell());
            lblBuySell.setSelected(isBuyOrder);
            lblProductPrice.setSelected(isBuyOrder);

            //按我方显示
            boolean subSidy = isBuyOrder ? (itemObj.getSubsidy() != null && itemObj.getSubsidy().compareTo(BigDecimal.ZERO) > 0) :
                    (itemObj.getSubsidy2() != null && itemObj.getSubsidy2().compareTo(BigDecimal.ZERO) > 0);
            lblSubSide.setVisibility(subSidy ? View.VISIBLE : View.GONE);

            //失效与未失效显示处理
            if (isValid) {
                imgNext.setVisibility(View.VISIBLE);
                imgNegoStatus.setVisibility(View.INVISIBLE);
                lblValidTime.setVisibility(View.VISIBLE);
                lblValidTimeSub.setVisibility(View.VISIBLE);
                lblValidTime.setText(DispUtility.validTime2Disp(itemObj.getValidTime()));
            } else {
                imgNegoStatus.setVisibility(View.VISIBLE);
                imgNext.setVisibility(View.INVISIBLE);
                lblValidTime.setVisibility(View.INVISIBLE);
                lblValidTimeSub.setVisibility(View.INVISIBLE);
                if (itemObj.getNegoStatus().equals("T")) {
                    imgNegoStatus.setImageResource(R.drawable.img_nego_cancel);
                } else {
                    imgNegoStatus.setImageResource(isDeal ? R.drawable.img_deal_all : R.drawable.img_nego_invalid);
                }
            }

//            不显示补贴标识了
            lblSubSide.setVisibility(View.GONE);
        }
    }

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
    private final static SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());

    private String date2String(SimpleDateFormat format, Date date) {
        return format.format(date);
    }
}