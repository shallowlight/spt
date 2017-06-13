package com.totrade.spt.mobile.ui.notifycenter;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.entity.NotiEntity;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HttpURLConnectionUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;

import java.util.Date;

/**
 * 消息详情界面
 */
public class MessageDetailActivity extends SptMobileActivityBase {

    private TextView tvTitle, tvTime, tvDetail;
    private CommonTitleZone title;
    private View ll_msg;
    private WebView wvContent;

    public static void start(Context context, NotiEntity notiEntity, String msgType) {
        Intent i = new Intent(context, MessageDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(NotiEntity.class.getName(), notiEntity);
        bundle.putSerializable("msgType", msgType);
        i.putExtras(bundle);
        context.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notidetail);

        ll_msg = findViewById(R.id.ll_msg);
        title = (CommonTitleZone) findViewById(R.id.title);
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        tvTime = (TextView) findViewById(R.id.tv_dealtime);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        wvContent = (WebView) findViewById(R.id.wv_noti_content);
        wvContent.setWebViewClient(new SptWebViewClient());
        title.getTvTongTong().setVisibility(View.GONE);

        parseIntent();
    }

    private void parseIntent() {
        Bundle extras = getIntent().getExtras();
        NotiEntity notiEntity = (NotiEntity) extras.getSerializable(NotiEntity.class.getName());
        String msgType = (String) extras.getSerializable("msgType");

        if (msgType != null && notiEntity != null) {
            switch (msgType) {
                case "商品通公告":
                    ll_msg.setVisibility(View.GONE);
                    //缩放开关，设置此属性，仅支持双击缩放，不支持触摸缩放
                    wvContent.getSettings().setSupportZoom(true);
                    //设置是否可缩放，会出现缩放工具（若为true则上面的设值也默认为true）
                    wvContent.getSettings().setBuiltInZoomControls(true);
                    //隐藏缩放工具
                    wvContent.getSettings().setDisplayZoomControls(false);
                    wvContent.loadUrl(Client.getH5Url(notiEntity.getMsgId()));
                    break;
                case "交易公告":
                case "交收处理":
                case "资金管理":
                case "违约处理":
                case "我的关注":
                    ll_msg.setVisibility(View.VISIBLE);
                    tvDetail.setText(notiEntity.getMsgContent());
                    Date date = notiEntity.getDate();
                    String time = FormatUtil.date2String(date, "yyyy-MM-dd HH:mm");
                    tvTime.setText(time);
                    tvTitle.setText(notiEntity.getMsgTitle());
                    break;
                default:
                    wvContent.loadData(notiEntity.getMsgContent(), "text/html; charset=UTF-8", null);
                    break;
            }

            title.setTitle(msgType);
        }
        /*
        String titleStr = "详情";
        if (msgType != null && notiEntity != null) {
            tvDetail.setText(notiEntity.getMsgContent());
            Date date = notiEntity.getDate();
            String time = FormatUtil.date2String(date, "yyyy-MM-dd HH:mm");
            tvTime.setText(time);
            tvTitle.setText(notiEntity.getMsgTitle());
            titleStr = msgType + "详情";
        }
        title.setTitle(titleStr);
        if ("公告".equals(msgType) || "新闻".equals(msgType)) {
//            loadHtml(notiEntity.getMsgId());/
//          缩放开关，设置此属性，仅支持双击缩放，不支持触摸缩放
            wvContent.getSettings().setSupportZoom(true);
            //设置是否可缩放，会出现缩放工具（若为true则上面的设值也默认为true）
            wvContent.getSettings().setBuiltInZoomControls(true);
            //隐藏缩放工具
            wvContent.getSettings().setDisplayZoomControls(false);
            wvContent.loadUrl(Client.getH5Url(notiEntity.getMsgId()));
        } else {
            wvContent.loadData(notiEntity.getMsgContent(), "text/html; charset=UTF-8", null);
        }*/
    }

    private void loadHtml(final String msgId) {
        SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<String>() {

            @Override
            public String requestService() throws DBException, ApplicationException {
                return HttpURLConnectionUtility.HttpURLConnectionRequest(Client.getH5Url(msgId), null, false);
            }

            @Override
            public void onDataSuccessfully(String obj) {
                if (obj != null) {
                    wvContent.loadData(obj, "text/html; charset=UTF-8", null);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    class SptWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }

}
