package com.totrade.spt.mobile.utility;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import com.totrade.spt.mobile.app.SptApplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class SysInfoUtil {
    public static final String getOsInfo() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static final String getPhoneModelWithManufacturer() {
        return Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }

    public static final String getPhoneMode() {
        return Build.MODEL;
    }

    public static final boolean isAppOnForeground(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> list = manager
                .getRunningAppProcesses();
        if (list == null)
            return false;
        boolean ret = false;
        Iterator<ActivityManager.RunningAppProcessInfo> it = list.iterator();
        while (it.hasNext()) {
            ActivityManager.RunningAppProcessInfo appInfo = it.next();
            if (appInfo.processName.equals(packageName)
                    && appInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public static final boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    public static boolean stackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<RunningTaskInfo> recentTaskInfos = manager.getRunningTasks(1);
        if (recentTaskInfos != null && recentTaskInfos.size() > 0) {
            RunningTaskInfo taskInfo = recentTaskInfos.get(0);
            if (taskInfo.baseActivity.getPackageName().equals(packageName) && taskInfo.numActivities > 1) {
                return true;
            }
        }

        return false;
    }

    public static final boolean mayOnEmulator(Context context) {
        if (mayOnEmulatorViaBuild()) {
            return true;
        }

        if (mayOnEmulatorViaTelephonyDeviceId(context)) {
            return true;
        }

        return mayOnEmulatorViaQEMU(context);

    }

    @SuppressLint("DefaultLocale")
    private static final boolean mayOnEmulatorViaBuild() {
        /**
         * ro.product.model likes sdk
         */
        if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().contains("sdk")) {
            return true;
        }

        /**
         * ro.product.manufacturer likes unknown
         */
        if (!TextUtils.isEmpty(Build.MANUFACTURER) && Build.MANUFACTURER.toLowerCase().contains("unknown")) {
            return true;
        }

        /**
         * ro.product.device likes generic
         */
        return !TextUtils.isEmpty(Build.DEVICE) && Build.DEVICE.toLowerCase().contains("generic");

    }

    private static final boolean mayOnEmulatorViaTelephonyDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return false;
        }

        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            return false;
        }

        /**
         * device id of telephony likes '0*'
         */
        for (int i = 0; i < deviceId.length(); i++) {
            if (deviceId.charAt(i) != '0') {
                return false;
            }
        }

        return true;
    }

    private static final boolean mayOnEmulatorViaQEMU(Context context) {
        String qemu = getProp(context, "ro.kernel.qemu");
        return "1".equals(qemu);
    }

    private static final String getProp(Context context, String property) {
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get", String.class);
            Object[] params = new Object[1];
            params[0] = property;
            return (String) method.invoke(SystemProperties, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static DisplayMetrics getScreenMetrics() {
        WindowManager wm = (WindowManager) SptApplication.context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getScreenWidth() {
        return getScreenMetrics().widthPixels;
    }

    /**
     * 是否为当前界面
     * 当前界面
     *
     * @return true 是当前界面
     */
    public static boolean isRunningActivity(Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) SptApplication.context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return cls.getName().equals(runningActivity);
    }

    public static boolean isAppRunning() {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) SptApplication.context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(SptApplication.context.getPackageName()) && info.baseActivity.getPackageName().equals(SptApplication.context.getPackageName())) {
                isAppRunning = true;
                // find it, break
                break;
            }
        }
        return isAppRunning;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = SptApplication.context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = SptApplication.context.getResources().getDimensionPixelSize(resourceId);
            }
        }

        return result;
    }
}
