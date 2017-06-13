package com.totrade.spt.mobile.ui.pointshop;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;

/**
 * 积分商城
 * Created by Timothy on 2017/5/18.
 */

public class PointShopActivity extends BaseActivity {

    private ComTitleBar title;
    private WebView webView;
    private TextView tv_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_shop);
        title = (ComTitleBar) findViewById(R.id.title);
        webView = (WebView) findViewById(R.id.webView);
        tv_action = (TextView) findViewById(R.id.tv_action);

        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("file:///android_asset/point.html");
        webView.loadUrl(Client.getPointShopUrl());
        webView.addJavascriptInterface(PointShopActivity.this,"android");

        tv_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                webView.loadUrl("javascript:javacalljs(" + "'JAVA调用js中的方法，这里是传递过来的积分商品实体json'" + ")");
                String title = "商品通客服平台";
                ConsultSource source = new ConsultSource(Client.getPointShopUrl(), "积分商城", "自定义消息");
                Unicorn.openServiceActivity(PointShopActivity.this, title, source);
            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(PointShopActivity.this).setMessage(text).show();
            }
        });
    }
}
