package com.totrade.spt.mobile.ui.maintrade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.zone.dto.ZoneMyOfferDownEntity;
import com.totrade.spt.mobile.ui.maintrade.fragment.MyOrderDetailFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.ZoneContractDetailFragment;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.ui.maintrade.fragment.MyOrderFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.ZoneResellFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.ZoneReEditOfferFragment;

/**
 * 商品专区我的订单
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class ZoneMyOrderActivity extends SptMobileActivityBase {

    private MyOrderDetailFragment myOrderDetailFragment;
    private ZoneContractDetailFragment zoneContractDetailFragment;
    private ZoneResellFragment zoneResellFragment;
    private ZoneReEditOfferFragment zoneReEditOfferFragment;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_zone_myorder);
        initView();
    }

    private void initView()
    {
        MyOrderFragment myOrderFragment = new MyOrderFragment();
        myOrderDetailFragment = new MyOrderDetailFragment();
        zoneResellFragment = new ZoneResellFragment();
        zoneContractDetailFragment = new ZoneContractDetailFragment();
        zoneReEditOfferFragment = new ZoneReEditOfferFragment();
        switchContent(myOrderFragment);
    }

    /**
     * 切换至实时挂单详细
     * @param downEntity 实时挂单实体
     */
    public void switchMyOrderDetailFragment(@NonNull ZoneMyOfferDownEntity downEntity)
    {
        myOrderDetailFragment.setOfferDownEntity(downEntity);
        switchContent(myOrderDetailFragment, true);
    }

    /**
     * 切换至 库存挂单详细
     * @param downEntity 库存合同实体
     */
    public void switchContractDetailFragment(@NonNull MyStockDownEntity downEntity, boolean isResell)
    {
        zoneContractDetailFragment.setContractEntity(downEntity, isResell);
        switchContent(zoneContractDetailFragment, true);
    }

    /**
     * 切换至转卖
     * @param entity 库存合同实体
     */
    public void switchResellFragment(@NonNull MyStockDownEntity entity, boolean isResell)
    {
        zoneResellFragment.setContractEntity(entity, isResell);
        switchContent(zoneResellFragment, true);
    }

    /**
     * 切换至修改订单
     * @param entity 订单实体
     */
    public void switchReEditOfferFragment(@NonNull ZoneMyOfferDownEntity entity)
    {
        zoneReEditOfferFragment.setContractEntity(entity);
        switchContent(zoneReEditOfferFragment, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (zoneResellFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
