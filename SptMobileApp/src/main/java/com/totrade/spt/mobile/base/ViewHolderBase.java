package com.totrade.spt.mobile.base;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totrade.spt.mobile.utility.AnnotateUtility;


/**
 * ViewHolder 基类
 * Created by 王柱全 on 2016/11/8.
 */
public abstract class ViewHolderBase<T> extends RecyclerView.ViewHolder {
    public T itemObj;
    public int position;

    /**
     * @param parent    父控件，
     * @param layoutRes 布局资源文件
     */
    public ViewHolderBase(ViewGroup parent, @LayoutRes int layoutRes) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
        AnnotateUtility.bindViewFormId(this, itemView);
    }

    /**
     * @param view item条目对应的 View
     */
    public ViewHolderBase(View view) {
        super(view);
        AnnotateUtility.bindViewFormId(this, itemView);
    }

    /**
     * item 条目显示设置及处理
     */
    public abstract void initItemData();

    /**
     * 数据处理需要position
     *
     * @param position
     */
    public void initItemData(int position) {

    }
}
