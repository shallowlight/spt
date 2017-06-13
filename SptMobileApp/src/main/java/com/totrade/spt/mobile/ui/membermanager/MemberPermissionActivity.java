package com.totrade.spt.mobile.ui.membermanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.entity.UserAccountEntity;
import com.autrade.spt.master.entity.UserSubAcctUpEntity;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.ToggleButtonView;

/**
 * 成员管理
 * 权限设置页面
 * Created by Timothy on 2017/4/24.
 */

public class MemberPermissionActivity extends BaseActivity {

    private ComTitleBar title;
    private ToggleButtonView tbv_trader_permission;//交易员权限
    private ToggleButtonView tbv_financial_permission;//财务权限
    private ToggleButtonView tbv_mcmater_permission;//物管权限
    private UserAccountEntity mEntity;
    private ToggleButtonView[] toggleButtonViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_member_permission_unbind);
        initView();
        initData();
    }


    protected void initView() {
        title = (ComTitleBar) findViewById(R.id.title);
        tbv_trader_permission = (ToggleButtonView) findViewById(R.id.tbv_trader_permission);
        tbv_financial_permission = (ToggleButtonView) findViewById(R.id.tbv_financial_permission);
        tbv_mcmater_permission = (ToggleButtonView) findViewById(R.id.tbv_mcmater_permission);

        tbv_trader_permission.setOnClickToggleListener(onClickToggleListener);
        tbv_financial_permission.setOnClickToggleListener(onClickToggleListener);
        tbv_mcmater_permission.setOnClickToggleListener(onClickToggleListener);

        toggleButtonViews = new ToggleButtonView[]{tbv_trader_permission, tbv_financial_permission, tbv_mcmater_permission};
    }

    private ToggleButtonView.OnClickToggleListener onClickToggleListener = new ToggleButtonView.OnClickToggleListener() {
        @Override
        public void onClickToggle(ToggleButtonView view) {
            if (!StringUtility.isNullOrEmpty(mEntity.getUserRole())) {
                if (mEntity.getUserRole().equals(SptConstant.USER_ROLE_COMPANYMASTER)) {
                    for (ToggleButtonView toggleButtonView : toggleButtonViews) {
                        toggleButtonView.switchOn(true);
                        ToastHelper.showMessage("企业管理员不可修改自身权限");
                        return;
                    }
                }
            }
            switchStatusToggle(view);
        }
    };

    private void switchStatusToggle(ToggleButtonView view) {
        if (view.isOn()) {
            for (ToggleButtonView toggleButtonView : toggleButtonViews) {
                if (view.getId() != toggleButtonView.getId()) {
                    toggleButtonView.switchOn(false);
                }
            }
        } else {
            view.switchOn(true);
        }
    }

    private void initData() {
        String entity = getIntent().getStringExtra(ActivityConstant.AccountManager.KEY_ENTITY_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT);
        if (!ObjUtils.isEmpty(entity))
            this.mEntity = JSON.parseObject(entity, UserAccountEntity.class);
        //  T交易账户 F资金账户 D物管账户
        if (mEntity != null && mEntity.getConfigGroupId() != null) {
            tbv_trader_permission.switchOn(mEntity.getConfigGroupId().contains("T"));
            tbv_financial_permission.switchOn(mEntity.getConfigGroupId().contains("F"));
            tbv_mcmater_permission.switchOn(mEntity.getConfigGroupId().contains("D"));
        }
        if (null == mEntity)
            mEntity = new UserAccountEntity();

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSave();
            }
        });
    }

    private void toSave() {
        // 权限配置
        String t = tbv_trader_permission.isOn() ? "T" : "";
        String f = tbv_financial_permission.isOn() ? "F" : "";
        String d = tbv_mcmater_permission.isOn() ? "D" : "";
        mEntity.setConfigGroupId(t + f + d);
        updateTradeSubAcct();
    }

    /**
     * 更新交易子账户
     */
    private void updateTradeSubAcct() {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                LoginUserDto dto = LoginUserContext.getLoginUserDto();
                UserSubAcctUpEntity upEntity = new UserSubAcctUpEntity();
                upEntity.setLoginUserId(dto.getUserId());
                upEntity.setCompanyTag(dto.getCompanyTag());
                upEntity.setUserName(mEntity.getUserName());
                upEntity.setPassword(mEntity.getPassword());
                upEntity.setMobileNumber(mEntity.getMobileNumber());
                upEntity.setUserId(mEntity.getUserId());
                upEntity.setImCode1(mEntity.getImCode1());
                upEntity.setEmail(mEntity.getEmail());
                upEntity.setConfigGroupId(mEntity.getConfigGroupId());
                Client.getService(IUserService.class).updateTradeSubAcct(upEntity);
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    Intent intent = new Intent();
                    intent.putExtra(ActivityConstant.AccountManager.KEY_ENTITY_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT, JsonUtility.toJSONString(mEntity));
                    setResult(ActivityConstant.Common.RESULT_CODE_0X0001, intent);
                    finish();
                } else {
                    ToastHelper.showMessage("操作失败");
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toSave();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
