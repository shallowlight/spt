package com.totrade.spt.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autrade.spt.bank.dto.BlockStatDownEntity;
import com.autrade.stage.utility.DateUtility;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.ui.fundmanager.FundManagerActivity;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CommonTextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Timothy on 2017/4/24.
 */

public class BlockingAdapter extends RecyclerAdapterBase<BlockStatDownEntity, BlockingAdapter.ViewHolder> {

    public BlockingAdapter(List<BlockStatDownEntity> list) {
        super(list);
    }

    @Override
    public ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_blocking_list, parent, false));
    }

    class ViewHolder extends ViewHolderBase<BlockStatDownEntity> {
        @BindViewId(R.id.ctv_blocking)
        private CommonTextView ctv_blocking;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void initItemData() {
            ctv_blocking.setLeftTopTextString(DictionaryUtility.getValue(AppConstant.DICT_BUSINESS_TYPE,itemObj.getBusinessType()));
            ctv_blocking.setLeftBottomTextString(DateUtility.formatToStr(itemObj.getBlockDate(),"yyyy/MM/dd"));
            DecimalFormat format = new DecimalFormat("###,###,###,##0.00");
            ctv_blocking.setRightTopTextString(format.format(itemObj.getBlockAmount()));
            ctv_blocking.setRightBottomTextString(itemObj.getPayee());
            ctv_blocking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FundManagerActivity)context).switchBFDF(itemObj);
                }
            });
        }
    }
}
