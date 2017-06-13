package com.totrade.spt.mobile.ui.maintrade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.ZoneChooseRequestAdapter;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;

/**
 * 从买卖单进入的
 * 可转卖库存和库存回补的列表
 *
 * @author huangxy
 * @date 2017/5/4
 */
public class ZoneChooseOfficeActivity extends BaseActivity implements RecyclerAdapterBase.ItemClickListener, XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;
    private ZoneChooseRequestAdapter zoneChooseRequestAdapter;
    private String buySell;

    private static final int numberPerPage = 10;            // 每页显示条数
    private static int currentPageNumber = 1;               // 当前页
    private String productType;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.zone_choose_request);

        parseIntent();
        initView();
        onRefresh();
    }

    private void initView() {
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);
        recyclerView.refresh();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.title)).setText(SptConstant.BUYSELL_BUY.equals(buySell) ? "库存回补" : "可转卖库存");
    }

    private void parseIntent() {
        productType = getIntent().getStringExtra(ActivityConstant.PRODUCTTYPE);
        buySell = getIntent().getStringExtra(ActivityConstant.SPTDICT_BUY_SELL);
    }

    /**
     * 库存转卖/回补列表
     */
    private void requestOfficeList() {
        SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<PagesDownResultEntity<MyStockDownEntity>>() {
            @Override
            public PagesDownResultEntity<MyStockDownEntity> requestService() throws DBException, ApplicationException {
                final QueryMyStockUpEntity upEntity = new QueryMyStockUpEntity();
                upEntity.setPageNo(numberPerPage);
                upEntity.setPageSize(currentPageNumber);
                upEntity.setProductType(productType);
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                boolean isSell = SptConstant.BUYSELL_SELL.equals(buySell);
                return isSell ?
                        Client.getService(IContractService.class).findMyStockList(upEntity) :
                        Client.getService(IContractService.class).findBuyBackStockList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<MyStockDownEntity> downResultEntity) {
                if (downResultEntity != null && downResultEntity.getDataList() != null) {
                    zoneChooseRequestAdapter = new ZoneChooseRequestAdapter(downResultEntity.getDataList());
                    zoneChooseRequestAdapter.setItemClickListener(ZoneChooseOfficeActivity.this);
                    recyclerView.setAdapter(zoneChooseRequestAdapter);
                }
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }


    @Override
    public void itemClick(@NonNull Object obj, int position) {

    }

    @Override
    public void onRefresh() {
        currentPageNumber = 1;
        requestOfficeList();
    }

    @Override
    public void onLoadMore() {
        currentPageNumber += 1;
        requestOfficeList();
    }

}
