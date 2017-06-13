package com.totrade.spt.mobile.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.entity.GetBuySellIntensionDownEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.common.LoginUserContext;
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

public class MainNegoRecAdapter extends RecyclerAdapterBase<GetBuySellIntensionDownEntity, MainNegoRecAdapter.ViewHolder> {
    protected DecimalFormat format = new DecimalFormat("#0.#######");

    public MainNegoRecAdapter(List<GetBuySellIntensionDownEntity> list) {
        super(list);
    }

    @Override
    public MainNegoRecAdapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }


    class ViewHolder extends ViewHolderBase<GetBuySellIntensionDownEntity> {
        @BindViewId(R.id.imgNext)
        ImageView imgNext;      //arrow right gray
        @BindViewId(R.id.imgNegoStatus)
        ImageView imgNegoStatus;    //成交等状态
        @BindViewId(R.id.imgSupLevel)
        ImageView imgSupLevel;    //供应商等级
        @BindViewId(R.id.lblProductName)
        TextView lblProductName;    //品名
        @BindViewId(R.id.lblBuySell)
        TextView lblBuySell;    //销售采购
        @BindViewId(R.id.lblSubSide)
        View lblSubSide;    //补贴
        @BindViewId(R.id.lblPaySystem)
        TextView lblPaySystem;    //支付系统
        @BindViewId(R.id.lblNegoTime)
        TextView lblNegoTime;    //挂单时间
        @BindViewId(R.id.lblValidTime)
        TextView lblValidTime;    //有效时间
        @BindViewId(R.id.lblValidTimeSub)
        TextView lblValidTimeSub;    // “后失效”
        @BindViewId(R.id.lblProductNumber)
        TextView lblProductNumber;    //数量
        @BindViewId(R.id.lblDeliveryTime)
        TextView lblDeliveryTime;    //交货期
        @BindViewId(R.id.lblDeliveryPlace)
        TextView lblDeliveryPlace;    //交货地
        @BindViewId(R.id.lblProductPrice)
        TextView lblProductPrice;    //价格
        @BindViewId(R.id.lblBond)
        TextView lblBond;    //定金


        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_nego_main);
        }

        @Override
        public void initItemData() {
            //显示值

            boolean zeroRemainNumber = itemObj.getProductRemainNumber().compareTo(BigDecimal.ZERO) <= 0;
            boolean isValid = itemObj.getBsStatus().equals("1") && itemObj.getValidTime().after(HostTimeUtility.getDate()) && !zeroRemainNumber;

            lblProductName.setText(itemObj.getProductName());
            lblBuySell.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BUY_SELL, itemObj.getBuySell()));
            if (itemObj.getPaySystem() == null) {
                lblPaySystem.setVisibility(View.GONE);
            } else {
                lblPaySystem.setVisibility(View.VISIBLE);
                lblPaySystem.setText(DispUtility.paySystem2DispIco(itemObj.getPaySystem()));
            }
            lblProductPrice.setText(DispUtility.price2Disp(itemObj.getProductPrice(), itemObj.getPriceUnit(), itemObj.getNumberUnit()));
            lblNegoTime.setText(date2String(itemObj.getUploadTime()));

            lblProductNumber.setText(DispUtility.productNum2Disp(itemObj.getProductRemainNumber(), itemObj.getProductNumber(), itemObj.getNumberUnit()));
            lblDeliveryTime.setText(DispUtility.deliveryTimeToDisp(itemObj.getDeliveryTime(), itemObj.getProductType(), itemObj.getTradeMode()));
            lblDeliveryPlace.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, itemObj.getDeliveryPlace()));
            lblBond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, itemObj.getBond()));


            //设置颜色
            boolean isBuyOrder = SptConstant.BUYSELL_BUY.equals(itemObj.getBuySell());
            lblBuySell.setSelected(isBuyOrder);
            lblProductPrice.setSelected(isBuyOrder);

            lblPaySystem.setSelected(lblPaySystem.getText().equals("中"));
            boolean subSidy = isBuyOrder ? (itemObj.getSubsidy2() != null && itemObj.getSubsidy2().compareTo(BigDecimal.ZERO) > 0) :
                    (itemObj.getSubsidy() != null && itemObj.getSubsidy().compareTo(BigDecimal.ZERO) > 0);
            lblSubSide.setVisibility(subSidy ? View.VISIBLE : View.GONE);
            //供应商等级
            if (LoginUserContext.isAnonymous()) {
                imgSupLevel.setVisibility(View.GONE);
            } else {
                int res = DispUtility.supLevel2DispImg(itemObj.getSupLevel());
                if (res != 0) {
                    imgSupLevel.setImageResource(res);
                    imgSupLevel.setVisibility(View.VISIBLE);
                } else {
                    imgSupLevel.setVisibility(View.GONE);
                }
            }

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
                imgNegoStatus.setImageResource(zeroRemainNumber ? R.drawable.img_deal_all : R.drawable.img_nego_invalid);
            }

//            不显示支付系统和补贴标识了
            lblPaySystem.setVisibility(View.GONE);
            lblSubSide.setVisibility(View.GONE);
        }
    }

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

    private String date2String(Date date) {
        return sdf.format(date);
    }
}
