package com.totrade.spt.mobile.base.core.actions;

public interface ICallBack3<T1, T2, T3> extends Action {

    void onResult1(T1 t1);

    void onResult2(T2 t2);

    void onResult3(T3 t3);

    void onError(Throwable throwable);
}
