package com.totrade.spt.mobile.view.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.adapter.SwipeAdapter;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.RecentContactSpt;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.BubbleView;
import com.totrade.spt.mobile.view.customize.SwipeView;
import com.totrade.spt.mobile.view.im.ChatIMActivity;

import java.util.List;

/**
 *
 * Created by Administrator on 2016/12/1.
 */
public class UserSetTeamAdapter extends SwipeAdapter<RecentContactSpt> {

    public UserSetTeamAdapter(Context context, List<RecentContactSpt> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(R.layout.item_recentchat_set);
            convertView = holder.itemView;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final RecentContactSpt spt = (RecentContactSpt) getItem(position);
        //设置图片
        String uri = LoginUserContext.getIconUrlByAccid(spt.getSessionId());
        if (spt.getImgId() != 0)
            holder.imgHeadPic.setImageResource(spt.getImgId());
        else{
            if (TextUtils.isEmpty(uri)) {
                holder.imgHeadPic.setImageResource(R.drawable.img_headpic_def);
            } else {
                Picasso.with(context).load(uri).placeholder(R.drawable.img_headpic_def).error(R.drawable.img_headpic_def).into(holder.imgHeadPic);
            }
        }
        //设置标题
        holder.tvUserName.setText(spt.getTitle());
        //设置内容
        holder.tvLastMsg.setText(spt.getContent());
        //设置未读消息数量
        int unReadCount = spt.getUnReadCount();
        if (unReadCount != 0) {
            holder.tvUnReadNum.setVisibility(View.VISIBLE);
            holder.tvUnReadNum.setText(String.valueOf(unReadCount));
        } else {
            holder.tvUnReadNum.setVisibility(View.GONE);
        }
        //设置消息时间
        if (spt.getTime() != 0) {
            String time = FormatUtil.getTimeShowString(spt.getTime(), false);
            holder.tvLastTime.setText(time);
        }

        holder.swipeView.setAdapter(this);
        holder.swipeView.setExpandWid();         //侧滑宽度
        holder.swipeView.setOnOpenListener(this);         //open监听
        holder.tvRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDelClickListener != null) {
                    onDelClickListener.onDelClick(v, spt);
                }
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contract = spt.getSessionId();
                String petName = LoginUserContext.getPickNameByAccid(contract);
                start2Chat(contract, petName, spt.getSessionType().name());
            }
        });

        return convertView;
    }

    public class ViewHolder extends ViewHolderBase {

        public ViewHolder(int layoutRes) {
            super(layoutRes);
        }

        @BindViewId(R.id.item)
        private LinearLayout item;
        @BindViewId(R.id.imgHeadPic)
        private ImageView imgHeadPic; // 用户头像
        @BindViewId(R.id.tvUserName)
        private TextView tvUserName; // 用户昵称
        @BindViewId(R.id.tvLastTime)
        private TextView tvLastTime; // 最新消息时间
        @BindViewId(R.id.tvLastMsg)
        private TextView tvLastMsg; // 最新消息内容
        @BindViewId(R.id.tvUnReadNum)
        private BubbleView tvUnReadNum; // 未读消息数
        @BindViewId(R.id.swipeView)
        private SwipeView swipeView; // 侧滑自定义View
        @BindViewId(R.id.tvRemarks)
        private TextView tvRemarks; // 编辑

    }

    private void start2Chat(String sessionId, String petName, String type) {
        ChatIMActivity.start(context, sessionId, petName, type);
    }

    public interface OnDelClickListener {
        void onDelClick(View v, RecentContactSpt spt);
    }

    private OnDelClickListener onDelClickListener;

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

}
