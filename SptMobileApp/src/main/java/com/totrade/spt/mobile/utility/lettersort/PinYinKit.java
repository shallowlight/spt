package com.totrade.spt.mobile.utility.lettersort;

import com.autrade.stage.utility.StringUtility;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinKit
{
	private static HanyuPinyinOutputFormat format;
	static
	{
		format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
	}

	public static String getPingYin(String chineseStr) throws BadHanyuPinyinOutputFormatCombination
	{
		String zhongWenPinYin = "";
		char[] chars = chineseStr.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i], format);
			if (pinYin != null)
				zhongWenPinYin += pinYin[0];
			else
				zhongWenPinYin += chars[i];
		}
		return zhongWenPinYin;
	}

	public static char getPingYin(char ca)
	{
		if(ca>'a' && ca<'z')
		{
			ca-=32;
			return ca;
		}
		if(ca >'A' && ca <'Z')
		{
			return ca;
		}
		String[] pinYin = null;
		try
		{
			pinYin = PinyinHelper.toHanyuPinyinStringArray(ca, format);
		}
		catch (BadHanyuPinyinOutputFormatCombination e)
		{

		}
		if (pinYin != null)
			return pinYin[0].charAt(0);
		else
			return '#';
	}


	public static char first2Upper(String pinyin)
	{
		char c;
		if (!StringUtility.isNullOrEmpty(pinyin))
		{
			c = pinyin.charAt(0);
			if (c > 'a' && c < 'z') // 转大写
			{
				c -= 32;
			}
			if (c < 'A' && c > 'Z')
			{
				c = '#';
			}
		} else
		{
			c = '#';
		}
		return c;
	}
}
