package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.TextView;

import com.autrade.spt.common.entity.NameValueItem;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

import java.util.List;

public class NameValueAdapter extends SptMobileAdapterBase<NameValueItem> {
    private static AbsListView.LayoutParams params;
    private static int paddingleft;
    private NameValueItem t;
    private int gravity = Gravity.CENTER_VERTICAL;

    public void setSelectItem(NameValueItem t) {
        this.t = t;
        notifyDataSetChanged();
    }

    public NameValueAdapter(Context context, List<NameValueItem> dataList) {
        super(context, dataList);
        params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, FormatUtil.dip2px(context, 40));
        paddingleft = FormatUtil.dip2px(context, 10);
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = new TextView(getContext());
        convertView.setLayoutParams(params);
        ((TextView) convertView).setGravity(gravity);
        ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        convertView.setPadding(paddingleft, 0, paddingleft, 0);

        final NameValueItem t = (NameValueItem) getItem(position);

        if (null != t) {
            ((TextView) convertView).setText(t.getName());
            if (nameValueEquals(this.t, t))        //已复写equals
                ((TextView) convertView).setTextColor(COLOR_BLUE);
            else
                ((TextView) convertView).setTextColor(COLOR_BLACK);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClickListener != null) {
                    onListItemClickListener.onItemClick(v, position);
                }
            }
        });

        return convertView;
    }


    public boolean nameValueEquals(NameValueItem o1, NameValueItem o2) {
        if (o1 == null || o2 == null) return false;
        String name = o1.getName();
        String value = o1.getValue();
        Object tag = o1.getTag();
        boolean nameEquals = (name == null && o2.getName() == null) || (name != null && name.equals(o2.getName()));
        boolean valueEquals = (value == null && o2.getValue() == null) || (value != null && value.equals(o2.getValue()));
        boolean tagEquals = (tag == null && o2.getTag() == null) || (tag != null && o2.getTag() != null && tag.equals(o2.getTag()));
        return nameEquals && valueEquals && tagEquals;
    }

    private OnListItemClickListener onListItemClickListener;

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }
}
