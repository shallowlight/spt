package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.deal.entity.MyContractDownEntity;
import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.KeyTextView;

/**
 * Created by Timothy on 2017/4/21.
 */

public class OrderTransferListAdapter extends AdapterBase<MyContractDownEntity> {
    public OrderTransferListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_orderdetail_transfer,parent,false);
            holder.ktv_transfer_price = (KeyTextView) convertView.findViewById(R.id.ktv_transfer_price);
            holder.tv_transfer_number = (TextView) convertView.findViewById(R.id.tv_transfer_number);
            holder.tv_transfer_constract = (TextView) convertView.findViewById(R.id.tv_transfer_constract);
            holder.tv_transfer_date = (TextView) convertView.findViewById(R.id.tv_transfer_date);
            holder.ktv_buyer = (KeyTextView) convertView.findViewById(R.id.ktv_buyer);
           convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MyContractDownEntity entity = getItem(position);
        holder.ktv_transfer_price.setText(DispUtility.price2Disp(entity.getProductPrice(), entity.getPriceUnit(), entity.getNumberUnit()));
        holder.tv_transfer_number.setText(DispUtility.productNum2Disp(entity.getDealNumber(), null, entity.getNumberUnit()));
        holder.tv_transfer_constract.setText(entity.getContractId());
        holder.tv_transfer_date.setText(entity.getDeliveryTimeStr());
        holder.ktv_buyer.setKey("采购商").setValue(entity.getBuyerCompanyName());
        return convertView;
    }

    static class ViewHolder{
        KeyTextView ktv_transfer_price;
        TextView tv_transfer_number;
        TextView tv_transfer_constract;
        TextView tv_transfer_date;
        KeyTextView ktv_buyer;
    }
}
