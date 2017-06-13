package com.totrade.spt.mobile.bean;

import com.autrade.spt.common.entity.NameValueItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 专区筛选  formBean
 */
public class ZoneScreen {
    public NameValueItem industry;      //行业
//    public NameValueItem broadType;      //类别
    public NameValueItem productType;      //品名
    public NameValueItem tradeMode;      //贸易方式
    public NameValueItem productQuality;      //质量标准
    public NameValueItem productPlace;      //原产地
    public NameValueItem deliveryPlace;      //交货地
    public NameValueItem deliveryTime;      //交收期

    private List<NameValueItem> list ;

    public List<NameValueItem> getList() {
        return list;
    }

    public ZoneScreen() {
        industry = new NameValueItem();
        productType = new NameValueItem();
        tradeMode = new NameValueItem();
        productQuality = new NameValueItem();
        productPlace = new NameValueItem();
        deliveryPlace = new NameValueItem();
        deliveryTime = new NameValueItem();

        copyProperties(tradeMode, getNoneNameValueItem());
        updateCfg();

        list = new ArrayList<>();
        list.add(industry);
        list.add(productType);
        list.add(tradeMode);
        list.add(productPlace);
        list.add(productQuality);
        list.add(deliveryPlace);
        list.add(deliveryTime);
    }

    public NameValueItem getFiled(int p) {
        return list.get(p);
    }

    /**
     * 设置指定索引的过滤条件的值
     * @param p 指定的索引
     * @param nvi 键值对实体
     */
    public void setFiled(int p, NameValueItem nvi) {
        copyProperties(list.get(p), nvi);
    }

    /**
     * 等于重置
     */
    public void updateCfg() {
        copyProperties(productQuality, getNoneNameValueItem());
        copyProperties(productPlace, getNoneNameValueItem());
        copyProperties(deliveryPlace, getNoneNameValueItem());
        copyProperties(deliveryTime, getNoneNameValueItem());
    }

    public static void copyProperties(NameValueItem dest, NameValueItem orig) {
        dest.setName(orig.getName());
        dest.setValue(orig.getValue());
        dest.setNameEn(orig.getNameEn());
        dest.setTag(orig.getTag());
    }

    public static NameValueItem getNoneNameValueItem() {
        NameValueItem none = new NameValueItem();
        none.setName("不限");
        return none;
    }
}
