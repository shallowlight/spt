package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.ui.maintrade.fragment.ZoneResellFragment;
import com.totrade.spt.mobile.utility.DictionaryTools;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;

import java.text.SimpleDateFormat;

/**
 * 库存转卖/回补详情
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class ZoneContractDetailFragment extends SptMobileFragmentBase implements View.OnClickListener {
    private BaseActivity activity;

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
    @BindViewId(R.id.tvMemo)
    private TextView tvMemo;                                    //备注
    @BindViewId(R.id.btnConfirm)
    private Button btnConfirm;                                  //修改
    @BindViewId(R.id.titleBar)
    private CommonTitleZone titleBar;                           //标题栏
    @BindViewId(R.id.tv_preview)
    private TextView tv_preview;                           //阅读合同
    public MyStockDownEntity contractEntity;

    private ZoneResellFragment zoneResellFragment;              //转卖挂单
    private boolean isResell;

    public ZoneContractDetailFragment() {
        setContainerId(R.id.framelayout);
    }

    public void setContractEntity(@NonNull MyStockDownEntity downEntity, boolean isResell) {
        this.contractEntity = downEntity;
        this.isResell = isResell;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        zoneResellFragment = new ZoneResellFragment();
        zoneResellFragment.setContractEntity(contractEntity, isResell);
        addFragments(zoneResellFragment);
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zone_contractrequest, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleBar.setTitle(isResell ? "库存转卖详情" : "库存回补详情");
        btnConfirm.setOnClickListener(this);
        tv_preview.setOnClickListener(this);
        titleBar.getImgBack().setOnClickListener(this);

/*
        if (contractEntity != null) {
            boolean isVisible = (!(Zone.CONTRACT_ZONE_STATUS_A_DELIVERY).equals(contractEntity.getContractZoneStatus()))
                    && (Zone.BUYER_ZONE_STATUS_NORMAL).equals(contractEntity.getBuyerZoneStatus());
            int visible = isVisible ? View.VISIBLE : View.GONE;
            btnConfirm.setVisibility(visible);
        }
*/

        updateViewContract();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == titleBar.getImgBack().getId()) {
            activity.popBack();
        }
        if (v.getId() == R.id.btnConfirm) {
            //转卖
//            activity.switchResellFragment(contractEntity);
        } else if (v.getId() == R.id.tv_preview) {
            //阅读合同
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
     * 合同详情
     */
    private void updateViewContract() {
        lblOrderNumber.setText(ContractHelper.instance.getOrderId(contractEntity.getZoneOrderNoDto()));
        tvDealTime.setText("成交时间: " + new SimpleDateFormat("yyyy/MM/dd").format(contractEntity.getContractTime()));
        lblProductName.setText(contractEntity.getProductName());
        lblProductPrice.setText(DispUtility.price2Disp(contractEntity.getProductPrice(), contractEntity.getPriceUnit(), contractEntity.getNumberUnit()));
        lblProductNumber.setText(DispUtility.productNum2Disp(contractEntity.getRemainNumber(), null, contractEntity.getNumberUnit()));
        lblSupplier.setText("供货商:" + contractEntity.getSellerCompanyName());
//        tvMemo.setText("备注：" + contractEntity.getMemo());
        tvMemo.setText("备注：" + (contractEntity.getMemo() == null ? "" : contractEntity.getMemo()));

        //更多信息：
        String moreInfo1 = "交收期：" + DispUtility.zoneDeliveryTime2Disp(contractEntity.getDeliveryTime(), contractEntity.getRequestType(), contractEntity.getProductType(), contractEntity.getTradeMode())
                + "\n交货地：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_DELIVERY_PLACE, contractEntity.getDeliveryPlace())
                + "\n质量标准：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, contractEntity.getProductQuality());
        lblMoreInfo1.setText(moreInfo1);
        String moreInfo2 = "定金：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_BOND, contractEntity.getBond())
                + "\n库区：" + DictionaryTools.i().getValue(AppConstant.CFG_RESERVOIRAREA, contractEntity.getReservoirArea())
                + "\n原产地：" + DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_PLACE, contractEntity.getProductPlace());
        lblMoreInfo2.setText(moreInfo2);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (zoneResellFragment != null && zoneResellFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
