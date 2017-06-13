package com.totrade.spt.mobile.bean;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.TblSptDictionaryEntity;
import com.autrade.spt.deal.constants.DealConstant;
import com.totrade.spt.mobile.utility.DictionaryUtility;

import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Timothy on 2017/4/17.
 */

public class Dictionary {

    //合同签订日期
    public static List<CodeItem> CONSTRACT_DATE = new ArrayList<>();
    //合同类型
    public static List<CodeItem> CONSTRACT_TYPE = new ArrayList<>();
    //合同状态
    public static List<CodeItem> CONSTRACT_STATUS = new ArrayList<>();
    //冻结资金交易类型
    public static List<CodeItem> FUND_BUSINESS_TYPE = new ArrayList<>();
    //交易类型
    public static List<CodeItem> FUND_TRADE_TYPE = new ArrayList<>();
    //用户身份
    public static List<CodeItem> MASTER_USER_DENTITY = new ArrayList<>();

    static {

        String curYear = com.totrade.spt.mobile.utility.DateUtils.getCurrentYear();

        CONSTRACT_DATE.add(new CodeItem("全部", ""));
        CONSTRACT_DATE.add(new CodeItem("近一周", "1W"));
        CONSTRACT_DATE.add(new CodeItem("近一月", "1M"));
        CONSTRACT_DATE.add(new CodeItem("近三月", "3M"));
        CONSTRACT_DATE.add(new CodeItem("今年合同", "CY"));
        CONSTRACT_DATE.add(new CodeItem(curYear + "年合同", curYear + "Y"));
        CONSTRACT_DATE.add(new CodeItem(Integer.parseInt(curYear) - 1 + "年合同", Integer.parseInt(curYear) - 1 + "Y"));
        CONSTRACT_DATE.add(new CodeItem(Integer.parseInt(curYear) - 2 + "年合同", Integer.parseInt(curYear) - 2 + "Y"));
        CONSTRACT_DATE.add(new CodeItem(Integer.parseInt(curYear) - 2 + "年以前合同", Integer.parseInt(curYear) - 2 + "YB"));


        CONSTRACT_TYPE.add(new CodeItem("全部", ""));
        CONSTRACT_TYPE.add(new CodeItem("购买", "B"));
        CONSTRACT_TYPE.add(new CodeItem("销售", "S"));
        CONSTRACT_TYPE.add(new CodeItem("转售", "R"));

        //从字典中加载
        CONSTRACT_STATUS.add(new CodeItem("全部", ""));
        CONSTRACT_STATUS.add(new CodeItem("待宣港", DealConstant.ORDER_STATUS_DECLAREDELIVERY));
        CONSTRACT_STATUS.add(new CodeItem("待付款", DealConstant.ORDER_STATUS_WAIT_PAYALL));
        CONSTRACT_STATUS.add(new CodeItem("待发货", DealConstant.ORDER_STATUS_WAIT_DELIVERY));
        CONSTRACT_STATUS.add(new CodeItem("待收货", DealConstant.ORDER_STATUS_TAK_DELIVERY));
        CONSTRACT_STATUS.add(new CodeItem("交易完成", DealConstant.ORDER_STATUS_DELIVERED));
        CONSTRACT_STATUS.add(new CodeItem("转让中", DealConstant.ORDER_STATUS_SELLOUT));
        CONSTRACT_STATUS.add(new CodeItem("已转让", DealConstant.ORDER_STATUS_TRANSFER));
        CONSTRACT_STATUS.add(new CodeItem("已违约", DealConstant.ORDER_STATUS_BREACH));
//        List<TblSptDictionaryEntity> list = DictionaryUtility.getListByCategory("dealOrderStatus");
//        for (TblSptDictionaryEntity entity : list) {
//            CONSTRACT_STATUS.add(new CodeItem(entity.getDictValue(), entity.getDictKey(), entity.getSortIndicator()));
//        }

        FUND_BUSINESS_TYPE.add(new CodeItem("全部", ""));
        FUND_BUSINESS_TYPE.add(new CodeItem("定金", "1"));
        FUND_BUSINESS_TYPE.add(new CodeItem("保证金", "2"));
        FUND_BUSINESS_TYPE.add(new CodeItem("支付余款", "3"));
        FUND_BUSINESS_TYPE.add(new CodeItem("收到余款", "4"));
        FUND_BUSINESS_TYPE.add(new CodeItem("手续费", "5"));

        FUND_TRADE_TYPE.add(new CodeItem("全部", ""));
        FUND_TRADE_TYPE.add(new CodeItem("支出", "E"));
        FUND_TRADE_TYPE.add(new CodeItem("收入", "I"));

        MASTER_USER_DENTITY.add(new CodeItem("交易员", SptConstant.CONFIGGROUPID_TAG_TRADE));
        MASTER_USER_DENTITY.add(new CodeItem("财务", SptConstant.CONFIGGROUPID_TAG_FUND));
        MASTER_USER_DENTITY.add(new CodeItem("单证", SptConstant.CONFIGGROUPID_TAG_DELIVE));
        MASTER_USER_DENTITY.add(new CodeItem("自由人", SptConstant.CONFIGGROUPID_TAG_FREEMAN));
        MASTER_USER_DENTITY.add(new CodeItem("企业管理者", SptConstant.USER_ROLE_COMPANYMASTER));
        MASTER_USER_DENTITY.add(new CodeItem("请登录", SptConstant.USER_ID_ANONYMOUS));

    }

    public static ArrayList<String> keyList(List<CodeItem> list) {
        ArrayList<String> keyList = new ArrayList<>();
        for (CodeItem codeItem : list) {
            keyList.add(codeItem.getKey());
        }
        return keyList;
    }

    public static ArrayList<String> codeList(List<CodeItem> list) {
        ArrayList<String> codeList = new ArrayList<>();
        for (CodeItem codeItem : list) {
            codeList.add(codeItem.getCode());
        }
        return codeList;
    }

    public static String keyToCode(List<CodeItem> list, String key) {
        for (CodeItem item : list) {
            if (item.getKey().equals(key)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String CodeToKey(List<CodeItem> list, String code) {
        for (CodeItem item : list) {
            if (item.getCode().equals(code)) {
                return item.getKey();
            }
        }
        return null;
    }

}
