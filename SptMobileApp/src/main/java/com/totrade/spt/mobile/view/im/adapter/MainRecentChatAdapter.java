package com.totrade.spt.mobile.view.im.adapter;

import java.util.List;
import java.util.Map;

import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.adapter.SptMobileAdapterBase;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.RecentContactSpt;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.BubbleView;
import com.totrade.spt.mobile.view.customize.SliderView;
import com.totrade.spt.mobile.view.im.IMMainActivity;
import com.totrade.spt.mobile.view.im.emoj.MoonUtil;

import android.content.Context;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainRecentChatAdapter extends SptMobileAdapterBase<RecentContactSpt> {
    private LayoutParams params;
    private int colorWhite;
    private int colorRed;
    private int pad15dp;

    public MainRecentChatAdapter(Context context, List<RecentContactSpt> lst) {
        super(context, lst);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        pad15dp = FormatUtil.dip2px(context, 15);
        colorWhite = context.getResources().getColor(R.color.white);
        colorRed = context.getResources().getColor(R.color.red);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        SliderView slideView = (SliderView) convertView;
        if (slideView == null) {
            View itemView = getLayoutInflater().inflate(R.layout.item_recentchat, null);
            slideView = new SliderView(getContext());
            slideView.setContentView(itemView);
            viewHolder = new ViewHolder(slideView);
            slideView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) slideView.getTag();
        }

        final RecentContactSpt recentContact = (RecentContactSpt) getItem(position);
        if (recentContact != null) {
//			String name = LoginUserContext.getPickNameByAccid(sessionId);
//			if (TextUtils.isEmpty(name) && !recentContact.getFromAccount().equalsIgnoreCase(LoginUserContext.getLoginUserAccId()))
//			{
//				// 消息为接受类型时候
//				name = recentContact.getFromNick();
//			}

            //设置消息标题
            viewHolder.tvUserName.setText(recentContact.getTitle());

            //设置内容
            String content = recentContact.getContent();
            MoonUtil.identifyFaceExpression(getContext(), viewHolder.tvLastMsg, content, ImageSpan.ALIGN_BOTTOM);

            //设置图像
            if (recentContact.getImgId() != 0) {
                viewHolder.imgHeadPic.setImageResource(recentContact.getImgId());
            } else {
                String uri = LoginUserContext.getIconUrlByAccid(recentContact.getSessionId());
                if (TextUtils.isEmpty(uri))
                    viewHolder.imgHeadPic.setImageResource(R.drawable.img_headpic_def);
                else
                    Picasso.with(context).load(uri).placeholder(R.drawable.img_headpic_def).error(R.drawable.img_headpic_def).into(viewHolder.imgHeadPic);
            }
            //设置未读消息数量
            int unReadCount = recentContact.getUnReadCount();
            if (unReadCount != 0) {
                viewHolder.tvUnReadNum.setVisibility(View.VISIBLE);
                viewHolder.tvUnReadNum.setText(String.valueOf(unReadCount));
            } else {
                viewHolder.tvUnReadNum.setVisibility(View.GONE);
            }

            //设置消息时间
            String time = FormatUtil.getTimeShowString(recentContact.getTime(), false);
            viewHolder.tvLastTime.setText(time);

            // 创建一个删除标签
            TextView del = new TextView(getContext());
            del.setText("删除");
            del.setTextColor(colorWhite);
            del.setBackgroundColor(colorRed);
            del.setGravity(Gravity.CENTER);
            del.setPadding(pad15dp, 0, pad15dp, 0);
            del.setLayoutParams(params);

            slideView.setSliderView(del);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recentContact.getImgId() != 0 && recentContact.getExtension().size() > 1) {
                        Map<String, Object> setRecent = recentContact.getExtension();
                        for (Map.Entry<String, Object> entry : setRecent.entrySet()) {
                            RecentContactSpt spt = (RecentContactSpt) entry.getValue();
                            ((IMMainActivity) getContext()).recentChatFragment.removeRecentChat(spt);
                        }
                    } else {
                        ((IMMainActivity) getContext()).recentChatFragment.removeRecentChat(recentContact);
                    }
                }
            });
        }
        slideView.shrink();
        return slideView;
    }

    public class ViewHolder extends ViewHolderBase {
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

        ViewHolder(View view) {
            super(view);
        }
    }


}
