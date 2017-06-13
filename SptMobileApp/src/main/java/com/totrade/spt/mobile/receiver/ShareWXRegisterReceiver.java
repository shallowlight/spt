package com.totrade.spt.mobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.totrade.spt.mobile.common.AppConstant;


public class ShareWXRegisterReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
		// 将该app注册到微信
		api.registerApp(AppConstant.SHARE_WX_APP_ID);
	}
}
