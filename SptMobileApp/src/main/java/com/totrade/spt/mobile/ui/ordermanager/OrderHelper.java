package com.totrade.spt.mobile.ui.ordermanager;

import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.entity.MyContractDownEntity;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.ObjUtils;

/**
 * 订单管理助手
 * Created by Timothy on 2017/5/18.
 */
public class OrderHelper {

    private static OrderHelper ourInstance = new OrderHelper();
    public ContractDownEntity entity;
    public MyContractDownEntity contractDownEntity;
    private OrderAction orderAction;
    //订单详情页的显示划分为：订单数据区，商品数据区，资金数据区，支付金额数据区，操作区，转让数据区
    public boolean[] viewClubVisables = new boolean[]{false, false, false, false, false, false};

    public static OrderHelper getInstance() {
        return ourInstance;
    }

    private OrderHelper() {
    }

    public void initContractEntity(MyContractDownEntity entity){
        this.contractDownEntity = entity;
    }

    public void init(ContractDownEntity entity) {
        this.entity = entity;

        if (entity.isCanDeclareDelivery()) orderAction = OrderAction.ACTION_DECLARE_DELIVERY;
        else if (entity.isCanPayRemain()) orderAction = OrderAction.ACTION_PAY;
        else if (entity.isCanDelivery()) orderAction = OrderAction.ACTION_DELIVETY;
        else if (entity.isCanTakeDelivery()) orderAction = OrderAction.ACTION_RECEIVE;
        else orderAction = null;

        if (!ObjUtils.isEmpty(entity.getOrderStatus())) {
            viewClubVisables[0] = true;
            viewClubVisables[1] = true;
            viewClubVisables[4] = null != orderAction;
            if (entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_DECLAREDELIVERY)//待宣港
                    || entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_SELLOUT)//转让中
                    || entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_BREACH)) {//已违约
                viewClubVisables[2] = true;
                viewClubVisables[3] = false;
                viewClubVisables[5] = false;
            } else if (entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_WAIT_PAYALL)) {//待付款
                viewClubVisables[2] = true;
                viewClubVisables[3] = true;
                viewClubVisables[5] = false;
            } else if (entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_WAIT_DELIVERY)//待发货
                    || entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_TAK_DELIVERY)//待收货
                    || entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_DELIVERED)) {//已交收
                viewClubVisables[2] = false;
                viewClubVisables[3] = true;
                viewClubVisables[5] = false;
            } else if (entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_TRANSFER)) {//已转让
                viewClubVisables[2] = false;
                viewClubVisables[3] = false;
                viewClubVisables[5] = true;
            }

            if (loadViewStubListener != null) {
                loadViewStubListener.initViewStub(viewClubVisables);
            }
        }
    }

    private ILoadViewStubListener loadViewStubListener;

    public void setLoadViewStubListener(ILoadViewStubListener listener) {
        this.loadViewStubListener = listener;
    }

    public interface ILoadViewStubListener {
        void initViewStub(boolean[] bos);
    }

    /**
     * 是否违约
     */
    public boolean isBreakContract() {
        return entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_BREACH);
    }

    /**
     * 是否待付款
     */
    public boolean isPayment() {
        return entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_WAIT_PAYALL);//待付款
    }

    /**
     * 是否待发货
     */
    public boolean isPayAllbond() {
        return entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_WAIT_DELIVERY);
    }

    /**
     * 待收货
     */
    public boolean isSend() {
        return entity.getOrderStatus().equals(DealConstant.ORDER_STATUS_TAK_DELIVERY);
    }

    /**
     * 是否买家
     *
     * @return
     */
    public boolean isBuyer() {
        return entity.getBuyerCompanyTag().equals(LoginUserContext.getLoginUserDto().getCompanyTag());
    }

    public OrderAction getOrderAction() {
        return orderAction;
    }
}
