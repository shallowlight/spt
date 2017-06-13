package com.totrade.spt.mobile.ui.maintrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.entity.NameValueItem;
import com.totrade.spt.mobile.adapter.SptMobileAdapterBase;
import com.totrade.spt.mobile.bean.ZoneScreen;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

/**
 * 筛选主界面列表适配器
 */
public class ScreenAdapter extends SptMobileAdapterBase<NameValueItem> {
    String[] categories;
    ZoneScreen zoneScreen;
    public ScreenAdapter(Context context, String[] categories, ZoneScreen zoneScreen) {
        super(context, null);
        this.categories = categories;
        this.zoneScreen = zoneScreen;
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectitemview, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView value = (TextView) convertView.findViewById(R.id.lblSelectView);
        ImageView arrow = (ImageView) convertView.findViewById(R.id.imgMore);

        if ("行业".equals(categories[position])) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(0, 1);
            convertView.setLayoutParams(layoutParams);
        }
         if ("贸易方式".equals(categories[position])) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(0, 1);
            convertView.setLayoutParams(layoutParams);
        }

        name.setText(categories[position]);
        value.setText(zoneScreen.getFiled(position ).getName() == null ? null : zoneScreen.getFiled(position ).getName());
        arrow.setVisibility(View.VISIBLE);

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


    private OnListItemClickListener onListItemClickListener;

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }
}