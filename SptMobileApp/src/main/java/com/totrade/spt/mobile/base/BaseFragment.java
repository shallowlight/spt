package com.totrade.spt.mobile.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

/**
 * Fragment基类
 * 初始化id管理，子Fragment
 */
public class BaseFragment extends Fragment {

    private int containerId;

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    protected void addFragments(BaseFragment... fragments) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        for (int i = 0; i < fragments.length; i++) {
            int id = fragments[i].getContainerId();

            BaseFragment fragment2 = (BaseFragment) fm.findFragmentById(id);
            if (fragment2 == null) {
                transaction.add(id, fragments[i]);
            }
        }

        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {

        }

    }

    private BaseFragment mTempFragment;

    /**
     * 使用hide和show 方法切换Fragment
     *
     * @param fragment 需要切换的fragment
     */
    public void showFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (mTempFragment == null) {
            mTempFragment = fragment;
            transaction.show(fragment);
        } else if (fragment != mTempFragment) {
            transaction.hide(mTempFragment).show(fragment);
            mTempFragment = fragment;
        }

        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

}