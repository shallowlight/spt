package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.totrade.spt.mobile.utility.AnnotateUtility;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Adapter基类
 *
 * @param <T>
 * @author dp
 */
public abstract class SptMobileAdapterBase<T> extends BaseAdapter {
    private LayoutInflater layoutInflater;
    public Context context;
    protected List<T> dataList;
    protected DecimalFormat format = new DecimalFormat("#0.#######");
    protected DecimalFormat format2 = new DecimalFormat("#0.00");
    protected static final int COLOR_ORANGE = 0xffff9500;
    protected static final int COLOR_RED = 0xffd84743;
    protected static final int COLOR_BLUE = 0xff3492e9;
    protected static final int COLOR_GRAY = 0xff878787;
    protected static final int COLOR_LTGRAY = 0xffcccccc;
    protected static final int COLOR_BLACK = 0xff000000;

    public SptMobileAdapterBase(Context context, List<T> dataList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
    }

    //得到总的数量
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    //根据ListView位置返回对象
    @Override
    public Object getItem(int position) {
        return this.dataList.get(position );
    }

    //根据ListView位置得到List中的ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public void setLayoutInflater(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public void clear() {
        dataList.clear();
        this.notifyDataSetChanged();
    }

    protected class ViewHolderBase {
        public View itemView;

        public ViewHolderBase(View itemView) {
            if (this.itemView == null) {
                this.itemView = itemView;
                AnnotateUtility.bindViewFormId(this, this.itemView);
                itemView.setTag(this);
            }
        }

        public ViewHolderBase(int layoutRes) {
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(layoutRes, null);
                AnnotateUtility.bindViewFormId(this, itemView);
                itemView.setTag(this);
            }
        }
    }

}
