package com.totrade.spt.mobile.ui.accountmanager;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.autrade.spt.common.dto.PushConfigDownEntity;
import com.autrade.spt.master.entity.TblPushConfigMasterEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ToggleButtonView;
import com.totrade.spt.mobile.base.BaseSptFragment;

/**
 * 账户管理
 * 消息设置
 * Created by Timothy on 2017/4/10.
 */

public class MessageSetFragment extends BaseSptFragment<UserAccMngActivity> implements ToggleButtonView.OnClickToggleListener {

    private ToggleButtonView tbv_trade_notice;
    private ToggleButtonView tbv_trade_focus;
    private ToggleButtonView tbv_tongtong;
    private ToggleButtonView tbv_push_time;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_notify_setting;
    }

    @Override
    protected void initView() {
        tbv_trade_notice = findView(R.id.tbv_trade_notice);
        tbv_trade_focus = findView(R.id.tbv_trade_focus);
        tbv_tongtong = findView(R.id.tbv_tongtong);
        tbv_push_time = findView(R.id.tbv_push_time);

        tbv_trade_notice.setOnClickToggleListener(this);
        tbv_trade_focus.setOnClickToggleListener(this);
        tbv_tongtong.setOnClickToggleListener(this);
        tbv_push_time.setOnClickToggleListener(this);

        ((TextView)findView(R.id.title)).setText("消息设置");
        findView(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });

        findPushConfig();
    }

    @Override
    public void onStop() {
        super.onStop();
        savePushConfig();
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
                SharePreferenceUtility.spSave(mActivity, SharePreferenceUtility.PUSH_TONGTONG, tbv_tongtong.isOn());
            }
        });
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
}
