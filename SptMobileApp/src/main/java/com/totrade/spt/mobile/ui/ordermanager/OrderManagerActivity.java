package com.totrade.spt.mobile.ui.ordermanager;

import android.os.Bundle;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.view.R;

/**
 * 订单管理
 * Created by Timothy on 2017/4/11.
 */

public class OrderManagerActivity extends BaseActivity {

    private OrderListFragment orderListFragment;//订单列表
    private CreditFragment creditFragment;//上传贷权凭证
    private String initFragmentName;//初始Fragment名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        initFragments();
        initFragmentName = getIntent().getStringExtra("fragmentName");
        if (!StringUtility.isNullOrEmpty(initFragmentName)) {
            if (CreditFragment.class.getName().equals(initFragmentName)) {
                switchCF(false);
            }
        } else {
            switchOLF();
        }
    }

    private void initFragments() {
        orderListFragment = new OrderListFragment();
        creditFragment = new CreditFragment();
    }

    public void switchOLF() {
        switchContent(orderListFragment);
    }

    public void switchCF(boolean bo) {
        switchContent(creditFragment, bo);
    }

}
