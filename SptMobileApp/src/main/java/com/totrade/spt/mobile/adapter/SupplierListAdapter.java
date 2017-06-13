package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.totrade.spt.mobile.entity.Company;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.SwipeView;
import com.totrade.spt.mobile.view.im.common.OnExtendClickListener;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

import java.util.List;

/**
 * 供应商列表适配器
 *
 * @author huangxy
 * @date 2017/1/18
 */
public class SupplierListAdapter extends SwipeAdapter<Company> {

    public String ACTION_DEL = "删除";
    public String ACTION_ADD_BLACK = "加入黑名单";
    public String ACTION_REMOVE = "移除";
    public String ACTION_ADD = "添加";
    private int colorGray;
    private int colorRed;
    private int colorBlue;

    private int listType;

    ViewHolder viewHolder;

    class ViewHolder {
        SwipeView swipeView;        //  策划布局
        TextView tvLetter;        //  首字母
        TextView tvName;        //  公司名称
        ImageView imgSupLevel;        //  供应商等级
        TextView tvAction1;        //  侧滑选项1
        TextView tvAction2;        //  侧滑选项2
        TextView tvExtend;          //  侧滑开关
        View llDetail;
    }

    public SupplierListAdapter(Context context, List<Company> dataList) {
        super(context, dataList);
        colorRed = R.color.ui_red;
        colorGray = R.color.ui_gray_light2;
        colorBlue = R.color.ui_blue;
    }

    public void setListType(int listType) {
        this.listType = listType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_supplier, null);
            viewHolder = new ViewHolder();
            viewHolder.swipeView = (SwipeView) convertView.findViewById(R.id.swipeView);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.tvLetter);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.imgSupLevel = (ImageView) convertView.findViewById(R.id.imgSupLevel);
            viewHolder.tvAction1 = (TextView) convertView.findViewById(R.id.tvAction1);
            viewHolder.tvAction2 = (TextView) convertView.findViewById(R.id.tvAction2);
            viewHolder.tvExtend = (TextView) convertView.findViewById(R.id.tv_extend);
            viewHolder.llDetail = convertView.findViewById(R.id.llDetail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Company company = dataList.get(position);
//        字母索引
        if (position == 0) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
        } else {
            Company company1 = dataList.get(position - 1);
            if (company.getFirstLetter() == (company1.getFirstLetter())) {
                viewHolder.tvLetter.setVisibility(View.GONE);
            } else {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
            }
        }

        viewHolder.tvLetter.setText(Character.toString(company.getFirstLetter()));
        viewHolder.tvName.setText(company.getCompanyName());
        String level = company.getLevel();
        int res = DispUtility.supLevel2DispImg(level);
        viewHolder.imgSupLevel.setImageResource(res);

        viewHolder.tvAction1.setVisibility(View.VISIBLE);
        viewHolder.tvAction2.setVisibility(View.VISIBLE);

        if (level == null) {
//            没有等级不是供应商
            viewHolder.tvAction1.setText(ACTION_ADD);
            viewHolder.tvAction1.setBackgroundResource(colorBlue);
            viewHolder.tvAction2.setVisibility(View.VISIBLE);
        } else if (SptConstant.SUPLEVEL_BLACK.equals(level)) {
//            黑名单供应商
            viewHolder.tvAction1.setText(ACTION_REMOVE);
            viewHolder.tvAction1.setBackgroundResource(colorGray);
            viewHolder.tvAction2.setVisibility(View.GONE);
        } else {
//            我的供应商
            viewHolder.tvAction1.setText(ACTION_DEL);
            viewHolder.tvAction1.setBackgroundResource(colorRed);
            viewHolder.tvAction2.setVisibility(View.VISIBLE);
        }

        viewHolder.tvExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeView pv = (SwipeView) v.getParent().getParent();
                if (pv.isOpen()) pv.smoothCloseMenu();
                else pv.smoothOpenMenu();
            }
        });

        setListener(viewHolder.tvAction1, company);
        setListener(viewHolder.tvAction2, company);
        viewHolder.swipeView.setOnContentClickListener(new SwipeView.OnContentClickListener() {
            @Override
            public void onContentClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, company);
                }
            }
        });

        viewHolder.swipeView.setAdapter(this);
        viewHolder.swipeView.setExpandWid();         //侧滑宽度
        viewHolder.swipeView.setOnOpenListener(this);         //open监听
        return convertView;
    }

    private void setListener(TextView textView, final Company company) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExtendClickListener != null) {
                    onExtendClickListener.onExtendClick(v, company);
                    SwipeView pv = (SwipeView) v.getParent().getParent();
                    if (pv.isOpen()) pv.smoothCloseMenu();
                }
            }
        });
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            Character firstLetter = dataList.get(i).getFirstLetter();
            char firstChar = Character.toUpperCase(firstLetter);
            if (firstChar == section)
                return i;
        }

        return -1;
    }

    private OnExtendClickListener onExtendClickListener;

    public void setOnExtendClickListener(OnExtendClickListener onExtendClickListener) {
        this.onExtendClickListener = onExtendClickListener;
    }

    private OnListItemClickListener itemClickListener;

    public void setListItemClickListener(OnListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
