package com.totrade.spt.mobile.ui.maintrade.adapter;

import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.utility.DictionaryTools;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.view.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author huangxy
 * @date 2016/11/24
 */
public class TradeOrderAdapter extends RecyclerAdapterBase<ZoneRequestDownEntity, TradeOrderAdapter.ViewHolder> {

    private String changedRequest;
    private int changedPosition = -1;

    public TradeOrderAdapter(List<ZoneRequestDownEntity> list) {
        super(list);
    }

    @Override
    public ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_trade_order, parent, false));
    }

    public void setChangedPrice(int changedPosition, String changedRequest) {
        this.changedPosition = changedPosition;
        this.changedRequest = changedRequest;
        notifyDataSetChanged();
    }


    class ViewHolder extends ViewHolderBase<ZoneRequestDownEntity> {

        TextView tv_deliveryplace;              //交货地
        TextView tv_bond;                       //定金
        TextView tv_first_sell;                 //卖一
        TextView tv_first_buy;                  //买一
        TextView tv_order_num;                  //订单号
        TextView tv_product_quality;            //质量标准

        public ViewHolder(View view) {
            super(view);
            tv_deliveryplace = (TextView) itemView.findViewById(R.id.tv_deliveryplace);
            tv_bond = (TextView) itemView.findViewById(R.id.tv_bond);
            tv_first_sell = (TextView) itemView.findViewById(R.id.tv_first_sell);
            tv_first_buy = (TextView) itemView.findViewById(R.id.tv_first_buy);
            tv_order_num = (TextView) itemView.findViewById(R.id.tv_order_num);
            tv_product_quality = (TextView) itemView.findViewById(R.id.tv_product_quality);
        }

        @Override
        public void initItemData() {
            //订单号
            tv_order_num.setText(ContractHelper.instance.getOrderId(itemObj.getZoneOrderNoDto()));
            //交货地
            String deliveryPlace = DictionaryTools.i().getValue(SptConstant.SPTDICT_DELIVERY_PLACE, itemObj.getDeliveryPlace());
            if (itemObj.getProductType().startsWith("SL") && TextUtils.isEmpty(deliveryPlace)) {
                deliveryPlace = itemObj.getDeliveryPlaceName();
            }
            tv_deliveryplace.setText(deliveryPlace);

            // 质量标准字段(塑料显示牌号，钢铁显示铁品位)
            if (itemObj.getProductType().startsWith("GT")) {
                String qualityEx1 = format.format(itemObj.getProductQualityEx1());
                tv_product_quality.setText(qualityEx1);
            } else if (itemObj.getProductType().startsWith("SL")) {
                tv_product_quality.setText(itemObj.getBrand());
            } else {
                tv_product_quality.setText(DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, itemObj.getProductQuality()));
            }

            //定金
            tv_bond.setText(DictionaryTools.i().getValue(SptConstant.SPTDICT_BOND, itemObj.getBond()));

            //获取买一卖一
            List<TblZoneRequestOfferEntity> offerList = itemObj.getOfferList();
            TblZoneRequestOfferEntity b = null;
            TblZoneRequestOfferEntity s = null;
            for (TblZoneRequestOfferEntity entity : offerList) {
                if (SptConstant.BUYSELL_SELL.equals(entity.getBuySell())) {
                    if (s == null) s = entity;
                } else {
                    if (b == null) b = entity;
                }
            }
//            设置卖一
            if (s != null && s.getProductPrice() != null) {
                tv_first_sell.setText(new DecimalFormat().format(s.getProductPrice().setScale(0, BigDecimal.ROUND_HALF_UP)));
            } else {
                tv_first_sell.setText("---");
            }
//            设置买一
            if (b != null && b.getProductPrice() != null) {
                tv_first_buy.setText(new DecimalFormat().format(b.getProductPrice().setScale(0, BigDecimal.ROUND_HALF_UP)));
            } else {
                tv_first_buy.setText("---");
            }

            //价格刷新
            if (changedPosition == -1 || changedPosition != position) return;
            if (SptConstant.BUYSELL_BUY.equals(NotifyUtility.getValue(changedRequest, "buySell"))) {
                if (!"---".equals(tv_first_buy.getText().toString()))
                changedBuySellTextView(tv_first_buy);         //TODO next version
            } else {
                if (!"---".equals(tv_first_sell.getText().toString()))
                changedBuySellTextView(tv_first_sell);        //TODO next version
            }
            changedPosition = -1;
        }

        /**
         * 改变买或者卖的颜色
         * @param tv
         */
        private void changedBuySellTextView(TextView tv) {
            String price = NotifyUtility.getValue(changedRequest, "productPrice");
//            tv.setText(price);
            int i = Integer.valueOf(price).compareTo(Integer.valueOf(tv.getText().toString().replace(",", "")));
            if (i == 1) {
                changedColor(tv, context.getResources().getColor(R.color.ui_red));
            } else if (i == -1) {
                changedColor(tv, context.getResources().getColor(R.color.ui_green));
            }
        }

        /**
         * 改变TextView字体和背景颜色 定时1秒
         *
         * @param tv
         * @param bgColor
         */
        private void changedColor(final TextView tv, final int bgColor) {
            ValueAnimator colorAnimator = ValueAnimator.ofInt(0, 100);
            colorAnimator.setDuration(1000);
            colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int progress = (int) animation.getAnimatedValue();
                    if (progress == 0) {
                        tv.setTextColor(context.getResources().getColor(R.color.white));
                        tv.setBackgroundColor(bgColor);
                    } else if (progress == 100) {
                        tv.setTextColor(context.getResources().getColor(R.color.ui_gray_black));
                        tv.setBackgroundColor(context.getResources().getColor(R.color.background));
                    }
                }
            });
            colorAnimator.start();
        }

    }
}

