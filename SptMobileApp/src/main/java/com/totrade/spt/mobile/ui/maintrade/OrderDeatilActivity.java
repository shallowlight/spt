package com.totrade.spt.mobile.ui.maintrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.ui.maintrade.fragment.BuyerFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.JoinSellOrBuyFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.OrderDetailFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.OrderFromStockFragment;
import com.totrade.spt.mobile.ui.maintrade.fragment.SellerFragment;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.ui.maintrade.fragment.ZoneResellFragment;

/**
 * 贸易大厅订单详情
 *
 * @author huangxy
 * @date 2017/4/12
 */
public class OrderDeatilActivity extends BaseActivity {
    private OrderDetailFragment orderDetailFragment;
    private JoinSellOrBuyFragment joinSellOrBuyFragment;
    private OrderFromStockFragment orderFromStockFragment;
    private BuyerFragment buyerFragment;
    private SellerFragment sellerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        orderDetailFragment = new OrderDetailFragment();
        joinSellOrBuyFragment = new JoinSellOrBuyFragment();
        orderFromStockFragment = new OrderFromStockFragment();
        buyerFragment = new BuyerFragment();
        sellerFragment = new SellerFragment();

        NotifyUtility.cancelJpush(this);
        parseIntent();
    }

    private void parseIntent() {
        if (orderDetailFragment.isAdded()) return;
        switchContent(orderDetailFragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        NotifyUtility.cancelJpush(this);
        if (intent.getStringExtra(ZoneResellFragment.class.getName()) != null) {
            popBack();
        }
    }

    private Object mCurDataFromStock;

    public Object getCurDataFromStock() {
        return mCurDataFromStock;
    }

    public void setCurDataFromStock(Object curDataFromStock) {
        mCurDataFromStock = curDataFromStock;
    }

    public void switchSOFSF(QueryMyStockUpEntity upEntity, TblZoneRequestOfferEntity offerEntity, ZoneRequestDownEntity requestDownEntity) {
        orderFromStockFragment.setData(upEntity, offerEntity, requestDownEntity);
        switchContent(orderFromStockFragment, true);
    }

    public void switchBBF(TblZoneRequestOfferEntity offerEntity, ZoneRequestDownEntity requestEntity, String title) {
        buyerFragment.setData(offerEntity, requestEntity, title);
        switchContent(buyerFragment, true);
    }

    public void switchBSF(TblZoneRequestOfferEntity offerEntity, ZoneRequestDownEntity requestEntity, String title) {
        sellerFragment.setData(offerEntity, requestEntity, title);
        switchContent(sellerFragment, true);
    }

    /**
     * 加入买/卖
     *
     * @param buySell    买卖方向
     * @param downEntity 对应的挂单模板实体
     */
    public void switchJSF(String buySell, ZoneRequestDownEntity downEntity) {
        joinSellOrBuyFragment.setZoneRequestDownEntity(buySell, downEntity);
        switchContent(joinSellOrBuyFragment, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (joinSellOrBuyFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
