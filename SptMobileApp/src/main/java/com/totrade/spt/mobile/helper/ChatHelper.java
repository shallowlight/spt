package com.totrade.spt.mobile.helper;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class ChatHelper
{

	@SuppressWarnings("unchecked")
	public static void doLogin(final String account, String token)
	{

		LoginInfo info = new LoginInfo(account, token);
		RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>()
		{

			@Override
			public void onException(Throwable e)
			{
				android.util.Log.i("mine", "onException : : : " + e.toString()); // XXX
			}

			@Override
			public void onFailed(int error)
			{
				android.util.Log.i("mine", "error : : : " + error); // XXX
			}

			@Override
			public void onSuccess(LoginInfo logInfo)
			{
				// 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
				android.util.Log.i("mine", "logInfo : : : " + logInfo); // XXX
			}
		};

		NIMClient.getService(AuthService.class).login(info).setCallback(callback);
	}

	public static void onlineStatus(final String roomId)
	{

//		@SuppressWarnings("serial")
//		Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>()
//		{
//			@Override
//			public void onEvent(ChatRoomStatusChangeData data)
//			{
//				if (data.status == StatusCode.UNLOGIN)
//				{
//					int errorCode = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId);
//					// 如果遇到错误码13001，13002，13003，403，404，414，表示无法进入聊天室，此时应该调用离开聊天室接口。
//					if (errorCode == 13001)
//					{
//
//					}
//
//				}
//
//			}
//		};

//		NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
	}
}
