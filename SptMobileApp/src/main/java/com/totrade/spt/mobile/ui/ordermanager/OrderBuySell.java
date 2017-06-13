package com.totrade.spt.mobile.ui.ordermanager;

/**
 * 订单类型  买卖转
 * Created by Timothy on 2017/5/19.
 */

public enum OrderBuySell {

    B("买", com.totrade.spt.mobile.view.R.drawable.bg_red_2coner_circle),
    S("卖",com.totrade.spt.mobile.view.R.drawable.bg_green_2coner_circle),
    R("转", com.totrade.spt.mobile.view.R.drawable.bg_gray_2coner_circle);

    public String text;
    public int bgResId;
    OrderBuySell(String text,int bgResId){
        this.text = text;
        this.bgResId = bgResId;
    }
}
