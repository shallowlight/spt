package com.totrade.spt.mobile.ui.accountmanager;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.autrade.spt.master.entity.TblUserInfoMasterEntity;
import com.autrade.spt.master.service.inf.IUserRegisterApplyService;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.entity.GeneralDownEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.ValidatorEditText;
import com.totrade.spt.mobile.base.BaseSptFragment;

/**
 * 账户管理
 * 修改手机号码
 * Created by Timothy on 2017/4/10.
 */

public class ModifyTelPhoneFragment extends BaseSptFragment<UserAccMngActivity> {

    private ComTitleBar title;
    private TextView tv_auth_code;
    private ValidatorEditText et_new_telphone;
    private EditText et_auth_code;

    private String telphone;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_modify_telphone;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        tv_auth_code = findView(R.id.tv_action_auth_code);
        et_new_telphone = findView(R.id.et_new_telphone);
        et_auth_code = findView(R.id.et_auth_code);
        et_new_telphone.setSaveEnabled(false);
        et_auth_code.setSaveEnabled(false);
        et_auth_code.setSaveEnabled(false);

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });

        tv_auth_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    downTimer.start();
                    sendSmsVerificationCode();
                }
            }
        });

        findView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAuthCodeFromWeb();
            }
        });
    }

    private CountDownTimer downTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_auth_code.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            tv_auth_code.setEnabled(true);
            tv_auth_code.setText("重新获取验证码");
        }
    };

    private boolean checkData() {
        telphone = et_new_telphone.getText().toString().trim();
        if (!et_new_telphone.checkBody(ValidatorEditText.ValidatorType.PHONE)) {
            ToastHelper.showMessage(ValidatorEditText.ValidatorType.PHONE.getMsg());
            return false;
        }
        if (!StringUtility.isNullOrEmpty(LoginUserContext.getLoginUserDto().getMobileNumber())) {
            if (LoginUserContext.getLoginUserDto().getMobileNumber().equals(telphone)) {
                ToastHelper.showMessage("此手机号已在使用");
                return false;
            }
        }
        return true;
    }

    private void sendSmsVerificationCode() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                GeneralDownEntity upEntity = new GeneralDownEntity();
                upEntity.setParamInt1(0);
                upEntity.setParamStr1(telphone);
                upEntity.setParamInt2(30); // 分钟数
                return Client.getService(IUserRegisterApplyService.class).sendSmsVerificationCode(upEntity);
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    ToastHelper.showMessage("验证码已发送");
                } else {
                    ToastHelper.showMessage("验证码发送失败，请重新获取");
                }
            }
        });
    }

    /**
     * 检查手机验证码是否正确
     */
    private void checkAuthCodeFromWeb() {
        final String verification = et_auth_code.getText().toString().trim();
        if (TextUtils.isEmpty(verification)) {
            ToastHelper.showMessage("请输入短信验证码");
            return;
        }
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                GeneralDownEntity upEntity = new GeneralDownEntity();
                upEntity.setParamStr1(telphone); // 手机号
                upEntity.setParamStr2(verification); // 验证码
                upEntity.setParamInt1(1);
                return Client.getService(IUserRegisterApplyService.class).validateCode(upEntity);
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    saveUserInfo();
                } else {
                    ToastHelper.showMessage("验证码错误!");
                }
            }
        });
    }

    /**
     * 保存手机号
     */
    private void saveUserInfo() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener() {
            @Override
            public Object requestService() throws DBException, ApplicationException {

                TblUserInfoMasterEntity upEntity = new TblUserInfoMasterEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setMobileNumber(telphone);
                Client.getService(IUserService.class).updateUserInfoDetail(upEntity);
                return true;
            }

            @Override
            public void onDataSuccessfully(Object obj) {
                if (null != obj) {
                    ToastHelper.showMessage("手机号保存成功");
                    mActivity.popBack();
                }
            }
        });
    }

}
