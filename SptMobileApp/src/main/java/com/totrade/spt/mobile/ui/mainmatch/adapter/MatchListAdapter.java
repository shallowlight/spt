package com.totrade.spt.mobile.ui.mainmatch.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.entity.GetBuySellIntensionDownEntity;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.maintrade.PowerHelper;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.ChatIMActivity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 撮合列表适配器
 *
 * @author huangxy
 * @date 2017/4/6
 */
public class MatchListAdapter extends RecyclerAdapterBase<GetBuySellIntensionDownEntity, MatchListAdapter.MatchViewHolder> {

    public MatchListAdapter(List<GetBuySellIntensionDownEntity> list) {
        super(list);
    }

    @Override
    public MatchViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new MatchViewHolder(parent);
    }

    class MatchViewHolder extends ViewHolderBase<GetBuySellIntensionDownEntity> {

        private TextView tv_product_name;       //品名
        private TextView tv_buy_sell;       //买卖
        private TextView tv_validtime;       //有效时间
        private TextView tv_product_price;       //价格
        private TextView tv_delivery_time;       //交货期
        private TextView tv_product_num;       //数量
        private TextView tv_delivery_place;       //交货地
        private TextView tv_contract;       //联系交易员 跳转云信
        private ImageView iv_deal_status;   //交易状态

        public MatchViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_match);

            tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            tv_buy_sell = (TextView) itemView.findViewById(R.id.tv_buy_sell);
            tv_validtime = (TextView) itemView.findViewById(R.id.tv_validtime);
            tv_product_price = (TextView) itemView.findViewById(R.id.tv_product_price);
            tv_delivery_time = (TextView) itemView.findViewById(R.id.tv_delivery_time);
            tv_product_num = (TextView) itemView.findViewById(R.id.tv_product_num);
            tv_delivery_place = (TextView) itemView.findViewById(R.id.tv_delivery_place);
            tv_contract = (TextView) itemView.findViewById(R.id.tv_contract);
            iv_deal_status = (ImageView) itemView.findViewById(R.id.iv_deal_status);
        }

        @Override
        public void initItemData() {
            tv_product_name.setText(itemObj.getProductName());
            tv_validtime.setText(DispUtility.validTime2Disp(itemObj.getValidTime()));
            tv_product_price.setText(DispUtility.price2Disp(itemObj.getProductPrice(), itemObj.getPriceUnit(), itemObj.getNumberUnit()));
            tv_delivery_time.setText(DispUtility.deliveryTimeToDisp(itemObj.getDeliveryTime(), itemObj.getProductType(), itemObj.getTradeMode()));
            tv_product_num.setText(DispUtility.productNum2Disp(itemObj.getProductRemainNumber(), itemObj.getProductNumber(), itemObj.getNumberUnit()));
            tv_delivery_place.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, itemObj.getDeliveryPlace()));

            if ("B".equalsIgnoreCase(itemObj.getBuySell())) {
                tv_buy_sell.setText("买");
                tv_buy_sell.setSelected(true);
            } else {
                tv_buy_sell.setText("卖");
                tv_buy_sell.setSelected(false);
            }


            boolean zeroRemainNumber = itemObj.getProductRemainNumber().compareTo(BigDecimal.ZERO) <= 0;
            boolean isValid = itemObj.getBsStatus().equals("1") && itemObj.getValidTime().after(HostTimeUtility.getDate()) && !zeroRemainNumber;
            //失效与未失效显示处理
            if (isValid) {
                iv_deal_status.setVisibility(View.GONE);
                iv_deal_status.setSelected(false);
                tv_contract.setVisibility(View.VISIBLE);
                tv_validtime.setVisibility(View.VISIBLE);
            } else {
                iv_deal_status.setVisibility(View.VISIBLE);
                iv_deal_status.setImageResource(zeroRemainNumber ? R.drawable.match_deal : R.drawable.match_invalid);
                tv_contract.setVisibility(View.GONE);
                tv_validtime.setVisibility(View.GONE);
            }
            tv_contract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!PowerHelper.i().hasPower(context)) return;
                    ChatIMActivity.start(context, itemObj.getMatchUserId(), "", SessionTypeEnum.P2P.name());
                }
            });
        }
    }
}
