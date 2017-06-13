package com.totrade.spt.mobile.adapter;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.entity.GetBuySellIntensionDetailForFormDownEntity;
import com.autrade.spt.nego.entity.GetBuySellIntensionDetailUpEntity;
import com.autrade.spt.nego.service.inf.IBuySellIntensionService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.MuteMemberAttachment;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.common.UserInfoProvider;
import com.totrade.spt.mobile.entity.nim.OrderAttachment;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FileUtils;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.im.ChatIMActivity;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.RoundRectImageView;
import com.totrade.spt.mobile.view.im.emoj.MoonUtil;
import com.totrade.spt.mobile.view.im.record.IMOnPlayListener;
import com.totrade.spt.mobile.view.im.record.RecordHelper;

import android.content.Context;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChatHolder
{
    public final static String TAG = "ChatHolder";
    protected View alertButton;
    protected TextView timeTextView;
    protected ProgressBar progressBar;
    protected TextView nameTextView;
    protected FrameLayout contentContainer;
    protected LinearLayout nameContainer;
    private CircleImageView avatarLeft;
    private CircleImageView avatarRight;
    public ImageView nameIconView;
    private View convertView;

    private ChatAdapter chatAdapter;
    private IMMessage immessage;
    public Context context;

    public ChatHolder(ChatAdapter chatAdapter)
    {
        super();
        this.chatAdapter = chatAdapter;
        this.context = chatAdapter.getContext();
    }

    // 判断消息方向，是否是接收到的消息
    protected boolean isReceivedMessage()
    {
        return immessage.getDirect() == MsgDirectionEnum.In;
    }

    public View initView()
    {
        convertView = View.inflate(context, R.layout.item_im_msg, null);
        timeTextView = (TextView) convertView.findViewById(R.id.message_item_time);
        avatarLeft = (CircleImageView) convertView.findViewById(R.id.message_item_portrait_left);
        avatarRight = (CircleImageView) convertView.findViewById(R.id.message_item_portrait_right);
        alertButton = convertView.findViewById(R.id.message_item_alert);
        progressBar = (ProgressBar) convertView.findViewById(R.id.message_item_progress);
        nameTextView = (TextView) convertView.findViewById(R.id.message_item_nickname);
        contentContainer = (FrameLayout) convertView.findViewById(R.id.message_item_content);
        nameIconView = (ImageView) convertView.findViewById(R.id.message_item_name_icon);
        nameContainer = (LinearLayout) convertView.findViewById(R.id.message_item_name_layout);
        convertView.setTag(this);

        return convertView;
    }

    /**
     * 设置时间显示
     */
    private void setTimeTextView() {
        if (chatAdapter.needShowTime(immessage)) {
            timeTextView.setVisibility(View.VISIBLE);
            String text = FormatUtil.getTimeShowString(immessage.getTime(), false);
            timeTextView.setText(text);
        } else {
            timeTextView.setVisibility(View.GONE);
        }

    }

    /**
     * 设置消息发送状态
     */
    private void setStatus() {

        MsgStatusEnum status = immessage.getStatus();
        switch (status) {
            case fail:
                progressBar.setVisibility(View.GONE);
                alertButton.setVisibility(View.VISIBLE);
                break;
            case sending:
                progressBar.setVisibility(View.VISIBLE);
                alertButton.setVisibility(View.GONE);
                break;
            default:
                progressBar.setVisibility(View.GONE);
                alertButton.setVisibility(View.GONE);
                break;
        }
    }

    // 返回该消息是不是居中显示
    protected boolean isMiddleItem() {
        return immessage.getMsgType().equals(MsgTypeEnum.notification);
    }

    // 是否显示头像，默认为显示
    protected boolean isShowHeadImage() {
        return true;
    }

    public void refreshView(IMMessage immsg) {
        this.immessage = immsg;
        setHeadImageView();
        setNameTextView();
        setTimeTextView();
        setStatus();
        setContent();
    }

    /**
     * 消息内容
     */
    private void setContent() {

        LinearLayout bodyContainer = (LinearLayout) convertView.findViewById(R.id.message_item_body);

        // 调整container的位置
        int index = isReceivedMessage() ? 0 : 2;
        if (bodyContainer.getChildAt(index) != contentContainer) {
            bodyContainer.removeView(contentContainer);
            bodyContainer.addView(contentContainer, index);
        }

        if (isMiddleItem()) {
            setGravity(bodyContainer, Gravity.CENTER);
        } else {
            if (isReceivedMessage()) {
                setGravity(bodyContainer, Gravity.LEFT);
                contentContainer.setBackgroundResource(leftBackground());
            } else {
                setGravity(bodyContainer, Gravity.RIGHT);
                contentContainer.setBackgroundResource(rightBackground());
            }
        }

        MsgTypeEnum msgType = immessage.getMsgType();
        View child = new View(context);
        switch (msgType) {
            case text:
                child = getTextLayout();
                break;
            case image:
                child = getImageLayout();
                break;
            case audio:
                child = getAudioLayout();
                break;
            case custom:
                child = getCustomAdviserLayout();
                contentContainer.setBackgroundResource(android.R.color.transparent);
                break;
            case notification:
                child = getNotificationLayout();
                contentContainer.setBackgroundResource(android.R.color.transparent);
                break;
            default:
                break;
        }
        contentContainer.removeAllViews();
        contentContainer.addView(child);

    }

    // 当是接收到的消息时，内容区域背景的drawable id
    protected int leftBackground() {
        return R.drawable.im_chat_dealer;
    }

    // 当是发送出去的消息时，内容区域背景的drawable id
    protected int rightBackground() {
        return R.drawable.im_chat_user;
    }

    /**
     * 文本
     *
     * @return
     */
    private View getTextLayout() {
        TextView child = new TextView(context);
        child.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.textsize_32px));
        child.setPadding(10, 0, 10, 0);
        child.setTextColor(0xff000000);
        MoonUtil.identifyFaceExpression(context, child, immessage.getContent(), ImageSpan.ALIGN_BOTTOM);
        return child;
    }

    /**
     * 图片
     *
     * @return
     */
    private View getImageLayout() {
        RoundRectImageView child = new RoundRectImageView(context);
        final ImageAttachment imgAttachment = (ImageAttachment) immessage.getAttachment();
        final String thumbPathForSave = imgAttachment.getThumbPathForSave();
        String pathForSave = imgAttachment.getPathForSave();

        if (FileUtils.exists(thumbPathForSave)) {
            Picasso.with(context).load(new File(thumbPathForSave)).into(child);
        } else if (FileUtils.exists(pathForSave)) {
            Picasso.with(context).load(new File(pathForSave)).into(child);
        } else {
            String url = imgAttachment.getUrl();
            if (url == null) {
                url = imgAttachment.getPath();
            }
            Picasso.with(context).load(url).into(child);
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        child.setMaxWidth(350);
        child.setLayoutParams(lp);
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ChatIMActivity) context).showPopupWindow(imgAttachment.getUrl(), imgAttachment);
            }
        });
        child.setAdjustViewBounds(true);
        return child;
    }

    /**
     * 录音
     *
     * @return
     */
    private View getAudioLayout() {
        final LinearLayout child = (LinearLayout) View.inflate(context, R.layout.audio_layout, null);
        final AudioAttachment audioAttachment = (AudioAttachment) immessage.getAttachment();

        String path;
        if (FileUtils.exists(audioAttachment.getPath())) {
            path = audioAttachment.getPath();
        } else if (FileUtils.exists(audioAttachment.getPathForSave())) {
            path = audioAttachment.getPathForSave();
        } else {
            path = audioAttachment.getUrl() != null ? audioAttachment.getUrl() : "";
        }
        final TextView tv_duration = (TextView) child.findViewById(R.id.tv_duration);

        final long duration = audioAttachment.getDuration();
        tv_duration.setText(FormatUtil.long2Time(duration));
        tv_duration.setTextColor(isReceivedMessage() ? context.getResources().getColor(R.color.black) : context.getResources().getColor(R.color.white));

        final String filePath = path;
        child.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                RecordHelper.setOnPlayListener(new IMOnPlayListener() {

                    @Override
                    public void onPlaying(long curPosition) {
                        ((TextView) (v.findViewById(R.id.tv_duration))).setText(FormatUtil.long2Time(curPosition));
                    }

                    @Override
                    public void onCompletion() {
                        v.setSelected(false);
                        ((TextView) (v.findViewById(R.id.tv_duration))).setText(FormatUtil.long2Time(duration));
                    }
                });
                if (v.isSelected()) // 播放状态
                {
                    // RecordHelper.pause();
                    RecordHelper.stop();
                } else // 暂停状态
                {
                    if (RecordHelper.isPlaying()) {
                        return;
                    }
                    RecordHelper.playAudio(filePath);
                }
                v.setSelected(!v.isSelected());
            }
        });

        ImageView iv_paly = (ImageView) child.findViewById(R.id.iv_play);
        if (isReceivedMessage()) {
            iv_paly.setBackgroundResource(R.drawable.selector_audio_paly_dealer);
        } else {
            iv_paly.setBackgroundResource(R.drawable.selector_audio_paly_user);
        }

        return child;
    }

    /**
     * 询价
     *
     * @return
     */
    private View getCustomAdviserLayout()
    {
        final OrderAttachment attachment = (OrderAttachment) immessage.getAttachment();
        if (attachment == null)
        {
            return getTextLayout();
        }

        if (TextUtils.isEmpty(attachment.getProductType())) return getTextLayout();
//        成交单直接显示成交item 非成交单请求网络再显示
        if (attachment.getNegoStatus().equals(SptConstant.NEGOSTATUS_D))
            return getCustomHostLayout();

        final FrameLayout container = new FrameLayout(context);
        final LinearLayout child = (LinearLayout) View.inflate(context, R.layout.custom_adviser_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FormatUtil.dip2px(context, 200), ViewGroup.LayoutParams.WRAP_CONTENT);
        child.setLayoutParams(layoutParams);
        TextView tv_product_name = (TextView) child.findViewById(R.id.tv_product_name);
        TextView tv_number_price = (TextView) child.findViewById(R.id.tv_number_price);
        final TextView tv_enquiry = (TextView) child.findViewById(R.id.tv_enquiry);
        TextView tv_time = (TextView) child.findViewById(R.id.tv_dealtime);
        final ImageView iv_busyell = (ImageView) child.findViewById(R.id.iv_busyell);
        final View ll_product = child.findViewById(R.id.ll_product);
        View ll_status = child.findViewById(R.id.ll_status);

//        final OrderAttachment attachment = (OrderAttachment) immessage.getAttachment();
//        if (attachment == null) {
//            return getTextLayout();
//        }
        if (attachment.getBuySell() != null) {
            boolean isSell = attachment.getBuySell().equals("S");
            iv_busyell.setBackgroundResource(isSell ? R.drawable.im_order_sell : R.drawable.im_order_buy);
            ll_product.setBackgroundResource(isSell ? R.drawable.selector_im_order_bg_green : R.drawable.selector_im_order_bg_orange);
        }

        tv_product_name.setText(attachment.getProductName());
//
//        SubAsyncTask.create().setOnDataListener("custom", new OnDataListener<GetBuySellIntensionDetailForFormDownEntity>() {
//            @Override
//            public GetBuySellIntensionDetailForFormDownEntity requestService() throws DBException, ApplicationException {
//                GetBuySellIntensionDetailUpEntity upEntity = new GetBuySellIntensionDetailUpEntity();
//                upEntity.setFromMobileClient(true);
//                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
//                upEntity.setSubmitId(attachment.getBizId());
//                return Client.getService(IBuySellIntensionService.class).getBuySellIntensionDetailForForm(upEntity);
//            }
//
//            @Override
//            public void onDataSuccessfully(GetBuySellIntensionDetailForFormDownEntity downEntity)
//            {
//                if (downEntity != null)
//                {
//
//                    negoStatus(downEntity.getNegoStatus(), tv_enquiry, iv_busyell);
//                    if (downEntity.getNegoStatus().equals(SptConstant.NEGOSTATUS_D)) {
//                        return;
//                    }
//
//                    if (downEntity.getValidTime().before(HostTimeUtility.getDate())) {
//                        tv_enquiry.setText("已过期");
//                        child.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                ((ChatIMActivity) context).showMessage("该询价已过期");
//                            }
//                        });
//                    }
//                }
//            }
//        });


        String negoStatus = ((ChatIMActivity)context).getNegoStatus(attachment.getBizId());
        if(TextUtils.isEmpty(negoStatus))
        {
            negoStatus = attachment.getNegoStatus();
        }

        negoStatus(negoStatus, tv_enquiry, iv_busyell);

//        布局偏移调整
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) ll_status.getLayoutParams();
        if (isReceivedMessage()) {
            lp.leftMargin = FormatUtil.dip2px(context, 2.5f);
            ll_product.setSelected(false);
        } else {
            lp.rightMargin = FormatUtil.dip2px(context, 2.5f);
            ll_product.setSelected(true);
        }
        ll_status.setLayoutParams(lp);

        if (
                attachment.getProductNumber() != null
                        && attachment.getNumberUnit() != null
                        && attachment.getPriceUnit() != null
                        && attachment.getProductPrice() != null
                ) {
            String numberPrice = attachment.getPriceUnit()
                    + FormatUtil.takeRound(attachment.getProductPrice())
                    + "/"
                    + attachment.getNumberUnit()
                    + " "
                    + FormatUtil.takeRound(attachment.getProductNumber())
                    + attachment.getNumberUnit();
            tv_number_price.setText(numberPrice);
        }
        tv_time.setText(attachment.getDeliveryTimeStr());

        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ChatIMActivity) context).showOrderDetail(attachment);
            }
        });

        container.addView(child);
        return container;
    }

    private void negoStatus(String status, TextView tv_enquiry, ImageView iv_busyell)
    {
        if (SptConstant.NEGOSTATUS_U.equals(status))
        {
            tv_enquiry.setText("未确认");
        }
        else if (SptConstant.NEGOSTATUS_C.equals(status))
        {
            tv_enquiry.setText("未成交");
        }
        else if (SptConstant.NEGOSTATUS_D.equals(status))
        {
            tv_enquiry.setText("已成交");
        }
        else if("CD".equals(status))
        {
            tv_enquiry.setText("已撤单");
        }
        else
        {
            tv_enquiry.setText("已过期");
        }


    }

    private View getCustomHostLayout() {
        LinearLayout child = (LinearLayout) View.inflate(context, R.layout.custom_host_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FormatUtil.dip2px(context, 260), ViewGroup.LayoutParams.WRAP_CONTENT);
        child.setLayoutParams(layoutParams);
        View ll_status = child.findViewById(R.id.ll_status);
        LinearLayout ll_product = (LinearLayout) child.findViewById(R.id.ll_product);
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) ll_product.getLayoutParams();
        if (isReceivedMessage()) {
            ll_status.setSelected(true);
            lp.leftMargin = FormatUtil.dip2px(context, 2.5f);
        } else {
            ll_status.setSelected(false);
            lp.rightMargin = FormatUtil.dip2px(context, 2.5f);
        }
        ll_product.setLayoutParams(lp);

        final OrderAttachment attachment = (OrderAttachment) immessage.getAttachment();
//        品名
        ((TextView) child.findViewById(R.id.tv_name)).setText(attachment.getProductName());
        String price = attachment.getPriceUnit()
                + FormatUtil.takeRound(attachment.getProductPrice())
                + "/"
                + attachment.getNumberUnit();
//        数量
        ((TextView) child.findViewById(R.id.tv_unitprice)).setText(price);
//        价格
        ((TextView) child.findViewById(R.id.tv_number)).setText(FormatUtil.takeRound(attachment.getProductNumber())
                + attachment.getNumberUnit());
//        交货期
        ((TextView) child.findViewById(R.id.tv_delivery_time)).setText(attachment.getDeliveryTimeStr());

        try {
            if (!attachment.getProductType().startsWith("GT")) {
                //        商品质量
                if (attachment.getProductQuality() != null) {
                    String productQuality = DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_QUALITY,
                            attachment.getProductQuality());
                    ((TextView) child.findViewById(R.id.tv_product_quality)).setText(productQuality);
                }
            } else {
                String productQualityEx1 = new DecimalFormat("#0.#######").format(new BigDecimal(attachment.getProductQualityEx1()));
                ((TextView) child.findViewById(R.id.tv_product_quality)).setText(productQualityEx1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "getProductQuality" + attachment.getProductQuality());
            LogUtils.e(TAG, "getProductQualityEx1" + attachment.getProductQualityEx1());
        }
//        产地
        ((TextView) child.findViewById(R.id.tv_product_place)).setText(attachment.getProductPlace());
//        定金
        ((TextView) child.findViewById(R.id.tv_bond)).setText(attachment.getBond());
//        交货地
        ((TextView) child.findViewById(R.id.tv_delivery_place)).setText(attachment.getDeliveryPlace());

        return child;
    }

    /**
     * 通知
     *
     * @return
     */
    private View getNotificationLayout() {
        TextView tv_notification = (TextView) View.inflate(context, R.layout.notification_layout, null);

        String tar = "";
        String notification = null;

        MsgAttachment msgAttach = immessage.getAttachment();
        if (msgAttach instanceof MemberChangeAttachment) {
            MemberChangeAttachment attachment = (MemberChangeAttachment) msgAttach;
            if (attachment.getType().equals(NotificationType.MuteTeamMember)) {
                //遍历目标人
                ArrayList<String> targets = attachment.getTargets();
                for (String str : targets) {
                    tar = tar + "," + UserInfoProvider.getUserInfo(str).getName();
                }
                if (tar.startsWith(",")) {
                    tar = tar.substring(1);
                }

                MuteMemberAttachment attach = (MuteMemberAttachment) attachment;
                notification = tar + "被" + (attach.isMute() ? "禁言" : "解禁");
            }
        }
        if (TextUtils.isEmpty(notification)) {
            tv_notification.setVisibility(View.GONE);
        } else {
            tv_notification.setVisibility(View.VISIBLE);
            tv_notification.setText(notification);
        }
        return tv_notification;
    }

    // 设置FrameLayout子控件的gravity参数
    protected final void setGravity(View view, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
    }

    /**
     * main_home_portrait
     */
    private void setHeadImageView() {
        ImageView show = isReceivedMessage() ? avatarLeft : avatarRight;
        ImageView hide = isReceivedMessage() ? avatarRight : avatarLeft;
        hide.setVisibility(View.GONE);
        if (!isShowHeadImage()) {
            show.setVisibility(View.GONE);
            return;
        }
        if (isMiddleItem()) {
            show.setVisibility(View.GONE);
        } else {
            show.setVisibility(View.VISIBLE);
            show.setImageResource(R.drawable.img_headpic_def);
            String uri = LoginUserContext.getIconUrlByAccid(immessage.getFromAccount());
            if (!TextUtils.isEmpty(uri))
                Picasso.with(context).load(uri).placeholder(R.drawable.img_headpic_def).into(show);
        }
    }

    /**
     * 昵称,单聊中不显示昵称
     */
    public void setNameTextView() {
        if (immessage.getSessionType() == SessionTypeEnum.Team && isReceivedMessage() && !isMiddleItem()) {
            nameTextView.setVisibility(View.VISIBLE);
            String name = immessage.getFromNick();
            if (TextUtils.isEmpty(name)) {
                name = LoginUserContext.getPickNameByAccid(immessage.getFromAccount());
            }
            nameTextView.setText(name);
            //            备注名
            Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(immessage.getFromAccount());
            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                nameTextView.setText(friend.getAlias());
            }

            if (immessage.getFromAccount().equals(UserInfoProvider.getNIMAccid())) {
                nameTextView.setText(context.getResources().getString(R.string.myself));
            }
        } else {
            nameTextView.setVisibility(View.GONE);
        }
    }


//	NotificationType
//	AcceptInvite	用户接受入群邀请
//	AddTeamManager	新增管理员通知
//	DismissTeam	群被解散
//	InviteMember	邀请群成员，用于讨论组中，讨论组可直接拉人入群
//	KickMember	移除群成员
//	LeaveTeam	有成员离开群
//	MuteTeamMember	群成员禁言/解禁
//	PassTeamApply	管理员通过用户入群申请
//	RemoveTeamManager	撤销管理员通知
//	TransferOwner	群组拥有者权限转移通知
//	undefined	未定义类型
//	UpdateTeam	群资料更新

}
