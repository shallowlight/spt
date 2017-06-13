package com.totrade.spt.mobile.base.core;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.totrade.spt.mobile.base.SptMobileActivityBase;

public abstract class UIViewActivity<P extends IPresenter> extends SptMobileActivityBase {
    public final static String TAG = "UIViewActivity";
    protected P mPresenter;

    private final SparseArray<View> mViews = new SparseArray<>();

    protected void onCreate(Bundle savedInstanceState) {
        if (getPresenter() != null) {
            try {
                mPresenter = getPresenter().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            mPresenter.attach(this);
        }
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    private <V extends View> V bindView(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = findViewById(resId);
            mViews.put(resId, view);
        }
        return (V) view;
    }

    // 动态添加的控件不能使用
    protected <V extends View> V get(int resId) {
        return bindView(resId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
        mViews.clear();
    }

    protected abstract Class<P> getPresenter();
}
