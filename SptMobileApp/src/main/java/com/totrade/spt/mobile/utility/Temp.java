package com.totrade.spt.mobile.utility;

import android.os.Bundle;

import java.util.ArrayList;

public final class Temp {
    private Temp() {
    }

    private Bundle data = new Bundle();

    private static class InnerClass {
        static Temp inner;

        static {
            inner = new Temp();
        }
    }

    public static Temp i() {
        return InnerClass.inner;
    }

    public void put(Class<?> clazz, Object obj) {
        ArrayList list = new ArrayList();
        list.add(0, obj);
        LogUtils.w(Temp.class.getSimpleName(), clazz.getName() + " PUT DATA");
        data.putSerializable(clazz.getName(), list);
    }

    public Object get(Class<?> clazz) {
        ArrayList list  = (ArrayList) data.get(clazz.getName());
        if (list == null) return null;
        Object obj = list.get(0);
        return obj;
    }

    public Object getAndClear(Class<?> clazz) {
        ArrayList list  = (ArrayList) data.get(clazz.getName());
        if (list == null) return null;
        Object obj = list.get(0);
        remove(clazz);
        return obj;
    }

    public void remove(Class<?> clazz) {
        data.remove(clazz.getName());
        LogUtils.w(Temp.class.getSimpleName(), clazz.getName() + " DATA REMOVE");
    }

    public void clear() {
        data.clear();
    }

}
