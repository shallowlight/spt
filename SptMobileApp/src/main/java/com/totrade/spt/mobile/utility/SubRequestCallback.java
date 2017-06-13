package com.totrade.spt.mobile.utility;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.netease.nimlib.sdk.RequestCallback;
import com.totrade.spt.mobile.base.SptMobileActivityBase;

/**
 * Created by iUserName on 2016/9/6.
 */
public abstract class SubRequestCallback<T> implements RequestCallback<T>
{
    private Context context;
    public SubRequestCallback(Context context)
    {
        this.context = context;
    }
    public abstract void callBackSuccess(T t);
    @Override
    public void onSuccess(T t)
    {
        callBackSuccess(t);
    }

    @Override
    public void onFailed(int i)
    {
        if(context!=null)
        {
            String errorMsg = ErrorCodeUtility.getIMErrorMsg(i);
            if(TextUtils.isEmpty(errorMsg))
            {
                errorMsg = "操作失败!";
            }
            if(context instanceof SptMobileActivityBase)
            {
                ((SptMobileActivityBase)context).showMessage(errorMsg);
            }
            else
            {
                Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onException(Throwable throwable)
    {
        if(context!=null)
        {
            //Toast.makeText(context,"网络请求失败!",Toast.LENGTH_SHORT).show();
//            if(context instanceof SptMobileActivityBase)
//            {
//                ((SptMobileActivityBase)context).showMessage("网络请求失败!");
//            }
//            else
//            {
//                Toast.makeText(context,"网络请求失败!",Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
