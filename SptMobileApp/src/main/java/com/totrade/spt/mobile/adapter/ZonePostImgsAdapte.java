package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.bean.PostImageBean;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.PostImageView;

/**
 * Created by Administrator on 2016/12/20.
 */
public class ZonePostImgsAdapte extends AdapterBase<PostImageBean> {


    public ZonePostImgsAdapte(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_post_imgs, parent, false);
            holder.piv = (PostImageView) convertView.findViewById(R.id.piv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PostImageBean bean = getItem(position);
        holder.piv.setImageResource(bean.getPath());
        int styleId = (bean.getServerId() == 0L) ? R.style.txt_main_font_12_ltgray : R.style.txt_main_font_12_ltgray;
        holder.piv.setHint(bean.getHint(), styleId);
        return convertView;
    }

    class ViewHolder {
        PostImageView piv;
    }
}
