package com.totrade.spt.mobile.utility;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.totrade.spt.mobile.common.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyUtility {
    /**
     * 通知栏点击关闭
     */
    public static void cancelNoti(Context context) {
        cancelNoti(context, null, 0);
    }

    public static void cancelIM(Context context, String tag) {
        cancelNoti(context, tag, AppConstant.NIM_NOTIFY_ID);
    }

    public static void cancelJpush(Context context) {
        int id = ((Activity) context).getIntent().getIntExtra(AppConstant.NOTIFY_ID, 0);
        String tag = ((Activity) context).getIntent().getStringExtra(AppConstant.NOTIFY_TAG);
        cancelNoti(context, tag, id);
    }

    public static void cancelNoti(Context context, String tag, int id) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(tag, id);
    }

    public static void cancelAll(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    /**
     * @return true 字典值已被销毁
     */
    public static boolean isDictionaryValueExist() {
        if (DictionaryUtility.getDicMap() == null) {
            return true;
        }
        return DictionaryUtility.getDicMap().isEmpty();

    }

    /**
     * 获取指定key的值
     *
     * @param receiveNotify 被解析的Json
     * @param key           要查找value的键
     * @return
     */
    public static String getValue(String receiveNotify, String key) {
        if (TextUtils.isEmpty(receiveNotify)) {
            return null;
        }
        try {
            JSONObject jobj = new JSONObject(receiveNotify);
            return jobj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
