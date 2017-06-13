package com.totrade.spt.mobile.entity;

import java.io.Serializable;
import java.util.Date;

public class NotiEntity implements Serializable {
    private String uerId;
    private String msgId;
    private String msgTitle;
    private String msgContent;
    private String msgType;
    private Date date;
    private String readFlag; // 0 未选择
    private String check; // 0 未标记

    public NotiEntity() {
        super();
    }

    public NotiEntity(String uerId, String msgId, String msgTitle, String msgContent, String msgType, Date date, String readFlag, String check) {
        super();
        this.uerId = uerId;
        this.msgId = msgId;
        this.msgTitle = msgTitle;
        this.msgContent = msgContent;
        this.msgType = msgType;
        this.date = date;
        this.readFlag = readFlag;
        this.check = check;
    }

    public String getUerId() {
        return uerId;
    }

    public void setUerId(String uerId) {
        this.uerId = uerId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

}
