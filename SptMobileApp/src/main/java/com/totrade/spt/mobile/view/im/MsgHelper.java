package com.totrade.spt.mobile.view.im;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.nim.OrderAttachment;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

@SuppressLint("DefaultLocale")
public class MsgHelper {

    private static String SESSION_ID = "sessionId";
    private static String SESSION_TYPE = "sessionType";

    private static String id = LoginUserContext.getLoginUserDto().getImUserAccid().toLowerCase();

    public static IMMessage sendText(String sessionId, SessionTypeEnum type, String content) {
        // 创建文本消息
        IMMessage message = MessageBuilder.createTextMessage(
                sessionId, // 聊天对象的ID，如果是单聊，为用户帐号，如果是群聊，为群组ID
                type, // 聊天类型，单聊或群组
                content // 文本内容
        );

        foriOS(type, message, sessionId);

        // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        InvocationFuture<Void> sendMessage = NIMClient.getService(MsgService.class).sendMessage(message, true);
        sendMessage.setCallback(new FutureRequestCallback<Void>());

        return message;
    }

    public static IMMessage sendPhoto(String sessionId, SessionTypeEnum type, File file) {
        IMMessage message = MessageBuilder.createImageMessage(
                sessionId, // 聊天对象的ID，如果是单聊，为用户帐号，如果是群聊，为群组ID
                type, // 聊天类型，单聊或群组
                file, // 图片文件对象
                file.getName() // 文件显示名字，如果第三方 APP 不关注，可以为 null
        );

        foriOS(type, message, sessionId);

        InvocationFuture<Void> sendMessage = NIMClient.getService(MsgService.class).sendMessage(message, false);
        sendMessage.setCallback(new FutureRequestCallback<Void>());

        return message;
    }

    public static IMMessage sendAudio(String sessionId, SessionTypeEnum type, File file, long audioLength) {
        // 创建音频消息
        IMMessage message = MessageBuilder.createAudioMessage(
                sessionId, // 聊天对象的ID，如果是单聊，为用户帐号，如果是群聊，为群组ID
                type, // 聊天类型，单聊或群组
                file, // 音频文件
                audioLength // 音频持续时间，单位是ms
        );

        foriOS(type, message, sessionId);

        InvocationFuture<Void> future = NIMClient.getService(MsgService.class).sendMessage(message, false);
        future.setCallback(new FutureRequestCallback<Void>());

        return message;
    }

    public static IMMessage sendCustomMsg(String sessionId, SessionTypeEnum sessionType, String content, OrderAttachment attachment) {
        CustomMessageConfig config = new CustomMessageConfig();
        IMMessage message = MessageBuilder.createCustomMessage(
                sessionId, // 会话对象
                sessionType, // 会话类型
                content,    //消息简要描述，可通过IMMessage#getContent()获取，主要用于用户推送展示
                attachment, // 自定义消息附件
                config // 自定义消息的参数配置选项
        );

        foriOS(sessionType, message, sessionId);

        InvocationFuture<Void> future = NIMClient.getService(MsgService.class).sendMessage(message, false);
        future.setCallback(new FutureRequestCallback<Void>());
        return message;
    }

    /**
     * iOS推送特供
     *
     * @param type
     * @param message
     * @param sessionId
     */
    private static void foriOS(SessionTypeEnum type, IMMessage message, String sessionId) {
        Map<String, Object> data = new HashMap<>();
        if (type.name().equals(SessionTypeEnum.Team.name())) {
            id = sessionId;
        } else {
            id = LoginUserContext.getLoginUserDto().getImUserAccid().toLowerCase();
        }
        data.put(SESSION_ID, id);
        data.put(SESSION_TYPE, type.name().toLowerCase());
        message.setPushPayload(data);
    }
}
