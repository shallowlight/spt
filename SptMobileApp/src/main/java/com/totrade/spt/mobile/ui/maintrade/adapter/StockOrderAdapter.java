package com.totrade.spt.mobile.ui.maintrade.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.MyContractDownEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;

import java.util.List;

/**
 * 商品专区我的库存列表适配器
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class StockOrderAdapter extends RecyclerAdapterBase<MyContractDownEntity, StockOrderAdapter.ViewHolder> {


    public StockOrderAdapter(List<MyContractDownEntity> list)
    {
        super(list);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public StockOrderAdapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_zone_stock_order, parent, false));
    }

    class ViewHolder extends ViewHolderBase<MyContractDownEntity>
    {
        public ViewHolder(View view)
        {
            super(view);
        }
        @BindViewId(R.id.tv_name)
        TextView tv_name;
        @BindViewId(R.id.tv_price)
        TextView tv_price;
        @BindViewId(R.id.tv_num)
        TextView tv_num;
        @BindViewId(R.id.tv_bond)
        TextView tv_bond;

        @Override
        public void initItemData()
        {
            //品名
            tv_name.setText(itemObj.getProductName());
            //价格
            tv_price.setText(DispUtility.price2Disp(itemObj.getProductPrice(), itemObj.getPriceUnit(), itemObj.getNumberUnit()));
            //数量
            tv_num.setText(DispUtility.productNum2Disp(null, itemObj.getDealNumber(), itemObj.getNumberUnit()));
            // 定金
            tv_bond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, itemObj.getBond()));
        }
    }
}
