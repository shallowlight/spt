package com.totrade.spt.mobile.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.view.R;

/**
 * Created by Timothy on 2017/1/11.
 */
public abstract class BaseSptFragment<T extends BaseActivity> extends SptMobileFragmentBase {

    protected T mActivity;
    protected ViewGroup mRootView;
    private final SparseArray<View> mViews = new SparseArray<>();

    public BaseSptFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (T) getActivity();
        mRootView = (ViewGroup) LayoutInflater.from(mActivity).inflate(setFragmentLayoutId(), container, false);
        initView();
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        mViews.clear();
        super.onDestroyView();
    }

    /**
     * 传入Fragment主布局资源ID
     *
     * @return
     */
    public abstract int setFragmentLayoutId();

    protected abstract void initView();

    /**
     * 不能用于动态添加的控件
     */
    protected <V extends View> V findView(int resId) {
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

    protected <V extends View> V findView(View view, int resId) {
        return (V) view.findViewById(resId);
    }


    public void setUnReadMsg(boolean unReadMsg) {

    }
}
