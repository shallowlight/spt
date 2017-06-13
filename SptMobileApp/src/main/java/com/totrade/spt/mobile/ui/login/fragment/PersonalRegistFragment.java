package com.totrade.spt.mobile.ui.login.fragment;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.autrade.spt.master.entity.NewUserRegisterEntity;
import com.autrade.spt.master.service.inf.IUserRegisterApplyService;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.entity.GeneralDownEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.ui.login.RegistActivity;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.base.BaseSptFragment;

/**
*
* 个人注册页面
* @author huangxy
* @date 2017/4/17
*
*/
public class PersonalRegistFragment extends BaseSptFragment<RegistActivity> implements View.OnClickListener {
    private EditText etPhoneNum;
    private EditText etPhoneVerification;
    private EditText etPwd;
    private EditText etConfirm;
    private EditText etInvation;
    private TextView tvGetVerification;
    private CheckBox cbAgreedterms;
    private String mobileNumber;
    private String loginPwd;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_regist_personal;
    }

    @Override
    protected void initView() {
        ((TextView)findView(R.id.title)).setText("快速注册");

        etPhoneNum = findView(R.id.etPhoneNum);
        etPhoneVerification = findView(R.id.etPhoneVerification);
        etPwd = findView(R.id.etPwd);
        etConfirm = findView(R.id.etConfirm);
        etInvation = findView(R.id.etInvation);
        tvGetVerification = findView(R.id.tvGetVerification);
        cbAgreedterms = findView(R.id.cbAgreedterms);

        findView(R.id.iv_back).setOnClickListener(this);
        findView(R.id.btn_done).setOnClickListener(this);
        findView(R.id.readTerms).setOnClickListener(this);
        findView(R.id.tvGetVerification).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.tvGetVerification:
                time = new TimeCount(100000, 1000);
                sendSmsVerificationCode(etPhoneNum.getText().toString());
                break;

            case R.id.readTerms:
                Intent intent = new Intent();
                intent.setClass(mActivity, WebViewActivity.class);
                intent.putExtra("titleString", "商品通用户服务协议");
                startActivity(intent);
                break;
            case R.id.btn_done:
//                mActivity.switchAddFocus();
                if (!validation()) {
                    break;
                }
                validatePhoneCode();
                break;
        }
    }

    private static String[] hintMsg = new String[]{
            "请输入正确的手机号码",
            "请输入短信验证码",
            "请输入正确的密码，只包含字母数字下划线，以字母开头8-20位",
            "两次输入的密码不一致",
            "验证码错误，请重新输入",
            "短信验证码错误，请重新输入",
            "请阅读并同意《商品通服务协议》",
    };

    private boolean validation() {
        mobileNumber = etPhoneNum.getText().toString();
        if (TextUtils.isEmpty(mobileNumber) || !mobileNumber.matches(AppConstant.RE_PHONE)) {
            ToastHelper.showMessage(hintMsg[0]);
            return false;
        }
        String verificationCode = etPhoneVerification.getText().toString();
        if (TextUtils.isEmpty(verificationCode)) {
            ToastHelper.showMessage(hintMsg[1]);
            return false;
        }
        loginPwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(loginPwd) || !loginPwd.matches(AppConstant.RE_PWD)) {
            ToastHelper.showMessage(hintMsg[2]);
            return false;
        }
        String confirmPwd = etConfirm.getText().toString();
        if (TextUtils.isEmpty(confirmPwd) || !loginPwd.equals(confirmPwd)) {
            ToastHelper.showMessage(hintMsg[3]);
            return false;
        }
        if (!cbAgreedterms.isChecked()) {
            ToastHelper.showMessage(hintMsg[6]);
            return false;
        }
        return true;
    }


    private TimeCount time;

    /**
     * 发送短信验证码
     *
     * @param phoneNumber ..
     */
    private void sendSmsVerificationCode(final String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches(AppConstant.RE_PHONE)) {
            ToastHelper.showMessage(hintMsg[0]);
            return;
        }
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                GeneralDownEntity upEntity = new GeneralDownEntity();
                upEntity.setParamInt1(0);
                upEntity.setParamStr1(phoneNumber);
                upEntity.setParamInt2(30); // 分钟数
                return Client.getService(IUserRegisterApplyService.class).sendSmsVerificationCode(upEntity);
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    ToastHelper.showMessage("验证码已发送");
                    findView(R.id.tvArrived).setVisibility(View.VISIBLE);
//                    String colorPhone = "验证码已发送到您<font color=\"#d88110\">" + phoneNumber + "</font>的手机上";
                    String colorPhone = "验证码已发送到您的手机上";
                    ((TextView) findView(R.id.tvArrived)).setText(Html.fromHtml(colorPhone));
                    tvGetVerification.setClickable(false); // 验证码发送成功则1分钟内不能点击
                    tvGetVerification.setSelected(true);
                    time.start();
                } else {
                    ToastHelper.showMessage("验证码发送失败，请重新获取");
                }
            }
        });
    }

    /**
     * 检查手机验证码
     */
    private void validatePhoneCode() {
        if (TextUtils.isEmpty(etPhoneVerification.getText().toString())) {
            ToastHelper.showMessage(hintMsg[5]);
            return;
        }
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                GeneralDownEntity upEntity = new GeneralDownEntity();
                upEntity.setParamStr1(etPhoneNum.getText().toString()); // 手机号
                upEntity.setParamStr2(etPhoneVerification.getText().toString()); // 验证码
                upEntity.setParamInt1(1);
                return Client.getService(IUserRegisterApplyService.class).validateCode(upEntity);
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    createUserAccount();
                } else {
                    ToastHelper.showMessage(hintMsg[5]);
                }
            }
        });
    }

    /**
     * 创建账户
     */
    private void createUserAccount() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<String>() {

            @Override
            public String requestService() throws DBException, ApplicationException {
                NewUserRegisterEntity userRegEntity = new NewUserRegisterEntity();
                userRegEntity.setMobileNumber(mobileNumber);
                userRegEntity.setPassword(loginPwd);
                return Client.getService(IUserService.class).createUserAccount(userRegEntity);
            }

            @Override
            public void onDataSuccessfully(String userId) {
                if (userId != null) {
                    if (time != null)
                        time.cancel();
                    mActivity.switchAddFocus();

                    NewUserRegisterEntity entity = new NewUserRegisterEntity();
                    entity.setUserId(userId);
                    entity.setMobileNumber(mobileNumber);
                    SharePreferenceUtility.spSave(mActivity, SharePreferenceUtility.REGISTER_PERSONAL, entity.toString());
                }
            }
        });
    }


    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); // 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            // 计时完毕时触发
            tvGetVerification.setText("发送短信验证码");
            tvGetVerification.setClickable(true);
            tvGetVerification.setSelected(false);
            findView(R.id.tvArrived).setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 计时过程显示
            tvGetVerification.setText(millisUntilFinished / 1000 + mActivity.getString(R.string.countdown_time_hint));
        }
    }
}
