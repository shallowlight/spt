package com.totrade.spt.mobile.view.customize;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.autrade.stage.utility.StringUtility;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;

/**
 * 分享内容到微信好友，朋友圈 ,PopupWindow 分享需要在 WXEntryActivity中
 */
public class PopWindowShare extends PopupWindowBottom implements OnClickListener {
    private static final int THUMB_WID = 150;
    private static final int THUMB_HEI = 75;
    private IWXAPI api;
    private Context context;
    private TextView lblWeChatCicle;
    // 分享消息体
    private WXMediaMessage mediaMessage;
    // 分享事务，webView 为："webpage"，图片为"img",文本为"text"
    private String transaction;

    public PopWindowShare(Context context) {
        super();
        String shareAppId = AppConstant.SHARE_WX_APP_ID;
        View view = LayoutInflater.from(context).inflate(R.layout.pop_negoitem_share, null);
        lblWeChatCicle = (TextView) view.findViewById(R.id.lblWeChatCicle);
        creatView(view);
        this.context = context;
        api = WXAPIFactory.createWXAPI(context, shareAppId, false);
        api.registerApp(shareAppId);
        setDismissPopView(view.findViewById(R.id.lblCancel));
        view.findViewById(R.id.lblWeChat).setOnClickListener(this);
        lblWeChatCicle.setOnClickListener(this);
    }


    /**
     * 分享网页
     */
    public void creat2ShareWeb(String url, String msgTitle, String description, Bitmap thumb) {
        if (StringUtility.isNullOrEmpty(url)) {
            emptyNotify("分享的url不能为空");
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        mediaMessage = new WXMediaMessage(webpage);
        mediaMessage.title = msgTitle;
        mediaMessage.description = description;
        if (thumb != null) {
            mediaMessage.thumbData = Util.bmpToByteArray(thumb, true);
        }
        transaction = "webpage";
    }

    /**
     * 分享文本
     */
    public void creat2ShareText(String text) {
        if (StringUtility.isNullOrEmpty(text)) {
            emptyNotify("分享内容不能为空");
            return;
        }
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObj;
        mediaMessage.description = text;
        transaction = "text";
    }

    /**
     * 分享链接
     */
    public void create2ShareURL(String registerCode) {
        if (TextUtils.isEmpty(registerCode)) {
            // emptyNotify("邀请码不能为空");
            return;
        }
        String url = AppConstant.DOWNLOADAPP;
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;
        mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = webpageObject;
        mediaMessage.title = "TOTRADE.CN";    //标题
        mediaMessage.description = "领先的工业原材料综合电商平台\n邀请码" + registerCode;    //描述

        Bitmap thmb = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_qr_code);
        Bitmap thmBitmap = Bitmap.createScaledBitmap(thmb, 120, 120, true); //缩略图
        mediaMessage.thumbData = Util.bmpToByteArray(thmBitmap, true);

        transaction = "url";
    }

    /**
     * 分享链接
     */
    public void ShareUrl(String url) {
        if (null == url || "".equals(url)) return;
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = url;
        transaction = "url";
    }

    /**
     * 分享图片
     */
    public void creat2ShareBitmap(Bitmap bmp) {
        if (bmp == null) {
            emptyNotify("图片不能为空");
            return;
        }
        WXImageObject imgObj = new WXImageObject(bmp);
        mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_WID, THUMB_HEI, true);
        bmp.recycle();
        mediaMessage.thumbData = Util.bmpToByteArray(thumbBmp, true); // 设置缩略图
        transaction = "img";
    }

    /**
     * 分享本地图片
     */
    public void creat2ShareImg(String path) {
        if (StringUtility.isNullOrEmpty(path)) {
            emptyNotify("图片路径不能为空");
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            emptyNotify("图片不存在,请重新选择");
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(path);
        mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = imgObj;
        Bitmap bmp = BitmapFactory.decodeFile(path);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_WID, THUMB_HEI, true);
        bmp.recycle();
        mediaMessage.thumbData = Util.bmpToByteArray(thumbBmp, true);
        transaction = "img";
    }

    public IWXAPI getApi() {
        return api;
    }

    @Override
    public void onClick(View v) {
        shareReq(transaction, mediaMessage, v == lblWeChatCicle);
        dismiss();
    }

    /**
     * 发送分享到微信好友，朋友圈
     */
    private void shareReq(String transaction, WXMediaMessage msg, boolean isWecharFrend) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(transaction);
        req.message = msg;
        req.scene = isWecharFrend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void emptyNotify(String emptyMsg) {
        ToastHelper.showMessage(emptyMsg);
    }
}
