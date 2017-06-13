package com.totrade.spt.mobile.ui.ordermanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.entity.ContractUpEntity;
import com.autrade.spt.deal.entity.MyContractDownEntity;
import com.autrade.spt.deal.entity.PayAllRemainPreviewDownEntity;
import com.autrade.spt.deal.entity.QueryMyContractUpEntity;
import com.autrade.spt.deal.service.inf.IContractBondService;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.ListDropDownAdapter;
import com.totrade.spt.mobile.adapter.OrderListAdapter;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.DropDownMenu;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 订单管理列表页
 * Created by Timothy on 2017/4/11.
 */

public class OrderListFragment extends BaseSptFragment<OrderManagerActivity> {

    private DropDownMenu dropDownMenu;
    private List<View> popupViews = new ArrayList<>();
    private String headers[] = {"合同类型", "签订日期", "合同状态"};

    private XRecyclerView contentView;
    private OrderListAdapter adapter;
    private int curPageNumber = 1;
    private static final int PAGE_NUMBER = 20;
    private List<MyContractDownEntity> dataList = new ArrayList<>();

    private String curOrderType;
    private String curOrderDate;
    private String curOrderStatus;

    private PayDetailFragment payFragment;

    enum FitTradeOrder {
        TYPE, DATE, STATUS
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_order_list;
    }

    @Override
    protected void initView() {
        curOrderStatus = mActivity.getIntent().getStringExtra("STATUS");
        if (null == curOrderStatus) {
            curOrderStatus = "";
        }
        dropDownMenu = findView(R.id.dropDownMenu);

        final ListView typeListView = new ListView(mActivity);
        typeListView.setTag(FitTradeOrder.TYPE);
        final ListDropDownAdapter typeAdapter = new ListDropDownAdapter(mActivity, Dictionary.keyList(Dictionary.CONSTRACT_TYPE));
        typeListView.setDividerHeight(0);
        typeListView.setAdapter(typeAdapter);

        final ListView dateListView = new ListView(mActivity);
        dateListView.setTag(FitTradeOrder.DATE);
        dateListView.setDividerHeight(0);
        final ListDropDownAdapter dateAdapter = new ListDropDownAdapter(mActivity, Dictionary.keyList(Dictionary.CONSTRACT_DATE));
        dateListView.setAdapter(dateAdapter);

        final ListView statusListView = new ListView(mActivity);
        statusListView.setTag(FitTradeOrder.STATUS);
        statusListView.setDividerHeight(0);
        final ListDropDownAdapter statusAdapter = new ListDropDownAdapter(mActivity, Dictionary.keyList(Dictionary.CONSTRACT_STATUS));
        statusListView.setAdapter(statusAdapter);

        popupViews.add(typeListView);
        popupViews.add(dateListView);
        popupViews.add(statusListView);

        typeListView.setOnItemClickListener(onItemClickListener);
        dateListView.setOnItemClickListener(onItemClickListener);
        statusListView.setOnItemClickListener(onItemClickListener);

        contentView = new XRecyclerView(mActivity);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        contentView.init2LinearLayout();
        contentView.setLoadingListener(loadingListener);
        adapter = new OrderListAdapter(dataList);
        contentView.setAdapter(adapter);
        adapter.setOnActionClickListener(onActionClickListener);
        adapter.setItemClickListener(recyItemClickListener);
        dropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);

    }

    @Override
    public void onResume() {
        super.onResume();
        register();//注册挂单成功时的广播，以主动刷新列表
        queryMyContractList();
    }

    private NotifyReceiver receiver;

    /**
     * 有新消息时刷新
     */
    static class NotifyReceiver extends BroadcastReceiver {
        private OrderListFragment orderListFragment;

        public NotifyReceiver(OrderListFragment orderListFragment) {
            this.orderListFragment = orderListFragment;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.INTENT_ACTION_NOTIFY_CENTER.equals(intent.getAction())) {
                orderListFragment.loadingListener.onRefresh();
            }
        }
    }

    /**
     * 注册刷新广播接收者
     */
    private void register() {
        receiver = new NotifyReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.INTENT_ACTION_NOTI);
        mActivity.registerReceiver(receiver, filter);
    }

    private void queryMyContractList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<MyContractDownEntity>>() {
            @Override
            public PagesDownResultEntity<MyContractDownEntity> requestService() throws DBException, ApplicationException {
                QueryMyContractUpEntity upEntity = new QueryMyContractUpEntity();
                upEntity.setCurrentPageNumber(curPageNumber);
                upEntity.setNumberPerPage(PAGE_NUMBER);
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setBuySell(curOrderType);
                upEntity.setConTimeType(curOrderDate);
                upEntity.setOrderStatus(curOrderStatus);
                return Client.getService(IContractService.class).queryMyContractList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<MyContractDownEntity> obj) {
                contentView.refreshComplete();
                contentView.loadMoreComplete();
                if (null == obj) return;
                if (null != obj.getDataList()) {
                    if (curPageNumber == 1 && dataList.size() > 0) {
                        dataList.clear();
                    }
                    dataList.addAll(obj.getDataList());
                    if (obj.getDataList().size() < PAGE_NUMBER) {
                        contentView.setNoMore(curPageNumber > 1);
                    } else {
                        contentView.setLoadingMoreEnabled(true);
                        contentView.setNoMore(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private OrderListAdapter.OnActionClickListener onActionClickListener = new OrderListAdapter.OnActionClickListener() {
        @Override
        public void onActionClick(MyContractDownEntity entity) {
            switch (entity.getOrderStatus()) {
                case DealConstant.ORDER_STATUS_DECLAREDELIVERY:
                    //宣布交收操作
                    OrderHelper.getInstance().initContractEntity(entity);
                    Intent intent = new Intent(mActivity, OrderDetailActivity.class);
                    intent.putExtra("action", DealConstant.ORDER_STATUS_DECLAREDELIVERY);
                    startActivity(intent);
                    break;
                case DealConstant.ORDER_STATUS_WAIT_PAYALL:
                    //支付余款操作
                    payMoneyPreview(entity.getContractId());
                    break;
                case DealConstant.ORDER_STATUS_WAIT_DELIVERY:
                    //确认发货
                    OrderHelper.getInstance().initContractEntity(entity);
                    Intent intent2 = new Intent(mActivity, OrderDetailActivity.class);
                    intent2.putExtra("action", DealConstant.ORDER_STATUS_WAIT_DELIVERY);
                    startActivity(intent2);
                    break;
                case DealConstant.ORDER_STATUS_TAK_DELIVERY:
                    OrderHelper.getInstance().initContractEntity(entity);
                    //确认收货
                    new CustomDialog.Builder(mActivity, AppConstant.ACTION_RECEIVE + "?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            confirm();
                        }
                    }).create().show();
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FitTradeOrder tag = (FitTradeOrder) parent.getTag();
            ListDropDownAdapter adapter = (ListDropDownAdapter) parent.getAdapter();
            switch (tag) {
                case TYPE:
                    dropDownMenu.setTabText(position == 0 ? headers[0] : Dictionary.CONSTRACT_TYPE.get(position).getKey());
                    curOrderType = Dictionary.CONSTRACT_TYPE.get(position).getCode();
                    break;
                case DATE:
                    dropDownMenu.setTabText(position == 0 ? headers[1] : Dictionary.CONSTRACT_DATE.get(position).getKey());
                    curOrderDate = Dictionary.CONSTRACT_DATE.get(position).getCode();
                    break;
                case STATUS:
                    dropDownMenu.setTabText(position == 0 ? headers[2] : Dictionary.CONSTRACT_STATUS.get(position).getKey());
                    curOrderStatus = Dictionary.CONSTRACT_STATUS.get(position).getCode();
                    break;
            }
            adapter.setCheckItem(position);
            dropDownMenu.closeMenu();
            curPageNumber = 1;
            queryMyContractList();
        }
    };

    private RecyclerAdapterBase.ItemClickListener recyItemClickListener = new RecyclerAdapterBase.ItemClickListener() {
        @Override
        public void itemClick(@NonNull final Object obj, int position) {
            Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
            intent.putExtra(ContractDownEntity.class.getName(), ((MyContractDownEntity) obj).getContractId());
            intent.putExtra(obj.getClass().getName(), JsonUtility.toJSONString(obj));
            startActivityForResult(intent, 0x0001);
        }
    };

    private XRecyclerView.LoadingListener loadingListener = new XRecyclerView.LoadingListener() {
        @Override
        public void onRefresh() {
            curPageNumber = 1;
            queryMyContractList();
        }

        @Override
        public void onLoadMore() {
            curPageNumber++;
            queryMyContractList();
        }
    };

    /**
     * 支付回调
     */
    private PayDetailFragment.PaySucessCallBack paySucessCallBack = new PayDetailFragment.PaySucessCallBack() {
        @Override
        public void onPaySucess(boolean isSucess) {
            if (isSucess) ToastHelper.showMessage("支付成功");
            payFragment.dismiss();
            loadingListener.onRefresh();
        }
    };

    /**
     * 确认收货
     */
    private void confirm() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IContractBondService.class).confirm(LoginUserContext.getLoginUserId(), OrderHelper.getInstance().contractDownEntity.getContractId());
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("操作成功");
                    loadingListener.onRefresh();
                }
            }
        });
    }

    /**
     * 支付余款
     *
     * @param contractId 合同Id
     */
    public void payMoneyPreview(final String contractId) {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<PayAllRemainPreviewDownEntity>() {
            @Override
            public PayAllRemainPreviewDownEntity requestService() throws DBException, ApplicationException {
                ContractUpEntity upEntity = new ContractUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setContractId(contractId);
                return Client.getService(IContractBondService.class).payAllRemainPreview(upEntity);
            }

            @Override
            public void onDataSuccessfully(final PayAllRemainPreviewDownEntity entity) {
                if (entity == null) {
                    ToastHelper.showMessage("请检查网络连接");
                    return;
                }
                payFragment = PayDetailFragment.newInstance(entity);
                payFragment.show(getChildFragmentManager(), "");
                payFragment.setPaySucessCallBack(paySucessCallBack);
            }
        });
    }
}
