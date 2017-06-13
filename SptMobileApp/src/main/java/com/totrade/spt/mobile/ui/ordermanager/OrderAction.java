package com.totrade.spt.mobile.ui.ordermanager;

import com.totrade.spt.mobile.common.AppConstant;

/**
 * 订单管理可触发动作枚举
 * Created by Timothy on 2017/5/18.
 */

public enum OrderAction {

    ACTION_DECLARE_DELIVERY(AppConstant.ACTION_DECLARE_DELIVERY),
    ACTION_PAY(AppConstant.ACTION_PAY),
    ACTION_DELIVETY(AppConstant.ACTION_DELIVETY),
    ACTION_RECEIVE(AppConstant.ACTION_RECEIVE);

    public String name;

    OrderAction(String name) {
        this.name = name;
    }
}
