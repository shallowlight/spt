package com.totrade.spt.mobile.common;


public class AppConstant {

    public static final String DATEFORMAT = "yyyy/MM/dd";
    public static final String DATEFORMAT2 = "yyyy/MM";
    public static boolean isRelease = true;
    public static final String ACTION_TYPE_LOGOUT = "logout";

    public static final String SHARE_WX_APP_ID = "wx8c352157f1f021d3";
    //微信分享
    public static final String STitle = "showmsg_title";
    public static final String SMessage = "showmsg_message";
    public static final String BAThumbData = "showmsg_thumb_data";

    //界面配置
    public static final String CFG_NEGO_BOND = "negoBond";
    public static final String CFG_ZONE_BOND = "zoneBond";
    public static final String CFG_MINNEGONUMBER = "minNegoNumber";
    public static final String CFG_FEE_RATE = "feeRate";
    public static final String CFG_FEE = "fee";
    public static final String CFG_ZONEFEE = "zoneFee";
    public static final String CFG_PRODUCTCLASS = "productClass";        //商品等级(塑料)
    public static final String CFG_TKGRADE = "tpw";                        //铁品位
    public static final String CFG_PRODUCTPRICE = "price";                //价格
    public static final String CFG_PRODUCTNUMBER = "number";            //数量
    public static final String CFG_VALIDHOUR = "validHour";                //时效(小时)
    public static final String CFG_VALIDMINUTE = "validMinute";            //数量(分钟)
    public static final String CFG_RESERVOIRAREA  = "reservoirArea";            //库区
    public static final String CFG_NUMBERMAX = "max";
    public static final String CFG_NUMBERMIN = "min";
    public static final String CFG_NUMBERPREC = "prec";
    public static final String CFG_SPOTDAYS = "spotDays";

    public static final String DICTIONARY = "dictionary";
    public static final String DICT_RUNNING_TYPE = "runningType";
    public static final String DICT_TRADEMODE_BUYSELL = "tradeModeBuySell";
    public static final String DICT_DELIVERY_PLACE_NAME = "deliveryPlaceName";
    public static final String DICT_DELIVERY_MODE = "deliveryMode";
    public static final String DICT_PRODUCT_QUALITY = "productQuality";
    public static final String DICT_BUSINESS_TYPE = "businessType";
    public static final String DICT_WARE_HOUSE_NAME = "wareHouseName";
    public static final String DICT_BRAND = "brand";
    public static final String SHARE_CALLBACK_ACTION = "share_callback_action";
    public static final String SHARE_CALLBACK_ARGUMENT = "share_callback_argument";
    public static final String COMPANYTAG_HBGT = "SPTQY0000001751";
    public static final String EMPTY = "";

    public static final String PRICEJSONCFG = "{\"DM\":{\"max\":100000,\"min\":1,\"prec\":2},\"FO\":{\"max\":15060.24,\"min\":0.15,\"prec\":2}}";

    //	intent constant
    public static final String RECENT_CONTACT = "recentContact";
    public static final String NIM_ACCOUNT = "account";
    public static final String NIM_TOKEN = "token";
    public static final String NIM_TEAMID = "teamId";
    public static final String NIM_SESSIONID = "sessionId";
    public static final String NIM_SESSIONTYPE = "sessionType";
    public static final String NIM_NICKNAME = "nickName";
    public static final String ICON_URL = "iconUrl";
    public static final String SESSIONTYPE = "sessionType";
    public static final String TAG_ENEIEY_IMNEGO = "tag_entity_imNego";
    public static final String RELEASE_COMEFROME_TYPE = "comeTag";

    //	intent action
    public static final String INTENT_ACTION_NOTI = "com.totrade.spt.mobile.totabnoti";
    public static final String INTENT_ACTION_REFRESH_ON_PRICE = "com.totrade.spt.refresh.onprice";
    public static final String INTENT_ACTION_TRADE_REFRESH = "com.totrade.spt.mobile.trade.refresh";
    public static final String INTENT_ACTION_FOUCS_REFRESH = "com.totrade.spt.mobile.focus.refresh";
    public static final String INTENT_ACTION_NOTIFY_CENTER = "com.totrade.spt.mobile.notify.center";

    public static final String NOTIFY_ID = "notify_id";        //	通知Id
    public static final String NOTIFY_TAG = "notify_tag";        //	通知tag

    public static final String TAG_NEGOSTATUS = "tag_negoStatus";
    public static final String TAG_BIZID = "tag_bizId";
    public static final String TAG_PETNAME = "tag_petname";

    //    photo constant
    public static final String MIME_JPEG = "image/jpeg";
    public static final String JPG = ".jpg";

    public static final String FRAGMENT_INDEX = "FRAGMENT_INDEX";

    //    正则
    public static final String RE_ACCOUNT = "^[a-zA-Z]+[a-zA-Z0-9]{4,19}$";//只允许字母和数字，以字母开头长度5到20位
    public static final String RE_COMPANYNAME = "^[a-zA-Z\\u4e00-\\u9fa5]{5,30}$";//只允许中文和英文字母字符长度5到30位
    public static final String RE_NICKNAME = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]{4,20}$";    //    汉字数字字母下划线，不以下划线开头和结尾
    public static final String RE_RELNAME = "^[a-zA-Z0-9\u4e00-\u9fa5]{2,20}$";    //字母数字汉字2-20位
    public static final String RE_INVITATION_CODE = "^[A-Za-z0-9]{4}$";    //验证码字母数字4位
    public static final String RE_PHONE = "^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";    //手机号码
    public static final String RE_EMAIL = "^[a-z A-Z 0-9 _]+@[a-z A-Z 0-9 _]+(\\.[a-z A-Z 0-9 _]+)+(\\,[a-z A-Z 0-9 _]+@[a-z A-Z 0-9 _]+(\\.[a-z A-Z 0-9 _]+)+)*$";    //手机号码
    public static final String RE_QQ = "^[1-9][0-9]{4,}$";
    public static final String RE_CHINESE = "[\\u4e00-\\u9fa5]";
    public static final String RE_URL = "http(s)?://([\\\\w-]+\\\\.)+[\\\\w-]+(/[\\\\w- ./?%&=]*)?";
    public static final String RE_PWD = "^[a-zA-Z][a-zA-Z0-9_]{7,19}$";    //密码 字母开头数字下划线8-20位
    public static final String RE_EVERYTHING = ".*";
    public static final String RE_ID_CARD = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";//身份证号码

    //	链接
    public static String RELEASE_IMAGE_URL = "http://www.totrade.cn/file/show/";
    public static String DEBUG_IMAGE_URL = "http://192.168.1.144:80/file/show/";
    public static final String DOWNLOADAPP = "http://www.totrade.cn/app.a";        //    app下载
    public static final String ALIYUN = "http://spt-public.oss-cn-shanghai.aliyuncs.com/appServer.json";        //    源地址
    public static final String SHARE_CHAT_TEST = "http://192.168.2.144:8085/mobile/wx/share/chatinfo/";
    public static final String SHARE_CHAT_RELEASE = "http://www.totrade.cn/mobile/wx/share/chatinfo/";
    public static final String REGISTER_PINGAN = "https://ebank.sdb.com.cn/corporbank/perRegedit.do?MainAcctId=11016981817007";

    public static final String RELEASE_PAQYZHQY = "http://http://www.totrade.cn/support/content/paybzhqy";//平安银行企业用户
    public static final String RELEASE_PAYBZHQY = "http://http://www.totrade.cn/support/content/paybzhqy";//平安易宝
    public static final String TEST_PAQYZHQY = "http://192.168.2.144:8085/support/content/paqyzhqy";//平安银行企业用户测试
    public static final String TEST_PAYBZHQY = "http://192.168.2.144:8085/support/content/paybzhqy";//平安易宝测试
    public static final String PA_BANK_URL = "https://ebank.sdb.com.cn/corporbank/perRegedit.do?MainAcctId=11016981817007";//平安银行网页

    public static final int JPUSH_NOTIFY_ID = 0;
    public static final int NIM_NOTIFY_ID = 1;

    public static final String ACTION_ARGUE        = "提出异议";
    public static final String ACTION_RECEIVE      = "确认收货";
    public static final String ACTION_PAY          = "确认付款";
    public static final String ACTION_DELIVETY     = "确认发货";
    public static final String ACTION_UNARGUE      = "解除异议";
    public static final String ACTION_UPDATEARGUE  = "修改异议";
    public static final String ACTION_DECLARE_DELIVERY  = "发起交收";

    public static final String MESSAGE_H5_URL = "/mobile/news/h5view/";
    public static final String NEWS_H5_URL = "/mobile/nv/loadNotifyLists.a";
    public static final String NEWS_IMG_URL = "/mobile/file/show/";
    public static final String NEWS_DETAIL_URL = "/mobile/nv/view_Notify/";
    public static final String NEWS_ACTION_URL = "/file/show/";
    public static final String TRADE_RULE = "/v2/help/getData/spt_rule_myzqgz";
    public static final String TRADE_RULE2 = "/v2/help/getData/spt_rule_wtdbhqzyxy.a";

    public static final String POINT_SHOP_URL = "/mobile/potmall/index.a";

}
