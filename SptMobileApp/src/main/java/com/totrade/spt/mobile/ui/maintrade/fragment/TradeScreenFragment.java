package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.autrade.spt.common.constants.ConfigKey;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.spt.master.entity.QueryProductUpEntity;
import com.autrade.spt.master.service.inf.IProductTypeService;
import com.autrade.spt.zone.dto.QueryPageZoneRequestUpEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.DateUtility;
import com.totrade.spt.mobile.adapter.NameValueAdapter;
import com.totrade.spt.mobile.bean.ZoneScreen;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.FocusContext;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.mainmatch.adapter.PopupWindowHelper;
import com.totrade.spt.mobile.utility.DateUtils;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SysInfoUtil;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;
import com.totrade.spt.mobile.base.BaseSptFragment;
import com.totrade.spt.mobile.ui.maintrade.adapter.ScreenAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 贸易专区筛选界面
 *
 * @author huangxy
 * @date 2017/2/15
 */
public class TradeScreenFragment extends BaseSptFragment<HomeActivity> implements View.OnClickListener {
    private ListView listView;
    private View viewPop;
    private ScreenAdapter screenAdapter;
    private View anchor;
    private boolean isFocus;

    public TradeScreenFragment() {
        setContainerId(R.id.fl_drawer);
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_zone_screen2;
    }

    @Override
    protected void initView() {
//        setTitleToIncludeStatusBar();
        listView = findView(R.id.listView);
        viewPop = findView(R.id.popfullselect);
        anchor = findView(R.id.line);

        viewPop.setVisibility(View.GONE);
        viewPop.setClickable(true);
        findView(R.id.imgBack).setOnClickListener(this);
        findView(R.id.tv_done).setOnClickListener(this);
        findView(R.id.tv_reset).setOnClickListener(this);

    }

    public void setTitleToIncludeStatusBar() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mRootView.getLayoutParams();
        lp.topMargin = SysInfoUtil.getStatusBarHeight();
        mRootView.setLayoutParams(lp);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        zoneScreen = new ZoneScreen();
        QueryPageZoneRequestUpEntity entity = ((TradeFragment) getParentFragment()).getTradeEntity();
        if (entity != null && entity.getProductType() != null) {
            String productType = entity.getProductType();
            if ("NONE".equals(productType)) productType = "HG";
            String topType = ProductTypeUtility.getProductName(productType.substring(0, 2));
            String productName = ProductTypeUtility.getProductName(entity.getProductType());
            if ("NONE".equals(productName)) productName = "不限";

//            当前列表productType
            zoneScreen.industry.setName(topType);
            zoneScreen.industry.setValue(entity.getProductType().substring(0, 2));
            zoneScreen.productType.setName(productName);
            zoneScreen.productType.setValue(entity.getProductType());
        } else { //            没有就设置默认值
            zoneScreen.industry.setName("基础化工");
            zoneScreen.industry.setValue("HG");
            zoneScreen.productType.setName(ZoneScreen.getNoneNameValueItem().getName());
            zoneScreen.productType.setValue(ZoneScreen.getNoneNameValueItem().getValue());
//            zoneScreen.productType.setName("乙二醇");
//            zoneScreen.productType.setValue("HG_CL_YE");
        }

//        品种不限时
        if ("HG".equals(entity.getProductType())) {
            zoneScreen.productType.setName("不限");
            zoneScreen.productType.setValue("NONE");
        }

//        if (isFocus) {
//            zoneScreen.getFiled(1).setValue("NONE");
//            zoneScreen.getFiled(1).setName("不限");
//        }

        screenAdapter = new ScreenAdapter(mActivity, categories, zoneScreen);
        listView.setAdapter(screenAdapter);
        screenAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onItemClick(View v, Object data) {
                showPopView((Integer) data);
            }
        });
    }

    private ZoneScreen zoneScreen;

    /**
     * 显示选择项
     *
     * @param pst 筛选主界面点击位置
     */
    private void showPopView(final int pst) {
        ListView listView = findView(R.id.lvPopBottomCommon);
        ((TextView) findView(R.id.lblPopBottomCommon)).setText(categories[pst]);
        NameValueItem selectedItem = null;
        NameValueAdapter adapter = null;
        final List<NameValueItem> nvIs = new ArrayList<>();
        switch (pst) {
            case 0:     //行业
                List<ProductTypeDto> dtos = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_ZONE, null, 0);
                ((TextView) viewPop.findViewById(R.id.lblPopBottomCommon)).setText(categories[pst]);
                for (ProductTypeDto dto : dtos) {
                    NameValueItem nvI = new NameValueItem();
                    nvI.setName(dto.getTypeName());
                    nvI.setValue(dto.getProductType());
                    nvIs.add(nvI);
                }
                selectedItem = zoneScreen.industry;
                break;
            case 1:     //品种
                if (isFocus) {
                    List<String> focusList = FocusContext.load();
                    for (String type : focusList) {
                        NameValueItem nvI = new NameValueItem();
                        nvI.setName(ProductTypeUtility.getProductName(type));
                        nvI.setValue(type);
                        nvIs.add(nvI);
                    }
                } else {
                    List<ProductTypeDto> dtoss = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_ZONE, zoneScreen.industry.getValue(), 2);
                    for (ProductTypeDto dto : dtoss) {
                        NameValueItem nvI = new NameValueItem();
                        nvI.setName(dto.getTypeName());
                        nvI.setValue(dto.getProductType());
                        nvIs.add(nvI);
                    }
                }
                nvIs.add(0, ZoneScreen.getNoneNameValueItem());
                selectedItem = zoneScreen.productType;
                zoneScreen.updateCfg();
                screenAdapter.notifyDataSetChanged();
                break;
            case 2:     //贸易方式
                if (zoneScreen.productType.getName() == null || "不限".equals(zoneScreen.productType.getName())) {
                    ToastHelper.showMessage("请选择品名");
                    return;
                }
                List<NameValueItem> formConfig = ProductCfgHelper.getFormConfig(zoneScreen.productType.getValue(), SptConstant.SPTDICT_TRADE_MODE);
                nvIs.addAll(formConfig);
                nvIs.add(0, ZoneScreen.getNoneNameValueItem());
                selectedItem = zoneScreen.tradeMode;
                break;
            case 3:     //原产地
                if (zoneScreen.productType.getName() == null || "不限".equals(zoneScreen.productType.getName())) {
                    ToastHelper.showMessage("请选择品名");
                    return;
                }
                List<NameValueItem> listProductPlace = ProductCfgHelper.getFormConfig(zoneScreen.productType.getValue(), SptConstant.SPTDICT_PRODUCT_PLACE);
                nvIs.addAll(listProductPlace);
                nvIs.add(0, ZoneScreen.getNoneNameValueItem());
                selectedItem = zoneScreen.productPlace;
                break;
            case 4:    //质量指标
                if (zoneScreen.productType.getName() == null || "不限".equals(zoneScreen.productType.getName())) {
                    ToastHelper.showMessage("请选择品名");
                    return;
                }
                List<NameValueItem> listProductQuality = ProductCfgHelper.getFormConfig(zoneScreen.productType.getValue(), SptConstant.SPTDICT_PRODUCT_QUALITY);
                nvIs.addAll(listProductQuality);
                nvIs.add(0, ZoneScreen.getNoneNameValueItem());
                selectedItem = zoneScreen.productQuality;
                break;
            case 5:     //交货地
                if (zoneScreen.productType.getName() == null || "不限".equals(zoneScreen.productType.getName())) {
                    ToastHelper.showMessage("请选择品名");
                    return;
                }
                List<NameValueItem> listDeliveryPlace = ProductCfgHelper.getFormConfig(zoneScreen.productType.getValue(), SptConstant.SPTDICT_DELIVERY_PLACE);
                if (listDeliveryPlace != null) nvIs.addAll(listDeliveryPlace);
                nvIs.add(0, ZoneScreen.getNoneNameValueItem());
                selectedItem = zoneScreen.deliveryPlace;
                break;
            case 6:     //交货期
                if (zoneScreen.productType.getName() == null || "不限".equals(zoneScreen.productType.getName())) {
                    ToastHelper.showMessage("请选择品名");
                    return;
                }
                final List<NameValueItem> delivertyTimes = new ArrayList<>();
                for (int i = 0; i < deliveryTimeType.length; i++) {
                    NameValueItem n = new NameValueItem();
                    n.setName(deliveryTimeType[i]);
                    n.setTag(i);
                    delivertyTimes.add(n);
                }
                nvIs.addAll(delivertyTimes);
                nvIs.add(0, ZoneScreen.getNoneNameValueItem());
                selectedItem = zoneScreen.deliveryTime;
                break;
            default:

                break;
        }

        adapter = new NameValueAdapter(mActivity, nvIs);
        adapter.setSelectItem(selectedItem);
        listView.setAdapter(adapter);
        adapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onItemClick(View v, Object data) {
                String text = ((TextView) v).getText().toString();
                if (deliveryTimeType[0].equals(text)) {    //近期
                    showPop(true);
                } else if (deliveryTimeType[1].equals(text)) {     //远期
                    showPop(false);
                } else {
                    zoneScreen.setFiled(pst, nvIs.get((Integer) data));
                    screenAdapter.notifyDataSetChanged();
                    viewPop.setVisibility(View.GONE);
                }
            }
        });
        viewPop.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                viewPop.setVisibility(View.GONE);
                break;
            case R.id.tv_done:
                done();
                break;
            case R.id.tv_reset:
                initData();
                break;
        }
    }

    /**
     * 提交筛选条件
     */
    private void done() {
        QueryPageZoneRequestUpEntity upEntity = new QueryPageZoneRequestUpEntity();
        upEntity.setTradeMode(zoneScreen.tradeMode.getValue());
        upEntity.setProductQuality(zoneScreen.productQuality.getValue());
        upEntity.setProductPlace(zoneScreen.productPlace.getValue());
        upEntity.setDeliveryPlace(zoneScreen.deliveryPlace.getValue());
        if ("不限".equals(zoneScreen.productType.getName())) {
            upEntity.setProductType("NONE");
        } else {
            upEntity.setProductType(zoneScreen.productType.getValue());
        }

        String llDelivertTimeTextValue = zoneScreen.deliveryTime.getName();
        if ("今日".equals(zoneScreen.deliveryTime.getName())){
            llDelivertTimeTextValue = DateUtility.formatToStr(HostTimeUtility.getDate(),"yyyy/MM/dd");
        }
        if (!"不限".equals(llDelivertTimeTextValue)) {
//            if (LoginUserContext.speicalProduct(upEntity.getProductType())) {
//                upEntity.setDeliveryTime(FormatUtil.disp2DeliveryDateSpeicalProduct(llDelivertTimeTextValue));
//            } else {
//                upEntity.setDeliveryTime(FormatUtil.disp2DeliveryDate(llDelivertTimeTextValue));      //交货期
//            }
            upEntity.setDeliveryTime(FormatUtil.disp2DeliveryDate(llDelivertTimeTextValue));      //交货期
            if (llDelivertTimeTextValue.contains("上") || llDelivertTimeTextValue.contains("中") ||
                    llDelivertTimeTextValue.contains("下")) {
                upEntity.setUpOrDown(caculatorUpOrDown(llDelivertTimeTextValue));
            }
        }

        ((TradeFragment) getParentFragment()).setScreenEntity(upEntity);
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
            LogUtils.e(PopupWindowHelper.class.getSimpleName(), "caculatorUpOrDown Exception");
            e.printStackTrace();
        }

        return umd;
    }

    private String[] categories = new String[]{"行业", "品名", "贸易方式", "产地", "质量标准", "交货地", "交收期"};

    private PopupWindow popupWindow;
    private String[] deliveryTimeType = new String[]{"立即交付", "远期交付"};

    /**
     * 近期远期列表弹窗
     *
     * @param b
     */
    public void showPop(boolean b) {
        View view = View.inflate(mActivity, R.layout.popfullselect, null);
        TextView title = (TextView) view.findViewById(R.id.lblPopBottomCommon);
        ListView lv = (ListView) view.findViewById(R.id.lvPopBottomCommon);
        view.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDismiss();
            }
        });
        title.setText(b ? deliveryTimeType[0] : deliveryTimeType[1]);

        final List<NameValueItem> deliveryTime = new ArrayList<>();
        for (int i = 0; i < deliveryTimeType.length; i++) {
            NameValueItem n = new NameValueItem();
            n.setName(deliveryTimeType[i]);
            n.setTag(i);
            deliveryTime.add(n);
        }

        new DeliveryTimeListener(lv, b);

        popupWindow = new PopupWindow(view, DensityUtil.dp2px(mActivity, 250), ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popupWindow.showAsDropDown(anchor);
    }

    //    近期远期列表弹窗适配器
    private List<String> deliveryTimeList = null;

    class DeliveryTimeListener {
        private ListView lv;

        public DeliveryTimeListener(ListView lv, boolean b) {
            this.lv = lv;
            getZoneDeliveryTime(b);
        }

        //获取近期远期列表并刷新listview
        private void getZoneDeliveryTime(boolean b) {
            if (b) {
                getProductConfig(lv);
            } else {
                deliveryTimeList = ProductCfgHelper.getZoneDeliveryTime(zoneScreen.productType.getName(), b);
                refreshDeliveryTime(lv);
            }
        }
    }

    private boolean popDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;

            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popDismiss();
    }

    /**
     * 根据productType获取交收时间
     */
    private void getProductConfig(final ListView lv) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<NameValueItem>>() {
            @Override
            public List<NameValueItem> requestService() throws DBException, ApplicationException {
                QueryProductUpEntity upEntity = new QueryProductUpEntity();
                upEntity.setConfigKey(ConfigKey.FORM_CONF_SPOTDAYSCFG);
                upEntity.setProductType(zoneScreen.productType.getValue());
                return Client.getService(IProductTypeService.class).getProductConfig(upEntity);
            }

            @Override
            public void onDataSuccessfully(List<NameValueItem> obj) {
                if (null != obj && obj.size() > 0) {
                    String num = obj.get(0).getValue();
                    deliveryTimeList = DateUtils.getPreviousDaysList(HostTimeUtility.getDate(), Integer.parseInt(num));
                    refreshDeliveryTime(lv);
                }
            }
        });
    }

    private void refreshDeliveryTime(ListView lv) {
        List<NameValueItem> dtos = new ArrayList<>();
        for (int j = 0; j < deliveryTimeList.size(); j++) {
            NameValueItem typeDto = new NameValueItem();
            typeDto.setName(deliveryTimeList.get(j));
            typeDto.setValue(deliveryTimeList.get(j));
            String curDateStr = DateUtility.formatToStr(HostTimeUtility.getDate(),"yyyy/MM/dd");
            if (deliveryTimeList.get(j).equals(curDateStr)){
                typeDto.setName("今日");
            }
            dtos.add(typeDto);
        }
        NameValueAdapter valueAdapter = new NameValueAdapter(mActivity, dtos);
        lv.setAdapter(valueAdapter);

        valueAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onItemClick(View v, Object data) {
                NameValueItem name = new NameValueItem();
                name.setName(((TextView) v).getText().toString());
                zoneScreen.setFiled(zoneScreen.getList().size() - 1, name);
                screenAdapter.notifyDataSetChanged();
                popDismiss();
                viewPop.setVisibility(View.GONE);
            }
        });
    }
}
