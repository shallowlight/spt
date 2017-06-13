package com.totrade.spt.mobile.utility;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.totrade.spt.mobile.app.SptApplication;

/**
 *
 */
public class LogUtils {

    public static final boolean DEBUG = (SptApplication.context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

    public static void v(String tag, String message) {
        if (DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Exception e) {
        if (DEBUG) {
            Log.e(tag, message, e);
        }
    }

    public static void log(String log) {
        if (DEBUG) {
            FileUtils.saveCache(log, "sptlog", true);
        }
    }
}
