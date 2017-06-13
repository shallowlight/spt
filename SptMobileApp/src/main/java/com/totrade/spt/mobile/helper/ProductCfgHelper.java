package com.totrade.spt.mobile.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.autrade.spt.common.constants.ConfigKey;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.FormConfigEntity;
import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.spt.master.entity.QueryProductUpEntity;
import com.autrade.spt.master.service.inf.IProductTypeService;
import com.autrade.spt.nego.entity.FormConfigDiffUpEntity;
import com.autrade.spt.nego.entity.TblTemplateEntity;
import com.autrade.spt.nego.service.inf.IRequestFormConfigService;
import com.autrade.stage.utility.CollectionUtility;
import com.autrade.stage.utility.JsonUtility;
import com.google.gson.reflect.TypeToken;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.ConstantArray;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.GTDeliveryPlaceEntity;
import com.totrade.spt.mobile.entity.NameValueTreeEntity;
import com.totrade.spt.mobile.utility.DictionaryUtility;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class ProductCfgHelper implements SptConstant, ConfigKey {
    //KEY：产品类别
    //VALUE: 产品挂单界面配置
    private static Map<String, FormConfigEntity> formConfigData;                //界面配置数据
    //KEY：产品类别
    //VALUE: 合同模板列表
    private static Map<String, List<TblTemplateEntity>> templateData;           //合同模板配置数据

    private static Map<String, List<NameValueItem>> formCfgMap;                 //界面配置数据,单个配置

    //KEY:productType + "#" + category + "#" + tradeMode
    private static Map<String, Map<String, Double>> mapNumberCfg;               //数字类型的配置，价格数量铁品位

    static {
        formConfigData = new HashMap<>();
        templateData = new HashMap<>();
        formCfgMap = new HashMap<>();
        mapNumberCfg = new HashMap<>();
    }

    /**
     * 是否加载了某产品的界面配置
     */
    public static boolean isCfgLoaded(String productType) {
        return formConfigData.get(productType) != null
                && templateData.get(productType) != null;
    }

    public static FormConfigEntity getCfgEntity(@NonNull String productType) {
        return formConfigData.get(productType);
    }

    /**
     * 获取产品的 TemplateId
     */
    public static String getTemplateId(String productType, String tradeMode, String bond) {
        if (!TextUtils.isEmpty(bond) && (bond.startsWith("-") || bond.equals("0"))) {
            if (productType.startsWith("SL"))
                return "CH_BOND_0_SL";
            else if (productType.startsWith("GT"))
                return "CH_BOND_0_GT";
            else
                return "CH_BOND_0";
        } else {
            List<TblTemplateEntity> lst = templateData.get(productType);
            for (TblTemplateEntity entity : lst) {
                //需要注意的是 name值 为 entity.getTemplateName().replace(".html", "")
                if (TRADEMODE_DOMESTIC.equals(tradeMode) && entity.getTemplateId().startsWith("CH")) {
                    return entity.getTemplateId();
                }
                if (TRADEMODE_FOREIGN.equals(tradeMode) && entity.getTemplateId().startsWith("EN")) {
                    return entity.getTemplateId();
                }
            }
        }
        return null;
    }

    public static String getTemplateId(String productType, String buySell) {
        return SptConstant.BUYSELL_SELL.equals(buySell) ? "SCF_TO_S" : "SCF_TO_B";
    }

    public static String getCfgId(String productType) {
        if (formConfigData.get(productType) != null)
            return formConfigData.get(productType).getFormConfigId();
        return null;
    }


    /**
     * 询价获取fee
     */
    public static BigDecimal getCfgFee(String productType) {
        FormConfigEntity entity = formConfigData.get(productType);
        if (entity != null) {
            return entity.getFeeCfg();
        } else {
            requestCfgByCategory(productType, AppConstant.CFG_FEE, null);
            NameValueItem item = formCfgMap.get(getMapKey(productType, AppConstant.CFG_FEE, null)).get(0);
            return new BigDecimal(item.getValue());
        }
    }

    /**
     * 加载界面配置
     */
    public static boolean tryLoadCfg(String productType) {
        if (isCfgLoaded(productType)) return true;
        FormConfigDiffUpEntity upEntity2 = new FormConfigDiffUpEntity();
        upEntity2.setProductType(productType);
        upEntity2.setVersion(0);
        try {
            //获取产品配置
            FormConfigEntity formConfigEntity = Client.getService(IRequestFormConfigService.class).getFormConfigDiff(upEntity2);
            //加入产品界面配置
            formConfigData.put(productType, formConfigEntity);
            //调用服务获得撮合合同模板
            List<TblTemplateEntity> templateList = Client.getService(IRequestFormConfigService.class).getTemplateSummaryByProductType(productType);
            templateData.put(productType, templateList);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 获取 铁品位，数量，价格 范围和小数位
     */
    public static Map<String, Double> getNumFormCfg(@NonNull String productType, @NonNull String category, @Nullable String tradeMode) {
        FormConfigEntity formConfigEntity = formConfigData.get(productType);
        //没有则请求单个
        if (formConfigEntity == null) {
            requestCfgByCategory(productType, category, null);
            if (!distinguishTradeMode(category)) {
                tradeMode = null;
            }
            return mapNumberCfg.get(getMapKey(productType, category, tradeMode));
        }

        //取出category对应的json
        String json = formConfigEntity2Json(formConfigEntity, category, false);
        //价格区分内外贸
        if (category.equals(AppConstant.CFG_PRODUCTPRICE)) {
            if (TextUtils.isEmpty(json)) {
                json = AppConstant.PRICEJSONCFG;
            }
            Map<String, Map<String, Double>> map = JsonUtility.toJavaObject(json, typePriceMap);
            return map.get(tradeMode);
        }
        return JsonUtility.toJavaObject(json, typeNumberMap);
    }


    /**
     * 根据category从formConfigEntity  中取出 配置的jsonc串
     *
     * @param formConfigEntity 网络获取的配置
     * @param category         类别
     * @return category 对应的配置
     */
    private static String formConfigEntity2Json(@NonNull FormConfigEntity formConfigEntity, @NonNull String category, boolean isJson) {
        String categoty2 = isJson ? category + ConstantArray.CATEGORY_JSON : category;
        switch (categoty2) {
            case SPTDICT_PRODUCT_QUALITY:
                return formConfigEntity.getQualityCfg();
            case SPTDICT_PAY_METHOD:
                return formConfigEntity.getPayMethodCfg();
            case SPTDICT_PAY_METHOD + ConstantArray.CATEGORY_JSON:
                return formConfigEntity.getPayMethodJsonCfg();
            case SPTDICT_PRODUCT_PLACE:
                return formConfigEntity.getProductPlaceCfg();
            case SPTDICT_DELIVERY_PLACE:
                return formConfigEntity.getDeliveryPlaceCfg();
            case SPTDICT_DELIVERY_PLACE + ConstantArray.CATEGORY_JSON:
                return formConfigEntity.getDeliveryPlaceJsonCfg();
            case AppConstant.CFG_ZONE_BOND:
                return formConfigEntity.getBondCfg();
            case AppConstant.CFG_ZONE_BOND + ConstantArray.CATEGORY_JSON:
                return formConfigEntity.getBondJsonCfg();
            case AppConstant.CFG_NEGO_BOND:
                return formConfigEntity.getNegoBondCfg();
            case AppConstant.CFG_NEGO_BOND + ConstantArray.CATEGORY_JSON:
                return formConfigEntity.getNegoBondJsonCfg();
            case SPTDICT_TRADE_MODE:
                return formConfigEntity.getTradeModeCfg();
            case SPTDICT_DELIVERY_MODE:
                return formConfigEntity.getDeliveryModeCfg();
            case SPTDICT_DELIVERY_MODE + ConstantArray.CATEGORY_JSON:
                return formConfigEntity.getDeliveryModeJsonCfg();
            case SPTDICT_WAREHOUSE:
                return formConfigEntity.getWareHouseCfg();
            case SPTDICT_NEGOTIATE_MODE:
                return formConfigEntity.getNegotiateModeCfg();
            case AppConstant.CFG_PRODUCTNUMBER:
                return formConfigEntity.getNumberCfg();
            case AppConstant.CFG_PRODUCTPRICE:
                return formConfigEntity.getPriceCfg();
            case AppConstant.CFG_TKGRADE:                        //铁品位小数位与范围
                return formConfigEntity.getTpwCfg();
            case AppConstant.CFG_VALIDHOUR:
                return formConfigEntity.getHourCfg();
            case AppConstant.CFG_VALIDMINUTE:
                return formConfigEntity.getMinuteCfg();
            case AppConstant.CFG_PRODUCTCLASS:
                return formConfigEntity.getProductClassCfg();
            case SPTDICT_NUMBER_UNIT:                            //数量单位
                return formConfigEntity.getNumberUnitCfg();
            default:
        }
        if (isJson) {
            // 部分配置因为加了 categoty2 未获取到则尝试不加
            return formConfigEntity2Json(formConfigEntity, category, false);
        }
        return null;
    }

    /**
     * 获取产品配置
     */
    public static List<NameValueItem> getFormConfig(String productType, @NonNull String category, String tradeMode) {
        if (SPTDICT_TRADE_MODE.equals(category)) return getTradeModeLst();
        if (TextUtils.isEmpty(productType) || category.isEmpty()) return null;
        if (productType.startsWith("GT") && SPTDICT_PRODUCT_QUALITY.equals(category))
            return getQualityEx1Lst();
        if (productType.equals("GT")) productType = "GT_TK";
        FormConfigEntity formConfigEntity = formConfigData.get(productType);
        if (formConfigEntity == null) {
            //未请求过数据或者请求失败则请求单个配置
            requestCfgByCategory(productType, category, tradeMode);
            return getFormConfig2(productType, category, tradeMode);
        }
        //钢铁交货地特殊处理
        if (productType.startsWith("GT") && SPTDICT_DELIVERY_PLACE.equals(category)) {
            List<NameValueTreeEntity> lst = getTKDeliveryPlaceLst(productType);
            if (lst == null || lst.isEmpty()) {
                return null;
            }
            List<NameValueItem> lst2 = new ArrayList<>();
            NameValueItem item;
            for (NameValueTreeEntity entity : lst) {
                if (entity.getLevel() == 2) {
                    item = new NameValueItem();
                    item.setName(entity.getName());
                    item.setValue(entity.getValue());
                    lst2.add(item);
                }
            }
            return lst2;
        }

        String value = formConfigEntity2Json(formConfigEntity, category, isJsonCfg(productType, category));
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        //String 切割成数组再转成NameValueItem
        return json2Split2NameValueList(category, tradeMode, value);
    }


    private static List<NameValueItem> json2Split2NameValueList(@NonNull String category, @Nullable String tradeMode, @NonNull String value) {
        // 区分内外贸的是Json格式,先转成map,再取出内贸或者外贸的值
        if (distinguishTradeMode(category) && !TextUtils.isEmpty(tradeMode)) {
            Map<String, String> map = JsonUtility.toJavaObject(value, typeStringMap);
            if (map == null || map.isEmpty()) {
                return null;
            }
            value = map.get(tradeMode);
        }
        if (TextUtils.isEmpty(value)) return null;
        List<NameValueItem> lst = new ArrayList<>();
        String[] values = value.split("[,]");

        if (category.equals(AppConstant.CFG_NEGO_BOND)
                || category.equals(AppConstant.CFG_ZONE_BOND)) {
            category = SPTDICT_BOND;
        }

        //塑料在上传时需要注意传入值为Name而不是Value
        NameValueItem item;
        for (String v : values) {
            item = new NameValueItem();
            item.setName(DictionaryUtility.getValue(category, v));
            item.setValue(v);
            lst.add(item);
        }
        return lst;
    }

    /**
     * 获取产品配置
     */
    public static List<NameValueItem> getFormConfig(String productType, String category) {
        return getFormConfig(productType, category, null);
    }


    /**
     * 获取铁矿石 交货地
     */
    public static List<NameValueTreeEntity> getTKDeliveryPlaceLst(String productType) {
        if (TextUtils.isEmpty(productType)) return null;
        FormConfigEntity entity = formConfigData.get(productType);
        String json;
        if (entity == null) {
            requestCfgByCategory(productType, SPTDICT_DELIVERY_PLACE, null);
            List<NameValueItem> lst = formCfgMap.get(getMapKey(productType, SPTDICT_DELIVERY_PLACE, null));
            if (CollectionUtility.isNullOrEmpty(lst)) {
                return null;
            }
            json = lst.get(0).getValue();
        } else {
            json = entity.getDeliveryPlaceJsonCfg();
        }
        return deliveryPlaceJson2List(json);
    }


    /**
     * 获取铁矿石 交货地
     */
    public static List<GTDeliveryPlaceEntity> getGTDeliveryPlaceLst(String productType) {
        if (TextUtils.isEmpty(productType)) return null;
        FormConfigEntity entity = formConfigData.get(productType);
        String json;
        if (entity == null) {
            requestCfgByCategory(productType, SPTDICT_DELIVERY_PLACE, null);
            List<NameValueItem> lst = formCfgMap.get(getMapKey(productType, SPTDICT_DELIVERY_PLACE, null));
            if (CollectionUtility.isNullOrEmpty(lst)) {
                return null;
            }
            json = lst.get(0).getValue();
        } else {
            json = entity.getDeliveryPlaceJsonCfg();
        }
        return gtDeliveryPlaceJson2List(json);
    }


    /**
     * Map存储的键
     */
    private static String getMapKey(@NonNull String productType, @NonNull String category, @Nullable String tradeMode) {
        if (TextUtils.isEmpty(tradeMode)) {
            return productType + "#" + category;
        } else {
            return productType + "#" + category + "#" + tradeMode;
        }
    }

    /**
     * 从单个配置中获取 区分内外贸
     */
    private static List<NameValueItem> getFormConfig2(@NonNull String productType, @NonNull String category, @Nullable String tradeMode) {
        if (!distinguishTradeMode(category)) {
            tradeMode = null;
        }
        return formCfgMap.get(getMapKey(productType, category, tradeMode));
    }


    /**
     * 获取 category 对应的 CfgKey
     *
     * @param category    categoty
     * @param productType 产品类别
     * @return category 对应的 CfgKey
     */
    private static String getCfgKey(@NonNull String category, String productType) {
        String[][] cfgKeyAndCategory = ConstantArray.categoryCfgKeys;
        boolean isJson = isJsonCfg(productType, category);
        String categoty2 = isJson ? category + ConstantArray.CATEGORY_JSON : category;

        for (String[] strs : cfgKeyAndCategory) {
            if (strs[0].equals(categoty2)) {
                return strs[1];
            }
        }
        //可能结果是json但是实际ConstantArray.categoryCfgKeys中category并非json开始
        for (String[] strs : cfgKeyAndCategory) {
            if (strs[0].equals(category)) {
                return strs[1];
            }
        }
        return null;
    }

    /**
     * 单个配置请求数据
     */
    private static void requestCfgByCategory(String productType, String category, String tradeMode) {
        if (!distinguishTradeMode(category)) {
            tradeMode = null;
        }
        //如果已经请求过，则不再请求
        if (!CollectionUtility.isNullOrEmpty(formCfgMap.get(getMapKey(productType, category, tradeMode)))) {
            return;
        }
        String cfgKey = getCfgKey(category, productType);
        QueryProductUpEntity upEntity = new QueryProductUpEntity();
        upEntity.setProductType(productType);
        upEntity.setConfigKey(cfgKey);
        upEntity.setTradeMode(tradeMode);
        //如果是negoBondCfg则特殊处理 categort设置为bond
        if (category.equals(AppConstant.CFG_NEGO_BOND) || category.equals(AppConstant.CFG_ZONE_BOND)) {
            upEntity.setDictCat(SptConstant.SPTDICT_BOND);
        } else {
            upEntity.setDictCat(category);
        }
        upEntity.setIsStr(isJsonCfg(productType, category));

        //获取单个配置
        try {
            List<NameValueItem> lstEntities = Client.getService(IProductTypeService.class).getProductConfig(upEntity);
            //如果区分内外贸并且获取的数据只有一条
            if (isJsonCfg(productType, category) && lstEntities.size() == 1) {
                cfgJson2Object(lstEntities.get(0).getValue(), productType, category);
            } else {
                formCfgMap.put(getMapKey(productType, category, tradeMode), lstEntities);
            }
        } catch (Exception e) {
            //
            e.printStackTrace();
        }
    }


    private static List<GTDeliveryPlaceEntity> gtDeliveryPlaceJson2List(String json) {
        if (TextUtils.isEmpty(json)) return null;
        Type type = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, List<String>>>>() {
        }.getType();
        LinkedHashMap<String, LinkedHashMap<String, List<String>>> map = JsonUtility.toJavaObject(json, type);
        if (map == null || map.isEmpty()) return null;
        //遍历取出所有值
        List<GTDeliveryPlaceEntity> list3;
        List<GTDeliveryPlaceEntity> list2;
        List<GTDeliveryPlaceEntity> list1;
        GTDeliveryPlaceEntity entity;

        list1 = new ArrayList<>();
        for (Entry<String, LinkedHashMap<String, List<String>>> entry : map.entrySet()) {

            list2 = new ArrayList<>();
            for (Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                list3 = new ArrayList<>();
                for (String ss : entry2.getValue()) {
                    entity = new GTDeliveryPlaceEntity();
                    entity.setName(DictionaryUtility.getValue(SPTDICT_DELIVERY_PLACE, ss));
                    entity.setValue(ss);
                    list3.add(entity);
                }
                entity = new GTDeliveryPlaceEntity();
                entity.setName(DictionaryUtility.getValue(SPTDICT_DELIVERY_PLACE, entry2.getKey()));
                entity.setValue(entry2.getKey());
                entity.setChildNots(list3);
                list2.add(entity);
            }

            entity = new GTDeliveryPlaceEntity();
            entity.setName(DictionaryUtility.getValue(SPTDICT_DELIVERY_PLACE, entry.getKey()));
            entity.setValue(entry.getKey());
            entity.setChildNots(list2);
            list1.add(entity);
        }
        return list1;
    }


    /**
     * 钢铁交货地 json转 List
     */
    private static List<NameValueTreeEntity> deliveryPlaceJson2List(String json) {
        if (TextUtils.isEmpty(json)) return null;
        Type type = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, List<String>>>>() {
        }.getType();
        LinkedHashMap<String, LinkedHashMap<String, List<String>>> map = JsonUtility.toJavaObject(json, type);
        if (map == null || map.isEmpty()) return null;
        //遍历取出所有值
        List<NameValueTreeEntity> lstEntities = new ArrayList<>();
        String name;
        for (Entry<String, LinkedHashMap<String, List<String>>> entry : map.entrySet()) {
            name = DictionaryUtility.getValue(SPTDICT_DELIVERY_PLACE, entry.getKey());
            lstEntities.add(new NameValueTreeEntity(0, name, entry.getKey(), null));
            for (Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
                name = DictionaryUtility.getValue(SPTDICT_DELIVERY_PLACE, entry2.getKey());
                lstEntities.add(new NameValueTreeEntity(1, name, entry2.getKey(), entry.getKey()));
                for (String ss : entry2.getValue()) {
                    name = DictionaryUtility.getValue(SPTDICT_DELIVERY_PLACE, ss);
                    lstEntities.add(new NameValueTreeEntity(2, name, ss, entry2.getKey()));
                }
            }
        }
        return lstEntities;
    }


    /**
     * 是否区分内外贸
     */
    private static boolean distinguishTradeMode(String category) {
        String[] categorys =
                {
                        SPTDICT_PAY_METHOD,
                        SPTDICT_DELIVERY_MODE,
                        AppConstant.CFG_NEGO_BOND,
                        AppConstant.CFG_ZONE_BOND,
                        AppConstant.CFG_PRODUCTPRICE,
                };

        for (String s : categorys) {
            if (s.equals(category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 请求数据配置是否为json串
     */
    private static boolean isJsonCfg(String productType, String category) {
        String[] categorys =
                {
                        SPTDICT_PAY_METHOD,
                        SPTDICT_DELIVERY_MODE,
                        AppConstant.CFG_ZONE_BOND,
                        AppConstant.CFG_NEGO_BOND,
                        AppConstant.CFG_PRODUCTPRICE,
                        AppConstant.CFG_PRODUCTNUMBER,
                        AppConstant.CFG_TKGRADE
                };
        for (String s : categorys) {
            if (s.equals(category)) {
                return true;
            }
        }
        return productType.startsWith("GT") && category.equals(SPTDICT_DELIVERY_PLACE);
    }

    /**
     * 询价交货期列表list
     *
     * @param productType
     * @param tradeMode
     * @return
     */
    @NonNull
    public static ArrayList<String> getNegoDeliveryTime(String productType, String tradeMode) {
        int MONTH = 6;
        boolean isDM = tradeMode.equals(SptConstant.TRADEMODE_DOMESTIC);
        // 塑料或者钢铁内贸是精确到天
        boolean isAccurateDay = productType.startsWith("SL") || (productType.startsWith("GT") && isDM);
        ArrayList<String> deliveryTimes = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        if (isAccurateDay) {    //塑料或者钢铁
            int dayNum;
            int day1 = calendar.get(Calendar.DAY_OF_YEAR);
            int yearDays = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
            calendar.add(Calendar.MONTH, MONTH);
            int day2 = calendar.get(Calendar.DAY_OF_YEAR);
            if (day2 < day1)        //跨年
            {
                dayNum = yearDays - day1 + day2;
            } else {
                dayNum = day2 - day1;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            calendar = Calendar.getInstance();
            for (int i = 0; i < dayNum; i++) {
                deliveryTimes.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1);
            }
        } else {    //化工
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM", Locale.getDefault());
            boolean down = calendar.get(Calendar.DAY_OF_MONTH) >= 15;
            String str = sdf.format(calendar.getTime());
            for (int i = 0; i < MONTH * 2; i++) {
                if (down) {
                    deliveryTimes.add(str + "下");
                    calendar.add(Calendar.MONTH, 1);
                    str = sdf.format(calendar.getTime());
                } else {
                    deliveryTimes.add(str + "上");
                }
                down = !down;        //每次取反
            }

//            是指定的产品类型
            if ("HG_CL_JC".equals(productType) || "HG_CL_EG".equals(productType) || "HG_FT_BY".equals(productType)) {
                for (int i = 0; i < MONTH * 3; i++) {

                }
            }
        }
        return deliveryTimes;
    }

    /**
     * 甲醇HG_CL_JC，二甘醇HG_CL_EG，苯乙烯HG_FT_BY的交货期，要增加月中的选项：
     * 0-10：上，11-20：中，21-30：下
     * 上：UP，中：MD，下：DOWN
     */

    /**
     * 专区交货期选择
     *
     * @param productType 产品类别
     * @param isSpot      是否是现货
     * @return 可选交货期
     */
    public static
    @NonNull
    List<String> getZoneDeliveryTime(String productType, boolean isSpot) {
        if (isSpot) {
            return getZoneDeliveryTimeSpot(productType);
        } else {
            //  是否特定产品
            if (LoginUserContext.speicalProduct(productType)) {
                return getZoneDeliveryTimeFutureSpecialProduct();
            } else {
                return getZoneDeliveryTimeFuture();
            }
        }
    }

    /**
     * 获取指定产品的交货期列表
     *
     * @return
     */
    public static List<String> getZoneDeliveryTimeFutureSpecialProduct() {
        int MONTH = 6;
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        int today = calendar.get(Calendar.DAY_OF_MONTH);
        String[] umd = new String[]{" 上", " 中", " 下"};
        int j;
        if (today < 11) j = 0;    // 1-10
        else if (today > 20) j = 2;   // 21-31
        else j = 1;    // 11-20

/*      //TODO 不要删除
        List<String> umds = new ArrayList<>();  //上中下  部分
        for (int i = 0; i < MONTH * 3; i++) {
            umds.add(umd[(j + i) % 3]);
        }

        for (String string : umds) {  //日期  部分
            String dateStr = sdf.format(calendar.getTime());
            list.add(dateStr + string);
            if (string.equals(umd[2])) {
                calendar.add(Calendar.MONTH, 1);
            }
        }
*/
        //新需求
        //交货期不可以选择当前日期所在区间,例如今天3月8号交货期则不可以选3月上
        List<String> umds = new ArrayList<>();  //上中下  部分
        for (int i = 0; i < MONTH * 3 + 1; i++) {
            umds.add(umd[(j + i) % 3]);
        }

        for (String string : umds) {  //日期  部分
            String dateStr = sdf.format(calendar.getTime());
            list.add(dateStr + string);
            if (string.equals(umd[2])) {
                calendar.add(Calendar.MONTH, 1);
            }
        }

        list.remove(0);
        return list;
    }


    /**
     * 获取专区远期交货期可选日期
     *
     * @return 交货期远期可选日期
     */

    @NonNull
    private static List<String> getZoneDeliveryTimeFuture() {
        int MONTH = 6;
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        int today = calendar.get(Calendar.DAY_OF_MONTH);
        String[] umd = new String[]{" 上", " 下"};
        int j;
        if (today > 15) j = 1;      // 15日-月底
        else j = 0;                 // 1日-15日

        //新需求
        //交货期不可以选择当前日期所在区间,例如今天3月8号交货期则不可以选3月上
        List<String> umds = new ArrayList<>();  //上下  部分
        for (int i = 0; i < MONTH * 2 + 1; i++) {
            umds.add(umd[(j + i) % 2]);
        }

        for (String string : umds) {  //日期  部分
            String dateStr = sdf.format(calendar.getTime());
            list.add(dateStr + string);
            if (string.equals(umd[1])) {
                calendar.add(Calendar.MONTH, 1);
            }
        }

        list.remove(0);
        return list;
    }


    /**
     * 获取专区现货交货期可选日期
     *
     * @param productType 产品类别
     * @return 现货交货期可选日期
     */
    private static
    @NonNull
    List<String> getZoneDeliveryTimeSpot(String productType) {
        FormConfigEntity configEntity = formConfigData.get(productType);
        int day = 7;
        if (configEntity != null && configEntity.getSpotDaysCfg() != null)    //getSpotDaysCfg 是 Integer 不是int  需要判空
        {
            day = configEntity.getSpotDaysCfg() + 1;        //可选日期 + 当天
        }
        List<String> list = new ArrayList<>();
        // yyyy/MM/dd
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATEFORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < day; i++) {
            list.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return list;
    }


    /**
     * 贸易方式,本地写死数据
     */
    private static
    @NonNull
    List<NameValueItem> getTradeModeLst() {
        String[][] tradeMode = ConstantArray.TRADEMODE_BUYSELL;
        List<NameValueItem> lst = new ArrayList<>();
        NameValueItem item;
        for (String[] ss : tradeMode) {
            item = new NameValueItem();
            item.setName(ss[1]);
            item.setValue(ss[0]);
            lst.add(item);
        }
        return lst;
    }


    /**
     * 铁品位数据，本地写死
     */
    private static
    @NonNull
    List<NameValueItem> getQualityEx1Lst() {
        String[][] ironGrades = ConstantArray.IRON_GRADE;
        List<NameValueItem> lst = new ArrayList<>();
        NameValueItem item;
        for (String[] ss : ironGrades) {
            item = new NameValueItem();
            item.setName(ss[0]);
            item.setValue(ss[1]);
            item.setTag(ss[2]);
            lst.add(item);
        }
        return lst;
    }

    private static final Type typePriceMap = new TypeToken<Map<String, Map<String, Double>>>() {
    }.getType();
    private static final Type typeNumberMap = new TypeToken<Map<String, Double>>() {
    }.getType();
    private static final Type typeStringMap = new TypeToken<Map<String, String>>() {
    }.getType();

    /**
     * 解析json并存储到相应的Map中
     *
     * @param json        json串
     * @param productType 产品类型
     * @param category    配置类别
     */
    private static void cfgJson2Object(@NonNull String json, @NonNull String productType, @NonNull String category) {
        if (AppConstant.CFG_PRODUCTPRICE.equals(category)) {
            //价格，Map<String,Map<String,Double>>
            Map<String, Map<String, Double>> mapPrice = JsonUtility.toJavaObject(json, typePriceMap);
            //有内贸则存储内贸
            if (mapPrice.containsKey(TRADEMODE_DOMESTIC)) {
                mapNumberCfg.put(getMapKey(productType, category, TRADEMODE_DOMESTIC), mapPrice.get(TRADEMODE_DOMESTIC));
            }
            //有外贸则存储内贸
            if (mapPrice.containsKey(TRADEMODE_FOREIGN)) {
                mapNumberCfg.put(getMapKey(productType, category, TRADEMODE_FOREIGN), mapPrice.get(TRADEMODE_FOREIGN));
            }
        } else if (AppConstant.CFG_PRODUCTNUMBER.equals(category) || category.equals(AppConstant.CFG_TKGRADE)) {
            //数量铁品位，Map<String,Double>
            Map<String, Double> mapNumber = JsonUtility.toJavaObject(json, typeNumberMap);
            mapNumberCfg.put(getMapKey(productType, category, null), mapNumber);
        } else {
            //如果区分内外贸,Map<String,String> String 需要切割
            //内贸
            if (json.contains(TRADEMODE_DOMESTIC)) {
                List<NameValueItem> strDM = json2Split2NameValueList(category, TRADEMODE_DOMESTIC, json);
                if (strDM != null && !strDM.isEmpty()) {
                    formCfgMap.put(getMapKey(productType, category, TRADEMODE_DOMESTIC), strDM);
                }
            }
            //如果区分内外贸,Map<String,String> String 需要切割
            //外贸
            if (json.contains(TRADEMODE_FOREIGN)) {
                List<NameValueItem> strFO = json2Split2NameValueList(category, TRADEMODE_FOREIGN, json);
                if (strFO != null && !strFO.isEmpty()) {
                    formCfgMap.put(getMapKey(productType, category, TRADEMODE_FOREIGN), strFO);
                }
            }
            //不区分内外贸
            if (!json.contains(TRADEMODE_DOMESTIC) && !json.contains(TRADEMODE_FOREIGN)) {
                List<NameValueItem> list = json2Split2NameValueList(category, TRADEMODE_FOREIGN, json);
                if (list != null && !list.isEmpty()) {
                    formCfgMap.put(getMapKey(productType, category, null), list);
                }
            }
        }
    }
}
