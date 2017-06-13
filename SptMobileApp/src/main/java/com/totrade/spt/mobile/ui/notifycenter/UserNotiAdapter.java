package com.totrade.spt.mobile.ui.notifycenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.entity.MessageEntity;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息首页适配器
 *
 * @author huangxy
 * @date 2017/2/28
 */
public class UserNotiAdapter extends RecyclerView.Adapter<UserNotiAdapter.UserNotiViewHolder> {

    private List<MessageEntity> messageEntities;
    private OnListItemClickListener onListItemClickListener;
    private Context context;

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }

    public UserNotiAdapter() {
        this.messageEntities = new ArrayList<>();
    }

    public void notifyDataSetChanged(List<MessageEntity> list) {
        messageEntities.clear();
        messageEntities.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public UserNotiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new UserNotiViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_tab_notify, parent, false));
    }

    @Override
    public void onBindViewHolder(UserNotiViewHolder holder, int position) {
        final MessageEntity messageEntity = messageEntities.get(position);
        holder.iv_noti_icon.setImageResource(messageEntity.getImgDrawable());
        holder.tv_noti_type.setText(messageEntity.getMsgType());
        holder.tv_noti_time.setText(messageEntity.getLastTime());
        holder.tv_noti_content.setText(StringUtility.isNullOrEmpty(messageEntity.getMsgContent()) ?
                context.getResources().getString(R.string.description) : messageEntity.getMsgContent());

        if (messageEntity.getUnread() > 0) {
            holder.tv_num_bubble.setVisibility(View.VISIBLE);
            String unMsgVisiable = messageEntity.getUnread() > 99 ? "99+" : String.valueOf(messageEntity.getUnread());
            holder.tv_num_bubble.setText(String.valueOf(unMsgVisiable));
        } else {
            holder.tv_num_bubble.setVisibility(View.GONE);
        }

//        if (StringUtility.isNullOrEmpty(messageEntity.getMsgContent())) return;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClickListener != null) {
                    onListItemClickListener.onItemClick(v, messageEntity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    class UserNotiViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_noti_icon;
        TextView tv_noti_type;
        TextView tv_noti_time;
        TextView tv_noti_content;
        TextView tv_num_bubble;

        public UserNotiViewHolder(View itemView) {
            super(itemView);
            iv_noti_icon = (ImageView) itemView.findViewById(R.id.iv_noti_icon);
            tv_noti_type = (TextView) itemView.findViewById(R.id.tv_noti_type);
            tv_noti_time = (TextView) itemView.findViewById(R.id.tv_noti_time);
            tv_noti_content = (TextView) itemView.findViewById(R.id.tv_noti_content);
            tv_num_bubble = (TextView) itemView.findViewById(R.id.tv_num_bubble);
        }
    }
}
