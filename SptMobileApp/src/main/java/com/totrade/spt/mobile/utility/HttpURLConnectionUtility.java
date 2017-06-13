package com.totrade.spt.mobile.utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionUtility
{


	public static String HttpURLConnectionRequest(String urlStr, String content, boolean isPost)
	{

		HttpURLConnection urlConn = null;
		try
		{
			// 通过openConnection 连接
			URL url = new URL((isPost || content==null || content.isEmpty()) ? urlStr : (urlStr + "?" + content));
			urlConn = (HttpURLConnection) url.openConnection();
			if(isPost)	//如果是post请求
			{
				// 设置输入和输出流
				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				urlConn.setRequestMethod("POST" );	 		// 默认是get
				urlConn.setUseCaches(false);				//post请求不能为true
				urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			}

			// 连接，从url.openConnection()至此的配置必须要在connect之前完成，
			// connection.getOutputStream会隐含的进行connect。
			urlConn.connect();
			// 要上传的参数
			// 将要上传的内容写入流中
			if (content != null && isPost)
			{
				DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
				out.writeBytes(content);
				// 刷新、关闭
				out.flush();
				out.close();
			}
			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = null;
			String resultData = "";
			while (((inputLine = buffer.readLine()) != null))
			{
				resultData += inputLine;
			}
			in.close();
			return resultData;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			urlConn.disconnect();
		}
	}

}
