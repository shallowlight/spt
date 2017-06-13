package com.totrade.spt.mobile.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.google.gson.reflect.TypeToken;
import com.totrade.spt.mobile.app.SptApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 我关注的询价产品上下文
 *
 * @author wangyilin
 */
public class FocusContext {
    private static volatile boolean focusValid = true; //启用/禁用
    private static volatile List<String> focusList; //关注询价产品列表

    private FocusContext() {
        load();
    }

    /**
     * 更新关注设置
     *
     * @param valid
     * @param focusList
     */
    public static void update(Context context, boolean valid, List<String> focusList) {
        FocusContext.focusValid = valid;
        FocusContext.focusList = focusList;
        save(context);
    }

    /**
     * 更新关注设置 内容
     *
     * @param focusList
     */
    public static void update(Context context, List<String> focusList) {
        FocusContext.focusList = focusList;
        save(context);
    }

    /**
     * 更新启用/禁用标志
     *
     * @param valid
     */
    public static void update(Context context, boolean valid) {
        focusValid = valid;
        save(context);
    }

    /**
     * 是否启用
     *
     * @return
     */
    public static boolean isValid() {
        return focusValid;
    }

    /**
     * 保存设置
     */
    public static void save(Context context) {
        SharedPreferences pref = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = pref.edit();
        editor.putString("focusValid", String.valueOf(focusValid));
        editor.putString("focusList", JsonUtility.list2JSONString(focusList));
        editor.commit();
    }

    /**
     * 装载设置
     */
    public static List<String> load() {
        SharedPreferences pref = getPref();
        String focusValidStr = pref.getString("focusValid", "true");
        String focusListStr = pref.getString("focusList", null);
        focusValid = Boolean.valueOf(focusValidStr);
        if (StringUtility.isNullOrEmpty(focusListStr)) {
            focusList = new ArrayList<>();
        } else {
            try {
                focusList = JsonUtility.toJavaObjectArray(focusListStr, new TypeToken<List<String>>() {
                }.getType());
            } catch (Exception e) {
                focusList = new ArrayList<>();
            }
        }
        return focusList;
    }

    /**
     * 获取SharedPreferences
     *
     * @return
     */
    private static SharedPreferences getPref( ) {
        return SptApplication.context.getSharedPreferences("login", Context.MODE_PRIVATE);
    }
}
