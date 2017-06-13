package com.totrade.spt.mobile.view.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.adapter.SwipeAdapter;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.SwipeView;
import com.totrade.spt.mobile.view.im.ChatIMActivity;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */
public class SetAdviserAdapter extends SwipeAdapter<NimUserInfo> {

    public SetAdviserAdapter(Context context, List<NimUserInfo> dataList) {
        super(context, dataList);
    }

    public void updateListView(List<NimUserInfo> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_team_member, null);
            viewHolder = new ViewHolder();
            viewHolder.item = (LinearLayout) convertView.findViewById(R.id.llItem);
            viewHolder.civPortrait = (CircleImageView) convertView.findViewById(R.id.civPortrait);
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

        final NimUserInfo userInfoEntity = dataList.get(position);

//        main_home_portrait
        if (!TextUtils.isEmpty(userInfoEntity.getAvatar())) {
            Picasso.with(context).load(userInfoEntity.getAvatar()).placeholder(R.drawable.img_headpic_def).into(viewHolder.civPortrait);
        }
//        名称
        viewHolder.tvMemberName.setText(userInfoEntity.getName());

        viewHolder.tvRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRemarksClickListener != null)
                    onRemarksClickListener.onRemarksClick(v, userInfoEntity);
            }
        });

/*       viewHolder.swipeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contract = userInfoEntity.getAccount();
                String petName = LoginUserContext.getPickNameByAccid(contract);
                start2Chat(contract, petName, SessionTypeEnum.P2P.name());
            }
        });
*/
        viewHolder.swipeView.setOnContentClickListener(new SwipeView.OnContentClickListener() {
            @Override
            public void onContentClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, userInfoEntity);
                }
            }
        });

        return convertView;
    }

    private OnListItemClickListener itemClickListener;

    public void setListItemClickListener(OnListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private ViewHolder viewHolder;

    class ViewHolder {
        LinearLayout item;
        TextView tvMemberName;
        TextView tvRemarks;
        CircleImageView civPortrait;
        SwipeView swipeView;
    }

    public interface OnRemarksClickListener {
        void onRemarksClick(View v, NimUserInfo userInfoEntity);
    }

    private OnRemarksClickListener onRemarksClickListener;

    public void setOnRemarksClickListener(OnRemarksClickListener onRemarksClickListener) {
        this.onRemarksClickListener = onRemarksClickListener;
    }


}
