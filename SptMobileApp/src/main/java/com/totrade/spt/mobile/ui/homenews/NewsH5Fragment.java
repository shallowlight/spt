package com.totrade.spt.mobile.ui.homenews;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.autrade.spt.activity.entity.ActivityShareUpEntity;
import com.autrade.spt.activity.service.inf.IActivityService;
import com.autrade.stage.entity.GeneralDownEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CommonTitle3;
import com.totrade.spt.mobile.view.customize.PopWindowShare;

/**
 * Created by Administrator on 2017/4/11.
 */

public class NewsH5Fragment extends BaseFragment implements View.OnClickListener {
    private Activity activity;
    private View rootView;
    private String newsUrl;
    private CommonTitle3 title;
    private ImageView titleRigthImg;
    private WebView webView;

    public NewsH5Fragment() {
        setContainerId(R.id.frameLayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        register();
        rootView = inflater.inflate(R.layout.webview_common, container, false);

        title = (CommonTitle3) rootView.findViewById(R.id.title);
        titleRigthImg = title.getTitleRightImg(R.drawable.share2);
        titleRigthImg.setOnClickListener(this);
        title.getImgBack().setOnClickListener(this);

        webView = (WebView) rootView.findViewById(R.id.webViewCommon);
        webView.requestFocus();//触摸焦点起作用
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);//取消滚动条
        initData();

        return rootView;
    }

    private void initData() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);//支持javascript
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置允许js弹出alert对话框
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                titleRigthImg.setVisibility(View.VISIBLE);
                title.setTitle("新闻详情");
                view.loadUrl(url);
                return true;
            }
        });

        parseIntent();
    }

    private void parseIntent() {
        String newsType = activity.getIntent().getStringExtra("newsType");
        if ("list".equals(newsType)) {
            title.setTitle("新闻");
            titleRigthImg.setVisibility(View.GONE);
        } else {
            title.setTitle("新闻详情");
        }
        newsUrl = activity.getIntent().getStringExtra("newsUrl");
        if (TextUtils.isEmpty(newsUrl)) {
            newsUrl = Client.getNewsListUrl();
        }
        webView.loadUrl(newsUrl);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgRight) {
            showSharePopWindow();
        } else if (v.getId() == R.id.imgBack) {
            if (!keyBack()) activity.finish();
        }
    }

    /**
     * 分享弹窗
     */
    private void showSharePopWindow() {
        PopWindowShare ps = new PopWindowShare(activity);
        ps.ShareUrl(newsUrl);
        ps.showAtBottom();
    }

    private void register() {
        receiver = new WXShareReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.SHARE_CALLBACK_ARGUMENT);
        activity.registerReceiver(receiver, filter);
    }

    private WXShareReceiver receiver;

    private class WXShareReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.SHARE_CALLBACK_ARGUMENT.equals(intent.getAction())) {
                int code = intent.getIntExtra(AppConstant.SHARE_CALLBACK_ARGUMENT, -10);
                if (BaseResp.ErrCode.ERR_OK == code) {
                    sendWxSharePoint();
                }
            }
        }
    }

    /**
     * 微信分享送积分
     */
    private void sendWxSharePoint() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<GeneralDownEntity>() {
            @Override
            public GeneralDownEntity requestService() throws DBException, ApplicationException {
                ActivityShareUpEntity upEntity = new ActivityShareUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                return Client.getService(IActivityService.class).sendWxSharePoint(upEntity);
            }

            @Override
            public void onDataSuccessfully(GeneralDownEntity obj) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(receiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyBack()) return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean keyBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            title.setTitle("新闻");
            titleRigthImg.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

}
