package com.totrade.spt.mobile.utility;

import com.autrade.stage.utility.ConvertUtility;
import com.autrade.stage.utility.DesUtility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 加解密工具类
 * @author wangyilin
 *
 */
public final class EncryptUtility
{
	private static byte[] inBuffer = new byte[64];
	private static byte[] outBuffer = new byte[64];
	private static byte[] key = new byte[]{(byte)0x79,(byte)0xA0,(byte)0x75,(byte)0x9E,
			(byte)0x6C,(byte)0x70,(byte)0x05,(byte)0x11,
			(byte)0x23,(byte)0xFE,(byte)0xDC,(byte)0x09,
			(byte)0x6B,(byte)0xC2,(byte)0x81,(byte)0x27};

	private EncryptUtility()
	{
	}

	/**
	 * 文本加密
	 * @param value
	 * @return
	 */
	public static String encrypt(String value)
	{
		try
		{
			initBuffer();
			byte[] valueBin = value.getBytes();
			for (int i=0; i<valueBin.length; i++)
			{
				inBuffer[i] = valueBin[i];
			}
			DesUtility.DES3CBC_Encryption(inBuffer, key, outBuffer);
			return ConvertUtility.byteArr2Str(outBuffer);

		}
		catch (Exception e)
		{
			return value;
		}
	}

	/**
	 * 文本解密
	 * @param value
	 * @return
	 */
	public static String decrypt(String value)
	{
		try
		{
			initBuffer();
			byte[] b = ConvertUtility.str2ByteArr(value);
			DesUtility.DES3CBC_Decryption(b, key, inBuffer);
			int pos;
			for (pos=0; pos<inBuffer.length; pos++)
			{
				if (inBuffer[pos] == (byte)0x00)
				{
					break;
				}
			}
			return new String(inBuffer, 0, pos);
		}
		catch (Exception e)
		{
			return value;
		}
	}

	private static void initBuffer()
	{
		for (int i=0; i<64; i++)
		{
			inBuffer[i] = (byte)0x00;
			outBuffer[i] = (byte)0x00;
		}
	}


	/**MD5值*/
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
}
