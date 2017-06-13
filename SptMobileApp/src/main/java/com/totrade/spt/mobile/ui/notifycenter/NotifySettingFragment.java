package com.totrade.spt.mobile.ui.notifycenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.dto.PushConfigDownEntity;
import com.autrade.spt.master.entity.TblPushConfigMasterEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ToggleButtonView;

/**
 * 消息设置
 *
 * @author huangxy
 * @date 2017/5/15
 */
public class NotifySettingFragment extends BaseFragment implements ToggleButtonView.OnClickToggleListener {

    private BaseActivity activity;
    private ViewGroup rootView;

    private ToggleButtonView tbv_trade_notice;
    private ToggleButtonView tbv_trade_focus;
    private ToggleButtonView tbv_tongtong;
    private ToggleButtonView tbv_push_time;

    public NotifySettingFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notify_setting, container, false);
        tbv_trade_notice = (ToggleButtonView) rootView.findViewById(R.id.tbv_trade_notice);
        tbv_trade_focus = (ToggleButtonView) rootView.findViewById(R.id.tbv_trade_focus);
        tbv_tongtong = (ToggleButtonView) rootView.findViewById(R.id.tbv_tongtong);
        tbv_push_time = (ToggleButtonView) rootView.findViewById(R.id.tbv_push_time);

        tbv_trade_notice.setOnClickToggleListener(this);
        tbv_trade_focus.setOnClickToggleListener(this);
        tbv_tongtong.setOnClickToggleListener(this);
        tbv_push_time.setOnClickToggleListener(this);
        rootView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.popBack();
            }
        });

        initData();
        return rootView;
    }

    private void initData() {
        ((TextView) rootView.findViewById(R.id.title)).setText("消息设置");
        findPushConfig();
    }

    @Override
    public void onClickToggle(ToggleButtonView view) {
        switch (view.getId()) {
            case R.id.tbv_trade_notice:
                break;
            case R.id.tbv_trade_focus:
                break;
            case R.id.tbv_tongtong:
                break;
            case R.id.tbv_push_time:
                break;

        }
    }

    private void findPushConfig() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PushConfigDownEntity>() {
            @Override
            public PushConfigDownEntity requestService() throws DBException, ApplicationException {
                TblPushConfigMasterEntity upEntity = new TblPushConfigMasterEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                return Client.getService(com.autrade.spt.master.service.inf.IPushConfigService.class).findPushConfig(upEntity);
            }

            @Override
            public void onDataSuccessfully(PushConfigDownEntity obj) {
                if (obj != null) {
                    tbv_trade_notice.switchOn(obj.isStartUpTrade());
                    tbv_trade_focus.switchOn(obj.isStartUpFocus());
                    tbv_tongtong.switchOn(obj.isStartUpTongTong());
                    if (TextUtils.isEmpty(obj.getTimeFrom()) || TextUtils.isEmpty(obj.getTimeFrom())) {
                        tbv_push_time.switchOn(false);
                    } else {
                        tbv_push_time.switchOn(true);
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        savePushConfig();
    }

    private void savePushConfig() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                TblPushConfigMasterEntity upEntity = new TblPushConfigMasterEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
//                D-交易通知，F-我的关注，T-通通消息
                StringBuffer sb = new StringBuffer();
                sb.append(tbv_trade_notice.isOn() ? "D" : "");
                sb.append(tbv_trade_focus.isOn() ? "F" : "");
                sb.append(tbv_tongtong.isOn() ? "T" : "");
                if (tbv_push_time.isOn()) {
                    upEntity.setTimeFrom("08:00");
                    upEntity.setTimeTo("22:00");
                }
                upEntity.setConfigFlag(sb.toString());
                Client.getService(com.autrade.spt.master.service.inf.IPushConfigService.class)
                        .savePushConfig(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                //tongtong推送由APP拦截
                SharePreferenceUtility.spSave(activity, SharePreferenceUtility.PUSH_TONGTONG, tbv_tongtong.isOn());
            }
        });
    }
}
