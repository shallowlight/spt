package com.totrade.spt.mobile.utility.http4service;

import android.text.TextUtils;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.stage.constants.CommonErrorId;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.exception.SystemException;
import com.autrade.stage.utility.JsonUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by iUserName on 2016/10/28.
 */

public class RequestUtils
{
    private static String httpUrl = null;
    private static String port = "8090";
    private static final String MASTER = "SptMaster";
    public static String getUrl2GetSupplier()
    {
        return getUrl(MASTER,"getSupplier");
    }

    private static void initIP()
    {
        if(TextUtils.isEmpty(httpUrl))
        {
            httpUrl = "http://"+SharePreferenceUtility.spGetOutIOSNetIp(SptApplication.context);
            port = SharePreferenceUtility.spGetOutIOSNetPort(SptApplication.context);
        }
        if(TextUtils.isEmpty(httpUrl))
        {
            httpUrl = "http://139.196.95.121";
            port = "8090";
        }
    }
    public static String getUrl(String magic, String serviceName)
    {
        initIP();
        return httpUrl +":"+ port +"/SptGate" + "/" + magic + "/" +serviceName;
    }

    public<K,T> K request(String magic ,String serviceName,T t,Class<K> responseType) throws IOException,SystemException
    {
        initIP();
        String requestUrl = getUrl(magic,serviceName);
        RequestUpDto dto = new RequestUpDto();
        dto.objJson = new Gson().toJson(t);
        String jsonUp = new Gson().toJson(dto);
        String jsonResult = HttpUtils.HttpURLConnectionRequest(requestUrl,jsonUp,true);

        tryThrowExceptionForJson(jsonResult);   //尝试抛下异常
        Type type = new TypeToken<RequestUpDto>(){}.getType();
        RequestUpDto dto2 = JsonUtility.toJavaObject(jsonResult, type);
        String json2= dto2.objJson;
        return JsonUtility.toJavaObject(json2, responseType);
    }



    public<K,T> K request(String requestUrl, T t,Type type) throws IOException,SystemException
    {
        RequestUpDto dto = new RequestUpDto();
        dto.objJson = new Gson().toJson(t);
        String jsonUp = new Gson().toJson(dto);
        String jsonResult = HttpUtils.HttpURLConnectionRequest(requestUrl,jsonUp,true);

        tryThrowExceptionForJson(jsonResult);   //尝试抛下异常
        Type typeF = new TypeToken<RequestUpDto>(){}.getType();
        RequestUpDto dto2 = JsonUtility.toJavaObject(jsonResult, typeF);
        String json2= dto2.objJson;
        return JsonUtility.toJavaObject(json2, type);
    }

    public<K,T> K request(String requestUrl,T t,Class<K> responseType) throws IOException,SystemException
    {
        RequestUpDto dto = new RequestUpDto();
        dto.objJson = new Gson().toJson(t);
        String jsonUp = new Gson().toJson(dto);
        String jsonResult = HttpUtils.HttpURLConnectionRequest(requestUrl,jsonUp,true);
        tryThrowExceptionForJson(jsonResult);   //尝试抛下异常
        try
        {
            Type type = new TypeToken<RequestUpDto>(){}.getType();
            RequestUpDto dto2 = JsonUtility.toJavaObject(jsonResult, type);
            String json2= dto2.objJson;
            return JsonUtility.toJavaObject(json2, responseType);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 根据下行返回的errorId,尝试抛出ApplicationException或DBException
     * @param errorId					错误ID
     * @throws ApplicationException
     * @throws DBException
     */
    protected void tryThrowException(int errorId, String errorMessage) throws ApplicationException, DBException
    {
        if (errorId == 0)
        {
            return;
        }
        else
        {
            if (errorId >= CommonErrorId.ERROR_DB_UNMAPPED
                    && errorId < CommonErrorId.ERROR_UNKNOWN_SERVICEID) //errorId属于DBExcetpion区间
            {
                throw new DBException(errorId, errorMessage);
            }
            else
            {
                throw new ApplicationException(errorId, errorMessage);
            }
        }
    }

    /**
     * 尝试抛出Json异常
     *
     * @param json
     * @return
     * @throws ApplicationException
     * @throws DBException
     */
    private Map<String, Object> tryThrowExceptionForJson(String json) throws ApplicationException, DBException
    {
        try
        {
            //Log.i("JSON", json);

            //先尝试使用Map解码
            Map<String, Object> map = JsonUtility.toJavaObject(json, Map.class);
            if (map == null)
            {
                return null;
            }

            //尝试取得errorId和errorMessage
            if (map.get("errorId") != null)
            {
                BigDecimal d = new BigDecimal(map.get("errorId").toString()); //这里可能会得到283.0这种数值，直接转int会错
                int errorId = d.intValue();
                if (errorId != 0)
                {
                    Object errorMessageObj = map.get("errorMessage");
                    String errorMessage;
                    if (errorMessageObj != null)
                    {
                        errorMessage = errorMessageObj.toString();
                    }
                    else
                    {
                        errorMessage = "";
                    }
                    tryThrowException(errorId, errorMessage);
                    return map;
                }
                else
                {
                    return map;
                }
            }
            else //不能获得errorId
            {
                //可能下行了一个String或者int之类的非entity对象
                return null;
            }
        }
        catch (Exception e)
        {
            if (e instanceof ApplicationException
                    || e instanceof DBException) //属于tryThrowException抛出的错误
            {
                throw e;
            }
            else
            {
                return null; //不能解析成MAP的，一定是LIST
            }
        }
    }

}
