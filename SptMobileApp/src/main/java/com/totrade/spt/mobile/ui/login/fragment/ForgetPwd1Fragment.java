package com.totrade.spt.mobile.ui.login.fragment;

import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.autrade.spt.master.service.inf.IUserRegisterApplyService;
import com.autrade.stage.entity.GeneralDownEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.ui.login.ForgetPwdActivity;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.BaseSptFragment;

/**
 * 登录
 * 忘记密码页面1
 *
 * @author huangxy
 * @date 2017/4/7
 */
public class ForgetPwd1Fragment extends BaseSptFragment<ForgetPwdActivity> implements View.OnClickListener {

    private EditText etAccount;
    private EditText etVerification;
    private Button btnNext;
    //    private ImageView imgCaptchaCodeA;
    private TextView tvGetVerification;
    private TextView tvDescription;

    private String phoneNumber;
    private TimeCount time;

    public ForgetPwd1Fragment() {
        setContainerId(R.id.framelayout);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_forget1;
    }

    @Override
    protected void initView() {

        btnNext = findView(R.id.btnNext);
        tvGetVerification = findView(R.id.tvGetVerification);
        tvDescription = findView(R.id.tvDescription);
//        imgCaptchaCodeA = findView(R.id.imgCaptchaCodeA);
        etAccount = findView(R.id.etAccount);
        etVerification = findView(R.id.etVerification);

//        imgCaptchaCodeA.setImageBitmap(CaptchaCodeUtility.getInstance().createBitmap());
//        imgCaptchaCodeA.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);

        findView(R.id.iv_back).setVisibility(View.VISIBLE);
        findView(R.id.iv_back).setOnClickListener(this);
        ((TextView) findView(R.id.title)).setText("忘记密码");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            mActivity.finish();
        } else if (v.getId() == btnNext.getId()) {
            next();
        }
//        else if (v.getId() == imgCaptchaCodeA.getId()) {
//            imgCaptchaCodeA.setImageBitmap(CaptchaCodeUtility.getInstance().createBitmap());
//        }
        else if (v.getId() == tvGetVerification.getId()) {
            phoneNumber = etAccount.getText().toString();
            sendSmsVerificationCode();
        }
    }

    /**
     * 判断验证码是否正确
     * 跳转至下一页
     */
    private void next() {
        final String verification = etVerification.getText().toString().trim();
        if (TextUtils.isEmpty(verification)) {
            ToastHelper.showMessage("请输入短信验证码");
            return;
        }

        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                GeneralDownEntity upEntity = new GeneralDownEntity();
                upEntity.setParamStr1(phoneNumber); // 手机号
                upEntity.setParamStr2(verification); // 验证码
                upEntity.setParamInt1(1);
                return Client.getService(IUserRegisterApplyService.class).validateCode(upEntity);
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    mActivity.switchForgetPwd2(phoneNumber);
                } else {
                    ToastHelper.showMessage("短信验证码错误!");
                }
            }
        });
    }

    /**
     * 发短信
     */
    private void sendSmsVerificationCode() {
        if (!validation()) return;

        time = new TimeCount(100000, 1000);
        tvGetVerification.setSelected(true);
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
                    tvDescription.setVisibility(View.VISIBLE);
//                    String phoneNum = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
//                    String colorPhone = "验证码已发送到您<font color=\"#d88110\">" + phoneNum + "</font>的手机上";
                    String colorPhone = "验证码已发送到您的手机上";
                    tvDescription.setText(Html.fromHtml(colorPhone));

                    tvGetVerification.setClickable(false); // 验证码发送成功则1分钟内不能点击
                    time.start();
                    etAccount.setEnabled(false);
                } else {
                    ToastHelper.showMessage("验证码发送失败，请重新获取");
                }
            }
        });
    }

    /**
     * 短信有效时间
     * 计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); // 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            // 计时完毕时触发
            tvGetVerification.setText("获取短信验证码");
            tvGetVerification.setClickable(true);
            tvGetVerification.setSelected(false);
            etAccount.setEnabled(true);
            tvDescription.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 计时过程显示
            tvGetVerification.setText(millisUntilFinished / 1000 + "秒后重新获取");
        }
    }


    private String[] hints = new String[]{"手机号不能为空", "请输入正确的手机号",};

    /**
     * 校验输入内容
     *
     * @return
     */
    private boolean validation() {
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastHelper.showMessage(hints[0]);
            return false;
        } else if (!phoneNumber.matches(AppConstant.RE_PHONE)) {
            ToastHelper.showMessage(hints[1]);
            return false;
        }
        return true;
    }

}
