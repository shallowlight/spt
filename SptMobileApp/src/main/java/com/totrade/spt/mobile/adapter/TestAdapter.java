package com.totrade.spt.mobile.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
 import com.autrade.spt.common.entity.NameValueItem;
import com.totrade.spt.mobile.view.R;

import java.util.List;

/**
 * Created by iUserName on 16/11/22.
 *  RecyclerAdapterBase使用示例
 */
class TestAdapter extends RecyclerAdapterBase<NameValueItem,TestAdapter.ViewHolder>
{

    TestAdapter(List<NameValueItem> list)
    {
        super(list);
        setEmptyRes("这里是空值",R.drawable.img_empty_l);
    }

    @Override
    public ViewHolder createViewHolderUseData(ViewGroup parent, int viewType)
    {
        return new ViewHolder(parent);
    }

    class ViewHolder extends ViewHolderBase<NameValueItem>
    {
        @BindViewId(R.id.lblValue) TextView lblValue;
        @BindViewId(R.id.lblName) TextView lblName;
        public ViewHolder(ViewGroup parent)
        {
            super(parent, R.layout.item_common_detail);
        }

        @Override
        public void initItemData()
        {
            //无需对itemObj 判断 null，在基类已经判断 为 null 则不调用initItemData()
            lblValue.setText(itemObj.getValue());
            lblName.setText(itemObj.getName());
        }
    }

}
