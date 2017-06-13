package com.totrade.spt.mobile.view.im.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.friend.model.Friend;

import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;

import com.netease.nimlib.sdk.team.model.Team;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.adapter.SptMobileAdapterBase;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.entity.SoftTeamMember;
import com.totrade.spt.mobile.utility.DisplayImageOptionsUtili;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.im.IMContractUserRemarkActivity;
import com.totrade.spt.mobile.view.im.IMMainActivity;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.SliderView;


/**
 * 相关数据类型：
 * String 索引
 * Team 群
 * SoftTeamMember 群成员
 *
 * @author iUserName
 */
public class MainContractAdapter extends SptMobileAdapterBase<Object> {
    private Team team;
//    private DisplayImageOptions options;
//    private ImageLoader imageLoader;
    private LayoutParams params;
    private int colorWhite;
    private int colorRed;
    private int colorGray;
    private int pad15dp;

    public MainContractAdapter(Context context, List<Object> lst) {
        super(context, lst);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        pad15dp = FormatUtil.dip2px(context, 15);
        colorWhite = context.getResources().getColor(R.color.white);
        colorRed = context.getResources().getColor(R.color.red);
        colorGray = context.getResources().getColor(R.color.gray);
//        options = DisplayImageOptionsUtili.getOptions();
//        imageLoader = ImageLoader.getInstance();
        SptApplication.initImageLoader(context);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        SliderView slideView = (SliderView) convertView;
        if (slideView == null) {
            View itemView = getLayoutInflater().inflate(R.layout.item_contact, null);
            slideView = new SliderView(getContext());
            slideView.setContentView(itemView);
            holder = new ViewHolder(slideView);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        final Object obj = getItem(position);
        slideView.setSliderView(null);
        if (obj == null) {
            convertView.setVisibility(View.GONE);
            return convertView;
        }
        slideView.shrink();
        if (obj instanceof String) {
            holder.llDeatil.setVisibility(View.GONE);
            holder.tvIndex.setVisibility(View.VISIBLE);
            holder.tvIndex.setText((String) obj);
        } else {
            holder.llDeatil.setVisibility(View.VISIBLE);
            holder.tvIndex.setVisibility(View.GONE);
            String url = "";
            String pickName = "";
            if (obj instanceof Team) {
                holder.imgSelect.setVisibility(View.VISIBLE);
                url = ((Team) obj).getIcon();
                pickName = ((Team) obj).getName();

                if (team.getId().equals(((Team) obj).getId())) {
                    holder.imgSelect.setImageResource(R.drawable.img_imteam_01_true);
                } else {
                    holder.imgSelect.setImageResource(R.drawable.img_imteam_01_false);
                }

                //创建一个删除标签
                TextView del = new TextView(getContext());
                del.setText("退出群聊");
                del.setTextColor(colorWhite);
                del.setBackgroundColor(colorRed);
                del.setGravity(Gravity.CENTER);
                del.setPadding(pad15dp, 0, pad15dp, 0);
                del.setLayoutParams(params);
                slideView.setSliderView(del);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((IMMainActivity) getContext()).contactListFragment.removeTeam((Team) obj);
                    }
                });

                holder.imgSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((IMMainActivity) getContext()).contactListFragment.selectTeam((Team) obj);
                    }
                });
                holder.tvUserName.setText(pickName);
            } else if (obj instanceof SoftTeamMember) {
                holder.imgSelect.setVisibility(View.GONE);
                url = ((SoftTeamMember) obj).iconUrl;
                pickName = ((SoftTeamMember) obj).petName;

                //创建群组成员的备注标签
                TextView remarkView = new TextView(getContext());
                remarkView.setText("备注");
                remarkView.setTextColor(colorWhite);
                remarkView.setBackgroundColor(colorGray);
                remarkView.setGravity(Gravity.CENTER);
                remarkView.setPadding(pad15dp, 0, pad15dp, 0);
                remarkView.setLayoutParams(params);
                slideView.setSliderView(remarkView);
                remarkView.setOnClickListener(new RemarkOnClick((SoftTeamMember) obj));

                //备注名
                holder.tvUserName.setText(pickName);
                Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(((SoftTeamMember) obj).teamMember.getAccount());
                if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                    holder.tvUserName.setText(friend.getAlias());
                }
            }
            ImageLoader.getInstance().displayImage(url, holder.imgUserHead, DisplayImageOptionsUtili.getOptions());
        }
        return slideView;
    }

    /**
     * 点击备注
     */
    private class RemarkOnClick implements View.OnClickListener {

        private SoftTeamMember mSoftTeamMember;

        public RemarkOnClick(SoftTeamMember softTeamMember) {
            this.mSoftTeamMember = softTeamMember;
        }

        @Override
        public void onClick(View v) {
            SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
                @Override
                public Boolean requestService() throws DBException, ApplicationException {
                    return NIMClient.getService(FriendService.class).isMyFriend(mSoftTeamMember.teamMember.getAccount());
                }

                @Override
                public void onDataSuccessfully(Boolean b) {
                    if (b)
                        IntentUtils.startActivity(context, IMContractUserRemarkActivity.class, "TEAM_ACCOUNT", mSoftTeamMember.teamMember.getAccount());
                    else
                        addFriend();
                }
            });

        }

        private void addFriend() {
            NIMClient.getService(FriendService.class).addFriend(new AddFriendData(mSoftTeamMember.teamMember.getAccount(), VerifyType.DIRECT_ADD, null))
                    .setCallback(new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int i, Void aVoid, Throwable throwable) {
                            if (i == ResponseCode.RES_SUCCESS) {
//                            添加成功
                                IntentUtils.startActivity(context, IMContractUserRemarkActivity.class, "TEAM_ACCOUNT", mSoftTeamMember.teamMember.getAccount());
                            }
                        }
                    });
        }
    }

    /**
     * 通过首字母查找序号
     */
    public int getPositionForSection(char c) {
        if (c == '群' || c == '顾') {
            return 0;
        }

        if (c == '↑') {
            c = (char) ('A' - 1);   //保证在A前面
        } else if (c == '#') {
            c = (char) ('Z' + 1);  //保证在 Z的后面
        }

        int i = 0;
        char c2;
        for (Object obj : getDataList()) {
            if (obj == null) continue;
            if (obj instanceof String) {
                c2 = ((String) obj).charAt(0);
                if (c2 == '群' || c2 == '顾') {
                    i++;
                    continue;
                }
                if (c2 == '↑') {
                    c2 = (char) ('A' - 1);     //保证在A前面
                } else if (c2 == '#') {
                    c2 = (char) ('Z' + 1);     //保证在 Z的后面
                } else {

                }
                if (c2 >= c) {
                    return i;
                }
            }
            i++;
        }

        if (i >= getCount()) {
            i = getCount() - 1;
        }
        return i;
    }


    public class ViewHolder extends ViewHolderBase {
        @BindViewId(R.id.imgUserHead)
        public ImageView imgUserHead;
        @BindViewId(R.id.tvUserName)
        private TextView tvUserName;
        @BindViewId(R.id.tvIndex)
        private TextView tvIndex;
        @BindViewId(R.id.llDetail)
        private LinearLayout llDeatil;
        @BindViewId(R.id.imgSelect)
        private ImageView imgSelect;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
