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
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.ZoneMyOfferDownEntity;
import com.autrade.spt.zone.dto.ZoneRequestCancelEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestOfferService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.bigkoo.pickerview.OptionsPickerView;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.annotation.EditTextValidator;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.DecimalEditText;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 修改订单
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class ZoneReEditOfferFragment extends SptMobileFragmentBase implements View.OnClickListener {
    private TransferInventoryActivity activity;
    @BindViewId(R.id.title)
    private CommonTitleZone titleBar;                   //标题
    @EditTextValidator(viewName = "价格")
    @BindViewId(R.id.txtProductPrice)
    private DecimalEditText txtProductPrice;            //价格
    @EditTextValidator(viewName = "数量")
    @BindViewId(R.id.txtProductNumber)
    private DecimalEditText txtProductNumber;           //数量
    @BindViewId(R.id.lblPriceUnit)
    private TextView lblPriceUnit;                      //价格单位
    @BindViewId(R.id.btnConfirm)
    private Button btnConfirm;                          //确定
    @BindViewId(R.id.btnCancel)
    private Button btnCancel;                           //撤单
    @BindViewId(R.id.lblValidTime)
    private TextView lblValidTime;                      //时效

    private ZoneMyOfferDownEntity downEntity;

    public ZoneReEditOfferFragment() {
        setContainerId(R.id.fl_edit);
    }

    public void setContractEntity(@NonNull ZoneMyOfferDownEntity downEntity) {
        this.downEntity = downEntity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (TransferInventoryActivity) getActivity();
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zone_reedit_order, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnConfirm.setOnClickListener(this);
        lblValidTime.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        titleBar.getImgBack().setOnClickListener(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        titleBar.setTitle("修改");
        setValidTimeView(0, 15);
        txtProductPrice.setNumCfgMap(ProductCfgHelper.getNumFormCfg(downEntity.getProductType(), AppConstant.CFG_PRODUCTPRICE, downEntity.getTradeMode()));
        txtProductNumber.setNumCfgMap(ProductCfgHelper.getNumFormCfg(downEntity.getProductType(), AppConstant.CFG_PRODUCTNUMBER, downEntity.getTradeMode()));
        txtProductPrice.setText(format.format(downEntity.getProductPrice()));
        txtProductNumber.setText(format.format(downEntity.getProductNumber()));
        lblPriceUnit.setText(downEntity.getPriceUnit().equals(SptConstant.PRICE_UNIT_RMB) ? "元/吨" : "美元/吨");

        if (downEntity.getOfferStatus().equals(ZoneConstant.OFFER_STATUS_PART_DEAL)) {
            txtProductPrice.setEnabled(false);
            txtProductNumber.setEnabled(false);
        } else if (!ObjUtils.isEmpty(downEntity.getContractId())) {
            txtProductPrice.setEnabled(true);
            txtProductNumber.setEnabled(false);
        } else {
            txtProductPrice.setEnabled(true);
            txtProductPrice.setFocusable(true);
            txtProductNumber.setEnabled(true);
            txtProductNumber.setFocusable(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == titleBar.getImgBack().getId()) {
            activity.popBack();
        } else if (v.getId() == R.id.btnConfirm) {
            if (validInput()) {
                confirmAndSubmit();
            }
        } else if (v.getId() == R.id.lblValidTime) {
            showPopValidTime();
        } else if (v.getId() == R.id.btnCancel) {   //撤单
            notify2Cancel();
        }
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
                upEntity.setOfferId(downEntity.getOfferId());
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
     * 校验填写的上行参数是否正确
     *
     * @return 用户填入值是否正确
     */
    private boolean validInput() {
        return AnnotateUtility.verifactEditText(this);
    }


    /**
     * 生成修改报价上行实体
     *
     * @return 修改报价上行实体
     */
    private TblZoneRequestOfferEntity formUpdateEntity() {
        TblZoneRequestOfferEntity upEntity = new TblZoneRequestOfferEntity();
        upEntity.setSubmitUserId(LoginUserContext.getLoginUserId());
        upEntity.setOfferUserId(LoginUserContext.getLoginUserId());
        upEntity.setRequestId(downEntity.getRequestId());
        upEntity.setOfferId(downEntity.getOfferId());
        upEntity.setProductPrice(new BigDecimal(txtProductPrice.getText().toString()));
        upEntity.setProductNumber(new BigDecimal(txtProductNumber.getText().toString()));
        int hour = (int) lblValidTime.getTag(R.id.viewtag_zonecreat);
        int minute = (int) lblValidTime.getTag(R.id.viewtag_zonecreat2);
        int validSecond = hour * 3600 + minute * 60;
        upEntity.setValidSecond(validSecond);
        return upEntity;
    }

    /**
     * 弹出dialog 提示用户是否提交
     */
    private void confirmAndSubmit() {
        new CustomDialog.Builder(getActivity(), "确定提交当前修改吗?")
                .setPositiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateOrSubmit();
                    }
                })
                .create().show();
    }

    /**
     * 网络请求提交数据
     */
    private void updateOrSubmit() {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IZoneRequestOfferService.class).updateZoneOffer(formUpdateEntity());
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    ToastHelper.showMessage(getString(R.string.submit_success));
                    activity.popBack();

                }
            }
        });
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

