package com.totrade.spt.mobile.ui.mainmatch.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.autrade.spt.master.dto.ProductTypeDto;
import com.totrade.spt.mobile.view.R;

import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */

public class SelectionAdapter extends BaseAdapter {

    private List<ProductTypeDto> secondTypeList;

    public SelectionAdapter(List<ProductTypeDto> secondTypeList) {
        this.secondTypeList = secondTypeList;
    }

    @Override
    public int getCount() {
        return secondTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(parent.getContext(), R.layout.item_match_select1, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(secondTypeList.get(position).getTypeName());
        view.setTag(secondTypeList.get(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(position, false);
                if (onViewClickListener != null) {
                    onViewClickListener.onViewClick(v, position);
                }
            }
        });
        if (selection == position) {
            tv_name.setSelected(true);
            if (isInit)view.performClick();
        } else {
            tv_name.setSelected(false);
        }


        return view;
    }

    private int selection;
    private boolean isInit;
    public void setSelection(int selection, boolean isInit) {
        this.selection = selection;
        this.isInit = isInit;
        notifyDataSetChanged();
    }

    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }
}
