package com.totrade.spt.mobile.utility;

import com.autrade.stage.constants.CommonErrorId;
import com.autrade.stage.exception.SystemException;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.view.customize.ToastForSpt;

/**
*
* toast显示
* @author huangxy
* @date 2017/4/7
*
*/
public class ToastHelper {
    public static void showMessage(CharSequence text) {
        ToastForSpt.makeText(SptApplication.context, text, ToastForSpt.LENGTH_SHORT).show();
    }

    public static void showExceptionMessage(SystemException e) {
        if (e == null) {
            return;
        }
        int errorId = e.getErrorId();
        if (errorId == CommonErrorId.ERROR_REMOTE_CALL_TIMEOUT) // 远程调用超时
        {
            showMessage("请您检查网络连接!");
        } else {
            showMessage(ErrorCodeUtility.getMessage(errorId));
        }

    }
}
