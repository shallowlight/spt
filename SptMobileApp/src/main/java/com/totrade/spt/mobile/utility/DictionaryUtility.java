package com.totrade.spt.mobile.utility;

import android.content.Context;

import com.autrade.spt.common.entity.TblSptDictionaryEntity;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.ConstantArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * 字典工具类
 * @Deprecated Use DictionaryTools instead
 */
@Deprecated
public final class DictionaryUtility {
    private static Map<String, Map<String, TblSptDictionaryEntity>> dicMap;
    private static String DICT = "dict";
    private static Set<String> sptDictionary;

    static {
        dicMap = new HashMap<>();
    }

    private DictionaryUtility() {
    }

    public static Map<String, Map<String, TblSptDictionaryEntity>> getDicMap() {
        return dicMap;
    }

    public static void setDicMap(Map<String, Map<String, TblSptDictionaryEntity>> dicMap) {
        DictionaryUtility.dicMap = dicMap;
    }

    /**
     * 初始化：读入所有的字典条目
     *
     * @param context
     * @throws ExecutionException
     */
    @Deprecated
    public static void init(Context context, List<TblSptDictionaryEntity> entityList) {
        DictionaryTools.i().initDict(entityList);
//        SharedPreferences sp = context.getSharedPreferences(DICT, Context.MODE_PRIVATE);
//        sptDictionary = sp.getStringSet(DICT, new HashSet<String>());
//        if (entityList == null) {
//            Type type = new TypeToken<TblSptDictionaryEntity>() {
//            }.getType();
//            for (String entity : sptDictionary) {
//                TblSptDictionaryEntity tblSptDictionaryEntity = JsonUtility.toJavaObject(entity, type);
//                initDict(tblSptDictionaryEntity);
//            }
//        } else {
//            for (TblSptDictionaryEntity entity : entityList) {
//
//                initDict(entity);
//            }
//        }
//        sp.edit().putStringSet(DICT, sptDictionary).commit();
    }

    /**
     * 初始化：读入所有的字典条目到缓存
     *
     * @throws ExecutionException
     */
    private static void initDict(TblSptDictionaryEntity entity) {
        Map<String, TblSptDictionaryEntity> element;
        String category = entity.getDictCategory();
        if (!dicMap.containsKey(category)) {
            element = new HashMap<>();
            element.put(entity.getDictKey(), entity);
            dicMap.put(category, element);
        } else {
            element = dicMap.get(category);
            element.put(entity.getDictKey(), entity);
        }
        sptDictionary.add(entity.toString());
    }

    /**
     * 根据类别键和标识键获取条目的中文名称 例如，传入： category = "priceUnit", key = "A", 返回："美元"
     *
     * @param key      值
     * @param category 字典类别
     * @Deprecated Use  DictionaryTools.i().getValue(category, key)  instead
     */
    @Deprecated
    public static String getValue(String category, String key) {
        if (StringUtility.isNullOrEmpty(key) || StringUtility.isNullOrEmpty(category)) {
            return null;
        }
        if (category.equals(AppConstant.DICT_RUNNING_TYPE)
                || category.equals(AppConstant.DICT_TRADEMODE_BUYSELL)) {
            return getValueByApp(category, key);
        }

        return DictionaryTools.i().getValue(category, key);
    }

    /**
     * 根据类别键和标识键获取条目的中文名称 例如，传入： category = "priceUnit",  返回：List<String> "
     * @param category 字典类别
     */
    public static List<String> getValueList(String category) {
        List<String> list = new ArrayList<>();
        if (StringUtility.isNullOrEmpty(category)) {
            return null;
        }
        List<TblSptDictionaryEntity> cateList = getListByCategory(category);
        for (TblSptDictionaryEntity entity : cateList) {
            list.add(entity.getDictValue());
        }
        return list;
    }


    public static String getValueByApp(String category, String key) {
        if (StringUtility.isNullOrEmpty(category) || StringUtility.isNullOrEmpty(key)) {
            return null;
        }
        String[][] keyArr = null;
        if (category.equals(AppConstant.DICT_RUNNING_TYPE)) {
            keyArr = ConstantArray.LOAN_RUNNINGTYPR;
        } else if (category.equals(AppConstant.DICT_TRADEMODE_BUYSELL)) {
            keyArr = ConstantArray.TRADEMODE_BUYSELL;
        }
        if (keyArr == null) {
            return null;
        }
        for (String[] ss : keyArr) {
            if (ss[0].equals(key)) {
                return ss[1];
            }
        }
        return null;
    }


    /**
     * 根据类别键获取所有字典表项
     *
     * @param category 类别键
     * @return
     * @throws ExecutionException
     * @Deprecated Use  DictionaryTools.i().getConfigList(category)  instead
     */
    @Deprecated
    public static List<TblSptDictionaryEntity> getListByCategory(String category) {
        return DictionaryTools.i().getConfigList(category);
//        List<TblSptDictionaryEntity> rtn = new ArrayList<TblSptDictionaryEntity>();
//
//        Map<String, TblSptDictionaryEntity> map = dicMap.get(category);
//        if (map != null) {
//            Set<String> keySet = map.keySet();
//            for (String key : keySet) {
//                rtn.add(map.get(key));
//            }
//        }
//        return rtn;
    }

    /**
     * 根据类别键和标识键获取条目的英文名称 例如，传入： category = "priceUnit", key = "A", 返回："USD"
     *
     * @param key
     * @param category
     * @Deprecated   Use  DictionaryTools.i().getValue(category, key, true)  instead
     */
    @Deprecated
    public static String getValueEn(String category, String key) {
        return DictionaryTools.i().getValue(category, key, true);
//        return getValueInternal(category, key, true);
    }

    /**
     * 根据类别键,标识键和语言标识符，获取条目的中文或者英文名称
     *
     * @param category
     * @param key
     * @param languageIndicator 语言标识符: SptConstant.LAN_CHINESE或SptConstant.LAN_ENGLISH
     * @return
     * @throws ExecutionException
     */
//    public static String getValueAssignLanguage(String category, String key,
//                                                String languageIndicator) {
//        if (languageIndicator.equals(SptConstant.LAN_CHINESE)) // 中文
//        {
//            return getValue(category, key);
//        } else
//        // 英文
//        {
//            return getValueEn(category, key);
//        }
//    }

//    private static String getValueInternal(String category, String key, boolean ifEnglish) {
//        try {
//            Map<String, TblSptDictionaryEntity> element = dicMap.get(category);
//            if (element == null) {
//                return null;
//            } else {
//                TblSptDictionaryEntity entity = element.get(key);
//                if (entity == null) {
//                    return null;
//                } else {
//                    if (!ifEnglish) {
//                        return entity.getDictValue();
//                    } else {
//                        return entity.getDictValueEn();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            return "";
//        }
//    }
}
