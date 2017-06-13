package com.totrade.spt.mobile.ui.mainmatch.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.master.dto.ProductTypeDto;
import com.totrade.spt.mobile.view.R;

import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */

public class SelectionAdapter2 extends BaseAdapter {

    private List<ProductTypeDto> thirdTypeList;

    public SelectionAdapter2(List<ProductTypeDto> thirdTypeList) {
        this.thirdTypeList = thirdTypeList;
    }

    @Override
    public int getCount() {
        return thirdTypeList.size();
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
        View view = View.inflate(parent.getContext(), R.layout.item_match_select2, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(thirdTypeList.get(position).getTypeName());
        view.setTag(thirdTypeList.get(position));


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
            view.findViewById(R.id.iv_right_symbol).setSelected(true);
            view.findViewById(R.id.line).setSelected(true);
            if (isInit)view.performClick();
        } else {
            tv_name.setSelected(false);
            view.findViewById(R.id.iv_right_symbol).setSelected(false);
            view.findViewById(R.id.line).setSelected(false);
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
