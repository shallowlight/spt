package com.totrade.spt.mobile.view.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.autrade.spt.activity.entity.ActivityShareUpEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;

/**
 * 微信分享回调页面 该类名不可变更,对应包名项目包名+"wxapi"，不可变更，变更后将无法收到回调
 * */
public class WXEntryActivity extends SptMobileActivityBase implements
		IWXAPIEventHandler {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		IWXAPI api = WXAPIFactory.createWXAPI(this, AppConstant.SHARE_WX_APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq resp)
	{
		ToastHelper.showMessage("分享回调");
	}

	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = "分享成功";
				sendWxSharePoint(resp);
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "发送取消";
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "发送被拒绝";
				break;
			default:
				result = "发送返回";
				break;
		}

		// 发送微信回调结果广播
		Intent intent = new Intent(AppConstant.SHARE_CALLBACK_ACTION);
		intent.putExtra(AppConstant.SHARE_CALLBACK_ARGUMENT, resp.errCode);
		sendBroadcast(intent);
		ToastHelper.showMessage(result);
		finish();
	}

	/**
	 * weixin，weibo，qq
	 * active-活动、query-询价
	 * 存储分享的内容，链接
	 * 微信分享成功后送积分
	 */
	private void sendWxSharePoint(final BaseResp resp){
		SubAsyncTask.create ().setOnDataListener ( new OnDataListener<Boolean> ( ) {
			@Override
			public Boolean requestService ( ) throws DBException, ApplicationException {
				ActivityShareUpEntity entity = new ActivityShareUpEntity ();
				entity.setTarget ( "weixin" );
				entity.setShareType ( "active" );
				entity.setMemo ( resp.transaction );
				return Boolean.TRUE;
			}

			@Override
			public void onDataSuccessfully ( Boolean obj ) {
				if ( null != obj && obj ){

				}
			}
		} );
	}
}
