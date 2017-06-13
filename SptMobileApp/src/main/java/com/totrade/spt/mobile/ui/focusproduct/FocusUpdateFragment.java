package com.totrade.spt.mobile.ui.focusproduct;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.spt.common.entity.TblNegoFocusMasterEntity;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.spt.zone.dto.ZoneRequestFocusUpEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestFocusService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;
import com.totrade.spt.mobile.view.customize.DecimalEditText;
import com.totrade.spt.mobile.view.customize.PopupTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 添加或者删除我关注的商品
 *
 * @author huangxy
 * @date 2017/1/16
 */
public class FocusUpdateFragment extends BaseSptFragment<FocusProductActivity> implements View.OnClickListener {
    private CommonTitleZone title;

    private PopupTextView lblProductTop;
    private PopupTextView lblProductType;
    private PopupTextView lblProductQuality;
    private PopupTextView lblBuySell;
    private PopupTextView lblProductPlace;
    private PopupTextView lblDeliveryPlace;
    private DecimalEditText txtMinNumber, txtMaxNumber;

    private TblNegoFocusMasterEntity tblNegoFocusMasterEntity;
    private boolean isAdd;
    private TextView btnAdd;

    public void setTblNegoFocusMasterEntity(TblNegoFocusMasterEntity tblNegoFocusMasterEntity) {
        this.tblNegoFocusMasterEntity = tblNegoFocusMasterEntity;
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_focus_update;
    }

    @Override
    protected void initView() {
        lblProductTop = findView(R.id.lblProductTop);
        lblProductType = findView(R.id.lblProductType);
        lblProductQuality = findView(R.id.lblProductQuality);
        lblBuySell = findView(R.id.lblBuySell);
        lblProductPlace = findView(R.id.lblProductPlace);
        lblDeliveryPlace = findView(R.id.lblDeliveryPlace);
        txtMinNumber = findView(R.id.txtMinNumber);
        txtMaxNumber = findView(R.id.txtMaxNumber);

        title = findView(R.id.title);
        title.getTvTongTong().setVisibility(View.GONE);
        btnAdd = findView(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        title.getImgBack().setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        isAdd = tblNegoFocusMasterEntity == null;
        if (isAdd) {
//            lblProductTop.setVisibility(View.VISIBLE);
            List<ProductTypeDto> lstTop = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_MATCH, null, 0);
            List<ProductTypeDto> lstRemoveFocus;
            List<NameValueItem> lstTop2 = new ArrayList<>();
            NameValueItem item;
            for (ProductTypeDto dto : lstTop) {
                lstRemoveFocus = ProductTypeUtility.getUnFocusListByProductTop(dto.getProductType());
                if (!CollectionUtility.isNullOrEmpty(lstRemoveFocus)) {
                    item = new NameValueItem();
                    item.setName(dto.getTypeName());
                    item.setValue(dto.getProductType());
                    lstTop2.add(item);
                }
            }

            if (CollectionUtility.isNullOrEmpty(lstTop2)) {
                ToastHelper.showMessage("您已关注了所有产品");
                mActivity.popBack();
                return;
            }

            lblProductTop.initDate(lstTop2, "行业", Gravity.FILL);
            initProductName();
            lblProductTop.setListener(new PopupTextView.OnTextChangerListener() {
                @Override
                public void textChanger(PopupTextView view) {
                    initProductName();
                }
            });
            lblProductType.setListener(new PopupTextView.OnTextChangerListener() {
                @Override
                public void textChanger(PopupTextView view) {
                    initNumberEditText();
                    tryLoadCfg();
                }
            });
            txtMinNumber.setText(null);
            txtMaxNumber.setText(null);
        } else {
            lblProductTop.setVisibility(View.GONE);
            // lblProductType不可变
            lblProductType.initDate(null, "品名", Gravity.FILL);
            tryLoadCfg();
            NameValueItem item = new NameValueItem();
            item.setName(ProductTypeUtility.getProductName(tblNegoFocusMasterEntity.getProductType()));
            item.setValue(tblNegoFocusMasterEntity.getProductType());
            lblProductType.setViewTag(item);
        }
        title.setTitle(isAdd ? "添加关注" : "修改关注");
        btnAdd.setText(isAdd ? "确认添加" : "确认修改");
    }

    private void initProductName() {
        final List<ProductTypeDto> lstProductTypeDto = ProductTypeUtility.getUnFocusListByProductTop(lblProductTop.getValue());

        List<ProductTypeDto> dtos = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_ZONE, null, 2);
        List<NameValueItem> lstProduct = ProductTypeUtility.listDto2NameValueItem(dtos);

        lblProductType.initDate(lstProduct, "品名", Gravity.FILL);
        tryLoadCfg();
    }

    /**
     * 加载配置产品界面配置（侧滑筛选）
     */
    private void tryLoadCfg() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                return ProductCfgHelper.tryLoadCfg(lblProductType.getValue());
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    String productType = lblProductType.getValue();
                    List<NameValueItem> lstQuality;
//                    NameValueItem emptyItem = new NameValueItem();
//                    emptyItem.setName("不限");
//                    emptyItem.setValue("");
                    lstQuality = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_PRODUCT_QUALITY);
                    if (!productType.startsWith("GT")) {
//                        lstQuality.add(0, emptyItem);
                    }
//                    List<NameValueItem> lstTradeMode = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_TRADE_MODE);
                    List<NameValueItem> lstTradeMode = listCreatTradeMode();
//                    lstTradeMode.add(0, emptyItem);
                    List<NameValueItem> lstProductPlace = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_PRODUCT_PLACE);
//                    lstProductPlace.add(0, emptyItem);
                    List<NameValueItem> lstDeliveryPlace = ProductCfgHelper.getFormConfig(productType, SptConstant.SPTDICT_DELIVERY_PLACE);
//                    lstDeliveryPlace.add(0, emptyItem);
                    lblProductQuality.initDate(lstQuality, productType.startsWith("GT") ? "铁品位" : "质量标准", Gravity.FILL);
                    lblBuySell.initDate(lstTradeMode, "贸易方式", Gravity.FILL);
                    lblProductPlace.initDate(lstProductPlace, "产地", Gravity.FILL);
                    lblDeliveryPlace.initDate(lstDeliveryPlace, productType.startsWith("SL") ? "交货港" : "交货地", Gravity.FILL);
                    if (isAdd) {
                        return;
                    }
                    if (tblNegoFocusMasterEntity.getProductNumberFrom() != null) {
                        txtMinNumber.setText(format.format(tblNegoFocusMasterEntity.getProductNumberFrom()));
                    } else {
                        txtMinNumber.setText(null);
                    }
                    if (tblNegoFocusMasterEntity.getProductNumberTo() != null) {
                        txtMaxNumber.setText(format.format(tblNegoFocusMasterEntity.getProductNumberTo()));
                    } else {
                        txtMaxNumber.setText(null);
                    }
                    lblBuySell.setViewTag(getNameValue(tblNegoFocusMasterEntity.getBuySell(), lstTradeMode));
                    lblProductPlace.setViewTag(getNameValue(tblNegoFocusMasterEntity.getProductPlace(), lstProductPlace));
                    lblDeliveryPlace.setViewTag(getNameValue(tblNegoFocusMasterEntity.getDeliveryPlace(), lstDeliveryPlace));
                    // 设置质量标准
                    if (productType.startsWith("GT")) {
                        BigDecimal qualityEx1From = tblNegoFocusMasterEntity.getProductQualityEx1From();
                        BigDecimal qualityEx1To = tblNegoFocusMasterEntity.getProductQualityEx1To();
                        NameValueItem item;
                        if (qualityEx1From == null || qualityEx1From.compareTo(BigDecimal.ZERO) <= 0) {
                            if (qualityEx1To == null || qualityEx1To.compareTo(new BigDecimal(100)) >= 0) {
                                item = new NameValueItem();
                                item.setName("不限");
                                item.setValue("0");
                                item.setTag("100");
                            } else {
                                item = new NameValueItem();
                                item.setName("不限—" + format.format(qualityEx1To));
                                item.setValue("0");
                                item.setTag(format.format(qualityEx1To));
                            }
                        } else {
                            String from = format.format(qualityEx1From);
                            if (qualityEx1To == null || qualityEx1To.compareTo(new BigDecimal(100)) >= 0) {
                                item = new NameValueItem();
                                item.setName(from + "以上");
                                item.setValue(from);
                                item.setTag("100");
                            } else {
                                item = new NameValueItem();
                                item.setName(from + "—" + format.format(qualityEx1To));
                                item.setValue(from);
                                item.setTag(format.format(qualityEx1To));
                            }
                        }
                        lblProductQuality.setViewTag(item);
                    } else {
                        lblProductQuality.setViewTag(getNameValue(tblNegoFocusMasterEntity.getProductQuality(), lstQuality));
                    }
                }
            }
        });
    }

    /**
     * 贸易方式去掉换成买卖方向
     * @return
     */
    @NonNull
    private List<NameValueItem> getNameValueItems() {
        List<NameValueItem> lstTradeMode = new ArrayList<>();
        NameValueItem item1 = new NameValueItem();
        item1.setName("采购");
        item1.setValue("B");
        NameValueItem item2 = new NameValueItem();
        item2.setName("销售");
        item2.setValue("S");
        lstTradeMode.add(item1);
        lstTradeMode.add(item2);
        return lstTradeMode;
    }

    /**
     * 买卖方向去掉换成贸易方式
     * @return
     */
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

    private NameValueItem getNameValue(String value, List<NameValueItem> lst) {
        if (value == null)
            value = "";
        for (NameValueItem item : lst) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }

    private void initNumberEditText() {
        String productType = lblProductType.getValue();
        Map<String, Double> map = ProductCfgHelper.getNumFormCfg(productType, AppConstant.CFG_PRODUCTNUMBER, null);
        txtMinNumber.setNumCfgMap(map);
        txtMaxNumber.setNumCfgMap(map);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            String bmaxNumber = txtMaxNumber.getText().toString();
            String bminNumber = txtMinNumber.getText().toString();
            if (!bmaxNumber.isEmpty() && !bminNumber.isEmpty()) {
                BigDecimal numberFrom = new BigDecimal(bminNumber);
                BigDecimal numberTo = new BigDecimal(bmaxNumber);
                if (numberFrom.compareTo(numberTo) > 0) {
                    ToastHelper.showMessage("最大数量不能小于最小数量");
                    return;
                }
            }
            updateFocus();
        } else if (v.getId() == title.getImgBack().getId()) {
            mActivity.finish();
        }
    }

    /**
     * 添加和修改我关注的产品
     */
    @SuppressLint("UseValueOf")
    private void updateFocus() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                if (isAdd) {
                    Client.getService(IZoneRequestFocusService.class).submitZoneRequestFocus(formUpEntity());
                } else {
                    Client.getService(IZoneRequestFocusService.class).deleteZoneRequestFocus(formUpEntity());
                }
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    if (isAdd) {
                        ToastHelper.showMessage("添加成功");
                    } else {
                        ToastHelper.showMessage("修改成功");
                    }
                    mActivity.finish();
                }
            }
        });
    }

    /**
     * 创建上行实体
     *
     * @return 上行实体
     */
    private ZoneRequestFocusUpEntity formUpEntity() {
        ZoneRequestFocusUpEntity upEntity = new ZoneRequestFocusUpEntity();
        upEntity.setProductType(lblProductType.getValue());
        upEntity.setSubmitUserId(LoginUserContext.getLoginUserDto().getUserId());
        if (!StringUtility.isNullOrEmpty(lblBuySell.getValue())) // 买卖方向
        {
//            upEntity.setBuySell(lblBuySell.getValue().split("#")[1]);
            upEntity.setTradeMode(lblBuySell.getValue());
        }
        if (!StringUtility.isNullOrEmpty(lblProductPlace.getValue())) // 产地
        {
            upEntity.setProductPlace(lblProductPlace.getValue());
        }
        if (!StringUtility.isNullOrEmpty(lblDeliveryPlace.getValue())) // 交货地
        {
            upEntity.setDeliveryPlace(lblDeliveryPlace.getValue());
        }
        if (!StringUtility.isNullOrEmpty(txtMinNumber.getText().toString())) {
//            upEntity.setProductNumberFrom(new BigDecimal(txtMinNumber.getText().toString()));
        }

        if (!StringUtility.isNullOrEmpty(txtMaxNumber.getText().toString())) {
//            upEntity.setProductNumberTo(new BigDecimal(txtMaxNumber.getText().toString()));
        }
        // 设置质量标准
        /*NameValueItem quality = lblProductQuality.getViewTag();
        if (!lblProductType.getValue().startsWith("GT")) {
            if (!StringUtility.isNullOrEmpty(quality.getValue())) {
                upEntity.setProductQuality(quality.getValue());
            }
        } else {
            if (!TextUtils.isEmpty(quality.getValue())) {
                upEntity.setProductQualityEx1From(new BigDecimal(quality.getValue()));
            }
            if (quality.getTag() != null && !quality.getTag().equals("0")) {
                upEntity.setProductQualityEx1To(new BigDecimal((String) quality.getTag()));
            }
        }*/
        upEntity.setProductQuality(lblProductQuality.getViewTag().getValue());
        upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
        return upEntity;
    }

}
