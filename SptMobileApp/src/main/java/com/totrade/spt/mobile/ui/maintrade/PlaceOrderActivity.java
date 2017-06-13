package com.totrade.spt.mobile.ui.maintrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.ConfigKey;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.spt.common.entity.TblSptDictionaryEntity;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.spt.master.entity.QueryProductUpEntity;
import com.autrade.spt.master.service.inf.IProductTypeService;
import com.autrade.spt.report.dto.NotifyUnReadDownEntity;
import com.autrade.spt.report.entity.QueryNotifyHistoryUpEntity;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.QueryPageZoneCompanyDownEntity;
import com.autrade.spt.zone.dto.ZoneRequestOfferUpEntity;
import com.autrade.spt.zone.dto.ZoneRequestUpEntity;
import com.autrade.spt.zone.entity.TblZoneRequestEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestManager;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.reflect.TypeToken;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.annotation.EditTextValidator;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.GTDeliveryPlaceEntity;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.ui.notifycenter.NotifyActivity;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.utility.DateUtils;
import com.totrade.spt.mobile.utility.DictionaryTools;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.BottomPopSelect;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.DecimalEditText;
import com.totrade.spt.mobile.view.customize.MyEditText;
import com.totrade.spt.mobile.view.customize.TextViewMapItem;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.totrade.spt.mobile.utility.AnnotateUtility.initBindView;

/**
 * 发布买卖挂单
 *
 * @author huangxy
 * @date 2017/4/11
 */
public class PlaceOrderActivity extends BaseActivity implements SptConstant, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    @BindViewId(R.id.iv_back)                                       //back
            ImageView iv_back;
    @BindViewId(R.id.iv_noti)                                       //消息
            ImageView iv_noti;
    @BindViewId(R.id.title)                                         //标题
            TextView title;
    @BindViewId(R.id.tabLayout)                                     //切换买卖单
            TabLayout tabLayout;
    @BindViewId(R.id.llMinDealNum)
    LinearLayout llMinDealNum;                                      //最低成交量条目
    @EditTextValidator(viewName = "最低成交量")
    @BindViewId(R.id.txtMinDealNumber)
    DecimalEditText txtMinDealNumber;                               //最低成交量输入框
    @EditTextValidator(viewName = "价格")
    @BindViewId(R.id.txtProductPrice)
    DecimalEditText txtProductPrice;                                //价格
    @BindViewId(R.id.lblPriceUnit)
    TextView lblPriceUnit;                                          //价格单位
    @EditTextValidator(viewName = "数量")
    @BindViewId(R.id.txtProductNumber)
    DecimalEditText txtProductNumber;                               //数量

    @BindViewId(R.id.rgProductNature)
    RadioGroup rgProductNature;                                     //group
    @BindViewId(R.id.rbSpotProduct)
    RadioButton rbSpotProduct;                                      //立即交付
    @BindViewId(R.id.llProductType)
    TextViewMapItem llProductType;                                  //品名
    @BindViewId(R.id.llTradeMode)
    TextViewMapItem llTradeMode;                                    //贸易方式
    @BindViewId(R.id.llProductPlace)
    TextViewMapItem llProductPlace;                                 //产地
    @BindViewId(R.id.llReservoirArea)
    TextViewMapItem llReservoirArea;                                //库区
    @BindViewId(R.id.llPayMethod)
    TextViewMapItem llPayMethod;                                    //付款方式
    @BindViewId(R.id.llBond)
    TextViewMapItem llBond;                                         //保证金
    @BindViewId(R.id.llDeliveryPlace)
    TextViewMapItem llDeliveryPlace;                                //交货地
    @BindViewId(R.id.llProductQuality)
    TextViewMapItem llProductQuality;                               //质量标准
    @BindViewId(R.id.llDeliveryTime)
    TextViewMapItem llDeliveryTime;                                 //交货期
    @BindViewId(R.id.llDeliveryMode)
    TextViewMapItem llDeliveryMode;                                 //提货方式
    @BindViewId(R.id.llValidTime)
    TextViewMapItem llValidTime;                                    //挂单时效
    //    @BindViewId(R.id.llDealCompanyTags) TextViewMapItem llDealCompanyTags;      //指定供货商企业
    @BindViewId(R.id.lblDealCompanys)
    TextView lblDealCompanys;                                       //指定供货商企业
    @BindViewId(R.id.llDealCompanys)
    View llDealCompanys;
    @BindViewId(R.id.llSeparable)
    LinearLayout llSeparable;                                      //是否可拆分
    @BindViewId(R.id.tbvSeparable)
    SwitchCompat tbvSeparable;                                      //是否可拆分
    @BindViewId(R.id.btnReset)
    Button btnReset;                                                //取消
    @BindViewId(R.id.btnRelease)
    Button btnRelease;                                              //发布
    @BindViewId(R.id.txtMemo)
    MyEditText txtMemo;                                             //备注
    @BindViewId(R.id.cbAgreeContract)
    CheckBox cbAgreeContract;                                       //阅读并同意合同
    @BindViewId(R.id.lblReadContract)
    TextView lblReadContract;                                       //阅读并同意合同

    private String buySell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reset();
    }

    private void reset() {
        setContentView(R.layout.activity_place_order);
        refreshNoti();
        initView();
        initData();
    }

    private void initView() {
        initBindView(this);
        iv_noti.setVisibility(View.VISIBLE);
        iv_noti.setOnClickListener(this);
        title.setText("我要挂单");
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cbAgreeContract.setOnCheckedChangeListener(this);

        tabLayout.addTab(tabLayout.newTab().setText("挂卖单"));
        tabLayout.addTab(tabLayout.newTab().setText("挂买单"));
        buySell = SptConstant.BUYSELL_SELL;
        new IndicatorHelper().setIndicator(tabLayout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                buySell = tab.getPosition() == 0 ? SptConstant.BUYSELL_SELL : SptConstant.BUYSELL_BUY;
                llDealCompanys.setVisibility(SptConstant.BUYSELL_BUY.equals(buySell) ? View.VISIBLE : View.GONE);
                llSeparable.setVisibility(SptConstant.BUYSELL_BUY.equals(buySell) ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        setClickAndViewType();

        llDeliveryTime.getTvSelect().addTextChangedListener(new mTextWatcher());
    }

    class mTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (rbSpotProduct.isChecked()) {
                //交货期选择今天
                if (llDeliveryTime.getTextValue().equals(new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime()))) {
                    llBond.setTextValue("100%");
                    List<NameValueItem> items = new ArrayList<>();
                    NameValueItem nameValueItem = new NameValueItem();
                    nameValueItem.setName("100%");
                    nameValueItem.setValue("100");
                    items.add(nameValueItem);
                    setTextViewMapTag(llBond, items.get(0));
                } else {
                    List<NameValueItem> items = getListSelect(llBond);
                    NameValueItem itemTag = (NameValueItem) llBond.getTag(R.id.viewtag_zonecreat);
                    if (!listHasSameValue(itemTag, items)) {
                        setTextViewMapTag(llBond, items.get(0));
                    }
                }
            } else {
                List<NameValueItem> items = getListSelect(llBond);
                NameValueItem itemTag = (NameValueItem) llBond.getTag(R.id.viewtag_zonecreat);
                if (!listHasSameValue(itemTag, items)) {
                    setTextViewMapTag(llBond, items.get(0));
                }
            }
        }
    }

    /**
     * 需要点击事件的控件 设置点击事件
     */
    private void setClickAndViewType() {
        //单行弹框选择类型点击事件
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindowFromCfgObj((TextViewMapItem) v, v == llDeliveryTime ? DATATAG1 : DATATAG2);
            }
        };
        //单行弹框选择类型条目
        TextViewMapItem[] itemViews = {llProductType, llTradeMode, llProductPlace, llPayMethod, llBond,
                llDeliveryPlace, llProductQuality, llDeliveryMode, llDeliveryTime, llReservoirArea};
        @StringRes int[] keyStrings = {R.string.productName, R.string.tradeMode, R.string.productPlace, R.string.payMethod, R.string.bond,
                R.string.deliverPlace, R.string.productQuality, R.string.deliverMethod, R.string.deliveryTime, R.string.reservoirArea};

        int size = Math.min(itemViews.length, keyStrings.length);
        for (int i = 0; i < size; i++) {
            itemViews[i].setOnClickListener(clickListener);
            itemViews[i].setCategoryAndType(getString(keyStrings[i]), TextViewMapItem.TYPE_SHOW_SELECT);
        }

        //其余控件点击事件
        llValidTime.setCategoryAndType(getString(R.string.validTime), TextViewMapItem.TYPE_SHOW_SELECT);
        llValidTime.setOnClickListener(this);
        lblDealCompanys.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnRelease.setOnClickListener(this);

        lblReadContract.setText(TradeRuleHelper.i.getClickableSpan(this));
        lblReadContract.setMovementMethod(LinkMovementMethod.getInstance());        //超链接可点击
        //是否可用分批切换
        tbvSeparable.setOnCheckedChangeListener(this);//setOnClickToggleListener(this);

        //切换远期现货
        rgProductNature.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeTradeNature();
            }
        });
    }


    /**
     * 初始化产品
     */
    private void initData() {
        //设置产品名称，设置贸易方式(因为设置内外贸会影响其他配置，故在第一次改变产品后再设置）
        List<NameValueItem> list = getListSelect(llProductType);
        if (list == null || list.isEmpty()) {
            return;
        }
        setTextViewMapTag(llProductType, list.get(0));
        setValidTimeView(0, 5);

        ArrayList<TblSptDictionaryEntity> configList = DictionaryTools.i().getConfigList(AppConstant.CFG_RESERVOIRAREA);
        if (configList != null) {
            TblSptDictionaryEntity reservoirArea = configList.get(0);
            llReservoirArea.setTextValue(reservoirArea.getDictValue());
            NameValueItem n = new NameValueItem();
            n.setName(reservoirArea.getDictValue());
            n.setValue(reservoirArea.getDictKey());
            llReservoirArea.setTag(R.id.viewtag_zonecreat, n);
        }
        getNotifyUnReadNum();
    }


    /**
     * 设置有效时效界面显示与tag 标记值
     *
     * @param hour 小时
     * @param minu 分钟
     */
    private void setValidTimeView(int hour, int minu) {
        String text = hour + "小时" + minu + "分钟";
        llValidTime.setTextValue(text);
        llValidTime.setTag(R.id.viewtag_zonecreat, hour);
        llValidTime.setTag(R.id.viewtag_zonecreat2, minu);
    }

    /**
     * TextViewMapItem 控件设置显示与标记选中值
     *
     * @param view   待设置值的控件
     * @param object 设置的值
     */
    private void setTextViewMapTag(TextViewMapItem view, Object object) {
        if (object instanceof CharSequence) {
            view.setTextValue((CharSequence) object);
            view.setTag(R.id.viewtag_zonecreat, object);
        } else if (object instanceof NameValueItem) {
            Object obj = view.getTag(R.id.viewtag_zonecreat);
            if (obj != null && obj.equals(object)) {
                return;
            }
            view.setTextValue(((NameValueItem) object).getName());
            view.setTag(R.id.viewtag_zonecreat, object);
            //处理关联
            if (view == llProductType) {
                //产品相关则先加载配置
                loadProductConfig();
            } else {
                cheangViewTagAfter(view);
            }
        }
    }

    /**
     * 控件点击处理
     *
     * @param v 被点击的控件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_noti:
                startActivity(new Intent(this, NotifyActivity.class));
                break;
            case R.id.llValidTime:
                showPopValidTime();
                break;
            case R.id.lblDealCompanys:
                Intent intent = new Intent(this, ZoneSupplierActivity.class);
                intent.putExtra("productType", getValue(llProductType));
                if (lblDealCompanys.getTag(R.id.viewtag_zonecreat) != null) {
                    intent.putExtra("TAGS", (String) lblDealCompanys.getTag(R.id.viewtag_zonecreat));
                }
                startActivityForResult(intent, REQUESTCODE);
                break;
            case R.id.btnReset:
                reset();
                break;
            case R.id.btnRelease:
                if (!doMyValidation()) {
                    return;
                } else if (!cbAgreeContract.isChecked()) {
                    ToastHelper.showMessage("请阅读并同意规则及协议!");
                    return;
                }
                //弹出Dialog 提示框
                new CustomDialog.Builder(this, getString(R.string.dialog_zone_submitrequest))
                        .setPositiveClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //提交挂单
                                creatRequest(formSubmitUpEntity());
                            }
                        })
                        .create().show();
                break;
            default:
                break;
        }
    }

    /**
     * 发布
     */
    public void creatRequest(final ZoneRequestUpEntity upEntity) {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {   //新报价
                TblZoneRequestEntity zoneRequestEntity = Client.getService(IZoneRequestService.class).getZoneRequestEntity(upEntity);

                if (zoneRequestEntity != null) {
                    if (zoneRequestEntity.getRequestId() != null) {
                        Client.getService(IZoneRequestManager.class).submitZoneOffer(createZoneOffer(upEntity, zoneRequestEntity.getRequestId()));
                    } else {
//                        新增修改专区
                        Client.getService(IZoneRequestManager.class).submitZoneRequest(upEntity);
                    }
                }

                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    ToastHelper.showMessage("提交成功");
                    finish();
                }
            }
        });
    }

    /**
     * 提交专区报价
     *
     * @param upEntity
     * @return
     */
    private ZoneRequestOfferUpEntity createZoneOffer(ZoneRequestUpEntity upEntity, String requestId) {
        ZoneRequestOfferUpEntity zoneRequestOfferUpEntity = new ZoneRequestOfferUpEntity();
        zoneRequestOfferUpEntity.setOfferUserId(upEntity.getRequestUserId());
        zoneRequestOfferUpEntity.setRequestId(requestId);
        zoneRequestOfferUpEntity.setBatchMode(upEntity.getBatchMode());
        zoneRequestOfferUpEntity.setProductNumber(upEntity.getProductNumber());
        zoneRequestOfferUpEntity.setProductPrice(upEntity.getProductPrice());
        zoneRequestOfferUpEntity.setBuySell(upEntity.getBuySell());
        zoneRequestOfferUpEntity.setValidSecond(upEntity.getValidSecond());
        zoneRequestOfferUpEntity.setSource(upEntity.getSource());
        zoneRequestOfferUpEntity.setMinNumber(upEntity.getMinNumber());
        zoneRequestOfferUpEntity.setOfferType("N");
        return zoneRequestOfferUpEntity;
    }


    /**
     * 切换现货与远期
     */
    private void changeTradeNature() {
        // 交货期需要变更
        List<String> list = ProductCfgHelper.getZoneDeliveryTime(getValue(llProductType), rbSpotProduct.isChecked());
        //产品切换时格式不变，判断选项中是否有已设置的值
//        String strDisp = llDelivertTime.getTextValue();
//        for (String str : list) {
//            if (strDisp.equals(str)) {
//                return;
//            }
//        }
        llDeliveryTime.setTag(R.id.viewtag_zonecreat, list.get(0));
        llDeliveryTime.setTextValue(list.get(0));

        if (rbSpotProduct.isChecked()) {
            getProductConfig();
            //交货期选择今天
            if (llDeliveryTime.getTextValue().equals(new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime()))) {
                llBond.setTextValue("100%");
                List<NameValueItem> items = new ArrayList<>();
                NameValueItem nameValueItem = new NameValueItem();
                nameValueItem.setName("100%");
                nameValueItem.setValue("100");
                items.add(nameValueItem);
                setTextViewMapTag(llBond, items.get(0));
            } else {
                List<NameValueItem> items = getListSelect(llBond);
                NameValueItem itemTag = (NameValueItem) llBond.getTag(R.id.viewtag_zonecreat);
                if (!listHasSameValue(itemTag, items)) {
                    setTextViewMapTag(llBond, items.get(0));
                }
            }
        } else {
            List<NameValueItem> items = getListSelect(llBond);
            NameValueItem itemTag = (NameValueItem) llBond.getTag(R.id.viewtag_zonecreat);
            if (!listHasSameValue(itemTag, items)) {
                setTextViewMapTag(llBond, items.get(0));
            }
        }
    }

    /**
     * 根据productType获取交收时间
     */
    private List<String> deliveryDateList;
    private  List<String> getProductConfig() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<NameValueItem>>() {
            @Override
            public List<NameValueItem> requestService() throws DBException, ApplicationException {
                QueryProductUpEntity upEntity = new QueryProductUpEntity();
                upEntity.setConfigKey(ConfigKey.FORM_CONF_SPOTDAYSCFG);
                upEntity.setProductType(getValue(llProductType));
                return Client.getService(IProductTypeService.class).getProductConfig(upEntity);
            }

            @Override
            public void onDataSuccessfully(List<NameValueItem> obj) {
                if (null != obj && obj.size() > 0) {
                    String num = obj.get(0).getValue();
                    deliveryDateList = DateUtils.getPreviousDaysList(HostTimeUtility.getDate(), Integer.parseInt(num));
                }
            }
        });
        return deliveryDateList;
    }

    /**
     * 加载产品相关配置
     */
    private void loadProductConfig() {
        SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                return ProductCfgHelper.tryLoadCfg(getValue(llProductType));
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                //不管失败与否
                if (null == llTradeMode.getTag(R.id.viewtag_zonecreat)) {
                    //内外贸第一次设置值
                    List<NameValueItem> list = getListSelect(llTradeMode);
                    setTextViewMapTag(llTradeMode, list != null ? list.get(0) : null);
                }
                cheangViewTagAfter(llProductType);
            }
        });
    }


    /**
     * 联动引起的变化
     */
    private void cheangViewTagAfter(TextViewMapItem tagChangedView) {
        TextViewMapItem[] items; //需要联动变更的选项
        if (tagChangedView == llProductType)  //产品改变影响产地，定金，交货地，质量标准，提货方式
        {
            items = new TextViewMapItem[]{llProductPlace, llBond, llDeliveryPlace, llProductQuality, llDeliveryMode};
            //清除选中的对手方企业
            setDealCompanys(null);
        } else if (tagChangedView == llTradeMode)   // 贸易方式改变影响:付款方式，提货方式，定金
        {
            items = new TextViewMapItem[]{llPayMethod, llDeliveryMode, llBond};
            //价格单位
            lblPriceUnit.setText(TRADEMODE_DOMESTIC.equals(getValue(llTradeMode)) ? "元/吨" : "美元/吨");
        } else if (tagChangedView == llTradeMode) {

            return;
        } else {
            return;
        }

        List<NameValueItem> list;
        NameValueItem itemTag;
        for (TextViewMapItem itemView : items) {
            list = getListSelect(itemView);
            itemTag = (NameValueItem) itemView.getTag(R.id.viewtag_zonecreat);
            if (list == null || list.isEmpty() || listHasSameValue(itemTag, list)) {
                continue;
            }
            //没有相同值则设置第一个值
            setTextViewMapTag(itemView, list.get(0));
        }
        //切换产品和内外贸都会影响到当前交货期是否可用
        changeTradeNature();
        //价格数量最低成交量的可输入范围
        String productType = getValue(llProductType);
        String tradeMode = getValue(llTradeMode);
        txtProductNumber.setNumCfgMap(ProductCfgHelper.getNumFormCfg(productType, AppConstant.CFG_PRODUCTNUMBER, tradeMode));
        txtProductPrice.setNumCfgMap(ProductCfgHelper.getNumFormCfg(productType, AppConstant.CFG_PRODUCTPRICE, tradeMode));
        txtMinDealNumber.setNumCfgMap(ProductCfgHelper.getNumFormCfg(productType, AppConstant.CFG_PRODUCTNUMBER, tradeMode));

    }

    /**
     * 获取 TextViewMapItem
     *
     * @param itemView 控件
     * @return 选中项对应的value值
     */
    private
    @NonNull
    String getName(View itemView) {
        Object object = itemView.getTag(R.id.viewtag_nego);
        if (object != null && object instanceof NameValueItem) {
            return ((NameValueItem) object).getName();
        } else if (object != null && object instanceof GTDeliveryPlaceEntity) {
            return ((GTDeliveryPlaceEntity) object).getName();
        }
        return "";
    }

    /**
     * 遍历列表中是否已经存在某个值
     *
     * @param itemTag 某个对象
     * @param list    需要遍历的列表
     * @return 是否有相同值
     */
    private boolean listHasSameValue(NameValueItem itemTag, List<NameValueItem> list) {
        if (itemTag == null) return false;
        for (NameValueItem item : list) {
            if (itemTag.equals(item)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 校验所有参数的合法性
     *
     * @return 所有参数是否符合要求
     */
    private boolean doMyValidation() {
        //价格数量最低成交量校验
        if (!AnnotateUtility.verifactEditText(this))     //价格数量最低成交量
        {
            return false;
        }

        if (tbvSeparable.isChecked()) {
            BigDecimal decimalNumber = new BigDecimal(txtProductNumber.getText().toString());
            BigDecimal decimalMinNumber = new BigDecimal(txtMinDealNumber.getText().toString());
            if (decimalNumber.compareTo(decimalMinNumber) < 0) {
                //不限制（数量的一半至数量）范围，留在成交时处理
                ToastHelper.showMessage("最低成交量不能大于总数量");
                return false;
            }
        }
        return true;
    }


    /**
     * 生成新挂单上行实体
     *
     * @return 专区挂单上行实体
     */
    private ZoneRequestUpEntity formSubmitUpEntity() {
        ZoneRequestUpEntity upEntity = new ZoneRequestUpEntity();
        upEntity.setBatchMode(tbvSeparable.isChecked() ? ZoneConstant.REQUEST_BATCHMODE_1 : ZoneConstant.REQUEST_BATCHMODE_0); // 交付批次模式：0: 整批交付（不可拆分），1: 分批交付（可拆分）
        upEntity.setBuySell(buySell);                                                               //买卖方向
        if (tbvSeparable.isChecked()) {
            upEntity.setMinNumber(new BigDecimal(txtMinDealNumber.getText().toString()));           //最小成交量
        }
        upEntity.setProductNumber(new BigDecimal(txtProductNumber.getText().toString()));           //数量
        upEntity.setProductPrice(new BigDecimal(txtProductPrice.getText().toString()));             //价格
        upEntity.setRequestType(rbSpotProduct.isChecked() ? REQUEST_REQUESTTYPE_R : REQUEST_REQUESTTYPE_F);   //挂单类别：N: 现货，F: 远期
        upEntity.setRequestUserId(LoginUserContext.getLoginUserId());                               //挂单人用户ID
        upEntity.setProductType(getValue(llProductType));                                           //商品类别:XX_XX_XX
        upEntity.setProductQuality(getValue(llProductQuality));                                     //商品品质（38）
        upEntity.setProductPlace(getValue(llProductPlace));                                         //商品产地（39）
        upEntity.setReservoirArea(getValue(llReservoirArea));                                       //商品产地（39）
        upEntity.setTradeMode("DM");                                                                //贸易方式
//        upEntity.setPayMethod(getValue(llPayMethod));                                             //付款方式(40)
        upEntity.setDeliveryPlace(getValue(llDeliveryPlace));                                       //交(到)货地(41)
        upEntity.setBond(getValue(llBond));                                                         //保证金比例
        upEntity.setMemo(txtMemo.getText().toString());
        upEntity.setDeliveryMode(getValue(llDeliveryMode));
        int hour = (int) llValidTime.getTag(R.id.viewtag_zonecreat);
        int minute = (int) llValidTime.getTag(R.id.viewtag_zonecreat2);
        int validSecond = hour * 3600 + minute * 60;
        upEntity.setValidSecond(validSecond);                                                        //有效时长,秒
        if (lblDealCompanys.getTag(R.id.viewtag_zonecreat) != null) {
            upEntity.setSupplierCompanyTags((String) lblDealCompanys.getTag(R.id.viewtag_zonecreat));
        }
//        upEntity.setValidTime(new Date(HostTimeUtility.getDate().getTime() + validSecond).getTime());
        upEntity.setSource(ZoneConstant.ZoneSource.Android);                                        //来源

        String llDelivertTimeTextValue = llDeliveryTime.getTextValue();
        if (LoginUserContext.speicalProduct(upEntity.getProductType())) {
            upEntity.setDeliveryTime(FormatUtil.disp2DeliveryDateSpeicalProduct(llDelivertTimeTextValue));
        } else {
            upEntity.setDeliveryTime(FormatUtil.disp2DeliveryDate(llDelivertTimeTextValue));      //交货期
        }
        upEntity.setUpOrDown(caculatorUpOrDown(llDelivertTimeTextValue));

        upEntity.setOfferType("N");

        return upEntity;
    }

    @NonNull
    private String caculatorUpOrDown(String llDelivertTimeTextValue) {
        String umd = null;
        try {
            if (llDelivertTimeTextValue.endsWith("上")) {
                umd = "UP";
            } else if (llDelivertTimeTextValue.endsWith("中")) {
                umd = "MD";
            } else if (llDelivertTimeTextValue.endsWith("下")) {
                umd = "DOWN";
            } else {
                String day = llDelivertTimeTextValue.substring(llDelivertTimeTextValue.length() - 3, llDelivertTimeTextValue.length() - 1);
                Integer i = Integer.valueOf(day);
                if (i < 11) {
                    umd = "UP";
                } else if (i < 21) {
                    umd = "MD";
                } else {
                    umd = "DOWN";
                }
            }
        } catch (NumberFormatException e) {
            LogUtils.e(PlaceOrderActivity.class.getSimpleName(), "caculatorUpOrDown Exception");
            e.printStackTrace();
        }

        return umd;
    }

    /**
     * 获取 TextViewMapItem
     *
     * @param itemView 控件
     * @return 选中项对应的value值
     */
    private
    @NonNull
    String getValue(TextViewMapItem itemView) {
        Object object = itemView.getTag(R.id.viewtag_zonecreat);
        if (object != null && object instanceof NameValueItem) {
            return ((NameValueItem) object).getValue();
        }
        return "";
    }

    private List<NameValueItem> listCreatTradeMode() {
        List<NameValueItem> listTradeMode = new ArrayList<>();
        NameValueItem item1 = new NameValueItem();
        item1.setValue(SptConstant.TRADEMODE_DOMESTIC);
        item1.setName("内贸");
        listTradeMode.add(item1);
        item1 = new NameValueItem();
        item1.setValue(SptConstant.TRADEMODE_FOREIGN);
        item1.setName("外贸");
        listTradeMode.add(item1);
        return listTradeMode;
    }

    /**
     * 控件与可选值关系
     *
     * @param itemView 控件
     * @return 可选值List
     */
    private
    @Nullable
    List<NameValueItem> getListSelect(TextViewMapItem itemView) {
        //产地，定金，交货地，质量标准，提货方式
        NameValueItem item = (NameValueItem) llProductType.getTag(R.id.viewtag_zonecreat);
        String productType;
        String tradeMode = getValue(llTradeMode);
        if (item != null)
            productType = item.getValue();
        else
            productType = "HG_FT_BY";
        String categoty = null;
        switch (itemView.getId()) {
            case R.id.llTradeMode:
                return listCreatTradeMode();
            case R.id.llProductPlace:
                categoty = SPTDICT_PRODUCT_PLACE;
                break;
            case R.id.llPayMethod:
                categoty = SPTDICT_PAY_METHOD;
                break;
            case R.id.llBond:
                categoty = AppConstant.CFG_ZONE_BOND;
                break;
            case R.id.llDeliveryMode:
                categoty = SPTDICT_DELIVERY_MODE;
                break;
            case R.id.llDeliveryPlace:
                categoty = SPTDICT_DELIVERY_PLACE;
                break;
            case R.id.llProductQuality:
                categoty = SPTDICT_PRODUCT_QUALITY;
                break;
            case R.id.llProductType:
                List<ProductTypeDto> list = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_ZONE, "HG", 2);
                return ProductTypeUtility.listDto2NameValueItem(list);
            case R.id.llReservoirArea:
                List<TblSptDictionaryEntity> listReservoirArea = DictionaryTools.i().getConfigList(AppConstant.CFG_RESERVOIRAREA);
                if (listReservoirArea == null)  break;
                List<NameValueItem> valueItemList = new ArrayList<>();
                for (TblSptDictionaryEntity en : listReservoirArea) {
                    NameValueItem n = new NameValueItem();
                    n.setName(en.getDictValue());
                    n.setValue(en.getDictKey());
                    valueItemList.add(n);
                }
                return valueItemList;
            default:
                categoty = null;
                break;
        }
        if (categoty != null) {
            return ProductCfgHelper.getFormConfig(productType, categoty, tradeMode);
        }
        return null;
    }


    /**
     * 获取选项为 String 类型的列表
     *
     * @param itemView 控件
     * @return 可选值
     */
    private
    @NonNull
    List<String> getListSelectString(TextViewMapItem itemView) {
        if (itemView == llDeliveryTime) {
            if (rbSpotProduct.isChecked()){
                return getProductConfig();
            }else{
                return ProductCfgHelper.getZoneDeliveryTime(getValue(llProductType), rbSpotProduct.isChecked());
            }
        }
        //暂时不会执行此处
        return new ArrayList<>();
    }

    private static final String DATATAG1 = "String";
    private static final String DATATAG2 = "NameValueItem";


    BottomPopSelect popItemSelect;

    /**
     * 单条弹框选择
     */
    private void showPopwindowFromCfgObj(final TextViewMapItem itemView, String tag) {
        //1.创建
        if (popItemSelect == null) {
            popItemSelect = new BottomPopSelect(this);
        }

        //2.更新选择项和默认选中值
        if (DATATAG1.equals(tag)) {
            popItemSelect.updateList(getListSelectString(itemView), itemView.getTag(R.id.viewtag_zonecreat));
        } else if (DATATAG2.equals(tag)) {
            List<NameValueItem> items = new ArrayList<>();
            items = getBondPopWindowList(itemView, items);
            popItemSelect.updateList(items, itemView.getTag(R.id.viewtag_zonecreat));
        } else {
            return;
        }
        //3.监听关闭事件
        popItemSelect.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Object object = popItemSelect.getViewTag();
                if (object != null) {
                    setTextViewMapTag(itemView, object);
                }
            }
        });
        //4.显示
        popItemSelect.showAtBottom();

    }

    /**
     * 交货期为今天，并选择立即交付时，定金的特殊选项
     *
     * @param itemView
     * @param items
     * @return
     */
    private List<NameValueItem> getBondPopWindowList(TextViewMapItem itemView, List<NameValueItem> items) {
        if (itemView == llBond) {
            if (rbSpotProduct.isChecked()) {
                //交货期选择今天
                if (llDeliveryTime.getTextValue().equals(new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime()))) {
                    llBond.setTextValue("100%");
                    NameValueItem nameValueItem = new NameValueItem();
                    nameValueItem.setName("100%");
                    nameValueItem.setValue("100");
                    items.add(nameValueItem);
                } else {
                    items = getListSelect(llBond);
                }
            } else {
                items = getListSelect(llBond);
            }
        } else {
            items = getListSelect(itemView);
        }
        return items;
    }

    private OptionsPickerView<Integer> validTimePickerView;

    /**
     * 有效时间选择
     */
    private void showPopValidTime() {
        if (validTimePickerView == null) {
            validTimePickerView = new OptionsPickerView<>(this);
            // 设置标题
            validTimePickerView.setTitle("询价时效");

            // 设置第一第二级数据
            final ArrayList<ArrayList<Integer>> minutessList = new ArrayList<>();
            final ArrayList<Integer> minutesList = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                minutesList.add(i);
            }
            final ArrayList<Integer> houtList = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                houtList.add(i);
                minutessList.add(minutesList);
            }
            validTimePickerView.setPicker(houtList, minutessList, false);

            // 设置是否循环滚动
            validTimePickerView.setCyclic(false);
            // 右侧单位
            validTimePickerView.setLabels("小时", "分钟");
            validTimePickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    int hours = houtList.get(options1);
                    int minutes = minutessList.get(options1).get(option2);
                    setValidTimeView(hours, minutes);
                }
            });
        }

        // 设置初始值
        Object obj = llValidTime.getTag(R.id.viewtag_zonecreat);
        Object obj2 = llValidTime.getTag(R.id.viewtag_zonecreat2);
        if (obj == null || obj2 == null) {
            validTimePickerView.setSelectOptions(0, 5);
        } else {
            validTimePickerView.setSelectOptionsObj((Integer) obj, (Integer) obj2);
        }
        validTimePickerView.show();
    }

    private static final int REQUESTCODE = 0x0099;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE && resultCode > 0 && data != null) {
            String json = data.getStringExtra("Json");
            if (TextUtils.isEmpty(json)) {
                setDealCompanys(null);
            } else {
                Type type = new TypeToken<List<QueryPageZoneCompanyDownEntity>>() {
                }.getType();
                List<QueryPageZoneCompanyDownEntity> list = JsonUtility.toJavaObjectArray(json, type);
                setDealCompanys(list);
            }
        }
    }

    /**
     * 设置指定供应商的显示值与Tags
     *
     * @param list 选择的指定供应商
     */
    private void setDealCompanys(@Nullable List<QueryPageZoneCompanyDownEntity> list) {
        if (list == null || list.isEmpty()) {
            lblDealCompanys.setTag(R.id.viewtag_zonecreat, null);
            lblDealCompanys.setText("");
        } else {
            String tags = "";
            String codes = "";
            for (QueryPageZoneCompanyDownEntity entity : list) {
                tags += SptConstant.SEP + entity.getCompanyTag();
                codes += SptConstant.SEP + entity.getBusinessCode();
            }
            if (tags.startsWith(SptConstant.SEP)) {
                tags = tags.substring(1);
                codes = codes.substring(1);
            }
            lblDealCompanys.setTag(R.id.viewtag_zonecreat, tags);
            lblDealCompanys.setText(codes);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbAgreeContract:
//                lblReadContract.setSelected(isChecked);
                break;
            case R.id.tbvSeparable:
                llMinDealNum.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (isChecked) {
                    llMinDealNum.requestFocus();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (validTimePickerView != null && validTimePickerView.isShowing()) {
                validTimePickerView.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (validTimePickerView != null && validTimePickerView.isShowing()) {
            validTimePickerView.dismiss();
        }

        unregisterReceiver(receiver);
    }

    /**
     * 刷新界面
     */
    private void refreshNoti() {
        receiver = new NotifyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.INTENT_ACTION_NOTI);
        registerReceiver(receiver, filter);
    }

    private NotifyReceiver receiver;

    /**
     * 有新消息时刷新
     */
    private class NotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.INTENT_ACTION_NOTI.equals(intent.getAction())) {
                iv_noti.setSelected(true);
            }
        }
    }

    /**
     * 查询未读消息
     */
    private void getNotifyUnReadNum() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<NotifyUnReadDownEntity>>() {
            @Override
            public List<NotifyUnReadDownEntity> requestService() throws DBException, ApplicationException {
                QueryNotifyHistoryUpEntity upEntity = new QueryNotifyHistoryUpEntity();
                upEntity.setNotifyTo(LoginUserContext.getLoginUserId());
                upEntity.setNotifyChannel("jpush");
                upEntity.setPageSize(100);
                return Client.getService(com.autrade.spt.report.service.inf.INotifyService.class).findNotifyUnReadNumList(upEntity);

            }

            @Override
            public void onDataSuccessfully(List<NotifyUnReadDownEntity> obj) {
                if (obj != null) {
                    int num = 0;
                    for (NotifyUnReadDownEntity notifyUnReadDownEntity : obj) {
                        if (notifyUnReadDownEntity.getCategory().equals("uncategory")) continue;
                        num += notifyUnReadDownEntity.getUnReadNum();
                    }
                    if (num > 0) {
                        iv_noti.setSelected(true);
                    } else {
                        iv_noti.setSelected(false);
                    }
                }
            }
        });
    }

}
