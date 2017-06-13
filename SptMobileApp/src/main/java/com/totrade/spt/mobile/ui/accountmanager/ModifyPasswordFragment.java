package com.totrade.spt.mobile.ui.accountmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.base.BaseSptFragment;

import cn.jpush.android.api.JPushInterface;

/**
 * 账户管理
 * 修改用户登录密码
 * Created by Timothy on 2017/4/10.
 */

public class ModifyPasswordFragment extends BaseSptFragment<UserAccMngActivity> {

    private ComTitleBar title;
    private TextView et_old_password;
    private TextView et_new_password;
    private TextView et_again_password;
    private Button btn_confirm;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_modify_password;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        et_old_password = findView(R.id.et_old_password);
        et_new_password = findView(R.id.et_new_password);
        et_again_password = findView(R.id.et_again_password);
        btn_confirm = findView(R.id.btn_confirm);

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = et_old_password.getText().toString();
                String newPwd = et_new_password.getText().toString();
                String confirmPwd = et_again_password.getText().toString();

                if (inputCheck(oldPwd, newPwd, confirmPwd)) {
                    changePassword(oldPwd, newPwd);
                } else {
                    return;
                }
            }
        });
    }

    /**
     * 修改用户登录或者资金密码
     */
    private void changePassword(final String oldPassword, final String newPassword) {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IUserService.class).updateUserPassword(LoginUserContext.getLoginUserDto().getUserId(), oldPassword, newPassword);
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    logout();
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    intent.putExtra("updateLoginPwdSuccess", "updateLoginPwdSuccess");
                    startActivity(intent);
                    ToastHelper.showMessage("操作成功");
                    mActivity.finish();
                }
            }
        });
    }

    private boolean isOnlyNumber(String pwd) {
        char[] pwdChar = pwd.toCharArray();
        for (char c : pwdChar) {
            if (c > 57 || c < 48) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查用户输入的密码
     *
     * @param oldPwd
     * @param newPwd
     * @param confirmPwd
     * @return
     */
    private boolean inputCheck(String oldPwd, String newPwd, String confirmPwd) {
        if (TextUtils.isEmpty(oldPwd)) {
            ToastHelper.showMessage("请输入原始密码");
            return false;
        }

        if (!checkPassord(newPwd)) {
            return false;
        }
        if (newPwd.equals(oldPwd)) {
            ToastHelper.showMessage("新密码不能与原密码相同");
            return false;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            ToastHelper.showMessage("请再次输入新密码");
            return false;
        }

        if (!newPwd.equals(confirmPwd)) {
            ToastHelper.showMessage("两次输入的新密码不一致");
            return false;
        }
        return true;

    }

    /**
     * 新密码校验
     */
    private boolean checkPassord(String newPwd) {
        if (StringUtility.isNullOrEmpty(newPwd)) {
            ToastHelper.showMessage("请输入新密码");
            return false;
        }
        if (newPwd.length() < 8 || newPwd.length() > 16) {
            ToastHelper.showMessage("密码长度为8-16位");
            return false;
        }
        if (isOnlyNumber(newPwd)) {
            ToastHelper.showMessage("密码不允许纯数字");
            return false;
        }
        if (!checkPwd(newPwd)) {
            ToastHelper.showMessage("密码只允许数字、字母、特殊符号");
            return false;
        }
        return true;
    }

    /**
     * 是否值是字母数字特殊符号
     */
    private boolean checkPwd(String pwd) {
        char[] pwdChar = pwd.toCharArray();
        for (char c : pwdChar) {
            if (c < 33 || c > 126) {
                return false;
            }
        }
        return true;
    }

    /**
     * 既有字母又有数字
     */
    private boolean hasZMandSZ(String passWord) {

        boolean hasZM = false;
        boolean hasSZ = false;
        for (int i = 0; i < passWord.length(); i++) {
            char cc = passWord.charAt(i);
            if ((cc <= 'z' && cc >= 'a') || (cc <= 'Z' && cc >= 'A')) {
                hasZM = true;
            }
            if (cc >= '0' || cc <= '9') {
                hasSZ = true;
            }
        }
        return hasZM && hasSZ;
    }

    /**
     * 注销
     */
    private void logout() {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", "");
        editor.putString("passWord", "");
        editor.putString("userId", "");
        editor.commit();
        ProductTypeUtility.setLstUserFocusDto(null);
        LoginUserContext.setLoginUserDto(LoginUserContext.getAnonymousDto());

        JPushInterface.setAliasAndTags(mActivity, LoginUserContext.getLoginUserId(), null);
    }
}
