package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.dto.ZoneRequestOfferUpEntity;
import com.autrade.spt.zone.dto.ZoneRequestUpEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestManager;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.StringUtility;
import com.bigkoo.pickerview.OptionsPickerView;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.annotation.EditTextValidator;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.ui.maintrade.TradeRuleHelper;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.Temp;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.DecimalEditText;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 转卖订单
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class ZoneResellFragment extends BaseFragment implements View.OnClickListener {
    private TransferInventoryActivity activity;
    @BindViewId(R.id.title)
    private CommonTitleZone titleBar;                       //标题
    @EditTextValidator(viewName = "价格")
    @BindViewId(R.id.txtProductPrice)
    private DecimalEditText txtProductPrice;                //价格
    @BindViewId(R.id.lblProductNumber)
    private EditText lblProductNumber;                      //数量
    @BindViewId(R.id.lblPriceUnit)
    private TextView lblPriceUnit;                          //价格单位
    @BindViewId(R.id.lblValidTime)
    private TextView lblValidTime;                          //询价时效
    @BindViewId(R.id.etMemo)
    private TextView etMemo;                                //备注
    @BindViewId(R.id.btnConfirm)
    private Button btnConfirm;                              //确定
    @BindViewId(R.id.cbAgreeContract)
    CheckBox cbAgreeContract;                               //阅读并同意合同
    @BindViewId(R.id.lblReadContract)
    TextView lblReadContract;                               //阅读并同意合同
    @BindViewId(R.id.rl_memo)
    View rl_memo;                                           //备注
    @BindViewId(R.id.rl_number)
    View rl_number;                                         //数量
    @BindViewId(R.id.tv_price_description)
    TextView tv_price_description;                          //价格描述

    private MyStockDownEntity myStockDownEntity;
    private boolean isResell;

    public ZoneResellFragment() {
        setContainerId(R.id.fl_edit);
    }

    public void setContractEntity(MyStockDownEntity downEntity, boolean isResell) {
        this.myStockDownEntity = downEntity;
        this.isResell = isResell;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (TransferInventoryActivity) getActivity();
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zone_order_resell_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnnotateUtility.bindViewFormId(this, view);
        btnConfirm.setOnClickListener(this);
        lblValidTime.setOnClickListener(this);
        titleBar.getImgBack().setOnClickListener(this);

        lblReadContract.setText(TradeRuleHelper.i.getClickableSpan(activity));
        lblReadContract.setMovementMethod(LinkMovementMethod.getInstance());        //超链接可点击

        cbAgreeContract.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                lblReadContract.setSelected(isChecked);
            }
        });
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (isResell) {
            btnConfirm.setText("转卖");
            rl_memo.setVisibility(View.VISIBLE);
            rl_number.setVisibility(View.GONE);
            tv_price_description.setText("转卖价格");
        } else {
            btnConfirm.setText("回补");
            rl_memo.setVisibility(View.GONE);
            rl_number.setVisibility(View.GONE);
            tv_price_description.setText("回补价格");
        }

        rl_memo.setVisibility(View.GONE);
        lblProductNumber.setText(FormatUtil.format.format(myStockDownEntity.getDealNumber()));
//        lblProductNumber.setEnabled("1".equals(downEntity.getBatchMode()) ? true : false);
        lblProductNumber.setEnabled(false);//后台要求，此处暂时强制不允许用户修改用转卖订单的数量（无论是否设置可拆分）
        setValidTimeView(0, 15);
        txtProductPrice.setNumCfgMap(ProductCfgHelper.getNumFormCfg(myStockDownEntity.getProductType(), AppConstant.CFG_PRODUCTPRICE, myStockDownEntity.getTradeMode()));
        lblPriceUnit.setText(myStockDownEntity.getPriceUnit().equals(SptConstant.PRICE_UNIT_RMB) ? "元/吨" : "美元/吨");


        if (!isResell) return;
        if (myStockDownEntity.getRemainNumber().compareTo(new BigDecimal(0)) == 0 || "S".equals(myStockDownEntity.getContractZoneStatus())) {
            btnConfirm.setVisibility(View.GONE);
            txtProductPrice.setEnabled(false);
            txtProductPrice.setText(FormatUtil.format.format(myStockDownEntity.getProductPrice()));
        } else {
            btnConfirm.setVisibility(View.VISIBLE);
            txtProductPrice.setEnabled(true);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == titleBar.getImgBack().getId()) {
            activity.popBack();
        }
        switch (v.getId()) {
            case R.id.lblValidTime:
                showPopValidTime();
                break;
            case R.id.btnConfirm:
                if (!validInput(true)) {
                    break;
                }
                confirmAndSubmit();
                break;
            case R.id.lblReadContract:
                showReadTemplate();
                break;
        }
    }

    /**
     * 校验填写的上行参数是否正确
     *
     * @param checkRead 是否检查阅读标记
     * @return
     */
    private boolean validInput(boolean checkRead) {
        String price = txtProductPrice.getText().toString();
        if (TextUtils.isEmpty(price)) {
            ToastHelper.showMessage("价格不能为空");
            return false;
        }
        if (!AnnotateUtility.verifactEditText(this))     //价格数量最低成交量
        {
            return false;
        }

        String time = lblValidTime.getText().toString();
        if (TextUtils.isEmpty(time)) {
            ToastHelper.showMessage("订单时效不能为空");
            return false;
        }
        if (checkRead && !cbAgreeContract.isChecked()) {
            ToastHelper.showMessage("请阅读并同意规则及协议");
            return false;
        }

        return true;
    }


    /**
     * 生成转卖上行实体
     *
     * @return 转卖上行实体
     */
    private ZoneRequestOfferUpEntity createZoneRequestOfferUpEntity() {
        ZoneRequestOfferUpEntity zoneRequestOfferUpEntity = new ZoneRequestOfferUpEntity();
        zoneRequestOfferUpEntity.setOfferUserId(LoginUserContext.getLoginUserId());
//        upEntity.setProductType(myStockDownEntity.getProductType());
//        upEntity.setRequestType(myStockDownEntity.getDealTag3());
        zoneRequestOfferUpEntity.setContractId(myStockDownEntity.getContractId());
//        upEntity.setTradeMode(myStockDownEntity.getTradeMode());
//        upEntity.setDeliveryMode(myStockDownEntity.getDeliveryMode());
//        upEntity.setBond(myStockDownEntity.getBond());
//        upEntity.setDeliveryPlace(myStockDownEntity.getDeliveryPlace());
//        upEntity.setDeliveryTime(myStockDownEntity.getDeliveryTime());
        zoneRequestOfferUpEntity.setProductNumber(myStockDownEntity.getRemainNumber());
//        upEntity.setPayMethod(myStockDownEntity.getPayMethod());
//        upEntity.setProductQuality(myStockDownEntity.getProductQuality());
//        upEntity.setMinNumber(myStockDownEntity.getDealNumber());
        zoneRequestOfferUpEntity.setProductPrice(new BigDecimal(txtProductPrice.getText().toString()));
        int hour = (int) lblValidTime.getTag(R.id.viewtag_zonecreat);
        int minute = (int) lblValidTime.getTag(R.id.viewtag_zonecreat2);
        int validSecond = hour * 3600 + minute * 60;
        zoneRequestOfferUpEntity.setValidSecond(validSecond);
        zoneRequestOfferUpEntity.setMemo(etMemo.getText().toString().trim());
        zoneRequestOfferUpEntity.setSource(ZoneConstant.ZoneSource.Android);
        if (isResell) {
            zoneRequestOfferUpEntity.setBuySell(SptConstant.BUYSELL_SELL);
            zoneRequestOfferUpEntity.setOfferType("Z");     //转让报价
            zoneRequestOfferUpEntity.setBatchMode(ZoneConstant.REQUEST_BATCHMODE_0);
        } else {
            zoneRequestOfferUpEntity.setBuySell(SptConstant.BUYSELL_BUY);
            zoneRequestOfferUpEntity.setOfferType("H");     //回补报价
        }
        if (Temp.i().get(JoinSellOrBuyFragment.class) != null) {
            zoneRequestOfferUpEntity.setRequestId((String) Temp.i().get(JoinSellOrBuyFragment.class));
        } else {
            zoneRequestOfferUpEntity.setRequestId(myStockDownEntity.getRequestId());
        }

        return zoneRequestOfferUpEntity;
    }

    /**
     * 生成转卖上行实体
     *
     * @return 转卖上行实体
     */
    private ZoneRequestUpEntity createZoneRequestUpEntity() {
        ZoneRequestUpEntity zoneRequestUpEntity = new ZoneRequestUpEntity();
        zoneRequestUpEntity.setRequestUserId(LoginUserContext.getLoginUserId());
//        upEntity.setProductType(myStockDownEntity.getProductType());
//        upEntity.setRequestType(myStockDownEntity.getDealTag3());
        zoneRequestUpEntity.setContractId(myStockDownEntity.getContractId());
//        upEntity.setTradeMode(myStockDownEntity.getTradeMode());
//        upEntity.setDeliveryMode(myStockDownEntity.getDeliveryMode());
//        upEntity.setBond(myStockDownEntity.getBond());
//        upEntity.setDeliveryPlace(myStockDownEntity.getDeliveryPlace());
//        upEntity.setDeliveryTime(myStockDownEntity.getDeliveryTime());
        zoneRequestUpEntity.setProductNumber(myStockDownEntity.getRemainNumber());
//        upEntity.setPayMethod(myStockDownEntity.getPayMethod());
//        upEntity.setProductQuality(myStockDownEntity.getProductQuality());
//        upEntity.setMinNumber(myStockDownEntity.getDealNumber());
        zoneRequestUpEntity.setProductPrice(new BigDecimal(txtProductPrice.getText().toString()));
        int hour = (int) lblValidTime.getTag(R.id.viewtag_zonecreat);
        int minute = (int) lblValidTime.getTag(R.id.viewtag_zonecreat2);
        int validSecond = hour * 3600 + minute * 60;
        zoneRequestUpEntity.setValidSecond(validSecond);
        zoneRequestUpEntity.setMemo(etMemo.getText().toString().trim());
        zoneRequestUpEntity.setSource(ZoneConstant.ZoneSource.Android);
        if (isResell) {
            zoneRequestUpEntity.setBuySell(SptConstant.BUYSELL_SELL);
            zoneRequestUpEntity.setOfferType("Z");     //转让报价
            zoneRequestUpEntity.setBatchMode(ZoneConstant.REQUEST_BATCHMODE_0);
        } else {
            zoneRequestUpEntity.setBuySell(SptConstant.BUYSELL_BUY);
            zoneRequestUpEntity.setOfferType("H");     //回补报价
        }
        return zoneRequestUpEntity;
    }

    /**
     * 弹出dialog 提示用户是否提交
     */
    private void confirmAndSubmit() {
        new CustomDialog.Builder(getActivity(), "确定转卖当前挂单吗?")
                .setPositiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitZoneRequest();
                    }
                })
                .create().show();
    }

    /**
     * 提交转卖/回补挂单
     */
    private void submitZoneRequest() {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                if (Temp.i().get(JoinSellOrBuyFragment.class) != null)
                Client.getService(IZoneRequestManager.class).submitZoneOffer(createZoneRequestOfferUpEntity());
                else
                Client.getService(IZoneRequestManager.class).submitZoneRequest(createZoneRequestUpEntity());
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    ToastHelper.showMessage(getString(R.string.submit_success));
                    if (Temp.i().get(JoinSellOrBuyFragment.class) != null) {
                        toTradeOrdetail((String) Temp.i().get(JoinSellOrBuyFragment.class));
                        Temp.i().remove(JoinSellOrBuyFragment.class);
                    }
                    activity.popBack();
                }
            }
        });
    }

    private void toTradeOrdetail(final String requestId) {
        SubAsyncTask.create().setOnDataListener("findZoneRequestList", new OnDataListener<ZoneRequestDownEntity>() {

            @Override
            public ZoneRequestDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IZoneRequestService.class).getZoneRequestDetailByRequestId(requestId);
            }

            @Override
            public void onDataSuccessfully(ZoneRequestDownEntity entity) {
                if (entity != null) {
                    Intent intent = new Intent(activity, com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity.class);
                    intent.putExtra(ZoneRequestDownEntity.class.getName(), entity.toString());
                    intent.putExtra(ZoneResellFragment.class.getName(), ZoneResellFragment.class.getName());
                    activity.startActivity(intent);
//                    activity.finish();
                }
            }
        });
    }

    /**
     * 阅读合同
     */
    private void showReadTemplate() {
        if (!validInput(false)) {
            return;
        }
        SubAsyncTask.create().setOnDataListener(activity, false, new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).mergeContractTemplate(formContractEntity());
            }

            @Override
            public void onDataSuccessfully(String obj) {
                if (!StringUtility.isNullOrEmpty(obj)) {
                    Intent intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra("resultString", obj);
                    intent.putExtra("titleString", "电子合同");
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * TODO 保留注释的行
     * 获取合同上行Entity
     */
    private ContractDownEntity formContractEntity() {
        String productType = myStockDownEntity.getProductType();
        ContractDownEntity upEntity = new ContractDownEntity();
        BigDecimal number = new BigDecimal(lblProductNumber.getText().toString());
        upEntity.setProductName(ProductTypeUtility.getProductName(productType)); // 商品名称
        upEntity.setProductType(productType); // 商品类别
        upEntity.setProductPlace(myStockDownEntity.getProductPlace()); // 商品产地
        upEntity.setDealNumber(number); // 商品成交数量
        upEntity.setProductPrice(new BigDecimal(txtProductPrice.getText().toString()));// 商品价格
        upEntity.setPayMethod(myStockDownEntity.getPayMethod()); // 付款方式
//        if (productType.startsWith("SL")) {
//            upEntity.setWareHouseName(txtWareHouse.getText().toString());
//            upEntity.setBrand(txtBrand.getText().toString());
//            upEntity.setProductClass(getName(llProductClass)); // 传中文，老版本是输入
//        }
        upEntity.setDeliveryPlace(myStockDownEntity.getDeliveryPlace());
        upEntity.setDeliveryPlaceName(myStockDownEntity.getDeliveryPlaceName());
        upEntity.setDeliveryMode(myStockDownEntity.getDeliveryMode()); // 提货方式
        upEntity.setProductQuality(myStockDownEntity.getProductQuality()); // 商品品质

//        if (!productType.startsWith("GT")) {
//            upEntity.setProductQuality(getValue(llProductQuality)); // 商品品质
//        } else {
//            upEntity.setProductQualityEx1(new BigDecimal(txtProductQualityEx1.getText().toString())); // 商品品质
//        }
        upEntity.setMemo(myStockDownEntity.getMemo());
        upEntity.setDeliveryTime(myStockDownEntity.getDeliveryTime());
        upEntity.setTradeMode(myStockDownEntity.getTradeMode()); // 贸易方式
        int hour = (int) lblValidTime.getTag(R.id.viewtag_zonecreat);
        int minute = (int) lblValidTime.getTag(R.id.viewtag_zonecreat2);
        int validSecond = hour * 3600 + minute * 60;
        Date validTime = new Date(DateUtility.getDate().getTime() + validSecond * 1000);
        upEntity.setValidTime(validTime); // 报价有效期
        upEntity.setFeeMode("U");                                        // 手续费模式
        BigDecimal fee = ProductCfgHelper.getCfgFee(productType);
        upEntity.setFee(fee);                                            // 手续费（单体）
        upEntity.setFeeTotal(fee.multiply(number));                      // 手续费总数(买方或者卖方)
        upEntity.setTemplateId(myStockDownEntity.getContractId()); // 电子合同模版ID
        upEntity.setPriceUnit(myStockDownEntity.getPriceUnit()); // 价格单位
        upEntity.setNumberUnit(myStockDownEntity.getNumberUnit()); // 数量单位
        upEntity.setBond(myStockDownEntity.getBond()); // 保证金（百分比）
        upEntity.setBuySell(SptConstant.BUYSELL_SELL);
        upEntity.setParam1(LoginUserContext.getLoginUserDto().getUserId());
        upEntity.setRequestUserId(LoginUserContext.getLoginUserDto().getUserId());
        upEntity.setAnonymousTag("1");
        upEntity.setDealType(SptConstant.DEAL_TYPE_ZONE);
        return upEntity;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (validTimePickerView != null && validTimePickerView.isShowing()) {
                validTimePickerView.dismiss();
                return true;
            }
        }
        return false;
    }

}

