package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.ZoneRequestMatchUpEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestMatchService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
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
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;
import com.totrade.spt.mobile.view.customize.CustomDialog;

import java.text.SimpleDateFormat;

/**
*
* 从订单详情进入的转卖回补
* @author huangxy
* @date 2017/5/8
*
*/
public class ResellRechargeDealFragment extends BaseFragment implements View.OnClickListener{

    private BaseActivity activity;
    private View rootView;

    @BindViewId(R.id.lblOrderNumber)
    private TextView lblOrderNumber;                            //订单号
    @BindViewId(R.id.tvDealTime)
    private TextView tvDealTime;                                //成交时间
    @BindViewId(R.id.lblProductName)
    private TextView lblProductName;                            //产品名称
    @BindViewId(R.id.tvFunds)
    private TextView lblProductPrice;                           //产品价格
    @BindViewId(R.id.lblProductNumber)
    private TextView lblProductNumber;                          //产品数量
    @BindViewId(R.id.lblMoreInfo1)
    private TextView lblMoreInfo1;                              //更多信息1
    @BindViewId(R.id.lblMoreInfo2)
    private TextView lblMoreInfo2;                              //更多信息2
    @BindViewId(R.id.lblSupplier)
    private TextView lblSupplier;                               //指定供货商
    @BindViewId(R.id.btnConfirm)
    private Button btnConfirm;                                  //修改
    @BindViewId(R.id.titleBar)
    private CommonTitleZone titleBar;                           //标题栏
    @BindViewId(R.id.cbAgreeContract)
    CheckBox cbAgreeContract;                                   //规则协议
    @BindViewId(R.id.lblReadContract)
    TextView lblReadContract;                                   //规则协议
    @BindViewId(R.id.ll_agree_contract)
    View ll_agree_contract;                                     //规则协议
    @BindViewId(R.id.tv_preview)
    TextView tv_preview;                                        //合同预览

    private MyStockDownEntity contractEntity;
    private boolean isResell;

    public ResellRechargeDealFragment() {
        setContainerId(R.id.framelayout);
    }

    public void setContractEntity(@NonNull MyStockDownEntity downEntity, boolean isResell) {
        this.contractEntity = downEntity;
        this.isResell = isResell;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_zone_contractrequest, container, false);
        AnnotateUtility.bindViewFormId(this, rootView);

        lblReadContract.setText(TradeRuleHelper.i.getClickableSpan(activity));
        lblReadContract.setMovementMethod(LinkMovementMethod.getInstance());        //超链接可点击
        ll_agree_contract.setVisibility(View.VISIBLE);

        initData();
        return rootView;
    }

    private void initData() {
        titleBar.setTitle(isResell ? "库存转卖详情" : "库存回补详情");
        btnConfirm.setVisibility(View.VISIBLE);
        btnConfirm.setText("成交");
        btnConfirm.setOnClickListener(this);
        titleBar.getImgBack().setOnClickListener(this);
        tv_preview.setOnClickListener(this);
        updateViewContract();
    }

    @Override
    public void onResume() {
        super.onResume();
        cbAgreeContract.setChecked(false);
    }

    /**
     * 合同详情
     */
    private void updateViewContract() {
        lblOrderNumber.setText(ContractHelper.instance.getOrderId(contractEntity.getZoneOrderNoDto()));
        tvDealTime.setText("成交时间: " + new SimpleDateFormat("yyyy/MM/dd").format(contractEntity.getContractTime()));
        lblProductName.setText(contractEntity.getProductName());
        lblProductPrice.setText(DispUtility.price2Disp(contractEntity.getProductPrice(), contractEntity.getPriceUnit(), contractEntity.getNumberUnit()));
        lblProductNumber.setText(DispUtility.productNum2Disp(contractEntity.getDealNumber(), null, contractEntity.getNumberUnit()));
        lblSupplier.setText("供货商:" + contractEntity.getSellerCompanyName());

        //更多信息：
        String moreInfo1 =
                "交收期：" + DispUtility.zoneDeliveryTime2Disp(contractEntity.getDeliveryTime(), contractEntity.getRequestType(), contractEntity.getProductType(), contractEntity.getTradeMode())
                        + "\n质量标准：" + DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, contractEntity.getProductQuality())
                        + "\n原产地：" + DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_PLACE, contractEntity.getProductPlace())
                        + "\n付款方式：" + DictionaryUtility.getValue(SptConstant.SPTDICT_PAY_METHOD, contractEntity.getPayMethod());
        lblMoreInfo1.setText(moreInfo1);
        String moreInfo2 =
                "交货地：" + DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, contractEntity.getDeliveryPlace())
                        + "\n定金：" + DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, contractEntity.getBond())
                        + "\n提货方式：" + DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_MODE, contractEntity.getDeliveryMode())
                        + "\n贸易方式：" + (SptConstant.TRADEMODE_DOMESTIC.equals(contractEntity.getTradeMode()) ? "内贸" : "外贸");      //不显示买卖方向
        lblMoreInfo2.setText(moreInfo2);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnConfirm) {
            if (!cbAgreeContract.isChecked()) {
                ToastHelper.showMessage("请阅读并同意规则及协议");
                return;
            }
            confirmAndSubmit();
        } else if (v.getId() == titleBar.getImgBack().getId()) {
            activity.popBack();
        } else if (v.getId() == R.id.tv_preview) {
            readContract();
        }
    }

    /**
     * 阅读合同
     */
    private void readContract() {
        SubAsyncTask.create().setOnDataListener(getActivity(), true, new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).mergeContractTemplate(contractEntity.getContractId());
            }

            @Override
            public void onDataSuccessfully(String strTemplate) {
                if (strTemplate != null) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("resultString", strTemplate);
                    intent.putExtra("titleString", "合同");
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 弹出dialog 提示用户是否提交
     */
    private void confirmAndSubmit() {CustomDialog.Builder builder = new CustomDialog.Builder(activity, "确定要成交吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dealZoneOffer();
            }
        });
        builder.create().show();
    }

    private void dealZoneOffer() {
        SubAsyncTask.create().setOnDataListener(activity, true, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                ZoneRequestMatchUpEntity upEntity = new ZoneRequestMatchUpEntity();
                upEntity.setDealNumber(contractEntity.getDealNumber());
                upEntity.setSubmitUserId(LoginUserContext.getLoginUserDto().getUserId());
//                upEntity.setBatchMode(contractEntity.getBatchMode());
                upEntity.setSource(ZoneConstant.ZoneSource.Android);
                upEntity.setContractId(contractEntity.getContractId());
                upEntity.setOfferType("Z");
                upEntity.setMatchOfferId((String ) Temp.i().get(BuyerFragment.class));
                Client.getService(IZoneRequestMatchService.class).dealZoneOffer(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("成交成功");
                    Temp.i().remove(BuyerFragment.class);
                    activity.popBack();
                }
            }
        });
    }


}
