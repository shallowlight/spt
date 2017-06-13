package com.totrade.spt.mobile.ui.login.fragment;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.service.inf.ILoginService;
import com.autrade.spt.master.service.inf.IUserService;
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
 * 忘记密码页面2
 *
 * @author huangxy
 * @date 2017/4/7
 */
public class ForgetPwd2Fragment extends BaseSptFragment<ForgetPwdActivity> implements View.OnClickListener {

    private EditText etPwd;
    private EditText etConfirm;
    private Button btnDone;

    public ForgetPwd2Fragment() {
        setContainerId(R.id.framelayout);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_forget2;
    }

    @Override
    protected void initView() {
        etPwd = findView(R.id.etPwd);
        etConfirm = findView(R.id.etConfirm);
        btnDone = findView(R.id.btnNext);

        btnDone.setOnClickListener(this);
        findView(R.id.iv_back).setVisibility(View.VISIBLE);
        findView(R.id.iv_back).setOnClickListener(this);
        ((TextView) findView(R.id.title)).setText("忘记密码");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            mActivity.finish();
//            mActivity.popBack();
        } else if (v.getId() == btnDone.getId()) {
            if (valid()) {
                getUserDtoByUserName();
            }
        }
    }

    private String[] hints = new String[]{"新密码不能为空", "请输入正确的密码,密码由数字字母下划线组成至少8位", "请确认登录密码", "两次输入的登录密码不一致"};

    /**
     * 校验输入内容
     * @return
     */
    private boolean valid() {
        String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastHelper.showMessage(hints[0]);
            return false;
        } else if (!pwd.matches(AppConstant.RE_PWD)) {
            ToastHelper.showMessage(hints[1]);
            return false;
        }
        String cnfmPwd = etConfirm.getText().toString().trim();
        if (TextUtils.isEmpty(cnfmPwd)) {
            ToastHelper.showMessage(hints[2]);
            return false;
        } else if (!pwd.equals(cnfmPwd)) {
            ToastHelper.showMessage(hints[3]);
            return false;
        }

        return true;
    }

    /**
     * 修改用户密码，用于忘记密码找回
     */
    @SuppressLint("UseValueOf")
    private void updatePasswordForget(final String userId) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IUserService.class).updatePasswordForget(userId, etPwd.getText().toString());
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    showMessage("修改成功");
                    btnDone.setBackgroundResource(R.drawable.button_background_press);
                    btnDone.setClickable(false);
                    mActivity.finish();
                }
            }
        });
    }


    private void getUserDtoByUserName() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<LoginUserDto>() {

            @Override
            public LoginUserDto requestService() throws DBException, ApplicationException {
                return Client.getService(ILoginService.class).getUserDtoByUserName(userName);
            }

            @Override
            public void onDataSuccessfully(LoginUserDto dto) {
                if (dto != null) {
                    updatePasswordForget(dto.getUserId());
                } else {
                    showMessage("请联系客服");
                }
            }
        });
    }

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
