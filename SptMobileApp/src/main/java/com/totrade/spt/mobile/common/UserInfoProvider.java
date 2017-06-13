package com.totrade.spt.mobile.common;

import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.utility.EncryptUtility;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class UserInfoProvider {

    public final static String TAG = "UserInfoProvider";
    private static List<NimUserInfo> userInfos = new ArrayList<>();

    public static String getNIMAccid() {
        String accid = SharePreferenceUtility.spGetOut(SptApplication.context, SharePreferenceUtility.NIM_ACCID, "");
        return EncryptUtility.decrypt(accid);
    }

    public static String getNIMToken() {
        String token = SharePreferenceUtility.spGetOut(SptApplication.context, SharePreferenceUtility.NIM_TOKEN, "");
        return EncryptUtility.decrypt(token);
    }

    public static NimUserInfo getUserInfo(String account) {
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);    //  查询本地
        if (user == null) {
            List<String> accounts = new ArrayList<>();
            accounts.add(account);
            getNimUserInfoList(accounts);
            if (userInfos != null && userInfos.size() > 0) {
                user = userInfos.get(0);
            }
        }
        return user;
    }

    private static void getNimUserInfoList(List<String> accounts) {
        // TODO:  getNimUserInfoList
        InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts);   //  请求服务器
        future.setCallback(new FutureRequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                super.onSuccess(param);
                userInfos.clear();
                userInfos = param;
            }
        });
    }
}
