package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.StockAdapter;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;

import java.util.ArrayList;
import java.util.List;

/**
 * 从库存选择
 * Created by Administrator on 2016/12/7.
 */
public class OrderFromStockFragment extends BaseFragment implements View.OnClickListener, XRecyclerView.LoadingListener, RecyclerAdapterBase.ItemClickListener {

    private View rootView;
    private OrderDeatilActivity activity;
    private CommonTitleZone titleView;
    private XRecyclerView xrv_stock_list;
    private StockAdapter mAdapter;
    private int curPageNum;
    private List<MyStockDownEntity> mContractDownEntities;
    private QueryMyStockUpEntity mUpEntity;
    private Object curSecObj;

    private static final int PAGES = 50;//每次加载的页数
    private TblZoneRequestOfferEntity mOfferEntity;
    private ZoneRequestDownEntity mRequestDownEntity;

    public OrderFromStockFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (OrderDeatilActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_zone_from_stock, container, false);
        initView();
        initSet();
        initData();
        return rootView;
    }

    private void initView() {
        titleView = (CommonTitleZone) rootView.findViewById(R.id.title);
        xrv_stock_list = (XRecyclerView) rootView.findViewById(R.id.recyclerView);
    }

    private void initSet() {
        titleView.setTitle("可转卖库存");
        titleView.getImgBack().setOnClickListener(this);

        xrv_stock_list.init2LinearLayout();
        xrv_stock_list.setLoadingListener(this);

    }

    private void initData() {
        mContractDownEntities = new ArrayList<>();

        curPageNum = 1;
        mAdapter = new StockAdapter(mContractDownEntities);
        mAdapter.setSelectOjb((MyStockDownEntity) activity.getCurDataFromStock());
        mAdapter.setItemClickListener(this);
        xrv_stock_list.setAdapter(mAdapter);

        queryMyContractList();
    }

    private void queryMyContractList() {
        SubAsyncTask.create().setOnDataListener("queryMyContractList", new OnDataListener<List<MyStockDownEntity>>() {
            @Override
            public List<MyStockDownEntity> requestService() throws DBException, ApplicationException {
                mUpEntity = new QueryMyStockUpEntity();
//                mUpEntity.setBuySell(mOfferEntity.getBuySell());
//                mUpEntity.setBond(mRequestDownEntity.getBond());
//                String bondPayStatusTag = DealConstant.BONDPAYSTATUS_TAG_BOND_PAID + SptConstant.SEP        //1 已付保证金
//                        + DealConstant.BONDPAYSTATUS_TAG_SELLOUT ;                                          //9 买家已挂出
//                mUpEntity.setBondPayStatusTagCondition(bondPayStatusTag);
//                mUpEntity.setBuyerZoneStatusCondition(DealConstant.ORDER_NORMALITY);
//                mUpEntity.setDeliveryPlace(mRequestDownEntity.getDeliveryPlace());
//                mUpEntity.setDeliveryTime(mRequestDownEntity.getDeliveryTime());
//                mUpEntity.setDealType(SptConstant.DEAL_TYPE_ZONE);  //N 询价 T 铁矿专区 Z 商品专区
//                mUpEntity.setMode("A");
//                mUpEntity.setDealNumber(mOfferEntity.getRemainNumber());
//                mUpEntity.setProductPlace(mRequestDownEntity.getProductPlace());
//                mUpEntity.setTradeMode(mRequestDownEntity.getTradeMode());
//                mUpEntity.setProductQuality(mRequestDownEntity.getProductQuality());
//                mUpEntity.setOriSellerCompanyTags(mRequestDownEntity.getSupplierCompanyTags());
                mUpEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                mUpEntity.setProductType(mRequestDownEntity.getProductType());
                mUpEntity.setCurrentPageNumber(curPageNum);
                mUpEntity.setNumberPerPage(PAGES);
                mUpEntity.setContractZoneStatus(ActivityConstant.Zone.CONTRACT_ZONE_STATUS_NOT_A_DELIVERY);

                return Client.getService(IContractService.class).findMyStockList(mUpEntity).getDataList();
            }

            @Override
            public void onDataSuccessfully(List<MyStockDownEntity> list) {
                if (list != null) {
                    if (curPageNum == 1) {
                        mContractDownEntities.clear();
                    }
                    mContractDownEntities.addAll(list);
                    mAdapter.notifyDataSetChanged();
//                    xrv_stock_list.setLoadingMoreEnabled(!(list.size() < PAGES));
//                    xrv_stock_list.setNoMore(list.size() < PAGES);
                }
                xrv_stock_list.refreshComplete();
                xrv_stock_list.loadMoreComplete();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_screen:
                //出栈前先取出选中项
                if (null != curSecObj)
                    activity.setCurDataFromStock(curSecObj);
                activity.popBack();
                break;
        }
    }

    @Override
    public void onRefresh() {
        curPageNum = 1;
        queryMyContractList();
    }

    @Override
    public void onLoadMore() {
        curPageNum++;
        queryMyContractList();
    }

    @Override
    public void itemClick(@NonNull Object obj, int position) {
        curSecObj = obj;
        mAdapter.setSelectPosition(position);

        activity.setCurDataFromStock(curSecObj);
        activity.popBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != curSecObj)
            {
                activity.setCurDataFromStock(curSecObj);
            }
            activity.popBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setData(QueryMyStockUpEntity upEntity, TblZoneRequestOfferEntity offerEntity, ZoneRequestDownEntity requestDownEntity) {
        this.mUpEntity = upEntity;
        this.mOfferEntity = offerEntity;
        this.mRequestDownEntity = requestDownEntity;
    }

}
