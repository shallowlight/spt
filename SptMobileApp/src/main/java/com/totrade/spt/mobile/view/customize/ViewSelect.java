package com.totrade.spt.mobile.view.customize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.ConstantArray;
import com.autrade.spt.common.entity.NameValueItem;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.view.R;

public class ViewSelect extends LinearLayout implements OnClickListener {

    private PopupTextView lblTradeMode;
    private PopupTextView lblProductQuality;
    private PopupTextView lblProductPlace;
    private PopupTextView lblDeliveryPlace;
    private PopupTextView lblDeliveryTime;
    private PopupTextView lblDealTime;
    private PopupTextView lblDealStatus;
    private MyEditText txtOppCompanyName;
    // 塑料
    private LinearLayout llDeliveryPlaceName;
    private LinearLayout llBrand;
    private LinearLayout llWhareHouseName;
    private MyEditText txtDeliveryPlaceName;
    private MyEditText txtBrand;
    private MyEditText txtWhareHouseName;

    // Tag标记之前的值(DrawerLayout点击侧边关闭后恢复之前的值)

    private SelectListener listener;
    private View drawView;

    private DrawerLayout drawerLayout;
    private String productType = "HG_FT_BY";
    private static int gravity = Gravity.LEFT;
    private static final String emptyName = "全部";

    private static NameValueItem itemEmpty;// = new NameValueItem(emptyName, null);

    public static final String TAG_NEGO = "tagNego";
    public static final String TAG_ACTIVE = "tagActive";
    public static final String TAG_HISTORY = "tagHistory";
    private String activityTag = TAG_NEGO; // 标记是询价，活跃合同或者历史合同；

    // 切换产品
    public void setProductType(String productType) {
        this.productType = productType;
        boolean isGT = productType.startsWith("GT");
        boolean isSL = productType.startsWith("SL");
        if (isGT) {
            lblProductQuality.setVisibility(View.VISIBLE);
            List<NameValueItem> listProductQuality = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_PRODUCT_QUALITY);
            listProductQuality.remove(0);
            listProductQuality.add(0, itemEmpty);
            lblProductQuality.initDate(listProductQuality, "铁品位", gravity);
        } else {
            lblProductQuality.initDate(null, "质量标准", gravity);
            lblProductQuality.setVisibility(View.GONE);
        }
        txtOppCompanyName.setVisibility(activityTag.equals(TAG_NEGO) ? View.GONE : View.VISIBLE);
        lblDeliveryTime.setVisibility(isSL ? View.GONE : View.VISIBLE); // 塑料不要交货期
        if (isSL && activityTag.equals(TAG_NEGO)) {
            llDeliveryPlaceName.setVisibility(View.VISIBLE);
            llBrand.setVisibility(View.VISIBLE);
            llWhareHouseName.setVisibility(View.VISIBLE);
        } else {
            llDeliveryPlaceName.setVisibility(View.GONE);
            llBrand.setVisibility(View.GONE);
            llWhareHouseName.setVisibility(View.GONE);
        }
        // 在获取配置后设置可见
        lblProductPlace.initDate(null, "产地", gravity);
        lblProductPlace.setVisibility(View.GONE);
        lblDeliveryPlace.initDate(null, "交货地", gravity);
        lblDeliveryPlace.setVisibility(View.GONE);
        //
        // boolean dayDetermine=isSL||isGT &&
        // lblTradeMode.getViewTag().equals(SptConstant.TRADEMODE_FOREIGN);
        boolean dayDetermine = isSL || isGT; // 默认切换产品时钢铁为内贸，故无需考虑外贸的情况；
        lblDeliveryTime.initDate(deliveryDataList(dayDetermine), "交货期", gravity);
        recoverAll();
    }

    // 重置
    private void recoverAll() {
        lblProductQuality.setViewTag(itemEmpty);
        lblProductPlace.setViewTag(itemEmpty);
        lblDeliveryPlace.setViewTag(itemEmpty);
        lblDeliveryTime.setViewTag(itemEmpty);
        lblDealTime.setViewTag(itemEmpty);
        lblDealStatus.setViewTag(itemEmpty);
        lblTradeMode.setViewTag(itemEmpty);
        txtDeliveryPlaceName.setText("");
        txtOppCompanyName.setText("");
        txtBrand.setText("");
        txtWhareHouseName.setText("");
    }

    // 标记哪个页面调用
    public void setActivityTag(String activityTag) {
        this.activityTag = activityTag;
        // 初始设置为可见，activityTag不会变化，故只需要设置隐藏，不需要设置显示
        // 部分选项的可选值也只与activityTag有关，故在此设置
        if (activityTag.equals(TAG_NEGO)) {
            lblDealStatus.setVisibility(View.GONE);
            lblDealTime.setVisibility(View.GONE);
            llDeliveryPlaceName.setVisibility(View.GONE);
            llBrand.setVisibility(View.GONE);
            llWhareHouseName.setVisibility(View.GONE);
            txtOppCompanyName.setVisibility(View.GONE);
        } else {
            gravity = Gravity.RIGHT;
            lblDealTime.initDate(dealTimeLst(), "成交时间", gravity);
            lblDealStatus.initDate(getDealStatus(), "成交状态", gravity);
        }
        lblTradeMode.initDate(getTradeModeList(), "贸易方式", gravity);
        lblTradeMode.setOnTextChangerReturnPreviousListener(new PopupTextView.OnTextChangerReturnPreviousListener() {
            @Override
            public void textChangerReturnPrevious(PopupTextView view, NameValueItem t) {
                if ((productType != null && productType.startsWith("GT"))
                        && ((t == null || t.getValue() == null || t.getValue().length() < 2) || (view.getValue() == null || view.getValue().length() < 2) || (!t.getValue()
                        .substring(0, 2).equals(view.getValue().substring(0, 2))))) {
                    boolean dayDetermine = lblTradeMode.getValue() == null || lblTradeMode.getValue().length() < 2
                            || lblTradeMode.getValue().startsWith(SptConstant.TRADEMODE_DOMESTIC);
                    lblDeliveryTime.initDate(deliveryDataList(dayDetermine), dayDetermine ? "交货期" : "最晚提单日", gravity);
                }
            }
        });
    }

    public String getProductType() {
        return productType;
    }

    public ViewSelect(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ViewSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ViewSelect(Context context) {
        super(context);
        initView(context);
    }

    //
    private void initView(Context context) {
        itemEmpty = new NameValueItem();
        itemEmpty.setName(emptyName);

        View view = LayoutInflater.from(context).inflate(R.layout.myselect, null);
        lblTradeMode = (PopupTextView) view.findViewById(R.id.lblTradeMode);
        lblProductQuality = (PopupTextView) view.findViewById(R.id.lblProductQuality);
        lblProductPlace = (PopupTextView) view.findViewById(R.id.lblProductPlace);
        lblDeliveryPlace = (PopupTextView) view.findViewById(R.id.lblDeliveryPlace);
        lblDeliveryTime = (PopupTextView) view.findViewById(R.id.lblDeliveryTime);
        lblDealTime = (PopupTextView) view.findViewById(R.id.lblDealTime);
        lblDealStatus = (PopupTextView) view.findViewById(R.id.lblDealStatus);
        txtOppCompanyName = (MyEditText) view.findViewById(R.id.txtOppCompanyName);
        txtDeliveryPlaceName = (MyEditText) view.findViewById(R.id.txtDeliveryPlaceName);
        txtBrand = (MyEditText) view.findViewById(R.id.txtBrand);
        txtWhareHouseName = (MyEditText) view.findViewById(R.id.txtWhareHouseName);
        llDeliveryPlaceName = (LinearLayout) view.findViewById(R.id.llDeliveryPlaceName);
        llBrand = (LinearLayout) view.findViewById(R.id.llBrand);
        llWhareHouseName = (LinearLayout) view.findViewById(R.id.llWhareHouseName);
        this.setOnClickListener(null);
        view.findViewById(R.id.lblConfirm).setOnClickListener(this);
        view.findViewById(R.id.lblRecoverAll).setOnClickListener(this);
        txtOppCompanyName.setOnEditorActionListener(searchLister);
        txtDeliveryPlaceName.setOnEditorActionListener(searchLister);
        txtBrand.setOnEditorActionListener(searchLister);
        txtWhareHouseName.setOnEditorActionListener(searchLister);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(view);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public String getSelectStr(String category) {
        // 在initData()设置productType,判断已加载数据是否为当前产品的
        if (StringUtility.isNullOrEmpty(productType)) {
            return "";
        }
        switch (category) {
            case SptConstant.SPTDICT_BUY_SELL:
                if (!StringUtility.isNullOrEmpty(lblTradeMode.getValue()))
                    return lblTradeMode.getValue().split("#")[1];
                else
                    return null;
            case SptConstant.SPTDICT_DELIVERY_PLACE:
                return lblDeliveryPlace.getValue();
            case SptConstant.SPTDICT_PRODUCT_QUALITY:
                return lblProductQuality.getValue();
            case SptConstant.SPTDICT_TRADE_MODE:
                if (!StringUtility.isNullOrEmpty(lblTradeMode.getValue()))
                    return lblTradeMode.getValue().split("#")[0];
                else
                    return null;
            case SptConstant.SPTDICT_PRODUCT_PLACE:
                return lblProductPlace.getValue();
            case "oppCompanyName":
                return txtOppCompanyName.getText().toString().trim();
            case AppConstant.DICT_BRAND:
                return txtBrand.getText().toString().trim();
            case AppConstant.DICT_WARE_HOUSE_NAME:
                return txtWhareHouseName.getText().toString().trim();
            case AppConstant.DICT_DELIVERY_PLACE_NAME:
                return txtDeliveryPlaceName.getText().toString().trim();
            case SptConstant.SPTDICT_BONDPAYSTATUSTAG:
                return lblDealStatus.getValue();
            default:
                return null;
        }
    }

    /**
     * 获取铁品位选择
     */
    public BigDecimal getProductQualityEx1(boolean isFrom) {
        // 在initData()设置productType,判断已加载数据是否为当前产品的
        if (StringUtility.isNullOrEmpty(productType)
                || !productType.startsWith("GT")
                || lblProductQuality.getName().equals(emptyName)
                || lblProductQuality.getName().equals("不限")
                ) {
            return null;
        }
        String qualityEx1Str;
        if (isFrom)
            qualityEx1Str = lblProductQuality.getValue();
        else
            qualityEx1Str = (String) lblProductQuality.getTag();
        if (!StringUtility.isNullOrEmpty(qualityEx1Str))
            return new BigDecimal(qualityEx1Str);
        return null;
    }

    OnEditorActionListener searchLister = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                selectSearch();
            }
            return false;
        }
    };

    // 返回0点0分0秒
    public Date getDealTime() {
        if (StringUtility.isNullOrEmpty(productType) || lblDealTime.getName().equals(emptyName))
            return null;

        Calendar calendar = FormatUtil.date2Calendar(HostTimeUtility.getDate());
        switch (lblDealTime.getName()) {
            case "24小时以内":
                calendar.add(Calendar.HOUR_OF_DAY, -24); // TODO 待确认
                break;
            case "近一周":
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                break;
            case "近一月":
                calendar.add(Calendar.MONTH, -1);
                break;
            case "近三月":
                calendar.add(Calendar.MONTH, -3);
                break;
            default:
                calendar = null;
                break;
        }
        if (calendar != null)
            return FormatUtil.dateDayOfFirstMillisecond(calendar.getTime());
        return null;
    }

    public Date getDeliveryTime(boolean isStart) {
        if (StringUtility.isNullOrEmpty(productType) || productType.startsWith("SL") // 塑料不要交货期
                || lblDeliveryTime.getName().equals(emptyName)) {
            return null;
        }
        if (productType.startsWith("GT") && lblTradeMode.getValue().startsWith(SptConstant.TRADEMODE_DOMESTIC)) {
            Date date = FormatUtil.dateDayOfFirstMillisecond(HostTimeUtility.getDate());
            Calendar calendar = FormatUtil.date2Calendar(date);

            if (isStart) // 交货期起始
            {
                if (lblDeliveryTime.getName().equals("大于10日")) {
                    calendar.add(Calendar.DAY_OF_MONTH, 10);
                }
            } else
            // 交货期终止
            {
                switch (lblDeliveryTime.getName()) {
                    case "当日":
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.MILLISECOND, -1);
                        break;
                    case "5日内":
                        calendar.add(Calendar.DAY_OF_MONTH, 5);
                        calendar.add(Calendar.MILLISECOND, -1);
                        break;
                    case "10日内":
                        calendar.add(Calendar.DAY_OF_MONTH, 10);
                        calendar.add(Calendar.MILLISECOND, -1);
                        break;
                    case "大于10日":
                        calendar.set(Calendar.YEAR, 2098);
                        break;
                    default:
                        calendar = null;
                        break;
                }

            }
            if (calendar != null)
                return calendar.getTime();
            else
                return null;
            // 钢铁nei'm
        } else {
            // 钢铁外贸、化工
            String dateStr = lblDeliveryTime.getName();
            Calendar calendar = FormatUtil.string2Calendar(dateStr.substring(0, 7), "yyyy/MM");
            if (isStart) {
                if (dateStr.endsWith("上")) {
                    calendar.set(Calendar.DAY_OF_MONTH, 1); // 某月1日00:00:00
                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, 16); // 某月16日00:00:00
                }
            } else {
                if (dateStr.endsWith("上")) {
                    calendar.set(Calendar.DAY_OF_MONTH, 16);
                    calendar.add(Calendar.MILLISECOND, -1); // 某月15日23:59:59:999
                } else {
                    calendar.add(Calendar.MONTH, 1);
                    calendar.add(Calendar.MILLISECOND, -1); // 某月最后一天23:59:59:999
                }
            }
            return calendar.getTime();
        }
    }

    public void setDrawerLayout(DrawerLayout drawerLayout, View drawView) {
        this.drawerLayout = drawerLayout;
        this.drawView = drawView;
        if (drawView == null) {
            this.drawView = this;
        }
    }

    /**
     * 初始化数据
     */
    public void initData(String productType) {
        if (!productType.equals(this.productType))
            return;
        List<NameValueItem> listProductPlace = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_PRODUCT_PLACE);
        listProductPlace.add(0, itemEmpty);
        lblProductPlace.initDate(listProductPlace, "产地", gravity);
        lblProductPlace.setVisibility(View.VISIBLE); // 切换产品时设置不可见
        if (!productType.startsWith("SL")) {
            List<NameValueItem> listDeliveryPlace = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_DELIVERY_PLACE);
            listDeliveryPlace.add(0, itemEmpty);
            lblDeliveryPlace.initDate(listDeliveryPlace, "交货地", gravity);
            lblDeliveryPlace.setVisibility(View.VISIBLE);
        }
        if (!productType.startsWith("GT")) {
            List<NameValueItem> listProductQuality = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_PRODUCT_QUALITY);
            listProductQuality.add(0, itemEmpty);
            lblProductQuality.initDate(listProductQuality, "质量标准", gravity);
            lblProductQuality.setVisibility(View.VISIBLE);
        }

    }

    public interface SelectListener {
        void selectDone(ViewSelect select);
    }

    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lblConfirm) {
            selectSearch();
        }
        if (v.getId() == R.id.lblRecoverAll) {
            recoverAll();
        }
    }

    private void selectSearch() {
        // oppCompanyName = txtOppCompanyName.getText().toString();
        // deliveryPlaceName = etDeliveryPlaceName.getText().toString();
        // shopSign = etShopSign.getText().toString();
        // whareHouse = etWhareHouse.getText().toString();
        // itemBuySell = popBuySell.getNameValueItem();
        // itemDeliveryPlace = popDeliveryPlace.getNameValueItem();
        // itemDeliveryTime = popDeliveryTime.getNameValueItem();
        // itemProductPlace = popProductPlace.getNameValueItem();
        // itemQuality = popQuality.getNameValueItem();
        // itemStatus = popStatus.getNameValueItem();
        // itemTradeMode = popTradeMode.getNameValueItem();
        // itemQualityEx1 = popQualityEx1.getNameValueItem();
        // itemDoneTime = popDoneTime.getNameValueItem();

        if (drawerLayout != null && drawView != null) {
            drawerLayout.closeDrawer(drawView);
        }
        if (listener != null) {
            listener.selectDone(this);
        }
    }

    /**
     * 获取交货期可选数据
     *
     * @return 交货期
     */
    private static List<NameValueItem> deliveryDataList(boolean dayDetermine) {
        List<NameValueItem> lst = new ArrayList<>();
        lst.add(itemEmpty);
        if (dayDetermine) // 塑料和钢铁内贸
        {
            String[] ss = {"当日", "5日内", "10日内", "大于10日"};
            NameValueItem item;
            for (String s : ss) {
                item = new NameValueItem();
                item.setName(s);
                lst.add(item);
            }
            return lst;
        } else {
            int maxMonth = 6;
            Calendar c = Calendar.getInstance();
            c.setTime(HostTimeUtility.getDate());
            int dayNow = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH) + 1; // 月份取值与显示值相差1;
            int year = c.get(Calendar.YEAR);
            NameValueItem item;
            if (dayNow > 15) {
                item = new NameValueItem();
                item.setName(year + "/" + month + "下");
                lst.add(item);
                month++;
                maxMonth--; // 单独加第一个值(除不限)和最后一个值
            }

            for (int i = 0; i < maxMonth; i++) {
                if (month > 12) {
                    year++;
                    month = 1;
                }
                item = new NameValueItem();
                item.setName(year + "/" + month + "上");
                lst.add(item);

                item = new NameValueItem();
                item.setName(year + "/" + month + "下");
                lst.add(item);
                month++;
            }
            if (dayNow >= 15) {
                if (month > 12) {
                    year++;
                    month = 1;
                }
                // 单独加最后一个值
                item = new NameValueItem();
                item.setName(year + "/" + month + "上");
                lst.add(item);
            }
            return lst;
        }

    }

    private List<NameValueItem> getDealStatus() {
        String[] dealStatus = null;
        if (activityTag.equals(TAG_ACTIVE)) {
            dealStatus = new String[]{
                    DealConstant.BONDPAYSTATUS_TAG_DECLAREDELIVERY,   //待宣港
                    DealConstant.BONDPAYSTATUS_TAG_TRANSFER,   //已转让
                    DealConstant.BONDPAYSTATUS_TAG_ALL_PAID,   //待发货
                    DealConstant.BONDPAYSTATUS_TAG_BOND_PAID,   //待付全款
                    DealConstant.BONDPAYSTATUS_TAG_DEAL,   //（待付定金）
                    DealConstant.BONDPAYSTATUS_TAG_BOND_PAYING,   //定金支付中
                    DealConstant.BONDPAYSTATUS_TAG_SENT,   //待收货
                    DealConstant.BONDPAYSTATUS_TAG_SELLOUT,   //转让中
                    DealConstant.BONDPAYSTATUS_TAG_REMAIN_PAYING,   //全款支付中
            };
        } else if (activityTag.equals(TAG_HISTORY)) {
            dealStatus = new String[]{
                    DealConstant.BONDPAYSTATUS_TAG_BOND_VOLATILE,
                    DealConstant.BONDPAYSTATUS_TAG_UNFINISHED,
                    DealConstant.BONDPAYSTATUS_TAG_DELIVERED,
                    DealConstant.BONDPAYSTATUS_TAG_UNPAYREMAIN};
        }

        if (dealStatus != null && dealStatus.length > 0) {
            List<NameValueItem> lst = new ArrayList<>();
            lst.add(itemEmpty);
            NameValueItem item;
            for (String s : dealStatus) {
                item = new NameValueItem();
                item.setName(DictionaryUtility.getValue(SptConstant.SPTDICT_BONDPAYSTATUSTAG, s));
                item.setValue(s);
                lst.add(item);
            }
            return lst;
        }
        return null;
    }

    private List<NameValueItem> getTradeModeList() {
        List<NameValueItem> lst = new ArrayList<>();
        lst.add(itemEmpty);
        String[][] tradeModes = ConstantArray.TRADEMODE_BUYSELL;
        NameValueItem item;
        for (String[] ss : tradeModes) {
            item = new NameValueItem();
            item.setName(ss[1]);
            item.setValue(ss[0]);
            lst.add(item);
        }
        return lst;
    }

    private List<NameValueItem> dealTimeLst() {
        String[] dealTimes = {"不限", "24小时以内", "近一周", "近一月", "近三月"};
        List<NameValueItem> lst = new ArrayList<>();
        NameValueItem item;
        for (String s : dealTimes) {
            // PopupTextView在比较时，只比较了Name或者Value,故此处都设置
            item = new NameValueItem();
            item.setName(s);
            item.setValue(s);
            lst.add(item);
        }
        return lst;
    }
}
