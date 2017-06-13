package com.totrade.spt.mobile.view.im.common;

import com.netease.nimlib.sdk.RequestCallback;

public class FutureRequestCallback<T> implements RequestCallback<T>
{

	public final static String TAG = "FutureRequestCallback";
	String show;

	public FutureRequestCallback()
	{
		super();
	}

	@Override
	public void onException(Throwable e)
	{
		com.totrade.spt.mobile.utility.LogUtils.i(TAG, " :exception : : " + e.toString()); // XXX
	}

	@Override
	public void onFailed(int code)
	{
		com.totrade.spt.mobile.utility.LogUtils.i(TAG, " :failed : : " + code); // XXX
	}

	@Override
	public void onSuccess(T param)
	{
		com.totrade.spt.mobile.utility.LogUtils.i(TAG, " :success : : " + param); // XXX
	}

}
