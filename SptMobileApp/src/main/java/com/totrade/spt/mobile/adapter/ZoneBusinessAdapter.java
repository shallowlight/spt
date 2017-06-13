package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;


/**
 *
 *  Created by Administrator on 2016/12/8.
 */
public class ZoneBusinessAdapter extends AdapterBase<TblZoneRequestOfferEntity> {

    private String priceUnit;
    private String numUnit;

    public enum BusinessType
    {
        S, B
    }

    public ZoneBusinessAdapter(Context context, String priceUnit, String numUnit) {
        super(context);
        this.priceUnit = priceUnit;
        this.numUnit = numUnit;
    }

    @Override
    public int getCount()
    {
        return Math.min(super.getCount(),3);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_zone_business, parent, false);
            holder.tv_trade_name = (TextView) convertView.findViewById(R.id.tv_trade_name);
            holder.tv_unit_price = (TextView) convertView.findViewById(R.id.tv_unit_price);
            holder.tv_increases = (TextView) convertView.findViewById(R.id.tv_increases);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TblZoneRequestOfferEntity entity = getItem(position);
        holder.tv_unit_price.setText(DispUtility.price2Disp(entity.getProductPrice(), priceUnit, numUnit));
        if (entity.getOfferStatus().equals(ZoneConstant.OFFER_STATUS_PART_DEAL)){
            holder.tv_increases.setText(DispUtility.productNum2Disp(null, entity.getRemainNumber(), numUnit));
        }else{
            holder.tv_increases.setText(DispUtility.productNum2Disp(null, entity.getProductNumber(), numUnit));
        }

        if (entity.getBuySell().equals(BusinessType.S.name())) {
            holder.tv_trade_name.setText(formatALBToChinese(position, BusinessType.S));
            setTextViewsColor(R.color.zone_green_txt, holder.tv_trade_name, holder.tv_unit_price, holder.tv_increases);
        } else {
            holder.tv_trade_name.setText(formatALBToChinese(position, BusinessType.B));
            setTextViewsColor(R.color.zone_red_txt, holder.tv_trade_name, holder.tv_unit_price, holder.tv_increases);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_trade_name, tv_unit_price, tv_increases;
    }

    private void setTextViewsColor(int color, TextView... tvs) {
        for (TextView tv : tvs) {
            tv.setTextColor(mContext.getResources().getColor(color));
        }
    }

    private String formatALBToChinese(int i, BusinessType type) {

        switch (type) {
            case S:
                switch (i) {
                    case 0:
                        return "卖一";
                    case 1:
                        return "卖二";
                    case 2:
                        return "卖三";
                }

            case B:
                switch (i) {
                    case 0:
                        return "买一";
                    case 1:
                        return "买二";
                    case 2:
                        return "买三";
                }
            default:
                return "";
        }
    }
}
