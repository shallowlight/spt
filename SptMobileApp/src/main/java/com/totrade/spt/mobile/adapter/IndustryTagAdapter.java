package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.autrade.spt.common.entity.TblSptDictionaryEntity;
import com.google.gson.Gson;
import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.view.R;

/**
 *
 * Created by Administrator on 2016/11/8.
 */
public class IndustryTagAdapter extends AdapterBase<TblSptDictionaryEntity> {

    public IndustryTagAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sec_industry_tag, parent,false);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_industry);
        TblSptDictionaryEntity entity = getItem(position);
        checkBox.setText(entity.getDictValue());
        Log.i("TAG",new Gson().toJson(entity));
        return view;
    }

}
