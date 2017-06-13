package com.totrade.spt.mobile.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.MyContractDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.StringUtility;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DecimalUtils;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SpanStrUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单管理-订单列表
 * Created by Timothy on 2017/4/11.
 */

public class OrderListAdapter extends RecyclerAdapterBase<MyContractDownEntity, OrderListAdapter.ViewHolder> implements DealConstant {
    public OrderListAdapter(List<MyContractDownEntity> list) {
        super(list);
    }

    @Override
    public OrderListAdapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    class ViewHolder extends ViewHolderBase<MyContractDownEntity> {

        @BindViewId(R.id.tv_order_id)
        TextView tv_order_id;                   //订单号
        @BindViewId(R.id.tv_deal_time)
        TextView tv_deal_time;                  //成交时间
        @BindViewId(R.id.tv_product_price)
        TextView tv_product_price;              //成交单价
        @BindViewId(R.id.tv_product_num)
        TextView tv_product_num;                //成交数量
        @BindViewId(R.id.tv_order_type)
        TextView tv_order_type;                //订单类型(买 or 卖)
        @BindViewId(R.id.tv_order_status)
        TextView tv_order_status;               //订单状态(有异议时也作异议)
        @BindViewId(R.id.tv_company_name)
        TextView tv_company_name;               //公司名称
        @BindViewId(R.id.tv_contract_id)
        TextView tv_contract_id;                //合同号
        @BindViewId(R.id.tv_deal_people)
        TextView tv_deal_people;                //交易员
        @BindViewId(R.id.tv_curday_index)
        TextView tv_curday_index;               //指数
        @BindViewId(R.id.iv_break_contract)
        ImageView iv_break_contract;            //违约图标
        @BindViewId(R.id.rl_action)
        private RelativeLayout rl_action;
        @BindViewId(R.id.tv_pay_money)
        TextView tv_pay_money;                  //需要支付的金额
        @BindViewId(R.id.tv_action)
        TextView tv_action;                     //操作按钮
        @BindViewId(R.id.tv_link)
        TextView tv_link;                       //查看贷权凭证

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_order_list);
        }

        @Override
        public void initItemData() {
            //订单号
            try {
                if (null != itemObj.getZoneOrderNoDto()) {
                    tv_order_id.setText(SpanStrUtils.createBuilder(itemObj.getZoneOrderNoDto().getBusinessCode())
                            .setForegroundColor(context.getResources().getColor(R.color.black_txt_1d))
                            .append(itemObj.getZoneOrderNoDto().getProductYear())
                            .setForegroundColor(context.getResources().getColor(R.color.gray_txt_99))
                            .append(itemObj.getZoneOrderNoDto().getProductCodeOrName())
                            .setForegroundColor(context.getResources().getColor(R.color.black_txt_1d))
                            .append(itemObj.getZoneOrderNoDto().getMonthAndUpDown())
                            .setForegroundColor(context.getResources().getColor(R.color.gray_txt_99))
                            .create());
                }
            } catch (Exception e) {

            }

            //买卖转
            boolean isBuyOrder = itemObj.getBuyerCompanyTag().equals(LoginUserContext.getLoginUserDto().getCompanyTag());
            if (itemObj.getBuySell().equals(DealConstant.BUYSELL_BUY)) {
                tv_order_type.setText("买");
                tv_order_type.setBackgroundResource(R.drawable.bg_red_2coner_circle);
            } else if (itemObj.getBuySell().equals(DealConstant.BUYSELL_SELL)) {
                tv_order_type.setText("卖");
                tv_order_type.setBackgroundResource(R.drawable.bg_green_2coner_circle);
            } else if (itemObj.getBuySell().equals("R")) {
                tv_order_type.setText("转");
                tv_order_type.setBackgroundResource(R.drawable.bg_gray_2coner_circle);
            }

            String orderStatus = itemObj.getOrderStatus();
            if (!StringUtility.isNullOrEmpty(orderStatus)) {
                tv_order_status.setText(Dictionary.CodeToKey(Dictionary.CONSTRACT_STATUS, orderStatus));
            }

            //是否违约
            if (!StringUtility.isNullOrEmpty(itemObj.getOrderStatus())) {
                int visiable = orderStatus.equals(DealConstant.ORDER_STATUS_BREACH) ? View.VISIBLE : View.GONE;
                iv_break_contract.setVisibility(visiable);
            }

            //成交时间
            tv_deal_time.setText("成交时间:" + DateUtility.formatToStr(itemObj.getContractTime(), "yyyy/MM/dd"));

            //商品价格
            tv_product_price.setText(DispUtility.price2Disp(itemObj.getProductPrice(), itemObj.getPriceUnit(), itemObj.getNumberUnit()));

            //商品数量
            tv_product_num.setText(DispUtility.productNum2Disp(itemObj.getDealNumber(), null, itemObj.getNumberUnit()));

            //公司名称(买订单显示卖方公司名称，卖订单显示买方公司名称)
            tv_company_name.setText(isBuyOrder ? itemObj.getSellerCompanyName() : itemObj.getBuyerCompanyName());

            //合同号
            tv_contract_id.setText("合同号:" + itemObj.getContractId());

            //交易员
            boolean isBuyer = itemObj.getBuyerCompanyTag().equals(LoginUserContext.getLoginUserDto().getCompanyTag());
            String dealName = isBuyer ? itemObj.getBuyerName() : itemObj.getSellerName();
            tv_deal_people.setText("交易员:" + dealName);

            //成交日当天指数
            if (!ObjUtils.isEmpty(itemObj.getDealTodayIndex())) {
                tv_curday_index.setText(itemObj.getDealTodayIndex().intValue() + "");
            }

            //是否显示ACTION区域(按钮触发 付款、交收、发货、收货等操作),由订单状态决定
            if (!StringUtility.isNullOrEmpty(itemObj.getOrderStatus())) {
                boolean isActionVisiable = itemObj.isCanDeclareDelivery() || itemObj.isCanPayRemain() || itemObj.isCanDelivery() || itemObj.isCanTakeDelivery();
                rl_action.setVisibility(isActionVisiable ? View.VISIBLE : View.GONE);
                tv_action.setVisibility(View.VISIBLE);
                tv_link.setVisibility(View.GONE);
            }

            //如果是交易完成状态，或者是已转让（流程走完状态）此处依然显示aciton区域，但是action的按钮将设置为超链接
            if (!StringUtility.isNullOrEmpty(itemObj.getOrderStatus())) {
                boolean isShow = itemObj.getOrderStatus().equals(DealConstant.ORDER_STATUS_DELIVERED) || itemObj.getBondPayStatusTag().equals(DealConstant.BONDPAYSTATUS_TAG_DELIVERED);
                if (isShow) {
                    rl_action.setVisibility(View.VISIBLE);
                    tv_action.setVisibility(View.GONE);
                    tv_link.setVisibility(View.VISIBLE);
                }
            }

            //确定是否显示待付余款项(只有 已交付保证金（待付款）状态，才显示待付款控件)
            if (!ObjUtils.isEmpty(itemObj.getOrderStatus())) {
                tv_pay_money.setVisibility(itemObj.isCanPayRemain() ? View.VISIBLE : View.GONE);
            }

            //待付款,总货款-冻结资金
            tv_pay_money.setText("待付货款:" + DecimalUtils.keep2PointStringAndCommaWithUnit(itemObj.getPriceTotal(), "元"));

            //按钮上的文本
            if (!ObjUtils.isEmpty(itemObj.getBondPayStatusTag())) {
                if (itemObj.isCanDeclareDelivery())
                    tv_action.setText(AppConstant.ACTION_DECLARE_DELIVERY);
                else if (itemObj.isCanPayRemain())
                    tv_action.setText(AppConstant.ACTION_PAY);
                else if (itemObj.isCanDelivery())
                    tv_action.setText(AppConstant.ACTION_DELIVETY);
                else if (itemObj.isCanTakeDelivery())
                    tv_action.setText(AppConstant.ACTION_RECEIVE);
            }

            tv_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionClickListener != null) {
                        onActionClickListener.onActionClick(itemObj);
                    }
                }
            });

            tv_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mergeContractProveTextTemplate(itemObj.getContractId());
                }
            });
        }
    }

    private void mergeContractProveTextTemplate(final String contractId) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).mergeContractProveTextTemplate(contractId);
            }

            @Override
            public void onDataSuccessfully(String obj) {
                if (null != obj) {
                    //TODO 跳转显示凭证
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("resultString", obj);
                    intent.putExtra("titleString", "贷权转移凭证");
                    context.startActivity(intent);
                }
            }
        });
    }

    public interface OnActionClickListener {
        void onActionClick(MyContractDownEntity entity);
    }

    public void setOnActionClickListener(OnActionClickListener onActionClickListener) {
        this.onActionClickListener = onActionClickListener;
    }

    public OnActionClickListener onActionClickListener;

}
