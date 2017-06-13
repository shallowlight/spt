package com.totrade.spt.mobile.base.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totrade.spt.mobile.base.SptMobileFragmentBase;

public abstract class UIViewFragment<P extends IPresenter> extends SptMobileFragmentBase {

    protected P mPresenter;

    protected View mRootView;

    private final SparseArray<View> mViews = new SparseArray<>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getPresenter() != null) {
            try {
                mPresenter = getPresenter().newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            mPresenter.attach(activity);
        }
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        // 放在view pager或fragment时，onCreateView会被重复调用，此时xml的控件会被重新绘制
        // mViews也需要在onDestroyView中清除所有加入的view.
        mRootView = inflater.inflate(getLayoutId(), container, false);
        onViewInflated(savedInstanceState);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViews.clear();
    }

    /**
     * 不能用于动态添加的控件
     */
    protected <V extends View> V get(int resId) {
        return bindView(resId);
    }

    @SuppressWarnings("unchecked")
    private <V extends View> V bindView(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = mRootView.findViewById(resId);
            mViews.put(resId, view);
        }
        return (V) view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
    }

    protected abstract Class<P> getPresenter();

    /**
     * fragment根布局
     *
     * @return rootView id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 绑定控件时调用
     *
     * @param savedInstanceState
     */
    protected abstract void onViewInflated(Bundle savedInstanceState);

}
