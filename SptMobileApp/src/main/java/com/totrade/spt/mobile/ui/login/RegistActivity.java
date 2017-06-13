package com.totrade.spt.mobile.ui.login;

import android.os.Bundle;
import android.view.KeyEvent;

import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.ui.login.fragment.AddFocusFragment;
import com.totrade.spt.mobile.ui.login.fragment.CompanyBindFragment;
import com.totrade.spt.mobile.ui.login.fragment.PersonalRegistFragment;
import com.totrade.spt.mobile.ui.login.fragment.RegistSuccessFragment;
import com.totrade.spt.mobile.view.R;

/**
 * 注册页面
 *
 * @author huangxy
 * @date 2017/4/7
 */
public class RegistActivity extends BaseActivity {

    private PersonalRegistFragment personalRegistFragment;
    private RegistSuccessFragment registSuccessFragment;
    private CompanyBindFragment companyBindFragment ;
    private AddFocusFragment addFocusFragment ;
    public String personal = "personal";
    public String company = "company";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        personalRegistFragment = new PersonalRegistFragment();
        registSuccessFragment = new RegistSuccessFragment();
        companyBindFragment = new CompanyBindFragment();
        addFocusFragment = new AddFocusFragment();

        parseIntent();
    }

    private void parseIntent() {
        String extra = getIntent().getStringExtra(CompanyBindFragment.class.getName());
        if (extra != null) {
            switchContent(companyBindFragment);
            return;
        }
        switchContent(personalRegistFragment);
    }

    public void switchCompanyBind() {
        switchContent(companyBindFragment);
    }

    public void switchSuccess(String successType) {
        registSuccessFragment.setSuccessType(successType);
        switchContent(registSuccessFragment);
    }

    public void switchAddFocus() {
        addFocusFragment.setStatusBar(false);
        switchContent(addFocusFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
