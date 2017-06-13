package com.totrade.spt.mobile.bean;

import java.util.Comparator;

/**
 * 字典
 * Created by Timothy on 2017/4/19.
 */

public class CodeItem implements Comparator<CodeItem> {

    private String key;
    private String code;
    private int sortIndicator;

    public CodeItem( String key, String code) {
        this.key = key;
        this.code = code;
    }

    public CodeItem(String key, String code, int sortIndicator) {
        this.key = key;
        this.code = code;
        this.sortIndicator = sortIndicator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSortIndicator() {
        return sortIndicator;
    }

    public void setSortIndicator(int sortIndicator) {
        this.sortIndicator = sortIndicator;
    }

    @Override
    public int compare(CodeItem lhs, CodeItem rhs) {
        return lhs.getSortIndicator()-rhs.getSortIndicator();
    }
}
