package com.totrade.spt.mobile.utility;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.totrade.spt.mobile.app.SptApplication;

/**
 * Created by Administrator on 2016/12/23.
 */
public class KeyBoardUtils {
    private KeyBoardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 会自动判断输入法是否打开，如果打开则动态隐藏软键盘
     * @param activity activity
     */
    public static void hideSoftInput(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        boolean isOpen = imm.isActive();
//        if (isOpen) {
//            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
//                    (activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }

        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && (activity.getCurrentFocus() != null)) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 动态显示软键盘
     * @param edit 输入框
     */
    public static void showSoftInput(EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) SptApplication.context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit, 0);
    }

    /**
     * 切换键盘显示与否状态
     */
    public static void toggleSoftInput() {
        InputMethodManager imm = (InputMethodManager) SptApplication.context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
