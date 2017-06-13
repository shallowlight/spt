package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.dto.ZoneRequestOfferUpEntity;
import com.autrade.spt.zone.dto.ZoneRequestUpEntity;
import com.autrade.spt.zone.entity.TblZoneRequestEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestManager;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.bigkoo.pickerview.OptionsPickerView;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.annotation.EditTextValidator;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.ui.maintrade.TradeRuleHelper;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.Temp;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.DecimalEditText;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 商品专区 加入卖或者买
 *
 * @author huangxy
 * @date 2016/12/6
 */
public class JoinSellOrBuyFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    private BaseActivity activity;
    private View rootView;

    @BindViewId(R.id.tv_order_num)
    private TextView tv_order_num;      //订单号
    @BindViewId(R.id.tv_deliveryplace)
    private TextView tv_deliveryplace;      //交货地
    @BindViewId(R.id.tv_product_quality)
    private TextView tv_product_quality;      //质量标准
    @BindViewId(R.id.tv_bond)
    private TextView tv_bond;      //定金
    @BindViewId(R.id.tv_price)
    private TextView tv_price;      //价格
    @BindViewId(R.id.rgRequest)
    private RadioGroup rgRequest;
    @BindViewId(R.id.view_Choose)
    private View view_Choose;
    @BindViewId(R.id.llAgreePartDeal)
    private View llAgreePartDeal;
    @EditTextValidator(viewName = "价格")
    @BindViewId(R.id.txtProductPrice)
    private DecimalEditText txtProductPrice;
    @EditTextValidator(viewName = "数量")
    @BindViewId(R.id.txtProductNumber)
    private DecimalEditText txtProductNumber;
    @BindViewId(R.id.lblPriceUnit)
    private TextView lblPriceUnit;
    @BindViewId(R.id.lblValidTime)
    private TextView lblValidTime;
    @BindViewId(R.id.lblPriceAll)
    private TextView lblPriceAll;
    @BindViewId(R.id.cbAgreePartDeal)
    private CheckBox cbAgreePartDeal;
    @BindViewId(R.id.llMinDealNum)
    private View llMinDealNum;
    @EditTextValidator(viewName = "最小成交量")
    @BindViewId(R.id.txtProductMinNumber)
    private DecimalEditText txtProductMinNumber;
    @BindViewId(R.id.btnRelease)
    private Button btnRelease;
    @BindViewId(R.id.lblOfficeId)
    private TextView lblOfficeId;
    @BindViewId(R.id.cbAgreeContract)
    CheckBox cbAgreeContract;                               //阅读并同意合同
    @BindViewId(R.id.lblReadContract)
    TextView lblReadContract;                               //阅读并同意合同

    private String priceUnit;
    private ZoneRequestDownEntity downEntity;
    //    private MyContractDownEntity contractEntity;
    private String buySell;

    String[] checkStr = new String[]{"创建买单", "库存回补", "新建卖单", "从库存卖出",};

    private boolean needClean;      //标记是否要清楚之前的缓存数据

    public JoinSellOrBuyFragment() {
        setContainerId(R.id.framelayout);
    }

    public void setZoneRequestDownEntity(String buySell, ZoneRequestDownEntity downEntity) {
        this.downEntity = downEntity;
        this.buySell = buySell;
        needClean = true;
        priceUnit = DictionaryUtility.getValue(SptConstant.SPTDICT_PRICE_UNIT, downEntity.getPriceUnit());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_trade_join_buysell, container, false);
        if (savedInstanceState != null) {
            savedInstanceState.clear();
        }

        rootView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.popBack();
            }
        });
        AnnotateUtility.bindViewFormId(this, rootView);

        initSet();

        lblReadContract.setText(TradeRuleHelper.i.getClickableSpan(activity));
        lblReadContract.setMovementMethod(LinkMovementMethod.getInstance());        //超链接可点击
        cbAgreeContract.setOnCheckedChangeListener(this);
        cbAgreePartDeal.setOnCheckedChangeListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHead();
        if (needClean) {
            clean();
            needClean = false;
        }
        cbAgreeContract.setChecked(false);
        rgRequest.check(R.id.rbCreatOffer);
    }

    private void clean() {
//        contractEntity = null;
        view_Choose.setVisibility(View.GONE);
        rgRequest.check(R.id.rbCreatOffer);
        lblOfficeId.setText("");
        txtProductNumber.setText("");
        txtProductPrice.setText("");
        cbAgreePartDeal.setChecked(false);
    }

    private void initEdit() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ProductCfgHelper.getNumFormCfg(downEntity.getProductType(), AppConstant.CFG_PRODUCTPRICE, downEntity.getTradeMode());
                ProductCfgHelper.getNumFormCfg(downEntity.getProductType(), AppConstant.CFG_PRODUCTNUMBER, downEntity.getTradeMode());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //设置最大值最小值，小数位
                txtProductPrice.setNumCfgMap(ProductCfgHelper.getNumFormCfg(downEntity.getProductType(), AppConstant.CFG_PRODUCTPRICE, downEntity.getTradeMode()));
                txtProductNumber.setNumCfgMap(ProductCfgHelper.getNumFormCfg(downEntity.getProductType(), AppConstant.CFG_PRODUCTNUMBER, downEntity.getTradeMode()));
                txtProductMinNumber.setNumCfgMap(ProductCfgHelper.getNumFormCfg(downEntity.getProductType(), AppConstant.CFG_PRODUCTNUMBER, downEntity.getTradeMode()));
            }
        }.execute();
    }

    /**
     * 初始化数据
     */
    private void initSet() {
        lblValidTime.setOnClickListener(this);
        btnRelease.setOnClickListener(this);
        lblOfficeId.setOnClickListener(this);
        rootView.findViewById(R.id.lblChooseRequest).setOnClickListener(this);

        txtProductNumber.addTextChangedListener(textWatcher);                   //设置监听事件，价格数量变化则总价变化
        txtProductPrice.addTextChangedListener(textWatcher);
        lblPriceUnit.setText(downEntity.getTradeMode().equals(SptConstant.TRADEMODE_DOMESTIC) ? "元/吨" : "美元/吨");
        initEdit();
        //设置默认时效
        setValidTimeView(0, 5);
        //设置控件显示与隐藏
        if (SptConstant.BUYSELL_BUY.equals(buySell)) {                          //加入买
            ((TextView) rootView.findViewById(R.id.title)).setText("加入买");
            ((RadioButton) rgRequest.getChildAt(0)).setText(checkStr[0]);
            ((RadioButton) rgRequest.getChildAt(1)).setText(checkStr[1]);
            llAgreePartDeal.setVisibility(View.GONE);
            cbAgreePartDeal.setText("同意拆分采购");
            view_Choose.setVisibility(View.GONE);
        } else {                                                                //加入卖
            ((TextView) rootView.findViewById(R.id.title)).setText("加入卖");
            ((RadioButton) rgRequest.getChildAt(0)).setText(checkStr[2]);
            ((RadioButton) rgRequest.getChildAt(1)).setText(checkStr[3]);
            llAgreePartDeal.setVisibility(View.VISIBLE);
            cbAgreePartDeal.setText("同意拆分销售");
        }
        rgRequest.setOnCheckedChangeListener(this);
    }


    /**
     * 价格数量变化监听，设置总价
     */
    TextWatcher textWatcher = new TextWatcher() {
        private String strPrice;
        private String strNumber;
        private DecimalFormat format = new DecimalFormat("#,##0.##");

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            strPrice = txtProductPrice.getText().toString();
            strNumber = txtProductNumber.getText().toString();
            if (strNumber.isEmpty() || strPrice.isEmpty()) {
                lblPriceAll.setText(String.format("%s0", priceUnit));
            } else {
                lblPriceAll.setText(String.format("%s%s", priceUnit, format.format(new BigDecimal(strPrice).multiply(new BigDecimal(strNumber)))));
            }
        }
    };

    private void setHead() {
        //订单号
        tv_order_num.setText(ContractHelper.instance.getOrderId(downEntity.getZoneOrderNoDto()));
//        交货地
        String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, downEntity.getDeliveryPlace());
        if (downEntity.getProductType().startsWith("SL") && TextUtils.isEmpty(deliveryPlace)) {
            deliveryPlace = downEntity.getDeliveryPlaceName();
        }
        tv_deliveryplace.setText(deliveryPlace);

        // 质量标准字段(塑料显示牌号，钢铁显示铁品位)
        if (downEntity.getProductType().startsWith("GT")) {
            String qualityEx1 = new DecimalFormat("#0.#######").format(downEntity.getProductQualityEx1());
            tv_product_quality.setText(qualityEx1);
        } else if (downEntity.getProductType().startsWith("SL")) {
            tv_product_quality.setText(downEntity.getBrand());
        } else {
            tv_product_quality.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, downEntity.getProductQuality()));
        }

        //定金
        tv_bond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, downEntity.getBond()));

        //价格
        if (downEntity.getDealPrice() == null) {
            tv_price.setTextColor(activity.getResources().getColor(R.color.ltBlack));
            tv_price.setText("暂无");
            tv_price.setCompoundDrawables(null, null, null, null);
        } else {
            String price = DispUtility.price2Disp(downEntity.getDealPrice(), downEntity.getPriceUnit(), downEntity.getNumberUnit());
            if (downEntity.getIncAmount() == null || downEntity.getIncAmount().compareTo(BigDecimal.ZERO) == 0) {
                tv_price.setText(String.format("%s-", price));
                tv_price.setTextColor(activity.getResources().getColor(R.color.ltBlack));
                tv_price.setCompoundDrawables(null, null, null, null);
            } else {
                tv_price.setText(price);
                Drawable drawable;
                if (downEntity.getIncAmount().compareTo(BigDecimal.ZERO) > 0) {
                    tv_price.setTextColor(activity.getResources().getColor(R.color.zone_red_view));
                    drawable = ContextCompat.getDrawable(activity, R.drawable.zone_arrow_red);
                } else {
                    tv_price.setTextColor(activity.getResources().getColor(R.color.zone_green_view));
                    drawable = ContextCompat.getDrawable(activity, R.drawable.zone_arrow_green);
                }

                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_price.setCompoundDrawables(null, null, drawable, null);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_screen:
                activity.popBack();
                break;
            case R.id.lblValidTime:
                showPopValidTime();
                break;
            case R.id.btnRelease:
                if (!validInput()) {
                    return;
                }
                if (!cbAgreeContract.isChecked()) {
                    ToastHelper.showMessage("请阅读并同意规则及协议");
                    return;
                }
                new CustomDialog.Builder(getActivity(), "确定提交当前报价吗？")
                        .setPositiveClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitOffice();
                            }
                        }).create().show();
                break;
            case R.id.lblChooseRequest:         //从库存选择
                Temp.i().put(JoinSellOrBuyFragment.class, downEntity.getRequestId());
                Intent intent = new Intent(getActivity(), TransferInventoryActivity.class);
                QueryMyStockUpEntity upEntity = new QueryMyStockUpEntity();
                upEntity.setBond(downEntity.getBond());
                upEntity.setProductPlace(downEntity.getProductPlace());
                upEntity.setDeliveryPlace(downEntity.getDeliveryPlace());
                upEntity.setDeliveryTime(downEntity.getDeliveryTime());
                upEntity.setProductQuality(downEntity.getProductQuality());
                upEntity.setTradeMode(downEntity.getTradeMode());

//                if (!LoginUserContext.getLoginUserDto().equals(downEntity.getRequestUserId())) {
//                    upEntity.setOriSellerCompanyTags(downEntity.getSupplierCompanyTags());
//                }
                upEntity.setPriceUnit(downEntity.getPriceUnit());
                upEntity.setProductType(downEntity.getProductType());
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setBondPayStatusTagCondition(DealConstant.BONDPAYSTATUS_TAG_BOND_PAID);    //DealConstant.BONDPAYSTATUS_TAG_BOND_PAID
                upEntity.setBuyerZoneStatusCondition(DealConstant.ORDER_NORMALITY);
                upEntity.setContractZoneStatus(DealConstant.ZONESTATUS_U);      //未宣布交收  取U
                if (SptConstant.BUYSELL_BUY.equals(buySell)) {
                    upEntity.setDealNumber(downEntity.getDealNumber());
                }

                intent.putExtra(QueryMyStockUpEntity.class.getName(), JsonUtility.toJSONString(upEntity));
                intent.putExtra(JoinSellOrBuyFragment.class.getName(), JoinSellOrBuyFragment.class.getName());
                intent.putExtra(ActivityConstant.SPTDICT_BUY_SELL, buySell);
                startActivity(intent);
//                activity.popBack();
                break;
        }
    }


    /**
     * 获取库存
     */
    private QueryMyStockUpEntity chooseOffer() {
        //TODO 需要传值，除了价格外的所有值
//        QueryMyContractUpEntity upEntity = new QueryMyContractUpEntity();
        QueryMyStockUpEntity upEntity = new QueryMyStockUpEntity();

        upEntity.setBuySell(SptConstant.BUYSELL_BUY.equals(buySell) ? SptConstant.BUYSELL_SELL : SptConstant.BUYSELL_BUY);
        upEntity.setProductType(downEntity.getProductType());
        upEntity.setUserId(LoginUserContext.getLoginUserId());
        upEntity.setNumberPerPage(50);
        upEntity.setCurrentPageNumber(1);
        upEntity.setProductPlace(downEntity.getProductPlace());
        upEntity.setDeliveryPlace(downEntity.getDeliveryPlace());
        upEntity.setDeliveryTime(downEntity.getDeliveryTime());
        upEntity.setTradeMode(downEntity.getTradeMode());
        upEntity.setPriceUnit(downEntity.getPriceUnit());
        upEntity.setMode("A");
        upEntity.setProductQuality(downEntity.getProductQuality());
        upEntity.setBond(downEntity.getBond());
        upEntity.setDealType(SptConstant.DEAL_TYPE_ZONE);
        String bondPayStatusTag = DealConstant.BONDPAYSTATUS_TAG_BOND_PAID + SptConstant.SEP //1 已付保证金
                + DealConstant.BONDPAYSTATUS_TAG_SELLOUT;                   //9 买家已挂出
        upEntity.setBondPayStatusTagCondition(bondPayStatusTag);
        upEntity.setBuyerZoneStatusCondition(DealConstant.ORDER_NORMALITY);
        upEntity.setOriSellerCompanyTags(downEntity.getSupplierCompanyTags());       //原始供货商
        upEntity.setContractZoneStatus(ActivityConstant.Zone.CONTRACT_ZONE_STATUS_NOT_A_DELIVERY);

        return upEntity;
    }

    private OptionsPickerView<Integer> validTimePickerView;

    /**
     * 有效时间选择
     */
    private void showPopValidTime() {
        if (validTimePickerView == null) {
            validTimePickerView = new OptionsPickerView<>(getActivity());
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
        Object obj = lblValidTime.getTag(R.id.viewtag_zonecreat);
        Object obj2 = lblValidTime.getTag(R.id.viewtag_zonecreat2);
        if (obj == null || obj2 == null) {
            validTimePickerView.setSelectOptions(0, 5);
        } else {
            validTimePickerView.setSelectOptionsObj((Integer) obj, (Integer) obj2);
        }
        validTimePickerView.show();
    }


    /**
     * 设置有效时效界面显示与tag 标记值
     *
     * @param hour 小时
     * @param minu 分钟
     */
    private void setValidTimeView(int hour, int minu) {
        String text = hour + "小时" + minu + "分钟";
        lblValidTime.setText(text);
        lblValidTime.setTag(R.id.viewtag_zonecreat, hour);
        lblValidTime.setTag(R.id.viewtag_zonecreat2, minu);
    }

    /**
     * 校验参数输入是否合法
     *
     * @return 参数是否合法
     */
    private boolean validInput() {
        //如果需要选择库存则先判断是否已经选择了库存

//        boolean fromRequest2Sell = SptConstant.BUYSELL_SELL.equals(buySell)
//                && rgRequest.getCheckedRadioButtonId() != R.id.rbCreatOffer;  //从库存选择
//        if (fromRequest2Sell && contractEntity == null) {
//            ToastHelper.showMessage("请选择库存或新建订单");
//            return false;
//        }

        //检查输入框是否都在范围内
        boolean inputValid1 = AnnotateUtility.verifactEditText(this);
        if (!inputValid1) {
            return false;
        }
//        //从库存选择不再校验其他值
//        if (fromRequest2Sell) {
//            return true;
//        }

        if (cbAgreePartDeal.isChecked()) {
            if (new BigDecimal(txtProductMinNumber.getText().toString())
                    .compareTo(new BigDecimal(txtProductNumber.getText().toString())) > 0) {
                ToastHelper.showMessage("最小成交量不能大于总数量");
                return false;
            }
        }
        return true;
    }

    /**
     * 加入买/卖
     */
    private void submitOffice() {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                ZoneRequestOfferUpEntity upEntity = createZoneRequestOffer();
                ZoneRequestUpEntity zoneRequestUpEntity = createZoneRequest(upEntity);

                TblZoneRequestEntity tblZoneRequestEntity = Client.getService(IZoneRequestService.class).getZoneRequestEntity(zoneRequestUpEntity);
                if (tblZoneRequestEntity != null) {
                    if (tblZoneRequestEntity.getRequestId() != null) {
//                        提交专区报价
                        Client.getService(IZoneRequestManager.class).submitZoneOffer(upEntity);
                    } else {
//                        新增修改专区
                        Client.getService(IZoneRequestManager.class).submitZoneRequest(zoneRequestUpEntity);
                    }
                }

                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    activity.popBack();
                }
            }
        });
    }

    @NonNull
    private ZoneRequestUpEntity createZoneRequest(ZoneRequestOfferUpEntity upEntity) {
        ZoneRequestUpEntity zoneRequestUpEntity = new ZoneRequestUpEntity();
        zoneRequestUpEntity.setProductPrice(upEntity.getProductPrice());
        zoneRequestUpEntity.setProductNumber(upEntity.getProductNumber());
        zoneRequestUpEntity.setMinNumber(upEntity.getMinNumber());
        zoneRequestUpEntity.setBuySell(upEntity.getBuySell());
        zoneRequestUpEntity.setMemo(upEntity.getMemo());
        zoneRequestUpEntity.setOfferType(upEntity.getOfferType());
        zoneRequestUpEntity.setBatchMode(upEntity.getBatchMode());
        zoneRequestUpEntity.setValidSecond(upEntity.getValidSecond());
        zoneRequestUpEntity.setContractId(upEntity.getContractId());
        zoneRequestUpEntity.setSource(ZoneConstant.ZoneSource.Android);
        zoneRequestUpEntity.setDeliveryTime(downEntity.getDeliveryTime());
        zoneRequestUpEntity.setUpOrDown(caculatorUpOrDown(downEntity.getDeliveryTime()));
        zoneRequestUpEntity.setPayMethod(downEntity.getPayMethod());
        zoneRequestUpEntity.setProductPlace(downEntity.getProductPlace());
        zoneRequestUpEntity.setProductQuality(downEntity.getProductQuality());
        zoneRequestUpEntity.setRequestType(downEntity.getRequestType());
        zoneRequestUpEntity.setRequestUserId(downEntity.getRequestUserId());
        zoneRequestUpEntity.setBond(downEntity.getBond());
        zoneRequestUpEntity.setDeliveryMode(downEntity.getDeliveryMode());
        zoneRequestUpEntity.setDeliveryPlace(downEntity.getDeliveryPlace());
        zoneRequestUpEntity.setProductType(downEntity.getProductType());
        zoneRequestUpEntity.setTradeMode(downEntity.getTradeMode());
        return zoneRequestUpEntity;
    }

    @NonNull
    private String caculatorUpOrDown(Date date) {
        String day = new SimpleDateFormat("dd").format(date);
        String umd;
        Integer i = Integer.valueOf(day);
        if (i < 11) {
            umd = "UP";
        } else if (i < 21) {
            umd = "MD";
        } else {
            umd = "DOWN";
        }
        return umd;
    }

    @NonNull
    private ZoneRequestOfferUpEntity createZoneRequestOffer() {
        ZoneRequestOfferUpEntity upEntity = new ZoneRequestOfferUpEntity();
        upEntity.setBuySell(buySell);
        upEntity.setRequestId(downEntity.getRequestId());
        upEntity.setSource(ZoneConstant.ZoneSource.Android);
        int hour = (int) lblValidTime.getTag(R.id.viewtag_zonecreat);
        int minute = (int) lblValidTime.getTag(R.id.viewtag_zonecreat2);
        int validSecond = hour * 3600 + minute * 60;
        upEntity.setValidSecond(validSecond);
        upEntity.setProductPrice(new BigDecimal(txtProductPrice.getText().toString()));
        upEntity.setOfferUserId(LoginUserContext.getLoginUserId());
        upEntity.setProductNumber(new BigDecimal(txtProductNumber.getText().toString()));
        upEntity.setBatchMode(cbAgreePartDeal.isChecked() ? ZoneConstant.REQUEST_BATCHMODE_1 : ZoneConstant.REQUEST_BATCHMODE_0);
        if (cbAgreePartDeal.isChecked()) {
            upEntity.setMinNumber(new BigDecimal(txtProductMinNumber.getText().toString()));
        }
//        boolean fromRequest2Sell = SptConstant.BUYSELL_SELL.equals(buySell)
//                && rgRequest.getCheckedRadioButtonId() != R.id.rbCreatOffer;  //从库存选择
        upEntity.setOfferType("N");
//        if (fromRequest2Sell && contractEntity != null) {
//            upEntity.setContractId(contractEntity.getContractId());
//            upEntity.setOfferType("Z");
//        }
        return upEntity;
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

    /**
     * RadioGroup
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.lblChooseRequest) {

        } else {
            view_Choose.setVisibility(View.GONE);
            txtProductNumber.setEnabled(true);
        }
    }

    /**
     * CheckBox
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cbAgreePartDeal) {
            llMinDealNum.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        } else if (buttonView.getId() == R.id.cbAgreeContract) {
//            lblReadContract.setSelected(isChecked);
        }

    }


}
