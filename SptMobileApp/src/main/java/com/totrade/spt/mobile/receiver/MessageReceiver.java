package com.totrade.spt.mobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.jpush.android.api.JPushInterface;

import com.totrade.spt.mobile.service.MessageService;
import com.totrade.spt.mobile.utility.LogUtils;

public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("Jpush", "intent.getAction() : : : \n" + intent.getAction());
        LogUtils.i("Jpush", "EXTRA_EXTRA : : : \n" + intent.getExtras().getString(JPushInterface.EXTRA_EXTRA));

        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            Intent msgIntent = new Intent(context, MessageService.class);
            msgIntent.putExtras(bundle);
            context.startService(msgIntent);
        }
    }
}
