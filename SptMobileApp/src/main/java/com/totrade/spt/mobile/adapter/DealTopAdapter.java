package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.datacentre.dto.ZoneDealNumberTopDownEntity;
import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.utility.DecimalUtils;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.view.R;


/**
 * Created by Timothy on 2017/4/5.
 */

public class DealTopAdapter extends AdapterBase<ZoneDealNumberTopDownEntity> {
    public DealTopAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_deal_list, parent, false);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
            viewHolder.tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
            viewHolder.tv_deal_num = (TextView) convertView.findViewById(R.id.tv_deal_num);
            viewHolder.tv_deal_price = (TextView) convertView.findViewById(R.id.tv_deal_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ZoneDealNumberTopDownEntity entity = getItem(position);
        if (position == 0) {
            viewHolder.iv.setImageResource(R.drawable.icon_golden);
        } else if (position == 1) {
            viewHolder.iv.setImageResource(R.drawable.icon_silver);
        } else if (position == 2) {
            viewHolder.iv.setImageResource(R.drawable.icon_copper);
        }
        viewHolder.tv_product_name.setText(ProductTypeUtility.getProductName(entity.getProductType()));
        viewHolder.tv_deal_price.setText(DecimalUtils.decimalToBillionWithUnit(entity.getTotalDealPrice(),"亿元"));
        viewHolder.tv_deal_num.setText(DecimalUtils.keep2PointStringAndCommaWithUnit(entity.getTotalDealNumber(),"吨"));
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView tv_product_name;
        TextView tv_deal_num;
        TextView tv_deal_price;
    }

}
