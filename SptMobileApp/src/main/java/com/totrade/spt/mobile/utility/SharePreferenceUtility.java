package com.totrade.spt.mobile.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharePreferenceUtility {
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "passWord";
    public static final String USERID = "userId";
    public static final String COMPANYTAG = "companyTag";
    public static final String IS_ALLOW_PUSH_NOT_SUPPLIERS = "isAllowPushNotSuppliers";
    public static final String PUSH_ON_TIME = "pushOnTime";
    public static final String PUSH_TRADE_NOTICE = "push_trade_notice";
    public static final String PUSH_TRADE_FOCUS = "push_trade_focus";
    public static final String PUSH_TONGTONG = "push_tongtong";
    public static final String IS_FIRST = "isFirst";
    public static final String NIM_ACCID = "nim_accid";
    public static final String NIM_TOKEN = "nim_token";
    public static final String NIM_APPKEY = "nim_appkey";
    public static final String PRODUCTTYPE = "productType";
    public static final String PUSH_PRODUCTSET = "pushProductType";
    public static final String NET_IP = "netIp";
    public static final String NET_PORT = "netPort";
    public static final String REGISTER_PERSONAL = "registerPersonal";

    public static void spSaveIOSNetInfo(Context context, String ip, String port) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(NET_IP, ip);
        editor.putString(NET_PORT, port);
        editor.commit();
    }

    public static String spGetOutIOSNetIp(Context context) {
        return spGetOut(context, NET_IP, "139.196.95.121");
    }

    public static String spGetOutIOSNetPort(Context context) {
        return spGetOut(context, NET_PORT, "8090");
    }

    public static void spSave(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void spSave(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void spSave(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void spSaveSet(Context context, String key, Set<String> value) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static String spGetOut(Context context, String key, String defValue) {
        return getPref(context).getString(key, defValue);
    }

    public static int spGetOut(Context context, String key, int defValue) {
        return getPref(context).getInt(key, defValue);
    }

    public static boolean spGetOut(Context context, String key, boolean defValue) {
        return getPref(context).getBoolean(key, defValue);
    }

    public static Set<String> spGetOutSet(Context context, String key, Set<String> set) {
        return getPref(context).getStringSet(PUSH_PRODUCTSET, set);
    }

    public static SharedPreferences getPref(Context context) {
        return context.getSharedPreferences("login", Context.MODE_PRIVATE);
    }
}
