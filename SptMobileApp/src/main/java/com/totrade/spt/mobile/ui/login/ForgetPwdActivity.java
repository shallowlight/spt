package com.totrade.spt.mobile.ui.login;

import android.os.Bundle;

import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.ui.login.fragment.ForgetPwd1Fragment;
import com.totrade.spt.mobile.ui.login.fragment.ForgetPwd2Fragment;
import com.totrade.spt.mobile.view.R;

/**
*
* 忘记密码
* @author huangxy
* @date 2017/4/7
*
*/
public class ForgetPwdActivity extends BaseActivity {

    private ForgetPwd1Fragment forgetPwd1Fragment;
    private ForgetPwd2Fragment forgetPwd2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        forgetPwd1Fragment = new ForgetPwd1Fragment();
        forgetPwd2Fragment = new ForgetPwd2Fragment();

        switchContent(forgetPwd1Fragment);
    }

    public void switchForgetPwd2(String userName) {
        forgetPwd2Fragment.setUserName(userName);
        switchContent(forgetPwd2Fragment);
    }
}
