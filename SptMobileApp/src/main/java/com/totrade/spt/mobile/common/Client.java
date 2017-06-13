package com.totrade.spt.mobile.common;

import java.util.HashMap;
import java.util.Map;

import com.autrade.stage.droid.common.ObjectFactory;
import com.autrade.stage.droid.helper.InjectionHelper;
import com.totrade.mebius.helper.ServiceRegisterHelper;
import com.totrade.spt.mobile.bean.Address;
import com.totrade.spt.mobile.utility.LogUtils;

public class Client {

    private static final Map<String, Object> serviceMap = new HashMap<>();
    private static Address address; // 网络地址

    public static Address getAddress() {
        return address;
    }

    public static void setAddress(Address address) {
        ServiceRegisterHelper.setRemoteServiceAddress(address.getIP(), address.getPort());
        Client.address = address;
    }

    public static String getH5Url(String msgId) {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.MESSAGE_H5_URL + msgId + ".a";
    }

    public static String getNewsDetailUrl(String notifyId) {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.NEWS_DETAIL_URL + notifyId;
    }

    public static String getNewsImgUrl(String imgId) {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.NEWS_IMG_URL + imgId + ".png";
    }

    public static String getNewsListUrl() {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.NEWS_H5_URL;
    }

    public static String getActionUrl(String imgId) {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.NEWS_ACTION_URL + imgId;
    }

    public static String getRuleUrl() {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.TRADE_RULE;
    }

    public static String getRuleUrl2() {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.TRADE_RULE2;
    }

    public static String getPointShopUrl() {
        return "http://" + address.getWebIP() + ":" + address.getWebPort() + AppConstant.POINT_SHOP_URL;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {
		if (!ClientHelper.getServicemapstub().containsKey(clazz.getName()))
		{
			throw new RuntimeException("Do you forget to implements the interface --- " + clazz.getSimpleName());
		}
        Class<? extends T> claxx = (Class<T>) ClientHelper.getServicemapstub().get(clazz.getName());

        /*  //		ObjectFactory 已经存储service对象
		if (serviceMap.containsKey(clazz.getName()))
		{
			return (T) serviceMap.get(clazz.getName());
		}
		T service = (T) ObjectFactory.getObject(claxx);
		InjectionHelper.injectAllInjectionFields(service);
		serviceMap.put(clazz.getName(), service);
        */

        T service = (T) ObjectFactory.getObject(claxx);
        InjectionHelper.injectAllInjectionFields(service);
		
        return service;
    }

}
