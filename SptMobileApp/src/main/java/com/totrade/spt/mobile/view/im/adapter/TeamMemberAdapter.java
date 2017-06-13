package com.totrade.spt.mobile.view.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.adapter.SwipeAdapter;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.SwipeView;
import com.totrade.spt.mobile.view.im.IMUserInfoEntity;
import com.totrade.spt.mobile.view.im.common.OnExtendClickListener;

import java.util.List;

/**
 * 群成员列表适配器
 *
 * @author huangxy
 * @date 2016/11/30
 */
public class TeamMemberAdapter extends SwipeAdapter<IMUserInfoEntity> {

    public TeamMemberAdapter(Context context, List<IMUserInfoEntity> dataList) {
        super(context, dataList);
    }

    public void updateListView(List<IMUserInfoEntity> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_team_member, null);
            viewHolder = new ViewHolder();
            viewHolder.civPortrait = (CircleImageView) convertView.findViewById(R.id.civPortrait);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.tvLetter);
            viewHolder.tvMemberName = (TextView) convertView.findViewById(R.id.tvMemberName);
            viewHolder.tvRemarks = (TextView) convertView.findViewById(R.id.tvRemarks);
            viewHolder.swipeView = (SwipeView) convertView.findViewById(R.id.swipeView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.swipeView.setAdapter(this);
        viewHolder.swipeView.setExpandWid();         //侧滑宽度
        viewHolder.swipeView.setOnOpenListener(this);         //open监听

        final IMUserInfoEntity userInfoEntity = dataList.get(position);
//        字母索引
        if (position == 0) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
        } else {
            IMUserInfoEntity userInfoEntity1 = dataList.get(position - 1);
            if (userInfoEntity.getLetter().equals(userInfoEntity1.getLetter())) {
                viewHolder.tvLetter.setVisibility(View.GONE);
            } else {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.tvLetter.setText(userInfoEntity.getLetter());
//        main_home_portrait
        if (!TextUtils.isEmpty(userInfoEntity.getAvatar())) {
            Picasso.with(context).load(userInfoEntity.getAvatar()).placeholder(R.drawable.img_headpic_def).into(viewHolder.civPortrait);
        }
//        名称
        viewHolder.tvMemberName.setText(userInfoEntity.getName());

        viewHolder.tvRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExtendClickListener != null)
                    onExtendClickListener.onExtendClick(v, userInfoEntity);
            }
        });

        return convertView;
    }

    ViewHolder viewHolder;

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = dataList.get(i).getLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section)
                return i;
        }

        return -1;
    }

    class ViewHolder {
        TextView tvLetter;
        TextView tvMemberName;
        TextView tvRemarks;
        CircleImageView civPortrait;
        SwipeView swipeView;
    }

    private OnExtendClickListener onExtendClickListener;

    public void setOnExtendClickListener(OnExtendClickListener onExtendClickListener) {
        this.onExtendClickListener = onExtendClickListener;
    }

}
