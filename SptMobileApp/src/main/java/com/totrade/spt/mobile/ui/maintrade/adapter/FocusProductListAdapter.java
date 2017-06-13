package com.totrade.spt.mobile.ui.maintrade.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.adapter.SptMobileAdapterBase;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.view.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FocusProductListAdapter extends SptMobileAdapterBase<ZoneRequestDownEntity> implements SptConstant {
    private BigDecimal HUNDRED = new BigDecimal(100);

    public FocusProductListAdapter(Context context, List<ZoneRequestDownEntity> dataList) {
        super(context, dataList);
    }

    public FocusProductListAdapter(Context context ) {
        super(context, new ArrayList<ZoneRequestDownEntity>());
    }

    public void notifyChanged(List<ZoneRequestDownEntity> entities) {
        dataList.clear();
        dataList.addAll(entities);
        this.notifyDataSetChanged();
    }

    ViewHolder holder = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = getLayoutInflater().inflate(R.layout.item_trade_order, null);

            holder.tv_deliveryplace = (TextView) convertView.findViewById(R.id.tv_deliveryplace);
            holder.tv_bond = (TextView) convertView.findViewById(R.id.tv_bond);
            holder.tv_first_sell = (TextView) convertView.findViewById(R.id.tv_first_sell);
            holder.tv_first_buy = (TextView) convertView.findViewById(R.id.tv_first_buy);
            holder.tv_order_num = (TextView) convertView.findViewById(R.id.tv_order_num);
            holder.tv_product_quality = (TextView) convertView.findViewById(R.id.tv_product_quality);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ZoneRequestDownEntity entity = (ZoneRequestDownEntity) getItem(position);

        //订单号
        holder.tv_order_num.setText(ContractHelper.instance.getOrderId(entity.getZoneOrderNoDto()));
        //交货地
        String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, entity.getDeliveryPlace());
        if (entity.getProductType().startsWith("SL") && TextUtils.isEmpty(deliveryPlace)) {
            deliveryPlace = entity.getDeliveryPlaceName();
        }
        holder.tv_deliveryplace.setText(deliveryPlace);

        //质量标准
        boolean isGT = entity.getProductType().startsWith("GT");
        if (!isGT) {
            holder.tv_product_quality.setText(value2Disp(SPTDICT_PRODUCT_QUALITY, entity.getProductQuality()));
        } else {
            BigDecimal qualityFrom = entity.getProductQualityEx1();
            BigDecimal qualityTo = entity.getProductQualityEx1To();
            String qualityStr;
            if (qualityFrom == null || qualityFrom.compareTo(BigDecimal.ZERO) <= 0) {
                if (qualityTo == null || qualityTo.compareTo(new BigDecimal(100)) >= 0) {
                    qualityStr = "不限";
                } else {
                    qualityStr = "不限-" + format.format(qualityTo);        //目前需求上不会出现改情况
                }
            } else {
                if (qualityTo == null || qualityTo.compareTo(HUNDRED) >= 0) {
                    qualityStr = format.format(qualityFrom) + "以上";
                } else {
                    qualityStr = format.format(qualityFrom) + "-" + format.format(qualityTo);
                }
            }
            holder.tv_product_quality.setText(qualityStr);
        }

        //定金
        holder.tv_bond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, entity.getBond()));

        //获取买一卖一
        List<TblZoneRequestOfferEntity> offerList = entity.getOfferList();
        TblZoneRequestOfferEntity b = null;
        TblZoneRequestOfferEntity s = null;
        for (TblZoneRequestOfferEntity offerEntity : offerList) {
            if (SptConstant.BUYSELL_SELL.equals(offerEntity.getBuySell())) {
                if (s == null) s = offerEntity;
            } else {
                if (b == null) b = offerEntity;
            }
        }
//            设置卖一
        if (s != null && s.getProductPrice() != null) {
            holder.tv_first_sell.setText(new DecimalFormat().format(s.getProductPrice().setScale(0, BigDecimal.ROUND_HALF_UP)));
        } else {
            holder.tv_first_sell.setText("---");
        }
//            设置买一
        if (b != null && b.getProductPrice() != null) {
            holder.tv_first_buy.setText(new DecimalFormat().format(b.getProductPrice().setScale(0, BigDecimal.ROUND_HALF_UP)));
        } else {
            holder.tv_first_buy.setText("---");
        }

        return convertView;
    }

    private String value2Disp(String category, String value) {
        if (StringUtility.isNullOrEmpty(value)) {
            return "不限";
        } else {
            return DictionaryUtility.getValue(category, value);
        }
    }

    private static class ViewHolder {
        TextView tv_deliveryplace;              //交货地
        TextView tv_bond;                       //定金
        TextView tv_first_sell;                 //卖一
        TextView tv_first_buy;                  //买一
        TextView tv_order_num;                  //订单号
        TextView tv_product_quality;            //质量标准
    }
}
