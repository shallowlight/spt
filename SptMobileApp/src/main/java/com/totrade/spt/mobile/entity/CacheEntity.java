package com.totrade.spt.mobile.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iUserName on 2016/8/31.
 */
public class CacheEntity
{
	private static Map<String, Object> map;
	static
	{
		map = new HashMap<String, Object>();
	}

	public static void putCacheEntity(String key, Object entity)
	{
		map.put(key, entity);
	}

	public static Object getCacheEntity(String key)
	{
		if (map.isEmpty())
			return null;
		else
		{
			return map.get(key);
		}
	}
}
