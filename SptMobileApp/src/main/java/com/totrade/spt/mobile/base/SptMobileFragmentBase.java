package com.totrade.spt.mobile.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.autrade.stage.constants.CommonErrorId;
import com.autrade.stage.droid.helper.InjectionHelper;
import com.autrade.stage.droid.validator.ValidatorExecutor;
import com.autrade.stage.exception.SystemException;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.utility.ErrorCodeUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;

import java.text.DecimalFormat;

/**
 * 常用Fragment基类
 * 注解对象，弹出提示
 */
public abstract class SptMobileFragmentBase extends BaseFragment implements ActivityConstant {
    // 领域名称常量
    protected DecimalFormat format = new DecimalFormat("#0.#######");
    protected DecimalFormat format2 = new DecimalFormat("#0.00");
    // 验证执行器
    protected ValidatorExecutor validatorExecutor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validatorExecutor = new ValidatorExecutor();
        // 注入所有@Injection字段
        InjectionHelper.injectAllInjectionFields(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //绑定所有@BindViewId控件Id
        AnnotateUtility.bindViewFormId(this, view);
    }

    /**
     * 注册所有@CheckField字段
     */
    protected void registerAllCheckFields() {
        InjectionHelper.registerAllCheckFields(this, validatorExecutor);
    }

    /**
     * 执行所有验证
     *
     * @return 验证结果
     */
    protected boolean doAllValidation() {
        return validatorExecutor != null && validatorExecutor.doAllValidation(this);
    }

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    /**
     * 根据ID执行验证
     *
     * @param id
     * @return
     */
    protected boolean doValidation(String id) {
        if (validatorExecutor != null) {
            return validatorExecutor.doValidation(getActivity(), id);
        } else {
            return true;
        }
    }


    private static Toast toast;
    private TextView toastView;

    /**
     * 显示信息
     *
     * @param msg Toast信息
     * @Deprecated Use ToastHelper.showMessage(msg) instead
     */
    @Deprecated
    protected void showMessage(String msg) {
        ToastHelper.showMessage(msg);
    }


    /**
     * 显示异常消息
     *
     * @param e 异常
     */
    protected void showExceptionMessage(SystemException e) {
        int errorId = e.getErrorId();
        if (errorId == CommonErrorId.ERROR_REMOTE_CALL_TIMEOUT) // 远程调用超时
        {
            ToastHelper.showMessage("请您检查网络连接!");
        } else {
            ToastHelper.showMessage(ErrorCodeUtility.getMessage(errorId));
        }
    }

    protected void showErrorUserPermissionMsg() {
        if (LoginUserContext.isAnonymous()) {
            ToastHelper.showMessage("您还未登录，请先登录");
        } else if (!LoginUserContext.getLoginUserDto().isCompanyUser()) {
            ToastHelper.showMessage("个人帐户不允许操作，请升级企业帐户");
        } else if (!LoginUserContext.hasBindAccount()) {
            ToastHelper.showMessage("您还未绑定资金账户，请先绑定资金账号");
        }
    }

}
