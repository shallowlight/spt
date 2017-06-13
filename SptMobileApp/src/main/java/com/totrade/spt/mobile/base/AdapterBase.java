package com.totrade.spt.mobile.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/26.
 * 说明：此适配器不支持都ItemType的情形，Item内部目前只支持文本控件，和图形控件，有待扩展
 */
public abstract class AdapterBase<T> extends BaseAdapter {

    protected List<T> mList;

    protected Context mContext;

    public AdapterBase(Context context) {
        this.mContext = context;
    }

    /**
     * 向外公开方法设置进数据源
     *
     * @param data 数据源
     */
    public void setData(List<T> data) {
        if (null == mList)
            mList = new ArrayList<>();
        this.mList.addAll(data);
    }

    public void clearData() {
        if (null != mList && mList.size() > 0)
            mList.clear();
    }

    /**
     * 设置数据源，并刷新视图
     *
     * @param data
     */
    public void refreshData(List<T> data) {
        clearData();
        setData(data);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return null == mList ? 0 : mList.size();
    }

    @Override
    public T getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
