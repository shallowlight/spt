package com.totrade.spt.mobile.bean;

/**
 * Created by Timothy on 2017/6/6.
 */

public class MenuNavigation {
    private String hint;
    private Class<?> clazz;

    public MenuNavigation(String hint, Class<?> clazz) {
        this.hint = hint;
        this.clazz = clazz;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
