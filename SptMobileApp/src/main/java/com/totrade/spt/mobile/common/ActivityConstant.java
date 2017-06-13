package com.totrade.spt.mobile.common;

import com.autrade.spt.common.constants.SptConstant;

/**
 * Created by Timothy on 2017/2/8.
 * 用于标记页面上的一些常量
 * (interface 常量已经默认为final static)
 */
public interface ActivityConstant extends SptConstant {

    String PRODUCTTYPE = "productType";

    /**
     * 通用
     */
    class Common{

        public static final int REQUEST_CODE_0X0001 = 0X0001;
        public static final int REQUEST_CODE_0X0002 = 0X0002;
        public static final int REQUEST_CODE_0X0003 = 0X0003;
        public static final int REQUEST_CODE_0X0004 = 0X0004;
        public static final int REQUEST_CODE_0X0005 = 0X0005;
        public static final int REQUEST_CODE_0X0006 = 0X0006;

        public static final int RESULT_CODE_0X0001 = 0X0001;
        public static final int RESULT_CODE_0X0002 = 0X0002;
        public static final int RESULT_CODE_0X0003 = 0X0003;
        public static final int RESULT_CODE_0X0004 = 0X0004;
        public static final int RESULT_CODE_0X0005 = 0X0005;
        public static final int RESULT_CODE_0X0006 = 0X0006;
    }

    /**
     * 账户管理
     */
    class AccountManager {
        public static final String EDITPWD_LOGIN = "loginPwd";
        public static final String EDITPWD_FUNDS = "fundsPwd";
        public static final String SUBACT_EDIT = "subactEdit";
        public static final String SUBACT_DETAIL = "subactDetail";
        public static final String SUBACT_ADD = "subactAdd";
        public static final String SUBACT_DEL = "subactDel";

        public static final String KEY_ENTITY_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT = "KEY_ENTITY_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT";
        public static final String KEY_EDITTYPE_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT = "KEY_TYPE_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT";
        public static final String KEY_ACC_POWER_FROM_FRAGMENT = "KEY_ACC_POWER_FROM_FRAGMENT";

    }

    /**
     * 专区
     */
    class Zone {
        public static final String TOP_LEVEL = "ZC_HG";
        //专区合同状态
        public static final String CONTRACT_ZONE_STATUS_NOT_A_DELIVERY = "U";//未发起交收
        public static final String CONTRACT_ZONE_STATUS_A_DELIVERY = "S";//已发起交收
        //买方专区状态
        public static final String BUYER_ZONE_STATUS_HANG_OUT = "G";//已挂出
        public static final String BUYER_ZONE_STATUS_TRANSFER = "Z";//已转让
        public static final String BUYER_ZONE_STATUS_NORMAL = "O";//正常
        //库存转卖 回补/变更订单/
        public static final String STOCK_BUY_DETAIL = "stock_buy_detail";
        public static final String STOCK_SELL_DETAIL = "stock_sell_detail";
        public static final String STOCK = "stock";
        public static final String DETAIL = "detail";
        public static final String STOCK_RESELL_RECHARGE = "stock_resell_recharge";
        public static final String CHANGE_ORDER = "change_order";
        //数据传递标识key
        public static final String KEY_ACTION = "action";
        public static final String PRODUCT_TYPE = "productType";
    }

    /**
     * 通通IM-云信
     */
    class IM {

    }

}
