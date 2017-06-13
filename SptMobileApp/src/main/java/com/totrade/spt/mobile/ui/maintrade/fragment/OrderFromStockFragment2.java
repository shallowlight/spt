package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.Temp;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;
import com.totrade.spt.mobile.ui.maintrade.adapter.TransferInventoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 从库存选择
 * Created by Administrator on 2016/12/7.
 */
public class OrderFromStockFragment2 extends BaseFragment implements XRecyclerView.LoadingListener, RecyclerAdapterBase.ItemClickListener {

    private TransferInventoryActivity activity;
    private View rootView;
    private XRecyclerView recyclerView;
    private TextView title;

    private TransferInventoAdapter stockOrderAdapter;
    private String buySell;
    private boolean isJoin;

    private static final int numberPerPage = 10;            // 每页显示条数
    private static int currentPageNumber = 1;               // 当前页
    //    private String productType;
    private QueryMyStockUpEntity upEntity;
    private List<MyStockDownEntity> entityList;                     //库存列表下行数据

    public OrderFromStockFragment2() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (TransferInventoryActivity) getActivity();
        rootView = inflater.inflate(R.layout.zone_choose_request, container, false);

        recyclerView = (XRecyclerView) rootView.findViewById(R.id.recyclerView);
        title = (TextView) rootView.findViewById(R.id.title);
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);
        rootView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        parseIntent();
        return rootView;
    }

    private void parseIntent() {
//        productType = activity.getIntent().getStringExtra(ActivityConstant.PRODUCTTYPE);
        String entityString = activity.getIntent().getStringExtra(QueryMyStockUpEntity.class.getName());
        upEntity = JSON.parseObject(entityString, QueryMyStockUpEntity.class);
        buySell = activity.getIntent().getStringExtra(ActivityConstant.SPTDICT_BUY_SELL);
        isJoin = activity.getIntent().getStringExtra(JoinSellOrBuyFragment.class.getName()) != null;
        if (ActivityConstant.BUYSELL_BUY.equals(buySell)) {
            title.setText("库存回补");
        } else {
            title.setText("可转卖库存");
        }
        entityList = new ArrayList<>();
        boolean isResell = SptConstant.BUYSELL_BUY.equals(buySell);
        stockOrderAdapter = new TransferInventoAdapter(entityList, isResell);
        stockOrderAdapter.setItemClickListener(this);
        recyclerView.setAdapter(stockOrderAdapter);
//        recyclerView.setLoadingListener(this);
        requestOfficeList();
    }

    private void initData() {
        SubAsyncTask.create().setOnDataListener(activity, false, new OnDataListener<ZoneRequestDownEntity>() {


            @Override
            public ZoneRequestDownEntity requestService() throws DBException, ApplicationException {
                String id = (String) Temp.i().get(ZoneRequestDownEntity.class);
                return Client.getService(IZoneRequestService.class).getZoneRequestDetailByRequestId(id);
            }

            @Override
            public void onDataSuccessfully(ZoneRequestDownEntity zoneRequestDownEntity) {
                if (zoneRequestDownEntity != null) {
                    requestOfficeList();
                }
            }
        });
    }

    /**
     * 库存转卖/回补列表
     */
    private void requestOfficeList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<MyStockDownEntity>>() {
            @Override
            public PagesDownResultEntity<MyStockDownEntity> requestService() throws DBException, ApplicationException {
                if (null == upEntity)  return null;
                upEntity.setNumberPerPage(numberPerPage);
                upEntity.setCurrentPageNumber(currentPageNumber);
                upEntity.setPageSize(numberPerPage);
                upEntity.setPageNo(currentPageNumber);
                boolean isSell = SptConstant.BUYSELL_SELL.equals(buySell);
                return isSell ?
                        Client.getService(IContractService.class).findMyStockList(upEntity) :
                        Client.getService(IContractService.class).findBuyBackStockList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<MyStockDownEntity> downResultEntity) {
                if (downResultEntity != null && downResultEntity.getDataList() != null) {
                    if (currentPageNumber == 1) {
                        entityList.clear();
                    }
                    entityList.addAll(downResultEntity.getDataList());
//                    recyclerView.setNoMore(downResultEntity.getDataList().size() < numberPerPage);
//                    recyclerView.setLoadingMoreEnabled(entityList.size() < downResultEntity.getTotalCount());
                    stockOrderAdapter.notifyDataSetChanged();

                }
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }


    @Override
    public void itemClick(@NonNull Object obj, int position) {
        if (isJoin) {
            activity.switchContractDetailFragment((MyStockDownEntity) obj, ActivityConstant.BUYSELL_SELL.equals(buySell));
        } else {
            activity.switchResellRechargeDealFragment((MyStockDownEntity) obj, ActivityConstant.BUYSELL_SELL.equals(buySell));
        }
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
