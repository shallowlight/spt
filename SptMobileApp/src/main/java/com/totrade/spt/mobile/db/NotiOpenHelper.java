package com.totrade.spt.mobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author huangxy
 *
 */
public class NotiOpenHelper extends SQLiteOpenHelper
{

	public NotiOpenHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
	}

	@Override
	/**
	 * 创建数据库时，调用此方法
	 */
	public void onCreate(SQLiteDatabase db)
	{

		db.execSQL("create table notication"
				+ "(_id integer primary key autoincrement,"
				+ "msgid varchar(200),"
				+ "tag varchar(2000),"
				+ "message varchar(2000),"
				+ "date varchar(200),"
				+ "ischeck integer(20),"
				+ "markread integer(20));");
	}

	@Override
	/**
	 *  更新数据库时，调用此方法
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

}
