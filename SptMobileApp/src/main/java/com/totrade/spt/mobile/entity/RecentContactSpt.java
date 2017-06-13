package com.totrade.spt.mobile.entity;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class RecentContactSpt implements RecentContact {

    enum RecentType{
        TEAM,PSP,SET
        //群，单聊，组
    }

    String sessionId; // 发消息人的聊天对象Id，群聊时为teamId
    String fromAccid; // 发消息人的云信id
    String fromNickName; // 发消息人的昵称
    String avatar; // 头像url
    String content; // 最近一条消息内容
    String recentMessageId;
    int unReadCount; // 未读消息数
    long time; // 最近一条消息时间
    SessionTypeEnum sessionType; // 0 team, 1 p2p
    MsgStatusEnum msgStatus;
    MsgAttachment msgAttachment;
    String title;//云信消息标题
    MsgTypeEnum msgType;//消息类型
    long tag;
    Map<String, Object> extension;
    List<String> extensionData;
    int imgId;
    RecentType recentType;//新定义的消息分类
    String extensionsStr;

    @Override
    public String getContactId() {
        return sessionId;
    }

    @Override
    public String getFromAccount() {
        return fromAccid;
    }

    @Override
    public String getFromNick() {
        return fromNickName;
    }

    @Override
    public String getRecentMessageId() {
        return recentMessageId;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return msgType;
    }

    @Override
    public MsgStatusEnum getMsgStatus() {
        return msgStatus;
    }

    @Override
    public void setMsgStatus(MsgStatusEnum msgStatusEnum) {
        this.msgStatus = msgStatusEnum;
    }

    @Override
    public int getUnreadCount() {
        return unReadCount;
    }

    @Override
    public MsgAttachment getAttachment() {
        return msgAttachment;
    }

    @Override
    public void setTag(long l) {
        this.tag = l;
    }

    @Override
    public long getTag() {
        return tag;
    }

    @Override
    public Map<String, Object> getExtension() {
        return extension;
    }

    @Override
    public void setExtension(Map<String, Object> map) {
        this.extension = map;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFromAccid() {
        return fromAccid;
    }

    public void setFromAccid(String fromAccid) {
        this.fromAccid = fromAccid;
    }

    public String getFromNickName() {
        return fromNickName;
    }

    public void setFromNickName(String fromNickName) {
        this.fromNickName = fromNickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public SessionTypeEnum getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionTypeEnum sessionType) {
        this.sessionType = sessionType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMsgType(MsgTypeEnum msgType) {
        this.msgType = msgType;
    }

    public void setExtensionData(List<String> extensionData) {
        this.extensionData = extensionData;
    }

    public List<String> getExtensionData() {
        return this.extensionData;
    }

    public void setRecentMessageId(String recentMessageId) {
        this.recentMessageId = recentMessageId;
    }

    public void setImgId(int resId) {
        this.imgId = resId;
    }

    public int getImgId() {
        return this.imgId;
    }

    public void setRecentType(RecentType recentType){this.recentType = recentType;}

    public RecentType getRecentType(){return recentType;}

    public RecentContactSpt() {
        super();
    }

    public void setExtensionsStr(String str){this.extensionsStr = str;}

    public String getExtensionsStr(){return this.extensionsStr;}

    public RecentContactSpt(String sessionId, String fromAccid, String fromNickName, String avatar, String content, int unReadCount, long time, SessionTypeEnum sessionType) {
        super();
        this.sessionId = sessionId;
        this.fromAccid = fromAccid;
        this.fromNickName = fromNickName;
        this.avatar = avatar;
        this.content = content;
        this.unReadCount = unReadCount;
        this.time = time;
        this.sessionType = sessionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this.sessionId.equals(((RecentContactSpt) o).getSessionId()))
            return true;
        if (this.sessionType == SessionTypeEnum.Team && ((RecentContactSpt) o).sessionType == SessionTypeEnum.Team) {
            if (this.getExtensionsStr().equals(((RecentContactSpt) o).getExtensionsStr())){
                return true;
            }
        }
        return false;
    }

    public Object deepClone() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);//从流里读出来
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (oi.readObject());
        } catch (Exception e) {
            return null;
        }
    }

}
