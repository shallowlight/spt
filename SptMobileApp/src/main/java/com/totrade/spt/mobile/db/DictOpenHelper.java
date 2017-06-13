package com.totrade.spt.mobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author huangxy
 *
 */
public class DictOpenHelper extends SQLiteOpenHelper
{

	public DictOpenHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
	}

	@Override
	/**
	 * 创建数据库时，调用此方法
	 */
	public void onCreate(SQLiteDatabase db)
	{

		db.execSQL("create table dictionary"
				+ "(_id integer primary key autoincrement,"
				+ "dictCategory varchar(100),"
				+ "dictKey varchar(100),"
				+ "dictValue varchar(100),"
				+ "dictValueEn varchar(200),"
				+ "errorId integer(20),"
				+ "updateTime integer(100));");
	}

	@Override
	/**
	 *  更新数据库时，调用此方法
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

}
