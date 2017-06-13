package com.totrade.spt.mobile.ui.maintrade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.dto.ZoneOrderNoDto;
import com.autrade.spt.common.entity.FormConfigEntity;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.WebViewActivity;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/12.
 */
public class ContractHelper {

    public void readContractById(final Activity activity, final String contractId) {
        SubAsyncTask.create().setOnDataListener(activity, true, new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).mergeContractTemplate(contractId);
            }

            @Override
            public void onDataSuccessfully(String strTemplate) {
                if (strTemplate != null) {
                    Intent intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra("resultString", strTemplate);
                    intent.putExtra("titleString", "合同");
                    activity.startActivity(intent);
                }
            }
        });
    }

    /**
     * 获取合同上行Entity
     */
    private ContractDownEntity formContractEntity(ZoneRequestDownEntity requestEntity, String num, String price, boolean isSell, TblZoneRequestOfferEntity offerEntity) {
        String productType = requestEntity.getProductType();
        ContractDownEntity upEntity = new ContractDownEntity();
        upEntity.setProductName(requestEntity.getProductName()); // 商品名称
        upEntity.setProductType(requestEntity.getProductType()); // 商品类别
        upEntity.setProductPlace(requestEntity.getProductPlace()); // 商品产地
        upEntity.setDealNumber(new BigDecimal(num)); // 商品成交数量
        upEntity.setProductPrice(new BigDecimal(price));// 商品价格
        upEntity.setPayMethod(requestEntity.getPayMethod()); // 付款方式
        if (requestEntity.getProductType().startsWith("SL")) {
            upEntity.setWareHouseName(requestEntity.getWarehouseName());
            upEntity.setBrand(requestEntity.getBrand());
            upEntity.setProductClass(requestEntity.getProductClass()); // 传中文，老版本是输入
        }
        if (requestEntity.getProductType().startsWith("GT")) {
            upEntity.setDeliveryPlace(requestEntity.getDeliveryPlace());
            upEntity.setDeliveryPlaceName(requestEntity.getDeliveryPlaceName());
        } else {
            upEntity.setDeliveryPlace(requestEntity.getDeliveryPlace());
            upEntity.setDeliveryPlaceName(requestEntity.getDeliveryPlaceName());
        }
        upEntity.setDeliveryMode(requestEntity.getDeliveryMode()); // 提货方式

        if (!productType.startsWith("GT")) {
            upEntity.setProductQuality(requestEntity.getProductQuality()); // 商品品质
        } else {
            upEntity.setProductQualityEx1(requestEntity.getProductQualityEx1()); // 商品品质
        }
        upEntity.setMemo(requestEntity.getMemo());
        upEntity.setDeliveryTime(requestEntity.getDeliveryTime());
        String tradeMode = requestEntity.getTradeMode();
        upEntity.setTradeMode(tradeMode); // 贸易方式
        upEntity.setValidTime(requestEntity.getValidTime()); // 报价有效期
        upEntity.setFeeMode("U");                                        // 手续费模式
        FormConfigEntity entity = ProductCfgHelper.getCfgEntity(productType);
        BigDecimal fee = entity.getZoneFeeCfg();
        upEntity.setFee(fee);                                            // 手续费（单体）
        upEntity.setFeeTotal(fee.multiply(new BigDecimal(num)));                      // 手续费总数(买方或者卖方)
        upEntity.setPriceUnit(tradeMode.equals(SptConstant.TRADEMODE_DOMESTIC) ? SptConstant.PRICE_UNIT_RMB : SptConstant.PRICE_UNIT_USD); // 价格单位
        upEntity.setNumberUnit(requestEntity.getNumberUnit()); // 数量单位
        upEntity.setBond(requestEntity.getBond()); // 保证金（百分比）
        upEntity.setBuySell(isSell ? SptConstant.BUYSELL_SELL : SptConstant.BUYSELL_BUY);
        upEntity.setRequestUserId(LoginUserContext.getLoginUserDto().getUserId());
        upEntity.setTemplateId(offerEntity.getTemplateId());
        upEntity.setDealType(SptConstant.DEAL_TYPE_ZONE);

        upEntity.setDealTag3(requestEntity.getRequestType());
        upEntity.setPairCode(offerEntity.getPairCode());

        return upEntity;
    }

    /**
     * 阅读合同
     */
    public void readContract(final ZoneRequestDownEntity requestEntity, final TblZoneRequestOfferEntity offerEntity, final String num, final String price, final boolean isSell) {
        SubAsyncTask.create().setOnDataListener(activity, true, new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                ProductCfgHelper.tryLoadCfg(requestEntity.getProductType());
                ContractDownEntity entity = formContractEntity(requestEntity, num, price, isSell, offerEntity);
                return Client.getService(IContractService.class).mergeContractTemplate(entity);
            }

            @Override
            public void onDataSuccessfully(String strTemplate) {
                if (strTemplate != null) {
                    Intent intent = new Intent(SptApplication.context, WebViewActivity.class);
                    intent.putExtra("resultString", strTemplate);
                    intent.putExtra("titleString", "合同");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SptApplication.context.startActivity(intent);
                }
            }
        });
    }

    public Spanned getOrderId(ZoneOrderNoDto zoneOrderNoDto) {
        if (zoneOrderNoDto == null) return new SpannedString("");
        StringBuffer sb = new StringBuffer();
        sb.append("<font color='#1d1d1d'>");
        sb.append(zoneOrderNoDto.getBusinessCode());
        sb.append("</font>");
        sb.append("<font color='#999999'>");
        sb.append(zoneOrderNoDto.getProductYear());
        sb.append("</font>");
        sb.append("<font color='#1d1d1d'>");
        sb.append(zoneOrderNoDto.getProductCodeOrName());
        sb.append("</font>");
        sb.append("<font color='#999999'>");
        sb.append(zoneOrderNoDto.getMonthAndUpDown());
        sb.append("</font>");

        return Html.fromHtml(sb.toString());
    }

    public static ContractHelper instance = new ContractHelper();

    private Activity activity;

    public ContractHelper setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }
}
