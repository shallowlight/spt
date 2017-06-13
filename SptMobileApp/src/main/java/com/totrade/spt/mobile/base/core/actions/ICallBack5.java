package com.totrade.spt.mobile.base.core.actions;

public interface ICallBack5<T1, T2, T3, T4, T5> extends Action {

    void onResult1(T1 t1);

    void onResult2(T2 t2);

    void onResult3(T3 t3);

    void onResult4(T4 t4);

    void onResult5(T5 t5);

    void onError(Throwable throwable);
}
