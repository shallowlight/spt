package com.totrade.spt.mobile.bean;

/**
 * 附件
 * Created by Administrator on 2016/12/21.
 */
public class Attatch {
    private long attatchId;
    private String filePath;
    private String url;


    public Attatch(long attatchId, String filePath,String url) {
        this.url = url;
        this.attatchId = attatchId;
        this.filePath = filePath;
    }

    public Attatch(long attatchId, String filePath) {
        this.attatchId = attatchId;
        this.filePath = filePath;
    }

    public Attatch(long attatchId) {
        this.attatchId = attatchId;
    }

    public Attatch(String url) {
        this.url = url;
    }

    public long getAttatchId() {
        return attatchId;
    }

    public void setAttatchId(long attatchId) {
        this.attatchId = attatchId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
