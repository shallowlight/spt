package com.totrade.spt.mobile.base.core;

import android.content.Context;

public interface IPresenter {

    void attach(Context context);

    void detach();
}
