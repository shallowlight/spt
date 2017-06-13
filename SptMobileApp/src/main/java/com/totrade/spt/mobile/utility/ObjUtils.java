package com.totrade.spt.mobile.utility;

import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Administrator on 2016/11/29.
 */
public class ObjUtils {

    /**
     * 比较两个List元素是否相同，与顺序无关
     *
     * @param a
     * @param b
     * @return true相同，false不相同
     */
    public static boolean equal(List<String> a, List<String> b) {
        boolean bo = false;
        if (null == a || null == b)
            bo = false;
        if (a.containsAll(b))
            bo = true;
        return bo;
    }

    /**
     * 去掉List中的重复数据
     *
     * @param list
     * @return
     */
    public static List<String> clearDuplicateList(List<String> list) {
        List<String> newArray = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            if (!newArray.contains(s)) {
                newArray.add(s);
            }
        }
        return newArray;
    }

    /**
     * 转换map,IM云信群组通用
     *
     * @return
     */
    public static Map<String, List<String>> converMapForProductType(Map<String, String> map) {
        Map<String, List<String>> teamMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            teamMap.put(entry.getKey(), StringUtils.split(entry.getValue(), ","));
        }
        return teamMap;
    }

    /**
     * key value反转
     *
     * @param map
     * @return
     */
    public static Map<String, List<String>> transposeMapForProductType(Map<String, List<String>> map) {

        Map<String, List<String>> teamMap = new HashMap<>();
        List<String>[] values = new List[map.size()];
        String[] keys = new String[map.size()];

        int i = 0;
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            values[i] = entry.getValue();
            keys[i] = entry.getKey();
            teamMap.put(new Gson().toJson(values[i]), new ArrayList<String>());
            i++;
        }

        //遍历这个数组，取出相同
        for (int j = 0; j < values.length; j++) {
            if (null == teamMap.get(values[j])) {
                List<String> jL = new ArrayList<>();
                jL.add(keys[j]);
                teamMap.put(new Gson().toJson(values[j]), jL);
            } else {
                teamMap.get(new Gson().toJson(values[j])).add(keys[j]);
            }

            for (int k = values.length - 1; k > j; k--) {
                if (equal(values[k], values[j])) {
                    teamMap.get(values[j]).add(keys[k]);
                }
            }
        }

        //新的map中可能会有重复的值，清除掉
        for (Map.Entry<String, List<String>> entry : teamMap.entrySet()) {
            entry.setValue(clearDuplicateList(entry.getValue()));
        }
        return teamMap;
    }

    /**
     * 根据Map<String,List<String>值中某一个值返回这个map的key
     *
     * @param map
     * @param valueIndex
     * @return
     */
    public static String getKeyFromMap(Map<String, List<String>> map, String valueIndex) {
        String key = "";
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            for (String s : entry.getValue()) {
                if (s.equals(valueIndex)) {
                    key = entry.getKey();
                }
            }
        }
        return key;
    }

    public static <T> T initObjectForZone(T o) throws Exception {
        Class oClass = (Class) o.getClass();
        Field[] fs = oClass.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true);
            Object v;
            v = f.get(o);
            LogUtils.e("Obj Other Type\n", "name:" + f.getName() + "\t value = " + v);
            if (null == v) {
                if (f.getType().getName().endsWith("BigDecimal")) {
                    f.set(o, new BigDecimal("0.00"));
                } else if (f.getType().getName().endsWith("Date")) {
                    f.set(o, new Date());
                } else {
                    f.set(o, f.getType().getClass().newInstance());
                    LogUtils.e("Obj Other Type:", f.getType() + "\t");
                }
            }
        }
        return o;
    }

    /**
     * 判断对象是否为空（包括一些非null但是无数据的集合）
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SparseArray && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseBooleanArray && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseIntArray && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (obj instanceof SparseLongArray && ((SparseLongArray) obj).size() == 0) {
                return true;
            }
        }
        return false;
    }

}
