package com.totrade.spt.mobile.helper;

import android.content.Context;
import android.support.annotation.NonNull;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.SubRequestCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by iUserName on 16/12/19.
 */

public class RequestIMUserInfoHelper
{
    private static final int maxFatchSize = 150;

    public void requestIMInfo(Context context,@NonNull List<String> accountIds)
    {
        Map<String, Map<String, String>> userInfo = LoginUserContext.getMapIMUserInfo();
        List<String> accountIds2 = new ArrayList<>();
        for(String str:accountIds)
        {
            if(!userInfo.containsKey(str))
            {
                accountIds2.add(str);
            }
        }

        if(accountIds2.isEmpty())
        {
            return;
        }

        List<String> tids;                              //单次请求
        int times = (accountIds2.size() - 1) / maxFatchSize + 1;        //超过150需要分批请求
        for (int i = 0; i < times; i++)
        {
            tids = new ArrayList<>();
            //脚标最大值
            int maxNum = Math.min((i + 1) * maxFatchSize, accountIds2.size());
            for (int j = i * maxFatchSize; j < maxNum; j++)
            {
                tids.add(accountIds2.get(j));
            }
            //请求网络
            NIMClient.getService(UserService.class).fetchUserInfo(tids).setCallback(new SubRequestCallback<List<NimUserInfo>>(context)
            {
                @Override
                public void callBackSuccess(List<NimUserInfo> list)
                {
                    if (list != null && !list.isEmpty())
                    {
                        loopUserIconName(list);
                        if(requestSuccess!=null)
                        {
                            requestSuccess.onSuccess(list);
                        }
                    }
                }
            });
        }
    }


    private void loopUserIconName(List<NimUserInfo> list)
    {
        Map<String, Map<String, String>> userInfo = LoginUserContext.getMapIMUserInfo();
        Map<String, String> mapSub;
        for (NimUserInfo info : list)
        {
            mapSub = new HashMap<>();
            mapSub.put(AppConstant.ICON_URL, info.getAvatar());
            mapSub.put(AppConstant.TAG_PETNAME, info.getName());
            userInfo.put(info.getAccount(), mapSub);
        }
        LoginUserContext.setMapIMUserInfo(userInfo);
    }


    private RequestIMInfoSuccess requestSuccess;

    public void setRequestSuccess(RequestIMInfoSuccess requestSuccess)
    {
        this.requestSuccess = requestSuccess;
    }

    public interface RequestIMInfoSuccess
    {
        void onSuccess(List<NimUserInfo> list);
    }
}
