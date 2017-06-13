package com.totrade.spt.mobile.ui.fundmanager;

import android.view.View;
import android.widget.TextView;

import com.autrade.spt.bank.entity.RunningReportDownEntity;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

/**
 * 资金管理
 * 对账单详情
 * Created by Timothy on 2017/4/14.
 */

public class AccountStatementDetailFragment extends BaseSptFragment<FundManagerActivity> {
    private ComTitleBar title;
    private SuperTextView stv_pay_amount;
    private SuperTextView stv_other_company;
    private SuperTextView stv_cur_status;
    private SuperTextView stv_deal_time;
    private SuperTextView stv_constract_number;
    private RunningReportDownEntity runningReportDownEntity;
    private boolean isTSB;
    private TextView tv_statement_name;


    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_account_statement_detail;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        tv_statement_name = findView(R.id.tv_statement_name);
        stv_pay_amount = findView(R.id.stv_pay_amount);
        stv_other_company = findView(R.id.stv_other_company);
        stv_cur_status = findView(R.id.stv_cur_status);
        stv_deal_time = findView(R.id.stv_deal_time);
        stv_constract_number = findView(R.id.stv_constract_number);

        if (runningReportDownEntity != null) {
            tv_statement_name.setText(runningReportDownEntity.getBusinessTag1());
            getContractDetail(runningReportDownEntity.getBizId());
        }

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });
    }

    public void setRunningReportDownEntity(RunningReportDownEntity runningReportDownEntity) {
        this.runningReportDownEntity = runningReportDownEntity;
    }

    public void setIsTSB(boolean isTSB) {
        this.isTSB = isTSB;
    }

    /**
     * 获取详情并显示
     *
     * @param contractId 合同id
     */
    public void getContractDetail(final String contractId) {
        SubAsyncTask.create().setOnDataListener(getActivity(), true, new OnDataListener<ContractDownEntity>() {
            @Override
            public ContractDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).getContractDetail(contractId, LoginUserContext.getLoginUserDto().getUserId());
            }

            @Override
            public void onDataSuccessfully(ContractDownEntity entity) {
                if (entity != null) {
                    stv_pay_amount.setRightString(StringUtils.parseMoney(runningReportDownEntity.getAcctBalance()));
                    stv_other_company.setRightString(runningReportDownEntity.getObjAccountName1());
                    String contractStatus =  DictionaryUtility.getValue(SptConstant.SPTDICT_BONDPAYSTATUSTAG, entity.getBondPayStatusTag());
                    stv_cur_status.setRightString(contractStatus);
                    String deliveryTime = FormatUtil.date2String(entity.getContractTime(), "yyyy/MM/dd HH:mm:ss");
                    stv_deal_time.setRightString(deliveryTime);
                    stv_constract_number.setRightString(entity.getContractId());
                }

            }
        });
    }

//    private void showDetial(ContractDownEntity entity) {
//        //订单号还是合同号
//        boolean isZoneOrder = SptConstant.DEAL_TYPE_ZONE.equals(entity.getDealType());
//        String orderIdKey = (isZoneOrder ? "订单号:" : "合同号:");
//        String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, entity.getDeliveryPlace());
//        if (TextUtils.isEmpty(deliveryPlace)) {
//            deliveryPlace = entity.getDeliveryPlaceName();
//        }
//        boolean isGT = entity.getProductType().startsWith("GT");
//        String deliveryTimeKey = (!isZoneOrder && isGT && entity.getTradeMode().equals(DealConstant.TRADEMODE_DOMESTIC)) ? "最晚提单日" : "交货期";
////                合同状态
//        final boolean isBuyerArgue = !TextUtils.isEmpty(entity.getBuyerArgueMemo());                 //是否买家异议
//        boolean isSellerArgue = !TextUtils.isEmpty(entity.getSellerArgueMemo());
//        String contractStatus = (isBuyerArgue || isSellerArgue) ? (isBuyerArgue ? "买家异议" : "卖家异议")
//                : DictionaryUtility.getValue(SptConstant.SPTDICT_BONDPAYSTATUSTAG, entity.getBondPayStatusTag());
//        //交货期
//        String deliveryTime = isZoneOrder ?
//                DispUtility.zoneDeliveryTime2Disp(entity.getDeliveryTime(), entity.getDealTag3(), entity.getProductType(), entity.getTradeMode()) :
//                DispUtility.deliveryTimeToDisp(entity.getDeliveryTime(), entity.getProductType(), entity.getTradeMode());
//
//        details = new String[][]{
//                {orderIdKey, entity.getOrderNo(),},
//                {"合同状态", contractStatus,},
//                {"单价", DispUtility.price2Disp(entity.getProductPrice(), entity.getPriceUnit(), entity.getNumberUnit()),},
//                {"成交数量", DispUtility.productNum2Disp(entity.getDealNumber(), null, entity.getNumberUnit()),},
//                {"成交时间", FormatUtil.date2String(entity.getContractTime(), "yyyy/MM/dd HH:mm"),},
//                {"交货地", deliveryPlace,},
//                {deliveryTimeKey, deliveryTime,},
//                {"对方企业", runningReportDownEntity.getObjAccountName1(),},
//                {"定金", DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, entity.getBond()),},
//                {"总货款", DictionaryUtility.getValue(SptConstant.SPTDICT_PRICE_UNIT, entity.getPriceUnit()) +
//                        StringUtils.parseMoney(entity.getDealNumber().multiply(entity.getProductPrice()))},
//                {"摘要", runningReportDownEntity.getBusinessTag1(),},
//        };
//        for (int i = 0; i < details.length; i++) {
//            if (TextUtils.isEmpty(details[i][1])) {
//                continue;
//            }
//            View item = activity.getLayoutInflater().inflate(R.layout.item_funds_statement_detail, null);
//            TextView key = (TextView) item.findViewById(R.id.tv_key);
//            TextView value = (TextView) item.findViewById(R.id.tv_value);
//            key.setText(details[i][0]);
//            value.setText(details[i][1]);
//            ll_detail.addView(item);
//        }
//    }

    private void isIncome(TextView tv, RunningReportDownEntity entity) {
//        BigDecimal bd = isTSB ? entity.getCredit() : entity.getFeeTotal();
//        String text = StringUtils.parseMoney(bd);
//        int id;
//        if ("+".equals(entity.getDirect())) {
//            text = "+" + text;
//            id = R.color.ui_blue;
//        } else if ("-".equals(entity.getDirect())) {
//            text = "-" + text;
//            id = R.color.ui_gray_black;
//        } else {
//            id = R.color.ui_gray_black;
//        }
//        tv.setText(text);
//        tv.setTextColor(mActivity.getResources().getColor(id));
    }
}
