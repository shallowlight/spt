package com.totrade.spt.mobile.utility;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class HostTimeUtility
{
	//服务器时间减去当前系统时间差值
	private static long timeGap=0;
	private HostTimeUtility()
	{

	}

	public static long getTimeGap()
	{
		return timeGap;
	}


	/**
	 * 如果已经设置timeGap，则返回服务器时间。否则返回系统时间。不太重要的操作时使用
	 * 不需要在异步线程使用
	 * @return
	 */
	public static Date getDate()
	{
//		return new Date();
		Date date=new Date();
		return new Date(date.getTime()+timeGap);
	}

	/**
	 * 获取服务端时间，需要在异步线程里面调用
	 * @return
	 */
	public static Date getTimeOnAsyncTask()
	{
		Date date=new Date();
		try
		{
			URL url=new URL("http://www.totrade.cn");//取得资源对象
			URLConnection uc=url.openConnection();//生成连接对象
			uc.connect();
			long time=uc.getDate(); //取得网站日期时间
			timeGap=time-date.getTime();
			date=new Date(time);
		}
		catch (Exception e)
		{
			timeGap=0;
		}
		return date;
	}
}
