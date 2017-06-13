package com.totrade.spt.mobile.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.view.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

public final class FormatUtil {

    private FormatUtil() {

    }

    /**
     * dip转成像素
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 设置同一个字符串不同字体不同大小颜色，setText()时调用
     *
     * @param str
     * @param unit
     * @return
     */
    public static SpannableStringBuilder formatSizeColor(Context context, String str, String unit) {
        String value = str + unit;
        SpannableStringBuilder ssb = new SpannableStringBuilder(value);

        ssb.setSpan(new AbsoluteSizeSpan(FormatUtil.sp2px(context, 9), true), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new AbsoluteSizeSpan(FormatUtil.sp2px(context, 6), true), str.length(), value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        CharacterStyle charaStyle = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
        ssb.setSpan(charaStyle, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        CharacterStyle charaStyle2 = new ForegroundColorSpan(context.getResources().getColor(R.color.gray));
        ssb.setSpan(charaStyle2, str.length(), value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb;
    }

    public static Date calendar2Date(Calendar calendar) {
        return calendar.getTime();
    }

    public static String calendar2String(Calendar calendar, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String dateStr = sdf.format(calendar.getTime());
        return dateStr;
    }

    public static Calendar date2Calendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String date2String(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);

    }

    public static Calendar string2Calendar(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Can't parse " + str + " using " + format);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date string2Date(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Can't parse " + str + " using " + format);
        }
    }

    public static Date dateDayOfLastMillisecond(Date date) {
        Calendar c = date2Calendar(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return calendar2Date(c);
    }

    public static Date dateDayOfFirstMillisecond(Date date) {
        Calendar c = date2Calendar(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return calendar2Date(c);
    }

    /**
     * 当前时间是否在指定时间段
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean isFixedTime() {
        boolean b = false;
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
        long time = new Date().getTime();
        String format = sdf1.format(time);
        String start = " 8/00/00";
        String end = " 22/00/00";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");

            if (time > sdf.parse(format + start).getTime() && time < sdf.parse(format + end).getTime()) {
                b = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 交货期年月String格式与上下转换成当月15日或者最后一天的日期格式
     *
     * @param dateString 日期字符串
     * @return 日期
     */
    public static Date disp2DeliveryDate(@NonNull String dateString) {
        Date date;
        if (dateString.endsWith("上") || dateString.endsWith("下")) {
            String dateFormat = AppConstant.DATEFORMAT2;
            // yyyy/MM 上
            Calendar calendar = string2Calendar(dateString.substring(0, dateFormat.length()), dateFormat);
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, dateString.endsWith("上") ? 15 : lastDay);

            date = calendar2Date(calendar);
        } else if (dateString.endsWith("前")) {
            // yyyy/MM/dd
            String dateFormat = AppConstant.DATEFORMAT;
            date = string2Date(dateString.substring(0, dateFormat.length()), dateFormat);
        } else {
            // yyyy/MM/dd
            date = string2Date(dateString, AppConstant.DATEFORMAT);
        }
        //设置为23:59:59 999;
        return dateDayOfLastMillisecond(date);
    }

    /**
     * 特定产品的交货期显示     分为上中下
     *
     * @param dateString
     * @return
     */
    public static Date disp2DeliveryDateSpeicalProduct(String dateString) {
        String dateFormat = AppConstant.DATEFORMAT2;
        Calendar calendar = string2Calendar(dateString.substring(0, dateFormat.length()), dateFormat);
        int lastDay = 10;
        if (dateString.endsWith("上")) {
            lastDay = 10;
        } else if (dateString.endsWith("中")) {
            lastDay = 20;
        } else if (dateString.endsWith("下")) {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);

        return calendar2Date(calendar);
    }

    public static String getTimeShowString(long milliseconds, boolean abbreviate) {
        String dataString = "";
        String timeStringBy24 = "";

        Date currentTime = new Date(milliseconds);
        Date today = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date todaybegin = todayStart.getTime();
        Date yesterdaybegin = new Date(todaybegin.getTime() - 3600 * 24 * 1000);
        Date preyesterday = new Date(yesterdaybegin.getTime() - 3600 * 24 * 1000);

        if (!currentTime.before(todaybegin)) {
            dataString = "今天";
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = "昨天";
        } else if (!currentTime.before(preyesterday)) {
            dataString = "前天";
        } else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime);
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dataString = dateformatter.format(currentTime);
        }

        SimpleDateFormat timeformatter24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);

        if (abbreviate) {
            if (!currentTime.before(todaybegin)) {
                return getTodayTimeBucket(currentTime);
            } else {
                return dataString;
            }
        } else {
            return dataString + " " + timeStringBy24;
        }
    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName =
                {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 根据不同时间段，显示不同时间
     *
     * @param date
     * @return
     */
    public static String getTodayTimeBucket(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat timeformatter0to11 = new SimpleDateFormat("KK:mm", Locale.getDefault());
        SimpleDateFormat timeformatter1to12 = new SimpleDateFormat("hh:mm", Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 5) {
            return "凌晨 " + timeformatter0to11.format(date);
        } else if (hour >= 5 && hour < 12) {
            return "上午 " + timeformatter0to11.format(date);
        } else if (hour >= 12 && hour < 18) {
            return "下午 " + timeformatter1to12.format(date);
        } else if (hour >= 18 && hour < 24) {
            return "晚上 " + timeformatter1to12.format(date);
        }
        return "";
    }

    public static String long2Time(long duration) {

        long d = duration / 1000;

        if (d < 1) {
            return "0″";
        } else if (d <= 60) {
            return String.valueOf(d) + "″";
        } else if (d > 60 && d <= 3600) {
            long dd = d / 60;
            long ddd = d % 60;

            return String.valueOf(dd) + "′" + ddd + "″";
        }

        return "";
    }

    /**
     * IM设置禁言时调用 date转天，时，分，秒，
     *
     * @param date
     * @return
     */
    public static String countDown(Date date) {
        StringBuffer result = new StringBuffer();

        if (date == null) {
            return null;
        } else if (date.getTime() == 1) {
            return "1";
        }

        double time = date.getTime();
        double minute = 1000 * 60;
        double Hour = minute * 60;
        double day = Hour * 24;

        int d = (int) (time / day);
        if (d > 0) {
            result.append(String.valueOf(d) + "天");
        }

        int H = (int) ((time - d * day) / Hour);
        if (H > 0) {
            result.append(String.valueOf(H) + "小时");
        }

        int m = (int) ((time - d * day - H * Hour) / minute);
        if (m > 0) {
            result.append(String.valueOf(m) + "分钟");
        }

        return result.toString();
    }

    /**
     * 字符串数字取整
     *
     * @param str
     * @return
     */
    public static String takeRound(String str) {
        BigDecimal big = new BigDecimal(str);
        DecimalFormat format = new DecimalFormat("#0.#######");
        return format.format(big);
    }

    /**
     * counter ASCII character as one, otherwise two
     *
     * @param str
     * @return count
     */
    public static int counterChars(String str) {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }

    public static DecimalFormat format = new DecimalFormat("#0.#######");
    public static DecimalFormat format2 = new DecimalFormat("#0.00");

}
