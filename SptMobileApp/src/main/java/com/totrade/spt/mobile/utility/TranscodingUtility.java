package com.totrade.spt.mobile.utility;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TranscodingUtility
{
	static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";


	/**
	 * MD5值
	 * @param string
	 * @return
	 */
	public static String md5Encoder(String string)
	{
		byte[] hash;
		try
		{
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash)
		{
			if ((b & 0xFF) < 0x10) hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	/**
	 * url编码
	 * @param url
	 * @return
	 */
	public static String urlEncoder(String url) throws UnsupportedEncodingException
	{
		return URLEncoder.encode(url, "UTF-8");
	}

	/**
	 * url解码
	 * @param url
	 * @return
	 */
	public static String urlDecoder(String url) throws UnsupportedEncodingException
	{
		if (TextUtils.isEmpty(url))
		{
			return null;
		}
		return URLDecoder.decode(url, "UTF-8");
	}

	/**
	 * base64编码
	 * @param byteArray
	 * @return
	 */
	public static String base64Encoder(byte[] byteArray)throws UnsupportedEncodingException
	{
		return new String(Base64.encode(byteArray,Base64.DEFAULT), "UTF-8");
	}

	/**
	 * Base64编码
	 * @param str
	 * @return
	 */
	public static String base64Encoder(String str)throws UnsupportedEncodingException
	{
		byte[] encode = str.getBytes("UTF-8");
		return base64Encoder(encode);
	}

	/**
	 * base64解码
	 * @param str
	 * @return
	 */
	public static String base64Decoder(String str)throws UnsupportedEncodingException
	{
		if (TextUtils.isEmpty(str))
		{
			return null;
		}
		return new String(base64Decoder2Byte(str), "UTF-8");
	}
	/**
	 * base64解码
	 * @param str
	 * @return
	 */
	public static byte[] base64Decoder2Byte(String str) throws UnsupportedEncodingException
	{
		if (TextUtils.isEmpty(str))
		{
			return null;
		}
		byte[] encode = str.getBytes("UTF-8");
		return Base64.decode(encode, 0, encode.length,Base64.NO_WRAP);
	}

}
