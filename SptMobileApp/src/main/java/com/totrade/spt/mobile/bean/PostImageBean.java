package com.totrade.spt.mobile.bean;

import android.net.Uri;

import java.net.URI;

/**
 * 图片上传的bean
 * Created by Administrator on 2016/12/20.
 */
public class PostImageBean {

    private String hint;
    private String path;
    private long serverId;

    public PostImageBean(String hint,String path, long serverId) {
        this.hint = hint;
        this.path = path;
        this.serverId = serverId;
    }

    public PostImageBean() {
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getServerId() {
        return serverId;
    }

    @Override
    public boolean equals(Object o) {
        return this.getServerId() == (((PostImageBean) o).getServerId());
    }
}
