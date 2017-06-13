package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.View;

import com.totrade.spt.mobile.view.customize.SwipeView;

import java.util.List;

/**
 * 条目侧滑adapter基类
 *
 * @author huangxy
 * @date 2016/12/1
 */
public abstract class SwipeAdapter<T> extends SptMobileAdapterBase<T> implements SwipeView.OnOpenListener {

    public SwipeAdapter(Context context, List<T> dataList) {
        super(context, dataList);
    }

    private SwipeView spv;

    public SwipeView getSpv() {
        return spv;
    }

    public void setSpv(SwipeView spv) {
        this.spv = spv;
    }

    @Override
    public void onOpen(View v, boolean isOpen) {
        this.spv = (SwipeView) v;
    }

}
