package com.totrade.spt.mobile.ui.maintrade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.zone.dto.ZoneMyOfferDownEntity;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.ui.maintrade.fragment.MyOrderDetailFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.OrderFromStockFragment2;
import com.totrade.spt.mobile.ui.maintrade.fragment.ResellRechargeDealFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.TradeFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.ZoneContractDetailFragment;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.ui.maintrade.fragment.TransferInventoryListFragment;

/**
 * 库存转卖，修改挂单，以及详情
 * Created by Timothy on 2017/2/23.
 */

public class TransferInventoryActivity extends SptMobileActivityBase {

    private TransferInventoryListFragment transferInventoryListFragment;    //库存转卖，修改挂单列表
    private MyOrderDetailFragment myOrderDetailFragment;                    //修改挂单详情
    private ZoneContractDetailFragment zoneContractDetailFragment;          //库存转卖/回补详情

    private OrderFromStockFragment2 orderFromStockFragment2;                //非 贸易大厅首页 进入库存转卖，修改挂单列表
    private ResellRechargeDealFragment resellRechargeDealFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        initFragment();
        parseIntent();
    }

    private void initFragment() {
        transferInventoryListFragment = new TransferInventoryListFragment();
        myOrderDetailFragment = new MyOrderDetailFragment();
        zoneContractDetailFragment = new ZoneContractDetailFragment();
        orderFromStockFragment2 = new OrderFromStockFragment2();
        resellRechargeDealFragment = new ResellRechargeDealFragment();
    }

    private void parseIntent() {
        if (TradeFragment.class.getName().equals(getIntent().getStringExtra(TradeFragment.class.getName()))) {
            switchTIF();
        } else {
            switchContent(orderFromStockFragment2);
        }
    }

    public void switchTIF() {
        switchContent(transferInventoryListFragment);
    }

    /**
     * 修改挂单
     * 切换至实时挂单详细
     *
     * @param downEntity 实时挂单实体
     */
    public void switchMyOrderDetailFragment(@NonNull ZoneMyOfferDownEntity downEntity) {
        myOrderDetailFragment.setOfferDownEntity(downEntity);
        switchContent(myOrderDetailFragment, true);
    }

    /**
     * 库存转卖
     * 切换至 库存挂单详细
     *
     * @param downEntity
     * @param isResell
     */
    public void switchContractDetailFragment(@NonNull MyStockDownEntity downEntity, boolean isResell) {
        zoneContractDetailFragment.setContractEntity(downEntity, isResell);
        switchContent(zoneContractDetailFragment, true);
    }

    public void switchResellRechargeDealFragment(MyStockDownEntity obj, boolean buySell) {
        resellRechargeDealFragment.setContractEntity(obj, buySell);
        switchContent(resellRechargeDealFragment, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (zoneContractDetailFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        if (myOrderDetailFragment.onKeyDown(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
