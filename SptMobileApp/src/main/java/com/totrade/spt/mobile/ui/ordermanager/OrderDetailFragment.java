package com.totrade.spt.mobile.ui.ordermanager;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.entity.ContractUpEntity;
import com.autrade.spt.deal.entity.PayAllRemainPreviewDownEntity;
import com.autrade.spt.deal.service.inf.IContractBondService;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.adapter.OrderTransferListAdapter;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.utility.DecimalUtils;
import com.totrade.spt.mobile.utility.DictionaryTools;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SpanStrUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.KeyTextView;
import com.totrade.spt.mobile.view.customize.ScroListView;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.view.customize.countdown.TimerUtils;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.text.DecimalFormat;

/**
 * 订单详情
 * Created by Timothy on 2017/5/22.
 */

public class OrderDetailFragment extends BaseSptFragment<OrderDetailActivity> {

    private ComTitleBar title;
    private TextView tv_total_price;//总货款
    private TextView tv_pay_bond;//已付定金
    private TextView tv_obligation;//待付款
    private SuperTextView stv_need_pay;//需付款
    private LinearLayout ll_count_time;//倒计时容器

    private String contractId;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        contractId = mActivity.getIntent().getStringExtra(ContractDownEntity.class.getName());
        getContractDetail(contractId);
        NotifyUtility.cancelJpush(mActivity);
    }

    /**
     * 获取订单详情
     */
    private void getContractDetail(final String contractId) {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<ContractDownEntity>() {
            @Override
            public ContractDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).getContractDetail(contractId, LoginUserContext.getLoginUserDto().getUserId());
            }

            @Override
            public void onDataSuccessfully(ContractDownEntity obj) {
                if (null != obj) {
                    initData(obj);
                }
            }
        });
    }

    private void initData(ContractDownEntity entity) {
        if (null != entity) {
            OrderHelper.getInstance().setLoadViewStubListener(loadViewStubListener);
            OrderHelper.getInstance().init(entity);
        }
    }

    private OrderHelper.ILoadViewStubListener loadViewStubListener = new OrderHelper.ILoadViewStubListener() {
        @Override
        public void initViewStub(boolean[] bos) {
            if (bos[0]) loadOrderDataView();
            if (bos[1]) loadProductInfoDataView();
            if (bos[2]) loadOrderFundDataView();
            if (bos[3]) loadOrderPaymentDataView();
            if (bos[4]) loadActionView();
            if (bos[5]) loadOrderTransferView();
            if (bos[2] || bos[3]) payAllRemainPreview();
        }
    };

    /**
     * 订单数据视图
     */
    private void loadOrderDataView() {
        ViewStub orderPayMsgStub = findView(R.id.view_order_info);
        View orderInfoView = orderPayMsgStub.inflate();
        KeyTextView ktv_order_id = (KeyTextView) orderInfoView.findViewById(R.id.ktv_order_id);
        TextView tv_deal_time = (TextView) orderInfoView.findViewById(R.id.tv_deal_time);
        TextView tv_product_price = (TextView) orderInfoView.findViewById(R.id.tv_product_price);
        TextView tv_product_num = (TextView) orderInfoView.findViewById(R.id.tv_product_num);
        TextView tv_order_type = (TextView) orderInfoView.findViewById(R.id.tv_order_type);
        TextView tv_order_status = (TextView) orderInfoView.findViewById(R.id.tv_order_status);
        TextView tv_company_name = (TextView) orderInfoView.findViewById(R.id.tv_company_name);
        KeyTextView tv_deal_people = (KeyTextView) orderInfoView.findViewById(R.id.tv_deal_people);
        KeyTextView tv_contract_id = (KeyTextView) orderInfoView.findViewById(R.id.tv_contract_id);
        ImageView iv_break_contract = (ImageView) orderInfoView.findViewById(R.id.iv_break_contract);
        TextView tv_curday_index = (TextView) orderInfoView.findViewById(R.id.tv_curday_index);

        //违约图标
        iv_break_contract.setVisibility(OrderHelper.getInstance().isBreakContract() ? View.VISIBLE : View.GONE);

        //订单号
        try{
            if (null != OrderHelper.getInstance().entity.getZoneOrderNoDto()) {
                ktv_order_id.setText(SpanStrUtils.createBuilder(OrderHelper.getInstance().entity.getZoneOrderNoDto().getBusinessCode())
                        .setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                        .append(OrderHelper.getInstance().entity.getZoneOrderNoDto().getProductYear()).setForegroundColor(getResources().getColor(R.color.gray_txt_99))
                        .append(OrderHelper.getInstance().entity.getZoneOrderNoDto().getProductCodeOrName()).setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                        .append(OrderHelper.getInstance().entity.getZoneOrderNoDto().getMonthAndUpDown()).setForegroundColor(getResources().getColor(R.color.gray_txt_99))
                        .create());
            }
        }catch (Exception e){

        }

        //买卖
        if (!ObjUtils.isEmpty(OrderHelper.getInstance().entity.getBuySell())) {
            OrderBuySell orderBuySell = OrderBuySell.valueOf(OrderHelper.getInstance().entity.getBuySell());
            tv_order_type.setText(orderBuySell.text);
            tv_order_type.setBackgroundResource(orderBuySell.bgResId);
        }

        //合同状态
        tv_order_status.setText(DictionaryTools.i().getValue(SptConstant.SPTDICT_BONDPAYSTATUSTAG, OrderHelper.getInstance().entity.getOrderStatus()));

        //成交时间
        tv_deal_time.setText("成交时间:" + OrderHelper.getInstance().entity.getDeliveryTimeStr());

        //商品价格
        tv_product_price.setText(DispUtility.price2Disp(OrderHelper.getInstance().entity.getProductPrice(), OrderHelper.getInstance().entity.getPriceUnit(), OrderHelper.getInstance().entity.getNumberUnit()));

        //商品数量
        tv_product_num.setText(DispUtility.productNum2Disp(OrderHelper.getInstance().entity.getDealNumber(), null, OrderHelper.getInstance().entity.getNumberUnit()));

        //公司名称(买订单显示卖方公司名称，卖订单显示买方公司名称)
        tv_company_name.setText(OrderHelper.getInstance().isBuyer() ? OrderHelper.getInstance().entity.getSellerCompanyName() : OrderHelper.getInstance().entity.getBuyerCompanyName());

        //合同号
        tv_contract_id.setText(SpanStrUtils.createBuilder("合同号:").setForegroundColor(getResources().getColor(R.color.gray_txt_87))
                .append(OrderHelper.getInstance().entity.getContractId()).setForegroundColor(getResources().getColor(R.color.blue_txt_3de)).setUnderline().create());

        //交易员
        String dealName = OrderHelper.getInstance().isBuyer() ? OrderHelper.getInstance().entity.getBuyerName() : OrderHelper.getInstance().entity.getSellerName();
        tv_deal_people.setText("交易员:" + dealName);

        //成交日当天指数
        if (!ObjUtils.isEmpty(OrderHelper.getInstance().entity.getDealTodayIndex())) {
            tv_curday_index.setText(OrderHelper.getInstance().entity.getDealTodayIndex().intValue() + "");
        }

        //合同的点击
        tv_contract_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContractHelper.instance.readContractById(mActivity, OrderHelper.getInstance().entity.getContractId());
            }
        });
    }

    /**
     * 加载产品信息视图
     */
    private void loadProductInfoDataView() {
        ViewStub productInfoStub = findView(R.id.view_product_info);
        View productInfoView = productInfoStub.inflate();
        KeyTextView ktv_delivery_period = (KeyTextView) productInfoView.findViewById(R.id.ktv_delivery_period);
        KeyTextView ktv_delevery_place = (KeyTextView) productInfoView.findViewById(R.id.ktv_delevery_place);
        KeyTextView ktv_quality_standard = (KeyTextView) productInfoView.findViewById(R.id.ktv_quality_standard);
        KeyTextView ktv_bond = (KeyTextView) productInfoView.findViewById(R.id.ktv_bond);
        KeyTextView ktv_origin_product_place = (KeyTextView) productInfoView.findViewById(R.id.ktv_origin_product_place);
        KeyTextView ktv_reservoir_area = (KeyTextView) productInfoView.findViewById(R.id.ktv_reservoir_area);
        KeyTextView ktv_supplier = (KeyTextView) productInfoView.findViewById(R.id.ktv_supplier);

        boolean isZoneDeal = SptConstant.DEAL_TYPE_ZONE.equals(OrderHelper.getInstance().entity.getDealType());//是否为专区买卖
        //交收期时间
        String deliveryTime = isZoneDeal ?
                DispUtility.zoneDeliveryTime2Disp(OrderHelper.getInstance().entity.getDeliveryTime(), OrderHelper.getInstance().entity.getDealTag3(), OrderHelper.getInstance().entity.getProductType(), OrderHelper.getInstance().entity.getTradeMode()) :
                DispUtility.deliveryTimeToDisp(OrderHelper.getInstance().entity.getDeliveryTime(), OrderHelper.getInstance().entity.getProductType(), OrderHelper.getInstance().entity.getTradeMode());

        //交货地
        String deliveryPlace = DictionaryTools.i().getValue(SptConstant.SPTDICT_DELIVERY_PLACE, OrderHelper.getInstance().entity.getDeliveryPlace());
        if (TextUtils.isEmpty(deliveryPlace)) {
            deliveryPlace = OrderHelper.getInstance().entity.getDeliveryPlaceName();
        }
        ktv_delevery_place.setKey("交货地").setValue(deliveryPlace);

//        //质量标准
        DecimalFormat format = new DecimalFormat("#0.#######");
        String productQ = OrderHelper.getInstance().entity.getProductType().startsWith("GT")
                ? format.format(OrderHelper.getInstance().entity.getProductQualityEx1())
                : DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, OrderHelper.getInstance().entity.getProductQuality());
        boolean isGT = OrderHelper.getInstance().entity.getProductType().startsWith("GT");
        //交货期 标题
        String deliveryTimeTitle = (!isZoneDeal && isGT && OrderHelper.getInstance().entity.getTradeMode().equals(DealConstant.TRADEMODE_DOMESTIC)) ? "最晚提单日" : "交货期";
        //质量标准 标题
        String productQTitle = isGT ? "铁品位" : "质量标准";
        ktv_quality_standard.setKey(productQTitle).setValue(productQ);
        ktv_delivery_period.setKey(deliveryTimeTitle).setValue(deliveryTime);//此处交货期标题据条件切换

        //定金
        ktv_bond.setKey("定金").setValue(DictionaryTools.i().getValue(SptConstant.SPTDICT_BOND, OrderHelper.getInstance().entity.getBond()));

        //库区
        if (!StringUtility.isNullOrEmpty(OrderHelper.getInstance().entity.getReservoirArea())){
            String area = OrderHelper.getInstance().entity.getReservoirArea().equals("M") ? "主流库" : "非主流库";
            ktv_reservoir_area.setKey("库区").setValue(area);
        }

        //原产地
        ktv_origin_product_place.setKey("原产地").setValue(DictionaryTools.i().getValue(SptConstant.SPTDICT_PRODUCT_PLACE, OrderHelper.getInstance().entity.getProductPlace()));

        //供应商
        if (OrderHelper.getInstance().entity.getOrderStatus().equals(DealConstant.BONDPAYSTATUS_TAG_TRANSFER))
            ktv_supplier.setVisibility(View.VISIBLE);
        else
            ktv_supplier.setVisibility(View.GONE);
        ktv_supplier.setKey("供应商").setValue(OrderHelper.getInstance().entity.getSellerCompanyName());

    }

    /**
     * 加载订单保证金信息视图
     */
    private void loadOrderFundDataView() {
        ViewStub orderPayDetailStub = findView(R.id.view_order_pay_detail);
        View orderPayDetailView = orderPayDetailStub.inflate();
        tv_total_price = (TextView) orderPayDetailView.findViewById(R.id.tv_total_price);
        tv_pay_bond = (TextView) orderPayDetailView.findViewById(R.id.tv_pay_bond);
        tv_obligation = (TextView) orderPayDetailView.findViewById(R.id.tv_obligation);

    }

    /**
     * 加载支付计时和金额视图
     */
    private void loadOrderPaymentDataView() {
        ViewStub orderPayMsgStub = findView(R.id.view_order_pay_msg);
        View orderPayMsgView = orderPayMsgStub.inflate();
        stv_need_pay = (SuperTextView) orderPayMsgView.findViewById(R.id.stv_need_pay);
        ll_count_time = (LinearLayout) orderPayMsgView.findViewById(R.id.ll_count_time);
    }

    /**
     * 动作触发视图
     */
    private void loadActionView() {
        ViewStub actionViewStub = findView(R.id.view_order_action);
        View actionView = actionViewStub.inflate();
        TextView tv_action = (TextView) actionView.findViewById(R.id.tv_action);
        if (null != OrderHelper.getInstance().getOrderAction()) {
            tv_action.setVisibility(View.VISIBLE);
            tv_action.setText(OrderHelper.getInstance().getOrderAction().name);
        }

        tv_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAction(OrderHelper.getInstance().getOrderAction());
            }
        });
    }

    /**
     * 转让列表
     */
    private void loadOrderTransferView() {
        ViewStub stub = findView(R.id.view_order_transfer);
        View orderTranferView = stub.inflate();
        ScroListView slv_transfer_list = (ScroListView) orderTranferView.findViewById(R.id.slv_transfer_list);
        OrderTransferListAdapter transferListAdapter = new OrderTransferListAdapter(mActivity);
        slv_transfer_list.setAdapter(transferListAdapter);
        if (null != OrderHelper.getInstance().entity.getReSellSubList()) {
            transferListAdapter.refreshData(OrderHelper.getInstance().entity.getReSellSubList());
        }
    }

    /**
     * 支付余款预览
     */
    public void payAllRemainPreview() {
        SubAsyncTask.create().setOnDataListener(null, false, new OnDataListener<PayAllRemainPreviewDownEntity>() {
            @Override
            public PayAllRemainPreviewDownEntity requestService() throws DBException, ApplicationException {
                ContractUpEntity upEntity = new ContractUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                upEntity.setContractId(OrderHelper.getInstance().entity.getContractId());
                return Client.getService(IContractBondService.class).payAllRemainPreview(upEntity);
            }

            @Override
            public void onDataSuccessfully(final PayAllRemainPreviewDownEntity entityA) {
                PayAllRemainPreviewDownEntity payEntity;
                if (entityA != null) {
                    payEntity = entityA;
                } else {
                    payEntity = new PayAllRemainPreviewDownEntity();
                    payEntity.setPriceTotal(OrderHelper.getInstance().entity.getProductPrice().multiply(OrderHelper.getInstance().entity.getDealNumber()));
                    payEntity.setPaidBond(OrderHelper.getInstance().entity.getBlockBond());
                    payEntity.setRemainAmount(OrderHelper.getInstance().entity.getProductPrice().multiply(OrderHelper.getInstance().entity.getDealNumber()).subtract(OrderHelper.getInstance().entity.getBlockBond()));
                }
                if (OrderHelper.getInstance().viewClubVisables[2]) {
                    loadDataForOrderFundView(payEntity);
                }
                if (OrderHelper.getInstance().viewClubVisables[3]) {
                    loadDataForOrderPaymentView(payEntity);
                }
            }
        });
    }

    private void loadDataForOrderFundView(PayAllRemainPreviewDownEntity payEntity) {
        if (null != payEntity) {
            tv_total_price.setText(DecimalUtils.keep2PointStringToCurrency(payEntity.getPriceTotal()));
            tv_pay_bond.setText(DecimalUtils.keep2PointStringToCurrency(payEntity.getPaidBond()));
            tv_obligation.setText(DecimalUtils.keep2PointStringToCurrency(payEntity.getRemainAmount()));
        }
    }

    /**
     * 设置订单资金数据视图
     */
    private void loadDataForOrderPaymentView(PayAllRemainPreviewDownEntity payEntity) {
        //处理不同订单状态下该区域的显示与隐藏
        if (OrderHelper.getInstance().entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_DELIVERED)) {//交易完成
            ll_count_time.setVisibility(View.GONE);
            stv_need_pay.setLeftString("总贷款")
                    .setRightTVColor(getResources().getColor(R.color.black_txt_1d));
            if (payEntity != null) {
                stv_need_pay.setRightString(DecimalUtils.keep2PointStringToCurrency(payEntity.getPriceTotal()));
            }
        }
        //待付款状态
        if (OrderHelper.getInstance().isPayment()) {
            if (OrderHelper.getInstance().entity.isCanPayRemain()) {
                ll_count_time.setVisibility(View.VISIBLE);
                stv_need_pay.setLeftString("需付款").setRightTVColor(getResources().getColor(R.color.red_txt_d56));
                stv_need_pay.setRightString(DecimalUtils.keep2PointStringToCurrency(payEntity.getRemainAmount()));
                ll_count_time.addView(initCountView(payEntity));
            } else {
                ll_count_time.setVisibility(View.GONE);
                if (OrderHelper.getInstance().isBuyer()) {
                    stv_need_pay.setLeftString("总贷款").setRightTVColor(getResources().getColor(R.color.black_txt_1d));
                    stv_need_pay.setRightString(DecimalUtils.keep2PointStringToCurrency(payEntity.getPriceTotal()));
                } else {
                    stv_need_pay.setLeftString("待收款").setRightTVColor(getResources().getColor(R.color.black_txt_1d));
                    if (payEntity != null) {
                        stv_need_pay.setRightString(DecimalUtils.keep2PointStringToCurrency(payEntity.getRemainAmount()));
                    }
                }
            }
        }

        if (OrderHelper.getInstance().isPayAllbond() || OrderHelper.getInstance().isSend()) { //已付全款，待发货 或者 待收货
            stv_need_pay.setLeftString("总贷款")
                    .setRightTVColor(getResources().getColor(R.color.black_txt_1d));
            if (payEntity != null) {
                ll_count_time.setVisibility(View.GONE);
                stv_need_pay.setRightString(DecimalUtils.keep2PointStringToCurrency(payEntity.getPriceTotal()));
            }
        }
    }

    @NonNull
    private TextView initCountView(PayAllRemainPreviewDownEntity payEntity) {
        TextView tvCountDown = null;
        try {
            if (null != payEntity) {
                tvCountDown = TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, mActivity, payEntity.getRemainTimeMillis(), TimerUtils.TIME_STYLE_THREE, 0)
                        .getmDateTv();
            } else {
                tvCountDown = new TextView(mActivity);
                tvCountDown.setText("--天--时--分--秒");
            }
            tvCountDown.setTextColor(getResources().getColor(R.color.red_txt_d56));
            tvCountDown.setTextSize(11);
        } catch (Exception e) {
            if (null == tvCountDown) {
                tvCountDown = new TextView(mActivity);
                tvCountDown.setText("--天--时--分--秒");
            }
            return tvCountDown;
        }
        return tvCountDown;
    }

    private void requestAction(final OrderAction action) {
        switch (action) {
            case ACTION_DELIVETY:
                //确认发货
                mActivity.switchCF();
                break;
            case ACTION_RECEIVE:
                new CustomDialog.Builder(mActivity, action.name + "?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        confirm();
                    }
                }).create().show();
                break;
            case ACTION_DECLARE_DELIVERY:
                //发起交收
                mActivity.switchDF();
                break;
            case ACTION_PAY:
                // 确认付款(余款)
                payAllRemain();
                break;
        }
    }

    private void confirm() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IContractBondService.class).confirm(LoginUserContext.getLoginUserId(), OrderHelper.getInstance().entity.getContractId());
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("操作成功");
                    mActivity.finish();
                }
            }
        });
    }

    private void payAllRemain() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IContractBondService.class).payAllRemain(LoginUserContext.getLoginUserId(), OrderHelper.getInstance().entity.getContractId(), false);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("操作成功");
                    mActivity.finish();
                }
            }
        });
    }
}
