package com.totrade.spt.mobile.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.autrade.stage.constants.CommonErrorId;
import com.autrade.stage.droid.helper.InjectionHelper;
import com.autrade.stage.droid.validator.ValidatorExecutor;
import com.autrade.stage.exception.SystemException;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.ErrorCodeUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.SplashActivity;
import com.totrade.spt.mobile.view.WebViewActivity;

import java.text.DecimalFormat;

/**
 * 常用Activity基类，处理界面及数据
 * 注解对象，弹出提示语
 */
public abstract class SptMobileActivityBase extends BaseActivity implements ActivityConstant {
    protected DecimalFormat format = new DecimalFormat("#0.#######");
    protected DecimalFormat format2 = new DecimalFormat("#0.00");
    protected ValidatorExecutor validatorExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.validatorExecutor = new ValidatorExecutor();
        InjectionHelper.injectAllInjectionFields(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // 绑定控件Id
        AnnotateUtility.initBindView(this);

    }

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    protected void startWebView(String title, String result) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("titleString", title);
        if (result != null) {
            intent.putExtra("resultString", result);
        }
        startActivity(intent);
    }

    private Toast toast;
    private TextView toastView;

    /**
     * 显示信息
     *
     * @param msg
     */
    public void showMessage(String msg) {
        if (toastView == null) {
            toastView = new TextView(this);
            int padding = FormatUtil.dip2px(this, 12);
            toastView.setBackgroundResource(R.drawable.borderline_toast);
            toastView.setTextColor(0xffffffff);
            toastView.setPadding(padding, padding, padding, padding);
            toastView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            toastView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        toastView.setText(msg);
        if (toast == null) {
            toast = Toast.makeText(SptApplication.context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setView(toastView);
        toast.show();
    }

    /**
     * 显示异常消息
     *
     * @param e 错误信息
     */
    public void showExceptionMessage(SystemException e) {
        if (e == null) {
            return;
        }
        int errorId = e.getErrorId();
        if (errorId == CommonErrorId.ERROR_REMOTE_CALL_TIMEOUT) // 远程调用超时
        {
            ToastHelper.showMessage("请您检查网络连接!");
        } else {
            ToastHelper.showMessage(ErrorCodeUtility.getMessage(errorId));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (DictionaryUtility.getDicMap() == null || DictionaryUtility.getDicMap().isEmpty()) {
            startActivity(SplashActivity.class);
            finish();
        }
    }

    protected void showErrorUserPermissionMsg() {
        if (LoginUserContext.isAnonymous()) {
            ToastHelper.showMessage("您还未登录，请先登录");
        } else if (!LoginUserContext.getLoginUserDto().isCompanyUser()) {
            ToastHelper.showMessage("个人帐户不允许操作，请升级企业帐户");
        } else if (!LoginUserContext.hasBindAccount()) {
            ToastHelper.showMessage("您还未绑定资金账户，请先绑定资金账号");
        } else {

        }
    }

    protected void registerAllCheckFields() {
        InjectionHelper.registerAllCheckFields(this, this.validatorExecutor);
    }

    protected boolean doAllValidation() {
        return this.validatorExecutor == null || this.validatorExecutor.doAllValidation(this.getApplicationContext());
    }

    protected boolean doValidation(String var1) {
        return this.validatorExecutor == null || this.validatorExecutor.doValidation(this.getApplicationContext(), var1);
    }

    protected boolean doValidation(String[] var1) {
        return this.validatorExecutor == null || this.validatorExecutor.doValidation(this.getApplicationContext(), var1);
    }
}
