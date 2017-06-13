package com.totrade.spt.mobile.ui.maintrade;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.ui.login.RegistActivity;
import com.totrade.spt.mobile.ui.login.fragment.CompanyBindFragment;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.customize.CustomDialog;

/**
 * 权限检查
 */
public class PowerHelper {
    private PowerHelper() {
    }

    public static PowerHelper i() {
        return new PowerHelper();
    }

    /**
     * 检查权限
     *
     * @return
     */
    public boolean hasPower(Context context) {
        if (LoginUserContext.isAnonymous()) {
            IntentUtils.startActivity(context, LoginActivity.class);
            return false;
        } else if (LoginUserContext.isFreeman()) {
            bindCompanyDialog(context);
//            ToastHelper.showMessage("您还未绑定企业，不能使用相关服务。");
            return false;
        } else if (LoginUserContext.isFundAccount()) {
            ToastHelper.showMessage("您不是管理者/交易账号");
            return false;
        } else if (LoginUserContext.isDeliver()) {
            ToastHelper.showMessage("您不是管理者/交易账号");
            return false;
        }
        return true;
    }

    private void bindCompanyDialog(final Context context) {
        // 构造对话框
        CustomDialog.Builder builder = new CustomDialog.Builder(context, "您还未升级至企业账号， 不能\n使用相关服务");
        builder.setPositiveButton("稍后再说", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("去升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, RegistActivity.class);
                intent.putExtra(CompanyBindFragment.class.getName(), CompanyBindFragment.class.getName());
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();
        builder.positiveButton.setSelected(false);
        builder.negativeButton.setSelected(true);
    }

}
