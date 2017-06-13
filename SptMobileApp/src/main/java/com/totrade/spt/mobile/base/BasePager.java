package com.totrade.spt.mobile.base;

import android.content.Context;
import android.view.View;

import com.autrade.stage.droid.helper.InjectionHelper;

/**
 * 轻量级碎片View基类
 */
public abstract class BasePager {
    protected Context context;
    protected View view;

    public BasePager(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void prepare() {
        // 注入所有@Injection字段
        InjectionHelper.injectAllInjectionFields(this);
        initData();
    }

    public abstract View initView();

    public abstract void initData();
}
