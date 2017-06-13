package com.totrade.spt.mobile.utility;


import com.autrade.spt.common.entity.TblSptDictionaryEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author huangxy
 * @date 2017/5/17
 *
 * 字典Key Value定义 见实体类 TblSptDictionaryEntity
 */
public final class DictionaryTools {

    private HashMap dictionary;

    private DictionaryTools() {
        dictionary = new HashMap<>();
    }

    public static DictionaryTools i() {
        return Inner.inner;
    }

    private static class Inner {
        static DictionaryTools inner;

        static {
            inner = new DictionaryTools();
        }
    }

    /**
     * 加载字典到内存
     * @param entityList
     */
    public void initDict(List<TblSptDictionaryEntity> entityList) {
        for (TblSptDictionaryEntity en : entityList) {
            if (!dictionary.containsKey(en.getDictCategory())) {
                dictionary.put(en.getDictCategory(), new ArrayList<TblSptDictionaryEntity>());
            }
            ((ArrayList) dictionary.get(en.getDictCategory())).add(en);
        }
    }

    /**
     * 获取字典类别键对应的list
     *
     * @param dictCategory 字典类别键
     * @return
     */
    public ArrayList<TblSptDictionaryEntity> getConfigList(String dictCategory) {
        try {
            return (ArrayList<TblSptDictionaryEntity>) dictionary.get(dictCategory);
        } catch (Exception e) {
            throw new RuntimeException("no match category");
        }
    }

    /**
     * @param dictCategory 字典类别键
     * @param dictKey      字典表示键
     * @return 字典值
     */
    public String getValue(String dictCategory, String dictKey) {
        return getValue(dictCategory, dictKey, false);
    }

    /**
     * @param dictCategory 字典类别键
     * @param dictKey      字典表示键
     * @param isEn         是否获取英文字典值
     * @return 字典值
     */
    public String getValue(String dictCategory, String dictKey, boolean isEn) {
        String value = String.valueOf("");
        try {
            for (TblSptDictionaryEntity entity : getConfigList(dictCategory)) {
                if (entity.getDictKey().equals(dictKey)) {
                    value = isEn ? entity.getDictValueEn() : entity.getDictValue();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     *
     * @param dictCategory  字典类别键
     * @param dictValue     字典值
     * @return              字典值对应的Key
     */
    public String getDictKey(String dictCategory, String dictValue) {
        String dictKey = String.valueOf("");
        for (TblSptDictionaryEntity entity : getConfigList(dictCategory)) {
            if (entity.getDictValue().equals(dictValue)) {
                dictKey = entity.getDictKey();
                break;
            }
        }
        return dictKey;
    }
}
