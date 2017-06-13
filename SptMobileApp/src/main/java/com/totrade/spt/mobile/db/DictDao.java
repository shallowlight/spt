package com.totrade.spt.mobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.autrade.spt.common.entity.TblSptDictionaryEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author huangxy
 */
public class DictDao {
    private DictDao(Context ctx) {
        dbHelper = new DictOpenHelper(ctx, "dict.db", 1);
    }

    private static DictDao instance;

    public synchronized static DictDao getInstance(Context ctx) {
        if (instance == null) {
            instance = new DictDao(ctx);
        }
        return instance;
    }

    private DictOpenHelper dbHelper;

    private String TABLE_NAME = "dictionary";

    public long addNoti(TblSptDictionaryEntity entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dictCategory", entity.getDictCategory());
        values.put("dictKey", entity.getDictKey());
        values.put("dictValue", entity.getDictValue());
        values.put("dictValueEn", entity.getDictValueEn());
        values.put("errorId", entity.getErrorId());
        values.put("updateTime", "" + entity.getUpdateTime());
        return db.insert(TABLE_NAME, "_id", values);
    }

    /**
     * @param key dictKey
     * @return dictValue 中文
     */
    public String queryByType(String key) {
        String dictValue = "";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dictKey", key);
        Cursor cursor = db.rawQuery("select dictValue from dictionary where dictKey = ?",
                new String[]
                        {key});
        if (cursor.moveToNext()) {
            dictValue = cursor.getString(cursor.getColumnIndex("dictValue"));
        }
        cursor.close();
        return dictValue;
    }

    public void delAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("delete from " + TABLE_NAME);

    }

    /**
     * @return
     */
    public List<TblSptDictionaryEntity> queryDict() {
        List<TblSptDictionaryEntity> dictList = new ArrayList<TblSptDictionaryEntity>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from dictionary;", null);
        while (cursor.moveToNext()) {
            String dictCategory = cursor.getString(1);
            String dictKey = cursor.getString(2);
            String dictValue = cursor.getString(3);
            String dictValueEn = cursor.getString(4);
            int errorId = cursor.getInt(5);
            String updateTime = cursor.getString(6);

            TblSptDictionaryEntity dict = new TblSptDictionaryEntity();
            dict.setDictCategory(dictCategory);
            dict.setDictKey(dictKey);
            dict.setDictValue(dictValue);
            dict.setDictValueEn(dictValueEn);
            dict.setErrorId(errorId);
            dict.setUpdateTime(new Date(Long.valueOf(updateTime)));
            dictList.add(dict);
        }
        cursor.close();
        return dictList;
    }

}
