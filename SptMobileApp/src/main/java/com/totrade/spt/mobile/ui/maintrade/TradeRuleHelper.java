package com.totrade.spt.mobile.ui.maintrade;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;

/**
 * 多个页面贸易规则的点击事件及跳转
 * @date 2017/5/5
 */
public class TradeRuleHelper {

    private TradeRuleHelper() {
    }

    public static TradeRuleHelper i = new TradeRuleHelper();

    /**
     * 获取可点击的SpannableString
     *
     * @return
     */
    public SpannableString getClickableSpan(final Context context) {
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.agree_contract));
        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 7, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("titleString", "贸易专区规则");
                intent.putExtra("urlString", Client.getRuleUrl());
                context.startActivity(intent);
            }
        }, 7, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue)), 7, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 17, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("titleString", "委托代办货权转移协议");
                intent.putExtra("urlString", Client.getRuleUrl2());
                context.startActivity(intent);
            }
        }, 17, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue)), 17, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}
