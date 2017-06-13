package com.totrade.spt.mobile.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.utility.ErrorCodeUtility;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.view.im.ChatIMActivity;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.SplashActivity;

import java.util.ArrayList;

public class NIMMessageReceiver extends BroadcastReceiver {
    public final static String TAG = "NIMMessageReceiver";
    private NotificationManager nm;
    private static String imSessionId;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(NimIntent.EXTRA_BROADCAST_MSG)) {
            @SuppressWarnings("unchecked")
            ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_BROADCAST_MSG);
            android.util.Log.i(TAG, "onReceive : : : " + messages.get(0).getFromNick()); // XXX
            if (messages != null) {
                if (SharePreferenceUtility.spGetOut(context, SharePreferenceUtility.PUSH_TONGTONG, true)) ;     //是否开启通通推送
                showNoti(context, messages);
            }
        }

    }

    public static void setImSessionId(String imSessionId) {
        NIMMessageReceiver.imSessionId = imSessionId;
    }

    private void showNoti(Context context, ArrayList<IMMessage> messages) {
        int requestCode = (int) System.currentTimeMillis();
        Intent intent;
        if (ErrorCodeUtility.isErrorMapEmpty()) {
            intent = new Intent(context, SplashActivity.class);
            intent.putExtra(AppConstant.NOTIFY_ID, 1);
        } else {
            intent = new Intent(context, ChatIMActivity.class);
            IMMessage msg = messages.get(0);
            if (msg.getSessionId().equalsIgnoreCase(imSessionId)) {
                return;
            }
            intent.putExtra(ChatIMActivity.EXTRA_LAST_CLASS, context.getClass().getName());
            intent.putExtra(ChatIMActivity.EXTRA_SESSION_TYPE, msg.getSessionType().name());
            intent.putExtra(ChatIMActivity.EXTRA_SESSION_ID, msg.getSessionId());

            intent.putExtra(AppConstant.NOTIFY_ID, AppConstant.NIM_NOTIFY_ID);
            if (imSessionId != null) {
                intent.putExtra("CLOSE", "ChatIM");
            }
            if (msg.getSessionType().name().equals(SessionTypeEnum.P2P.name())) {
                intent.putExtra(ChatIMActivity.EXTRA_SESSION_NAME, msg.getFromNick());
            }
        }
        intent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        IMMessage im = messages.get(0);
        String title = new String();
        String content = new String();
        MsgTypeEnum msgType = im.getMsgType();
        switch (msgType) {
            case text:
                content = im.getContent();
                break;
            case image:
                content = "[图片消息]";
                break;
            case audio:
                content = "[语音消息]";
                break;
            case custom:
                content = "一条询价消息";
                break;
            case notification:
                content = "群通知";
                //群消息不展示
                return;
            default:
                break;
        }

        String name = im.getFromNick();
        Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(im.getFromAccount());
        if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
            name = friend.getAlias();
        }
        title = name;
        if (im.getSessionType().name().equals(SessionTypeEnum.Team.name())) {
            title = "群成员消息";
            content = name + ":" + content;
        }

        Notification noti = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(content)
                .build();
        noti.defaults |= Notification.DEFAULT_LIGHTS;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.defaults |= Notification.DEFAULT_SOUND;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(im.getSessionId(), AppConstant.NIM_NOTIFY_ID, noti);
    }

}
