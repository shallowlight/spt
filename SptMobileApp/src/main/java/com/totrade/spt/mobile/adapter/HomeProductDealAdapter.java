package com.totrade.spt.mobile.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.dto.ZoneMatchDownEntity;
import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;

import java.math.BigDecimal;

/**
 * Created by Timothy on 2017/2/28.
 */

public class HomeProductDealAdapter extends AdapterBase<ZoneMatchDownEntity> {
    public HomeProductDealAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView || null == convertView.getTag()) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_match_list, parent, false);
            holder.tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
            holder.tv_delivery_time = (TextView) convertView.findViewById(R.id.tv_delivery_time);
            holder.tv_delivery_place = (TextView) convertView.findViewById(R.id.tv_delivery_place);
            holder.tv_deal_price = (TextView) convertView.findViewById(R.id.tv_deal_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ZoneMatchDownEntity entity = getItem(position);
        holder.tv_product_name.setText(entity.getProductName());
        holder.tv_delivery_time.setText(entity.getDeliveryTimeStr());
        String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, entity.getDeliveryPlace());
        holder.tv_delivery_place.setText(deliveryPlace);
        if (entity.getDealPrice() == null) {
            holder.tv_deal_price.setTextColor(mContext.getResources().getColor(R.color.ltBlack));
            holder.tv_deal_price.setText("暂无");
            holder.tv_deal_price.setCompoundDrawables(null, null, null, null);
        } else {
            String price = DispUtility.price2Disp(entity.getDealPrice(), entity.getPriceUnit(), entity.getNumberUnit());
            if (entity.getIncAmount() == null || entity.getIncAmount().compareTo(BigDecimal.ZERO) == 0) {
                holder.tv_deal_price.setText(price+"-");
                holder.tv_deal_price.setTextColor(mContext.getResources().getColor(R.color.ltBlack));
                holder.tv_deal_price.setCompoundDrawables(null, null, null, null);
            } else {
                holder.tv_deal_price.setText(price);
                Drawable drawable;
                if (entity.getIncAmount().compareTo(BigDecimal.ZERO) > 0) {
                    holder.tv_deal_price.setTextColor(mContext.getResources().getColor(R.color.zone_red_view));
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.zone_arrow_red);
                } else {
                    holder.tv_deal_price.setTextColor(mContext.getResources().getColor(R.color.zone_green_view));
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.zone_arrow_green);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.tv_deal_price.setCompoundDrawables(null, null, drawable, null);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_product_name, tv_delivery_time, tv_delivery_place, tv_deal_price;
    }

}
