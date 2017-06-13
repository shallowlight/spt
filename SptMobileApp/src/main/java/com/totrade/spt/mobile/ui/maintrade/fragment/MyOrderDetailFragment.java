package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.ZoneMyOfferDownEntity;
import com.autrade.spt.zone.dto.ZoneRequestCancelEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestOfferService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.ui.maintrade.fragment.ZoneReEditOfferFragment;
import com.totrade.spt.mobile.utility.DictionaryTools;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.countdown.TimerUtils;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;


/**
 * 修改挂单详情
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class MyOrderDetailFragment extends SptMobileFragmentBase implements View.OnClickListener {

    private TransferInventoryActivity activity;

    @BindViewId(R.id.tv_order_num)
    private TextView tv_order_num;                            //订单号描述
    @BindViewId(R.id.lblOrderNumber)
    private TextView lblOrderNumber;                            //订单号
    @BindViewId(R.id.lblStatus)
    private TextView lblStatus;                                 //订单状态
    @BindViewId(R.id.lblProductName)
    private TextView lblProductName;                            //产品名称
    @BindViewId(R.id.fl_valid_time)
    private FrameLayout fl_valid_time;                             //剩余时效
    @BindViewId(R.id.lblProductPrice)
    private TextView lblProductPrice;                           //产品价格
    @BindViewId(R.id.lblProductNumber)
    private TextView lblProductNumber;                          //产品数量
    @BindViewId(R.id.lblMoreInfo1)
    private TextView lblMoreInfo1;                              //更多信息1
    @BindViewId(R.id.lblMoreInfo2)
    private TextView lblMoreInfo2;                              //更多信息2
    @BindViewId(R.id.lblSupplier)
    private TextView lblSupplier;                               //指定供货商
    @BindViewId(R.id.btnCancel)
    private Button btnCancel;                                   //撤单
    @BindViewId(R.id.btnConfirm)
    private Button btnConfirm;                                  //修改
    @BindViewId(R.id.titleBar)
    private CommonTitleZone titleBar;                           //标题栏
    public ZoneMyOfferDownEntity offerEntity;

    private ZoneReEditOfferFragment zoneReEditOfferFragment;

    public MyOrderDetailFragment() {
        setContainerId(R.id.framelayout);
    }

    public void setOfferDownEntity(@NonNull ZoneMyOfferDownEntity offerDownEntity) {
        this.offerEntity = offerDownEntity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (TransferInventoryActivity) getActivity();
        zoneReEditOfferFragment = new ZoneReEditOfferFragment();
        zoneReEditOfferFragment.setContractEntity(offerEntity);
        addFragments(zoneReEditOfferFragment);
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zone_my_order_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == titleBar.getImgBack().getId()) {
            activity.popBack();
        }
        if (v.getId() == R.id.btnCancel) {
            //撤单
            notify2Cancel();
        } else if (v.getId() == R.id.btnConfirm) {
            if (offerEntity.getOfferStatus().equals(ZoneConstant.OFFER_STATUS_PART_DEAL))
                ToastHelper.showMessage("部分成交的订单不可修改");
//            else
//                activity.switchReEditOfferFragment(offerEntity);
        }
    }

    private void initData() {
        titleBar.setTitle("修改挂单");
        updateViewOffice();
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        titleBar.getImgBack().setOnClickListener(this);
    }


    /**
     * 订单详情
     */
    private void updateViewOffice() {
        //订单号
        lblOrderNumber.setText(ContractHelper.instance.getOrderId(offerEntity.getZoneOrderNoDto()));
        tv_order_num.setVisibility(offerEntity.getPairCode() == null ? View.GONE : View.VISIBLE);
        try {
            lblStatus.setText(DictionaryUtility.getValue("offerStatus", offerEntity.getOfferStatus()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //剩余时效
        long time = offerEntity.getValidTime().getTime() - System.currentTimeMillis();
        TextView tv2 = TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, activity, time, TimerUtils.TIME_STYLE_THREE, 0).getmDateTv();
        tv2.setTextColor(getResources().getColor(R.color.red_txt_d56));
        tv2.setTextSize(11);
        fl_valid_time.addView(tv2);
        //品名
        lblProductName.setText(offerEntity.getProductName());
//        lblBuySell.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BUY_SELL, offerEntity.getBuysell()));
//        lblBuySell.setBackgroundResource(SptConstant.BUYSELL_BUY.equals(offerEntity.getBuysell()) ? R.drawable.corner_red : R.drawable.corner_green);
        lblProductPrice.setText(DispUtility.price2Disp(offerEntity.getProductPrice(), offerEntity.getPriceUnit(), offerEntity.getNumberUnit()));

        if (offerEntity.getOfferStatus().equals(ZoneConstant.OFFER_STATUS_PART_DEAL)) {
            lblProductNumber.setText(DispUtility.productNum2Disp(null, offerEntity.getRemainNumber(), offerEntity.getNumberUnit()));
        } else {
            lblProductNumber.setText(DispUtility.productNum2Disp(null, offerEntity.getProductNumber(), offerEntity.getNumberUnit()));
        }
//        if(!TextUtils.isEmpty(offerEntity.getSupplierCompanyTags()))
        if (!CollectionUtility.isNullOrEmpty(offerEntity.getCompanyInfo())) {
            lblSupplier.setText("供货商:" + offerEntity.getCompanyInfo().get(0).getBusinessCode() +
                    offerEntity.getCompanyInfo().get(0).getSupplierCompanyNames());
        } else {
            lblSupplier.setText("供货商:暂无");
        }

        //更多信息：
        String moreInfo1 =
                "交收期：" + DispUtility.zoneDeliveryTime2Disp(offerEntity.getDeliveryTime(), offerEntity.getRequestType(), offerEntity.getProductType(), offerEntity.getTradeMode())
                        + "\n质量标准：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, offerEntity.getProductQuality())
                        + "\n原产地：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_PLACE, offerEntity.getProductPlace());
        lblMoreInfo1.setText(moreInfo1);
        String moreInfo2 =
                "交货地：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, offerEntity.getDeliveryPlace())
                        + "\n定金：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_BOND, offerEntity.getBond())
                        + "\n库区：" + DictionaryTools.i().getValue(AppConstant.CFG_RESERVOIRAREA, offerEntity.getReservoirArea());     //TODO  库区
        lblMoreInfo2.setText(moreInfo2);


    }


    /**
     * 弹出dialog 提示撤单
     */
    private void notify2Cancel() {
        new CustomDialog.Builder(getActivity(), "确定撤销当前挂单吗？")
                .setPositiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelOrder();
                    }
                })
                .create().show();
    }


    /**
     * 撤单
     */
    private void cancelOrder() {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                ZoneRequestCancelEntity upEntity = new ZoneRequestCancelEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                upEntity.setOfferId(offerEntity.getOfferId());
                Client.getService(IZoneRequestOfferService.class).cancelZoneOffer(upEntity);
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    ToastHelper.showMessage("撤单成功");
                    activity.popBack();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (zoneReEditOfferFragment != null && zoneReEditOfferFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
