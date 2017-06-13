package com.totrade.spt.mobile.utility;


import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class TaskCache {
    private final static Map<String, SubAsyncTask.mAsyncTask> taskMap = new HashMap<String, SubAsyncTask.mAsyncTask>();

    public static Map<String, SubAsyncTask.mAsyncTask> getTaskmap() {
        return taskMap;
    }

    public static void put(String tag, SubAsyncTask.mAsyncTask task) {
        getTaskmap().put(tag, task);
    }

    public static void remove(String tag) {
        getTaskmap().remove(tag);
    }
}
