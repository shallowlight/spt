package com.totrade.spt.mobile.utility.http4service;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by iUserName on 2016/10/28.
 */

public class HttpUtils
{

    public static String HttpURLConnectionRequest(String urlStr, String content, boolean isPost) throws IOException
    {
        HttpURLConnection urlConn = null;
        try
        {
            // 通过openConnection 连接
            URL url = new URL((isPost || TextUtils.isEmpty(content)) ? urlStr : (urlStr + "?" + content));
            urlConn = (HttpURLConnection) url.openConnection();
            if(isPost)	//如果是post请求
            {
                urlConn.setDoOutput(true);                  // 设置输入和输出流
                urlConn.setDoInput(true);
                urlConn.setRequestMethod("POST" );	 		// 默认是get
                urlConn.setUseCaches(false);				//post请求不能为true
                urlConn.setRequestProperty("Content-Type", "application/json");
            }
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
            String inputLine;
            String resultData = "";
            while (((inputLine = buffer.readLine()) != null))
            {
                resultData += inputLine;
            }
            in.close();
            return resultData;
        }
        finally
        {
            urlConn.disconnect();
        }
    }
}
