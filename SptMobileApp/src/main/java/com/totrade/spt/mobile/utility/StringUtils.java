package com.totrade.spt.mobile.utility;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.autrade.stage.utility.StringUtility;

public class StringUtils {


    /**
     * 将某些字符串替换为空字符串
     *
     * @param str
     * @param ss
     * @return
     */
    public static String replaceToEmpty(String str, char[] ss) {
        for (char s : ss) {
            str.replace(s, ' ');
        }
        return str;
    }

    /**
     * 切割字符串操作类
     *
     * @param s 待切割字符串
     * @param c 切割规则字符串
     * @return 返回值数组中，确保每一个位置都没有空值
     */
    public static List<String> split(String s, String c) {
        List<String> ls = new ArrayList<>();
        String[] ss;
        if (s.contains(c)) {
            ss = s.split(c);
        } else {
            ss = new String[1];
            ss[0] = s;
        }
        //处理空串
        for (int i = 0; i < ss.length; i++) {
            if (!StringUtility.isNullOrEmpty(ss[i]))
                ls.add(ss[i]);
        }
        return ls;
    }

    /**
     * 用于反转map，目前只适用于获取产品和team关系时
     *
     * @param map
     * @return
     */
    public static Map<String, List<String>> transposeMapForProductType(Map<String, String> map) {

        Map<String, List<String>> team = new HashMap<>();
        //初始化team的key集合
        List<String> keyList = new ArrayList<String>();
        for (String value : map.values()) {
            keyList.addAll(split(value, ","));
        }
        for (String key : keyList) {
            team.put(key, new ArrayList<String>());
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            for (String key : keyList) {
                List<String> itemList = split(entry.getValue(), ",");
                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).equals(key)) {
                        //这里在往集合里加入数据时，判断原集合内部是否有相同的数据
                        List<String> itemValue = team.get(key);
                        boolean bo = false;
                        for (int j = 0; j < itemValue.size(); j++) {
                            if (itemValue.get(j).equals(entry.getKey())) {
                                bo = true;
                            }
                        }
                        if (!bo) {
                            team.get(key).add(entry.getKey());
                        }
                    }
                }
            }
        }
        return team;
    }


    public static String parseMoney(BigDecimal bd) {
        return parseMoney(null, bd);
    }

    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    /**
     * BigDecimal按三位分组
     *
     * @param pattern .
     * @param bd      .
     * @return .
     */
    public static String parseMoney(String pattern, BigDecimal bd) {
        if (bd == null) return null;
        if (TextUtils.isEmpty(pattern)) {
            return df.format(bd);
        } else {
            return new DecimalFormat(pattern).format(bd);
        }
    }

    /**
     * 将double转为数值，并最多保留num位小数。例如当num为2时，1.268为1.27，1.2仍为1.2；1仍为1，而非1.00;100.00则返回100。
     *
     * @param d
     * @param num 小数位数
     * @return
     */
    public static String double2String(double d, int num) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(num);//保留两位小数
        nf.setGroupingUsed(false);//去掉数值中的千位分隔符

        String temp = nf.format(d);
        if (temp.contains(".")) {
            String s1 = temp.split("\\.")[0];
            String s2 = temp.split("\\.")[1];
            for (int i = s2.length(); i > 0; --i) {
                if (!s2.substring(i - 1, i).equals("0")) {
                    return s1 + "." + s2.substring(0, i);
                }
            }
            return s1;
        }
        return temp;
    }

    /**
     * 将double转为数值，并最多保留num位小数。
     *
     * @param d
     * @param num      小数个数
     * @param defValue 默认值。当d为null时，返回该值。
     * @return
     */
    public static String double2String(Double d, int num, String defValue) {
        if (d == null) {
            return defValue;
        }

        return double2String(d, num);
    }

    /**
     * 将double 除以一个数据转为数值，并最多保留num位小数
     *
     * @param d
     * @param num      小数个数
     * @param defValue 默认值。当d为null时，返回该值。
     * @return
     */
    public static String double2String(Double d, int divisor, int num, String defValue) {
        if (d == null) {
            return defValue;
        }

        if (divisor != 0) {
            d = d/divisor;
        }
        return double2String(d, num);
    }

}
