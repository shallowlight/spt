package com.totrade.spt.mobile.ui.maintrade.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 转卖库存列表适配器
 * Created by Timothy on 2017/2/24.
 */

public class TransferInventoAdapter extends RecyclerAdapterBase<MyStockDownEntity, TransferInventoAdapter.ViewHolder> {

    private boolean isResell;
    public TransferInventoAdapter(List<MyStockDownEntity> list, boolean isResell) {
        super(list);
        this.isResell = isResell;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public TransferInventoAdapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new TransferInventoAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_trade_order3, parent, false));
    }

    class ViewHolder extends ViewHolderBase<MyStockDownEntity> {
        public ViewHolder(View view) {
            super(view);
        }

        @BindViewId(R.id.tv_order_num)
        private TextView tv_order_num;              //订单号
        @BindViewId(R.id.tv_deliveryplace)
        private TextView tv_delivery_place;      //交货地
        @BindViewId(R.id.tv_product_quality)
        private TextView tv_product_quality;      //质量标准
        @BindViewId(R.id.tv_bond)
        private TextView tv_bond;      //定金
        @BindViewId(R.id.tv_price)
        private TextView tv_price;      //价格
        @BindViewId(R.id.tv_number)
        private TextView tv_number;      //数量
        @BindViewId(R.id.tv_deal_time)
        private TextView tv_deal_time;      //时间
        @BindViewId(R.id.iv_arrow)
        private ImageView iv_arrow;
        @Override
        public void initItemData() {
            iv_arrow.setVisibility(View.VISIBLE);
            //订单号
            tv_order_num.setText(ContractHelper.instance.getOrderId(itemObj.getZoneOrderNoDto()));
            //交货地
            String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, itemObj.getDeliveryPlace());
            if (itemObj.getProductType().startsWith("SL") && TextUtils.isEmpty(deliveryPlace)) {
                deliveryPlace = itemObj.getDeliveryPlaceName();
            }
            tv_delivery_place.setText(deliveryPlace);
            // 质量标准字段(塑料显示牌号，钢铁显示铁品位)
            if (itemObj.getProductType().startsWith("GT")) {
                String qualityEx1 = new DecimalFormat("#0.#######").format(itemObj.getProductQualityEx1());
                tv_product_quality.setText(qualityEx1);
            } else if (itemObj.getProductType().startsWith("SL")) {
                tv_product_quality.setText(itemObj.getBrand());
            } else {
                tv_product_quality.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, itemObj.getProductQuality()));
            }

            //定金
            tv_bond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, itemObj.getBond()));
            //价格
            tv_price.setText(DispUtility.price2Disp(itemObj.getProductPrice(), itemObj.getPriceUnit(), itemObj.getNumberUnit()));
            //数量

            BigDecimal number = isResell ? itemObj.getRemainNumber() : itemObj.getDealNumber();
            tv_number.setText(DispUtility.productNum2Disp(number, null, itemObj.getNumberUnit()));
            //成交时间
            tv_deal_time.setText("成交时间:" + new SimpleDateFormat("yyyy/MM/dd").format(itemObj.getContractTime()));
        }
    }
}
