package com.totrade.spt.mobile.base.core;


import com.totrade.spt.mobile.base.core.actions.Action;

/**
 *  请求网络
 * @param <T>
 */
public abstract class Model<T extends Action> {

    protected T mCallBack;

    public void setCallBack(T callBack) {
        mCallBack = callBack;
    }

    public abstract void cancel();
}
