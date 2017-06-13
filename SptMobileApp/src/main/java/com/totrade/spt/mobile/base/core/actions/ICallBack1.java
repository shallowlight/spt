package com.totrade.spt.mobile.base.core.actions;

public interface ICallBack1<T1> extends Action {

    void onResult(T1 t1);

    void onError(Throwable throwable);
}
