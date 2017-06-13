//package com.totrade.spt.mobile.utility;
//
//import com.autrade.stage.utility.StringUtility;
//
//
//public class WashStringUtility
//{
//
//	public static boolean isLawfulString(String memo)
//	{
//		if(StringUtility.isNullOrEmpty(memo))
//		{
//			return true;
//		}
//		char[] charArr = memo.toCharArray();
//		for(char c:charArr)
//		{
//			if(c>128 && c<=160)
//			{
//				return false;
//			}
//		}
//		return true;
//	}
//
////	/**
////	 * 判断字符串是否合法
////	 *  暂时为拦截 128
////	 *
////	 * @param memo
////	 * @return
////	 */
////	public static String lawfulString(String memo)
////	{
////		try
////		{
////			if (StringUtility.isNullOrEmpty(memo))
////			{
////				return memo;
////			}
////			else
////			{
////				char[] charArr = memo.toCharArray();
////				int len = charArr.length;
////				char[] newCharArr = new char[len];
////				for (int i = 0; i < len; i++)
////				{
////					char c = charArr[i];
////					short code = (short) c;
////					if (code == 160)
////					{
////						c = ' ';
////					}
////					code = (short) c;
////					if(code>=128 && code<=160)
////					{
////
////					}
////					newCharArr[i] = c;
////				}
////				return new String(newCharArr);
////			}
////		}
////		catch (Exception e)
////		{
////			return memo;
////		}
////	}
//}
