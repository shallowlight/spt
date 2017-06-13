package com.totrade.spt.mobile.entity.nim;


import com.alibaba.fastjson.JSONObject;

/**
 * IM聊天order消息体
 *
 * @author huangxy
 * @date 2016/9/2
 */
public class OrderAttachment extends CustomAttachment {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bizId;        // 当为C是为询价Id，当D是存储为合同id
    private String negoStatus;    // 询价状态:U-未确认，C-未成交，D-已成交
    private String productName;    // 商品名称
    private String productType;    // 商品类别
    private String tradeMode;    // 贸易方式（中文）
    private String fromAccid;    // 交易员用户id
    private String toAccid;        // 对方用户id
    private String buySell;            // 买卖方向
    private String numberUnit;        // 数量单位
    private String priceUnit;        // 价格单位
    private String productNumber;    // 产品数量
    private String productPrice;    // 产品价格
    private String deliveryTime;    // 交货时间 long
    private String deliveryTimeStr;    // 交货时间

    private String productQuality;    // 商品品质
    private String productQualityEx1;    // 商品品质铁品位
    private String productPlace;        // 商品产地
    private String deliveryPlace;    // 交(到)货地
    private String bond;    // 保证金(百分比)

    private int type;           // 1-猜拳，2-阅后即焚，3-本地图片，4-白板，11-询价

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getNegoStatus() {
        return negoStatus;
    }

    public void setNegoStatus(String negoStatus) {
        this.negoStatus = negoStatus;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTradeMode() {
        return tradeMode;
    }

    public void setTradeMode(String tradeMode) {
        this.tradeMode = tradeMode;
    }

    public String getFromAccid() {
        return fromAccid;
    }

    public void setFromAccid(String fromAccid) {
        this.fromAccid = fromAccid;
    }

    public String getToAccid() {
        return toAccid;
    }

    public void setToAccid(String toAccid) {
        this.toAccid = toAccid;
    }

    public String getBuySell() {
        return buySell;
    }

    public void setBuySell(String buySell) {
        this.buySell = buySell;
    }

    public String getNumberUnit() {
        return numberUnit;
    }

    public void setNumberUnit(String numberUnit) {
        this.numberUnit = numberUnit;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryTimeStr() {
        return deliveryTimeStr;
    }

    public void setDeliveryTimeStr(String deliveryTimeStr) {
        this.deliveryTimeStr = deliveryTimeStr;
    }

    public String getProductQuality() {
        return productQuality;
    }

    public void setProductQuality(String productQuality) {
        this.productQuality = productQuality;
    }

    public String getProductQualityEx1() {
        return productQualityEx1;
    }

    public void setProductQualityEx1(String productQualityEx1) {
        this.productQualityEx1 = productQualityEx1;
    }

    public String getProductPlace() {
        return productPlace;
    }

    public void setProductPlace(String productPlace) {
        this.productPlace = productPlace;
    }

    public String getDeliveryPlace() {
        return deliveryPlace;
    }

    public void setDeliveryPlace(String deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
    }

    public String getBond() {
        return bond;
    }

    public void setBond(String bond) {
        this.bond = bond;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public OrderAttachment() {
        super(CustomAttachmentType.Type);
    }

    private final String KEY_BIZID = "bizId";
    private final String KEY_NEGOSTATUS = "negoStatus";
    private final String KEY_PRODUCTNAME = "productName";
    private final String KEY_PRODUCTTYPE = "productType";
    private final String KEY_TRADEMODE = "tradeMode";
    private final String KEY_FROMACCID = "fromAccid";
    private final String KEY_TOACCID = "toAccid";
    private final String KEY_TYPE = "type";
    private final String KEY_BUYSELL = "buySell";

    private final String KEY_NUMUNIT = "numberUnit";
    private final String KEY_PRICEUNIT = "priceUnit";
    private final String KEY_PRIDUCTNUMBER = "productNumber";
    private final String KEY_PRIODUCTPRICE = "productPrice";
    private final String KEY_DELIVERYTIME = "deliveryTime";
    private final String KEY_DELIVERYTIMESTR = "deliveryTimeStr";

    private final String KEY_PRODUCTQUALITY = "productQuality";
    private final String KEY_PRODUCTQUALITYEX1 = "productQualityEx1";
    private final String KEY_PRODUCTPLACE = "productPlace";
    private final String KEY_DELIVERYPLACE = "deliveryPlace";
    private final String KEY_BOND = "bond";

    @Override
    protected void parseData(JSONObject data) {
        this.bizId = data.getString(KEY_BIZID);
        this.negoStatus = data.getString(KEY_NEGOSTATUS);
        this.productName = data.getString(KEY_PRODUCTNAME);
        this.productType = data.getString(KEY_PRODUCTTYPE);
        this.tradeMode = data.getString(KEY_TRADEMODE);
        this.fromAccid = data.getString(KEY_FROMACCID);
        this.toAccid = data.getString(KEY_TOACCID);
        this.buySell = data.getString(KEY_BUYSELL);
        this.type = data.getInteger(KEY_TYPE);

        this.numberUnit = data.getString(KEY_NUMUNIT);
        this.priceUnit = data.getString(KEY_PRICEUNIT);
        this.productNumber = data.getString(KEY_PRIDUCTNUMBER);
        this.productPrice = data.getString(KEY_PRIODUCTPRICE);
        this.deliveryTime = data.getString(KEY_DELIVERYTIME);
        this.deliveryTimeStr = data.getString(KEY_DELIVERYTIMESTR);

        this.productQuality = data.getString(KEY_PRODUCTQUALITY);
        this.productQualityEx1 = data.getString(KEY_PRODUCTQUALITYEX1);
        this.productPlace = data.getString(KEY_PRODUCTPLACE);
        this.deliveryPlace = data.getString(KEY_DELIVERYPLACE);
        this.bond = data.getString(KEY_BOND);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_BIZID, bizId);
        data.put(KEY_NEGOSTATUS, negoStatus);
        data.put(KEY_PRODUCTNAME, productName);
        data.put(KEY_PRODUCTTYPE, productType);
        data.put(KEY_TRADEMODE, tradeMode);
        data.put(KEY_FROMACCID, fromAccid);
        data.put(KEY_TOACCID, toAccid);
        data.put(KEY_BUYSELL, buySell);
        data.put(KEY_TYPE, type);

        data.put(KEY_NUMUNIT, numberUnit);
        data.put(KEY_PRICEUNIT, priceUnit);
        data.put(KEY_PRIDUCTNUMBER, productNumber);
        data.put(KEY_PRIODUCTPRICE, productPrice);
        data.put(KEY_DELIVERYTIME, deliveryTime);
        data.put(KEY_DELIVERYTIMESTR, deliveryTimeStr);

        data.put(KEY_PRODUCTQUALITY, productQuality);
        data.put(KEY_PRODUCTQUALITYEX1, productQualityEx1);
        data.put(KEY_PRODUCTPLACE, productPlace);
        data.put(KEY_DELIVERYPLACE, deliveryPlace);
        data.put(KEY_BOND, bond);

        return data;
    }
}
