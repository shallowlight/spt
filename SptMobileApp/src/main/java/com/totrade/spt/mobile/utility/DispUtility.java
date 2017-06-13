package com.totrade.spt.mobile.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.autrade.spt.bank.constants.BankConstant;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.stage.utility.DateUtility;
import com.totrade.spt.mobile.common.ConstantArray;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.view.R;

/**
 * 负责界面显示格式的转换等
 *
 * @author wangzq
 */
public final class DispUtility {
    private final static BigDecimal KILO = new BigDecimal(1000);
    private final static BigDecimal TEN_KILO = new BigDecimal(10000);
    private final static DecimalFormat format = new DecimalFormat("#0.#######");

    private DispUtility() {
    }

    /**
     * 数量转换显示用格式
     *
     * @param productNumber 数量；
     * @param numberUnit    数量单位
     * @return 数量显示值
     */
    private static String productNum2Disp(BigDecimal productNumber, String numberUnit) {
        if (productNumber == null) {
            return null;
        }
        if (numberUnit == null)
            numberUnit = "";
        return (productNumber.compareTo(KILO) >= 0) ?
                (format.format(productNumber.divide(TEN_KILO)) + "万" + numberUnit) :
                (format.format(productNumber) + numberUnit);
    }

    /**
     * 数量转换显示用格式
     *
     * @param priceUnit  价格;
     * @param numberUnit 数量单位;
     * @return 价格显示值
     */
    public static String price2Disp(BigDecimal productPrice, String priceUnit, String numberUnit) {
        if (productPrice == null) {
            return null;
        }
        String productPriceStr = format.format(productPrice);
        numberUnit = DictionaryUtility.getValue(SptConstant.SPTDICT_NUMBER_UNIT, numberUnit);
        priceUnit = DictionaryUtility.getValue(SptConstant.SPTDICT_PRICE_UNIT, priceUnit);
        if (priceUnit == null) {
            return productPriceStr;
        } else if (numberUnit == null) {
            return priceUnit + productPrice;
        } else {
            return priceUnit + productPriceStr + "/" + numberUnit;
        }
    }

    /**
     * 数量转换显示用格式  格式为  剩余数量(含单位)/数量(含单位)
     *
     * @param remainNum  剩余数量,可以设置为null
     * @param numberUnit 数量单位
     *                   数量单位
     * @return 数量显示值
     */
    public static String productNum2Disp(@Nullable BigDecimal remainNum, @Nullable BigDecimal productNum, @Nullable String numberUnit) {
        numberUnit = DictionaryUtility.getValue(SptConstant.SPTDICT_NUMBER_UNIT, numberUnit);

        String productNumDisp = productNum2Disp(productNum, numberUnit);
        String remainNumDisp = productNum2Disp(remainNum, numberUnit);
        if ((productNumDisp != null) && (remainNumDisp != null)) {
            return remainNumDisp + "/" + productNumDisp;
        } else if (remainNumDisp != null) {
            return remainNumDisp;
        } else if (productNumDisp != null) {
            return productNumDisp;
        } else {
            return null;
        }
    }

    /**
     * 交货期转换成显示用的格式
     *
     * @param deliveryTime 交货期
     * @param productType  产品名称
     * @param tradeMode    贸易方式
     * @return 交货期显示值
     */
    public static String deliveryTimeToDisp(Date deliveryTime, String productType, String tradeMode) {
        boolean isFO = (tradeMode != null) && tradeMode.equals(SptConstant.TRADEMODE_FOREIGN);    //默认为内贸，专区的时候可能传入null
        if (productType.startsWith("SL")) {
            return DateUtility.formatToStr(deliveryTime, "yyyy/MM/dd") + "前";
        }
        if (productType.startsWith("GT") && !isFO) {
            return DateUtility.formatToStr(deliveryTime, "yyyy/MM/dd");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deliveryTime);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return DateUtility.formatToStr(deliveryTime, "yyyy/MM") + (day <= 15 ? "上" : "下");
    }

    /**
     * 专区交货期显示值
     *
     * @param deliveryTime 交货期
     * @param productType  产品类型
     * @param tradeMode    贸易方式
     * @param requestType  远期与现货
     * @return 交货期显示值
     */
    public static
    @NonNull
    String zoneDeliveryTime2Disp(@NonNull Date deliveryTime, @NonNull String requestType, String productType, String tradeMode) {
        if (SptConstant.REQUEST_REQUESTTYPE_F.equals(requestType)) {
            //远期
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deliveryTime);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return DateUtility.formatToStr(deliveryTime, "yyyy/MM") + (day <= 15 ? "上" : "下");
        } else {
            //现货
            return DateUtility.formatToStr(deliveryTime, "yyyy/MM/dd");
        }
    }


    public static
    @NonNull
    String zoneDealTime2Disp(@Nullable Date dealTime) {
        if ((dealTime == null) || (dealTime.getTime() < (3600 * 1000 * 24)))    //一般为0,系统初始时间
        {
            return "";
        }
        long dealTimeL = HostTimeUtility.getDate().getTime() - dealTime.getTime();
        if (dealTimeL >= 0) {
            return "";
        }
        int hour = (int) (dealTimeL / (3600 * 1000));
        int min = (int) (dealTimeL % (3600 * 1000)) / (60 * 1000);
        if (hour == 0) {
            return min + "分钟前成交";
        } else if (hour < 24) {
            return hour + "小时前成交";
        } else {
            return "1天前前成交";
        }
    }


    public static String validTime2Disp(Date validTime) {
        long validTimeLong = validTime.getTime() - HostTimeUtility.getDate().getTime();
        if (validTimeLong <= 0) {
            return "失效";
        }

        int hour = (int) (validTimeLong / (3600 * 1000));
        int min = (int) (validTimeLong % (3600 * 1000)) / (60 * 1000);
        return hour + "小时" + min + "分";
    }

    /**
     * 供应商等级
     */
    public static String supLevel2Disp(String level) {
        if (TextUtils.isEmpty(level)) return "未设置";
        for (String[] ss : ConstantArray.SUPLEVELS) {
            if (level.equals(ss[0])) {
                return ss[1];
            }
        }
        return "未设置";
    }

    /**
     * 供应商等级
     */
    public static int supLevel2DispImg(String level) {
        if (TextUtils.isEmpty(level)) {
            return 0;
        }
        int res;
        switch (level) {
            case SptConstant.SUPLEVEL_ZS:
                res = R.drawable.img_level1;
                break;
            case SptConstant.SUPLEVEL_HG:
                res = R.drawable.img_level2;
                break;
            case SptConstant.SUPLEVEL_XX:
                res = R.drawable.img_level3;
                break;
            case SptConstant.SUPLEVEL_HD:
                res = R.drawable.img_level4;
                break;
            case SptConstant.SUPLEVEL_LC:
                res = R.drawable.img_level5;
                break;
            case SptConstant.SUPLEVEL_LD:
                res = R.drawable.img_level6;
                break;
            case SptConstant.SUPLEVEL_BLACK:
                res = R.drawable.img_level7;
                break;
            default:
                res = 0;
                break;
        }
        return res;
    }

    /**
     * 交货方式，付款方式最后显示到界面的效果
     */
    public static String value2DispImgStr(String value) {
        String[][] cfg_imgStr = ConstantArray.CFG_IMGSTR;
        for (String[] strs : cfg_imgStr) {
            if (strs[0].equals(value)) {
                return strs[1];
            }
        }
        return "";
    }


    public static String paySystem2DispIco(String key) {
        if (TextUtils.isEmpty(key))
            return "";
        switch (key) {
            case BankConstant.PAY_SYSTEM_CITIC:
                return "中";
            case BankConstant.PAY_SYSTEM_PINGAN:
                return "平";
            default:
                return "";
        }
    }

    public static String paySystem2Disp(String key) {
        if (TextUtils.isEmpty(key))
            return "";
        switch (key) {
            case BankConstant.PAY_SYSTEM_CITIC:
                return "中信银行";
            case BankConstant.PAY_SYSTEM_PINGAN:
                return "平安银行";
            default:
                return "";
        }
    }
}
