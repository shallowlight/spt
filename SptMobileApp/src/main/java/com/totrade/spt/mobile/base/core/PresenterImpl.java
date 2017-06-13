package com.totrade.spt.mobile.base.core;

import android.content.Context;

public abstract class PresenterImpl<V extends IView, M extends Model<?>> implements IPresenter {

    protected V iView;
    protected M mModel;
    protected Context mContext;

    @Override
    public void attach(Context context) {
        mContext = context;
        if (getModel() != null) {
            try {
                mModel = getModel().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void addIView(V view) {
        iView = view;
    }

    @Override
    public void detach() {
        if (mModel != null) {
            mModel.cancel();
        }
        mModel = null;
        mContext = null;
        iView = null;
    }

    /**
     * @return Class
     */
    protected abstract Class<M> getModel();
}
