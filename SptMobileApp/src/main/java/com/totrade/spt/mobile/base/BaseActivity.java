package com.totrade.spt.mobile.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.totrade.spt.mobile.view.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Activity基类
 * 管理fragment，退出应用
 */
public class BaseActivity extends FragmentActivity {

    private static List<BaseActivity> baseActivities = new CopyOnWriteArrayList<BaseActivity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivities.add(this);
//        需要时重新获取 否则界面切换无效
//        fragmentManager = getSupportFragmentManager();
//        transaction = fragmentManager.beginTransaction();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public void exitApp() {
        for (int i = 0; i < baseActivities.size(); i++) {
            BaseActivity baseActivity = baseActivities.get(i);
            baseActivity.finish();
        }
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseActivities.remove(this);
    }

    public void addFragments(BaseFragment... fragments) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            int id = fragments[i].getContainerId();

            BaseFragment fragment2 = (BaseFragment) fragmentManager.findFragmentById(id);
            if (fragment2 == null) {
                transaction.add(id, fragments[i]);
                transaction.hide(fragments[i]);
            }
        }

        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {

        }
    }

    /**
     * 添加单个Fragment
     * @param fragment
     * @param isAddToBackStack 是否加入回退栈
     */
    public void addFragment(BaseFragment fragment,boolean isAddToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.next_in, R.anim.next_out, R.anim.pre_in, R.anim.pre_out);
        int id = fragment.getContainerId();
        if (fragment != null && !fragment.isAdded()) {
            transaction.add(id, fragment);
        }

        if (isAddToBackStack)
            transaction.addToBackStack(null);

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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

    /**
     * 切换fragment
     *
     * @param fragment
     */
    protected void switchFragmentContent(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(fragment.getContainerId(), fragment);

        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BaseFragment switchContent(BaseFragment fragment) {
        return switchContent(fragment, false);
    }

    /**
     * 切换fragment 设置是否加入返回栈
     *
     * @param fragment
     * @param needAddToBackStack
     * @return
     */
    protected BaseFragment switchContent(BaseFragment fragment, boolean needAddToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.next_in, R.anim.next_out, R.anim.pre_in, R.anim.pre_out);
        transaction.replace(fragment.getContainerId(), fragment);
        if (needAddToBackStack) {
            transaction.addToBackStack(null);
        }
        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {

        }

        return fragment;
    }

    /**
     * fragment返回上一栈
     */
    public void popBack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

}
