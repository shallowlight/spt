package com.totrade.spt.mobile.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Timothy on 2017/4/12.
 */

public class DecimalUtils {

    private static NumberFormat currency = NumberFormat.getCurrencyInstance();//货币格式化引用
    private static NumberFormat integer = NumberFormat.getIntegerInstance();
    private static DecimalFormat decimalFormat = new DecimalFormat();

    private static final double Billion = 100000000;
    private static final int TenThousand = 10000;
    private static final int Thousand = 1000;

    private static final String CURRENCY_STYLE_SPACE = "### ### ### ##0.00";
    private static final String CURRENCY_STYLE_COMMA = "###,###,###,##0.00";

    /**
     * 将bigDecimal四舍五入保留两位小数输出BigDecimal
     */
    public static BigDecimal keepNumPointDecimal(int num, BigDecimal decimal) {
        return decimal.setScale(num, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 将bigDecimal四舍五入保留两位小数输出BigDecimal
     */
    public static BigDecimal keep2PointDecimal(BigDecimal decimal) {
        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 将bigDecimal保留两位小数输出Double
     */
    public static double keep2PointDouble(BigDecimal decimal) {
        return keep2PointDecimal(decimal).doubleValue();
    }

    /**
     * 将bigDecimal保留两位小数输出String
     */
    public static String keep2PointString(BigDecimal decimal) {
        return String.valueOf(keep2PointDouble(decimal));
    }

    /**
     * 将bigDecimal保留两位小数输出String(携带货币标识)
     *
     * @param decimal
     * @return
     */
    public static String keep2PointStringToCurrency(BigDecimal decimal) {
        return currency.format(keep2PointDouble(decimal));
    }

    /**
     * 将bigDecimal保留两位小数并携带指定单位
     */
    public static String keep2PointStringWithUnit(BigDecimal decimal, String unit) {
        return String.valueOf(keep2PointDouble(decimal)) + unit;
    }

    /**
     * 将bigDecimal保留两位小数并用逗号分隔并携带指定单位
     */
    public static String keep2PointStringAndCommaWithUnit(BigDecimal decimal, String unit) {
        decimalFormat.applyPattern(CURRENCY_STYLE_COMMA);
        return decimalFormat.format(keep2PointDouble(decimal)) + unit;
    }

    /**
     * 将bigDecimal保留两位小数并携带指定单位(携带货币标识)
     */
    public static String keep2PointStringToCurrencyWithUnit(BigDecimal decimal, String unit) {
        return keep2PointStringToCurrency(decimal) + unit;
    }


    /**
     * 将bigDecimal四舍五入保留0位小数输出BigDecimal
     */
    public static BigDecimal keepIntPointDecimal(BigDecimal decimal) {
        return new BigDecimal(decimal.intValue());
    }

    /**
     * 将bigDecimal保留0位小数输出Double
     */
    public static double keepIntPointDouble(BigDecimal decimal) {
        return keepIntPointDecimal(decimal).intValue();
    }

    /**
     * 将bigDecimal保留0位小数输出String
     */
    public static String keepIntPointString(BigDecimal decimal) {
        return String.valueOf(keepIntPointDouble(decimal));
    }

    /**
     * 将bigDecimal保留0位小数输出String(携带货币标识)
     *
     * @param decimal
     * @return
     */
    public static String keepIntPointStringToCurrency(BigDecimal decimal) {
        return currency.format(keepIntPointDouble(decimal));
    }

    /**
     * 将bigDecimal保留0位小数并携带指定单位
     */
    public static String keepIntPointStringWithUnit(BigDecimal decimal, String unit) {
        return String.valueOf(keepIntPointDouble(decimal)) + unit;
    }

    /**
     * 将bigDecimal保留0位小数并携带指定单位(携带货币标识)
     */
    public static String keepIntPointStringToCurrencyWithUnit(BigDecimal decimal, String unit) {
        return keepIntPointStringToCurrency(decimal) + unit;
    }

    /**
     * 将bigDecimal除一亿
     */
    public static String decimalToBillionWithUnit(BigDecimal bigDecimal, String unit) {
        BigDecimal billionDecimal = bigDecimal.divide(new BigDecimal(Billion));
        Long longDecimal = billionDecimal.longValue();
        return billionDecimal.longValue() + unit;
    }

    /**
     * 将bigDecimal除一万
     */
    public static String decimalToTenThousandWithUnit(BigDecimal bigDecimal, String unit) {
        BigDecimal billionDecimal = bigDecimal.divide(new BigDecimal(TenThousand));
        Long longDecimal = billionDecimal.longValue();
        return billionDecimal.longValue() + unit;
    }

    /**
     * 将bigDecimal除一千
     */
    public static String decimalToThousandWithUnit(BigDecimal bigDecimal, String unit) {
        BigDecimal bigDecimal1 = new BigDecimal(Thousand);
        return keep2PointStringWithUnit(bigDecimal.divide(bigDecimal1), unit);
    }

}
