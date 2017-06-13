package com.totrade.spt.mobile.ui.maintrade.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.dto.ZoneMyOfferDownEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.ui.maintrade.fragment.MyOrderFragment;

import java.util.List;

/**
 * 商品专区实时挂单列表适配器
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class RealTimeOrderAdapter extends RecyclerAdapterBase<ZoneMyOfferDownEntity, RealTimeOrderAdapter.ViewHolder> {


    public RealTimeOrderAdapter(List<ZoneMyOfferDownEntity> list) {
        super(list);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public RealTimeOrderAdapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_zone_realtime_order, parent, false));
    }

    class ViewHolder extends ViewHolderBase<ZoneMyOfferDownEntity> {

        public ViewHolder(View view) {
            super(view);
            itemView.setTag(R.id.item_type, MyOrderFragment.REALTIME);
        }

        @BindViewId(R.id.tv_name)
        TextView tv_name;
        @BindViewId(R.id.tv_price)
        TextView tv_price;
        @BindViewId(R.id.tv_num)
        TextView tv_num;
        @BindViewId(R.id.tv_validtime)
        TextView tv_validtime;
        @BindViewId(R.id.tv_deliveryplace)
        TextView tv_deliveryplace;
        @BindViewId(R.id.tv_bond)
        TextView tv_bond;
        @BindViewId(R.id.tv_supplier)
        TextView tv_supplier;

        @Override
        public void initItemData() {
//            品名
            tv_name.setText(itemObj.getProductName());
//            价格
            String price = DispUtility.price2Disp(itemObj.getProductPrice(), itemObj.getPriceUnit(), itemObj.getNumberUnit());
            int priceColor;
            if (SptConstant.BUYSELL_SELL.equals(itemObj.getBuysell()))
            {
                priceColor = R.color.zone_green_view;
                tv_name.setSelected(true);
            }
            else
            {
                priceColor = R.color.zone_red_view;
                tv_name.setSelected(false);
            }
            tv_price.setTextColor(context.getResources().getColor(priceColor));
            tv_price.setText(price);
//            数量
            tv_num.setText(DispUtility.productNum2Disp(null, itemObj.getRemainNumber(), itemObj.getNumberUnit()));
//            时效
            tv_validtime.setText(DispUtility.validTime2Disp(itemObj.getValidTime()));
//            交货地
            String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, itemObj.getDeliveryPlace());
            if (itemObj.getProductType().startsWith("SL") && TextUtils.isEmpty(deliveryPlace)) {
                deliveryPlace = itemObj.getDeliveryPlaceName();
            }
            tv_deliveryplace.setText(deliveryPlace);
//            定金
            tv_bond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, itemObj.getBond()));
//            供货商
            if (itemObj.getCompanyInfo() != null && itemObj.getCompanyInfo().size() > 0) {
                String code = "【" + itemObj.getCompanyInfo().get(0).getBusinessCode() + "】";
                String companyName = itemObj.getCompanyInfo().get(0).getSupplierCompanyNames();
                tv_supplier.setText(code + companyName);
            }
        }
    }
}
