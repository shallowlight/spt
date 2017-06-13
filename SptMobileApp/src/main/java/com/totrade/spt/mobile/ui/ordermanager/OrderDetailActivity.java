package com.totrade.spt.mobile.ui.ordermanager;

import android.os.Bundle;
import android.view.KeyEvent;

import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.view.R;

/**
 * 订单管理模块
 * 订单详情
 * Created by Timothy on 2017/4/20.
 */

public class OrderDetailActivity extends BaseActivity {

    private OrderDetailFragment orderDeatilFragment;
    private CreditFragment creditFragment;
    private DeliveryFragment deliveryFragment;

    public String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        initFragments();

        action = getIntent().getStringExtra("action");
        if (!StringUtility.isNullOrEmpty(action)) {
            if (action.equals(DealConstant.ORDER_STATUS_DECLAREDELIVERY)) switchDF();
            else if (action.equals(DealConstant.ORDER_STATUS_WAIT_DELIVERY)) switchCF();
        } else {
            switchODF();
        }
    }

    private void initFragments() {
        orderDeatilFragment = new OrderDetailFragment();
        creditFragment = new CreditFragment();
        deliveryFragment = new DeliveryFragment();
    }

    public void switchODF() {
        switchContent(orderDeatilFragment);
    }

    public void switchCF() {
        switchContent(creditFragment, true);
    }

    public void switchDF() {
        switchContent(deliveryFragment, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (deliveryFragment.isAdded()) {
                return deliveryFragment.onKeyDown(keyCode, event);
            }
            if (creditFragment.isAdded()) {
                creditFragment.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
