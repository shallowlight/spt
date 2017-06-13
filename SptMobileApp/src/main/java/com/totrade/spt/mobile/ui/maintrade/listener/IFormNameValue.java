package com.totrade.spt.mobile.ui.maintrade.listener;

/**
 * Created by Administrator on 2016/12/19.
 */
public interface IFormNameValue {
    void setName(String name);
    void setValue(String value);
    String getName();
    String getValue();
    void setHint(String hint);
    void setIfRequired(boolean ifRequired);
    boolean getIfRequired();
}
