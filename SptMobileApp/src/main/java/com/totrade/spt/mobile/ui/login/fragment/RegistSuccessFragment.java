package com.totrade.spt.mobile.ui.login.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.login.RegistActivity;
import com.totrade.spt.mobile.view.R;

/**
 * 注册成功页面
 * 包含 个人注册成功和企业注册成功
 *
 * @author huangxy
 * @date 2017/4/17
 */
public class RegistSuccessFragment extends BaseFragment implements View.OnClickListener{
    private RegistActivity activity;
    private View rootView;

    private String successType;

    private View ll_regist1;
    private View ll_regist2;
    private PendingIntent pendingIntent;

    public RegistSuccessFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (RegistActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_regist_success, container, false);
        ll_regist1 = rootView.findViewById(R.id.ll_regist1);
        ll_regist2 = rootView.findViewById(R.id.ll_regist2);

        rootView.findViewById(R.id.iv_back).setOnClickListener(this);
        rootView.findViewById(R.id.tv_upgrade).setOnClickListener(this);
        rootView.findViewById(R.id.tv_upgrade_not).setOnClickListener(this);

        if (activity.personal.equals(successType)) {
            ll_regist1.setVisibility(View.VISIBLE);
            ll_regist2.setVisibility(View.GONE);
        } else if (activity.company.equals(successType)){
            ll_regist1.setVisibility(View.GONE);
            ll_regist2.setVisibility(View.VISIBLE);
        }
        startPending();

        return rootView;
    }

    public void setSuccessType(String successType) {
        this.successType = successType;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                activity.finish();
                break;
            case R.id.tv_upgrade:
                activity.switchCompanyBind();
                break;
            case R.id.tv_upgrade_not:
                activity.finish();
                break;
        }
        canclePending();
    }


    /**
     * 30秒无操作跳转至首页
     */
    private void startPending() {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        long l = SystemClock.currentThreadTimeMillis() + 30 * 1000;
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, l, pendingIntent);
    }

    /**
     * 取消跳转至首页
     */
    private void canclePending() {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
