package com.totrade.spt.mobile.ui.accountmanager;

import android.os.Bundle;

import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.view.R;

/**
 * 账户管理
 * Created by Timothy on 2017/4/7.
 */

public class UserAccMngActivity extends BaseActivity {


    private SetFragment setFragment;//设置页
    private AboutSptFragment abountSptFragment;//关于商品通
    private AccountManagerFragment accountManagerFragment;//账户管理(用户账户设置页)
    private ModifyPasswordFragment modifyPasswordFragment;//修改密码
    private ModifyTelPhoneFragment modifyTelPhoneFragment;//修改手机号
    private MessageSetFragment messageSetFragment;//消息设置
    private ModifyEmailFragment modifyEmailFragment;//修改邮箱
    private ModifyIDNumberFragment modifyIDNumberFragment;//修改身份证号码

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_layout );

        initFragment ( );
        switchSf ( );
    }

    private void initFragment ( ) {
        setFragment = new SetFragment ( );
        abountSptFragment = new AboutSptFragment ( );
        accountManagerFragment = new AccountManagerFragment ( );
        modifyPasswordFragment = new ModifyPasswordFragment ( );
        modifyTelPhoneFragment = new ModifyTelPhoneFragment ( );
        messageSetFragment = new MessageSetFragment ( );
        modifyEmailFragment = new ModifyEmailFragment ( );
        modifyIDNumberFragment = new ModifyIDNumberFragment ( );
    }

    public void switchSf ( ) {
        switchContent ( setFragment );
    }

    public void switchASF ( ) {
        switchContent ( abountSptFragment, true );
    }

    public void switchAMF ( ) {
        switchContent ( accountManagerFragment, true );
    }

    public void switchMPF ( ) {
        switchContent ( modifyPasswordFragment, true );
    }

    public void switchMTPF ( ) {
        switchContent ( modifyTelPhoneFragment, true );
    }

    public void switchMSF ( ) {
        switchContent ( messageSetFragment, true );
    }

    public void switchMEF ( ) {
        switchContent ( modifyEmailFragment, true );
    }

    public void switchMDIDC ( ) {
        switchContent ( modifyIDNumberFragment, true );
    }

}
