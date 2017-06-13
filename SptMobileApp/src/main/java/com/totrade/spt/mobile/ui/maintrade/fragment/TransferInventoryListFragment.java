package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.zone.dto.QueryPageZoneOfferUpEntity;
import com.autrade.spt.zone.dto.ZoneMyOfferDownEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestOfferService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.maintrade.IndicatorHelper;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;
import com.totrade.spt.mobile.ui.maintrade.adapter.ChangeOrderAdapter;
import com.totrade.spt.mobile.ui.maintrade.adapter.TransferInventoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存转卖，修改挂单页面
 * Created by Timothy on 2017/2/23.
 */

public class TransferInventoryListFragment extends SptMobileFragmentBase implements XRecyclerView.LoadingListener, RecyclerAdapterBase.ItemClickListener {

    private TransferInventoryActivity activity;
    private View rootView;
    private XRecyclerView recyclerView;
    private TabLayout tabLayout;
    private TextView title;

    private static final int numberPerPage = 10;                                // 每页显示条数
    private static int currentPageNumber = 1;                                   // 当前页
    private ChangeOrderAdapter realTimeOrderAdapter;                            //修改挂单列表适配器
    private TransferInventoAdapter stockOrderAdapter;                           //转卖库存列表适配器
    private List<ZoneMyOfferDownEntity> zoneMyOfferDownEntities;                //实时挂单下行数据
    private List<MyStockDownEntity> myContractDownEntities;                     //库存列表下行数据
    private String action;
    private String productType;
    private boolean isResell;

    public TransferInventoryListFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (TransferInventoryActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_transfer_changeorder, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        recyclerView = (XRecyclerView) rootView.findViewById(R.id.xrcv_order);
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);
        rootView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        title = (TextView) rootView.findViewById(R.id.title);

        initData();
        return rootView;
    }

    private void initData() {
        action = activity.getIntent().getStringExtra(Zone.KEY_ACTION);
        productType = activity.getIntent().getStringExtra(Zone.PRODUCT_TYPE);
        isResell = true;
        initNeck(action);
        if (action.startsWith(Zone.STOCK)) {
            onReSellRecharge();

        } else if (action.equals(Zone.CHANGE_ORDER)) {
            title.setText("修改挂单");
            tabLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    /**
     * 转卖/回补
     */
    private void onReSellRecharge() {
            title.setText("可转卖库存");
            tabLayout.setVisibility(View.VISIBLE);
            tabLayout.addTab(tabLayout.newTab().setText("库存转卖"));
            tabLayout.addTab(tabLayout.newTab().setText("库存回补"));
            new IndicatorHelper().setIndicator(tabLayout);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    currentPageNumber = 1;
                    isResell = tab.getPosition() == 0;
                    getReSellRecharge();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
    }

    /**
     * 库存转卖回补列表
     */
    private void getReSellRecharge() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<MyStockDownEntity>>() {
            @Override
            public PagesDownResultEntity<MyStockDownEntity> requestService() throws DBException, ApplicationException {
                QueryMyStockUpEntity upEntity = new QueryMyStockUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setProductType(productType);
                upEntity.setNumberPerPage(numberPerPage);
                upEntity.setCurrentPageNumber(currentPageNumber);
                upEntity.setBondPayStatusTagCondition ("1|9");
                upEntity.setBuyerZoneStatusCondition("O|G");
                upEntity.setContractZoneStatus(DealConstant.ZONESTATUS_U);      //未宣布交收  取U

                return isResell ? Client.getService(IContractService.class).findMyStockList(upEntity) :
                        Client.getService(IContractService.class).findBuyBackStockList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<MyStockDownEntity> downResultEntity) {
                if (downResultEntity != null && !ObjUtils.isEmpty(downResultEntity.getDataList())) {
                    if (currentPageNumber == 1) {
                        myContractDownEntities.clear();
                    }
                    myContractDownEntities.addAll(downResultEntity.getDataList());
                    recyclerView.setNoMore(downResultEntity.getDataList().size() < numberPerPage);
                    recyclerView.setLoadingMoreEnabled(myContractDownEntities.size() < downResultEntity.getTotalCount());
                    stockOrderAdapter.notifyDataSetChanged();
                }
                recyclerView.loadMoreComplete();
                recyclerView.refreshComplete();
            }
        });
    }

    /**
     * 修改挂单列表
     */
    private void findZoneMyOfferList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<ZoneMyOfferDownEntity>>() {
            @Override
            public List<ZoneMyOfferDownEntity> requestService() throws DBException, ApplicationException {
                QueryPageZoneOfferUpEntity upEntity = new QueryPageZoneOfferUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setProductType(productType);
                upEntity.setPageSize(numberPerPage);
                upEntity.setPageNo(currentPageNumber);
                return Client.getService(IZoneRequestOfferService.class).findZoneMyOfferList(upEntity).getDataList();
            }

            @Override
            public void onDataSuccessfully(List<ZoneMyOfferDownEntity> list) {
                if (list != null) {
                    if (currentPageNumber == 1) {
                        zoneMyOfferDownEntities.clear();
                    }
                    zoneMyOfferDownEntities.addAll(list);
                    recyclerView.setNoMore(list.size() < numberPerPage);
                    recyclerView.setLoadingMoreEnabled(!(list.size() < numberPerPage));
                    realTimeOrderAdapter.notifyDataSetChanged();
                }
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }


    @Override
    public void onRefresh() {
        currentPageNumber = 1;
        clearAdapter(action);
        switchLoadData(action);
    }

    private void clearAdapter(String action) {
        if (action.equals(Zone.STOCK_RESELL_RECHARGE)) {
            myContractDownEntities.clear();
            stockOrderAdapter.clear();
        } else if (action.equals(Zone.CHANGE_ORDER)) {
            zoneMyOfferDownEntities.clear();
            realTimeOrderAdapter.clear();
        }
    }

    @Override
    public void onLoadMore() {
        currentPageNumber++;
        switchLoadData(action);
    }

    @Override
    public void itemClick(@NonNull Object obj, int position) {
        if (obj instanceof ZoneMyOfferDownEntity) {
            activity.switchMyOrderDetailFragment((ZoneMyOfferDownEntity) obj);
        } else if (obj instanceof MyStockDownEntity) {
            activity.switchContractDetailFragment((MyStockDownEntity) obj, isResell);
        }
    }

    private void initNeck(String action) {
        if (action.startsWith(Zone.STOCK)) {
            myContractDownEntities = new ArrayList<>();
            stockOrderAdapter = new TransferInventoAdapter(myContractDownEntities, isResell);
            stockOrderAdapter.setItemClickListener(this);
            recyclerView.setAdapter(stockOrderAdapter);
        } else if (action.equals(Zone.CHANGE_ORDER)) {
            zoneMyOfferDownEntities = new ArrayList<>();
            realTimeOrderAdapter = new ChangeOrderAdapter(zoneMyOfferDownEntities);
            realTimeOrderAdapter.setItemClickListener(this);
            recyclerView.setAdapter(realTimeOrderAdapter);
        }
    }

    private void switchLoadData(String action) {
        if (action.startsWith(Zone.STOCK)) {
            getReSellRecharge();
        } else if (action.equals(Zone.CHANGE_ORDER)) {
            findZoneMyOfferList();
        }
    }

}
