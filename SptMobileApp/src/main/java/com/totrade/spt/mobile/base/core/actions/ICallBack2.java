package com.totrade.spt.mobile.base.core.actions;

public interface ICallBack2<T1, T2> extends Action {

    void onResult1(T1 t1);

    void onResult2(T2 t2);

    void onError(Throwable throwable);
}
