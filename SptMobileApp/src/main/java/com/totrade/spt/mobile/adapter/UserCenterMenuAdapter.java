package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.bean.Menu;
import com.totrade.spt.mobile.view.R;

/**
 * Created by Timothy on 2017/4/7.
 */

public class UserCenterMenuAdapter extends AdapterBase<Menu> {

    public UserCenterMenuAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_usercenter_menu,parent,false);
        Menu menu = getItem(position);
        textView.setCompoundDrawablesWithIntrinsicBounds(0,menu.getDrawId(),0,0);
        textView.setText(menu.getTitle());
        return textView;
    }
}
