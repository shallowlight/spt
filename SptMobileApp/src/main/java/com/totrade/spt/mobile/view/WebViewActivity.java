package com.totrade.spt.mobile.view;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.customize.CommonTitle3;

/**
 * webView 显示页面，需要在intent中传入两个String参数， 一个是需要显示的内容，另一个是标题显示内容
 *
 * @author dp
 */
public class WebViewActivity extends SptMobileActivityBase {
    private WebView webView;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.webview_common);
        webView = (WebView) findViewById(R.id.webViewCommon);

        Intent intent = getIntent();
        String title = intent.getStringExtra("titleString");
        String result = intent.getStringExtra("resultString");
        String url = intent.getStringExtra("urlString");
        ((CommonTitle3) findViewById(R.id.title)).setTitle(title);

        WebSettings settings = webView.getSettings();
        settings.setAllowContentAccess(true);
        settings.getBlockNetworkImage();
        settings.setDefaultTextEncodingName("gb2312");
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setBlockNetworkImage(true);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new SptWebViewClient());
        if (title == null && StringUtility.isNullOrEmpty(result)) {
            return;
        }

        if (!StringUtility.isNullOrEmpty(url)) {
            webView.loadUrl(url);
            return;
        }

        if (StringUtility.isNullOrEmpty(result)) {
            String urlLocal = getUrl(title);
            if (!StringUtility.isNullOrEmpty(urlLocal)) {
                webView.loadUrl(urlLocal);
            }
        } else {
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            webView.loadData(result, "text/html; charset=UTF-8", null);
        }
    }

    private String getUrl(String title) {
        String[][] urlArrays =
                {
                        {"商品信息", "http://www.totrade.cn/resources/add_html/productProperty.html"},
                        {"电子合同", "http://www.totrade.cn/resources/add_html/Electronic_contract.html"},
                        {"免责声明", "http://www.totrade.cn/resources/add_html/Disclaimer.html"},
                        {"交易规则", "http://www.totrade.cn/resources/add_html/Trading_rules.html"},
                        {"客服中心", "http://www.totrade.cn/resources/add_html/Service_center.html"},
                        {"帮助中心", "http://www.totrade.cn/resources/add_html/Help_center.html"},
                        {"商品通用户注册协议", "http://www.totrade.cn/user/reg/tradeProtocolView.a"},
                        {"如何只查看我关注的商品", "file:///android_asset/only_get_focus.html"},
                        {"如何一键发布我常用的询价", "file:///android_asset/creat_template_nego.html"},
                        {"为什么有的订单我不能成交", "file:///android_asset/cant_not_trade.html"},
                        {"如何查看所有我发布的询价或我收到的议价", "file:///android_asset/find_my_nego.html"},
                        {"什么叫分批交付", "file:///android_asset/batch_delivery.html"},
                        {"什么是强制成交", "file:///android_asset/force_deal.html"},
                        {"快速注册后不能交易怎么办", "file:///android_asset/registered_but_cannot_toade.html"},
                        {"资金管理信息有哪些", "file:///android_asset/funds_management_info.html"},
                        {"账户的钱足够，我为什么不能成交/支付全款", "file:///android_asset/money_but_can_not_trade.html"},
                        {"为什么成交后合同被判违约", "file:///android_asset/deal_but_breach_of_contract.html"},
                        {"关于我们", "file:///android_asset/about_as.html"},
                        {"对手方", "file:///android_asset/about_assigner.html"}

                };

        for (String[] ss : urlArrays) {
            if (ss[0].equals(title)) {
                return ss[1];
            }
        }
        return null;
    }

    class SptWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }
}