package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.MyContractDownEntity;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.entity.QueryMyContractUpEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.zone.dto.QueryPageZoneOfferUpEntity;
import com.autrade.spt.zone.dto.ZoneMyOfferDownEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestOfferService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.view.customize.BottomPopSelect;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;
import com.totrade.spt.mobile.ui.maintrade.ZoneMyOrderActivity;
import com.totrade.spt.mobile.ui.maintrade.adapter.RealTimeOrderAdapter;
import com.totrade.spt.mobile.ui.maintrade.adapter.StockOrderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订单
 *
 * @author huangxy
 * @date 2016/12/7
 */
public class MyOrderFragment extends SptMobileFragmentBase
        implements View.OnClickListener, XRecyclerView.LoadingListener, RecyclerAdapterBase.ItemClickListener {
    private ZoneMyOrderActivity activity;

    private CommonTitleZone title;
    private RadioGroup rgTab;
    private LinearLayout ll_description;

    private XRecyclerView rlv_order;
    private RealTimeOrderAdapter realTimeOrderAdapter;
    private StockOrderAdapter stockOrderAdapter;
    private List<ZoneMyOfferDownEntity> zoneMyOfferDownEntities;
    private List<MyContractDownEntity> myContractDownEntities;
    private TextView tv_ordernum;
    private TextView tv_select;
    private ImageView iv_arrow;

    private String productType;
    private static final int numberPerPage = 15; // 每页显示条数
    private int currentPageNumber = 1; // 当前页
    private String buySellStr;

    public MyOrderFragment() {
        setContainerId(R.id.frameLayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (ZoneMyOrderActivity) getActivity();
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zone_my_order, container, false);

        rlv_order = (XRecyclerView) rootView.findViewById(R.id.rlv_order);
        tv_ordernum = (TextView) rootView.findViewById(R.id.tv_ordernum);
        tv_select = (TextView) rootView.findViewById(R.id.tv_select);
        iv_arrow = (ImageView) rootView.findViewById(R.id.ivArrow);
        rlv_order.init2LinearLayout();
        title = (CommonTitleZone) rootView.findViewById(R.id.title);
        ll_description = (LinearLayout) rootView.findViewById(R.id.ll_description);
        rgTab = (RadioGroup) rootView.findViewById(R.id.rgTab);
        rootView.findViewById(R.id.ll_selection).setOnClickListener(this);

//        initData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        title.setTitle("我的订单");
        ((RadioButton) rgTab.getChildAt(0)).setText("实时挂单");
        ((RadioButton) rgTab.getChildAt(1)).setText("我的库存");

        productType = activity.getIntent().getStringExtra("productType");

        rlv_order.setLoadingListener(this);
        switchCheckId(rgTab.getCheckedRadioButtonId());
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switchCheckId(checkedId);
            }
        });
        rgTab.check(R.id.rbLeft);
    }

    private void switchCheckId(int checkedId) {
        if (checkedId == R.id.rbRight) {
            ll_description.setVisibility(View.GONE);
            loadStockOrder();
        } else if (checkedId == R.id.rbLeft) {
            ll_description.setVisibility(View.VISIBLE);
            loadRealTimeOrder();
        }
    }

    /**
     * 实时订单
     */
    private void loadRealTimeOrder()
    {
        if (stockOrderAdapter != null)
        {
            stockOrderAdapter.clear();
        }
        zoneMyOfferDownEntities = new ArrayList<>();
        currentPageNumber = 1;
        realTimeOrderAdapter = new RealTimeOrderAdapter(zoneMyOfferDownEntities);
        realTimeOrderAdapter.setItemClickListener(this);
        rlv_order.setAdapter(realTimeOrderAdapter);

        findZoneMyOfferList();
    }

    /**
     * 库存订单
     */
    private void loadStockOrder()
    {
        if (realTimeOrderAdapter != null)
        {
            realTimeOrderAdapter.clear();
        }
        myContractDownEntities = new ArrayList<>();
        currentPageNumber = 1;
        stockOrderAdapter = new StockOrderAdapter(myContractDownEntities);
        stockOrderAdapter.setItemClickListener(this);
        rlv_order.setAdapter(stockOrderAdapter);

        queryMyContractList();
    }

    public static final int REALTIME = 1;
    public static final int STOCK = 2;

    private void queryMyContractList() {
        SubAsyncTask.create().setOnDataListener("queryMyContractList", new OnDataListener<List<MyContractDownEntity>>()
        {
            @Override
            public List<MyContractDownEntity> requestService() throws DBException, ApplicationException {
                QueryMyContractUpEntity upEntity = new QueryMyContractUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setDealType(SptConstant.DEAL_TYPE_ZONE);  //N 询价 T 铁矿专区 Z 商品专区
                upEntity.setMode("A");
                upEntity.setBuySell(SptConstant.BUYSELL_BUY);
                upEntity.setProductType(productType);
                upEntity.setNumberPerPage(numberPerPage);
                upEntity.setCurrentPageNumber(currentPageNumber);
                String bondPayStatusTag = DealConstant.BONDPAYSTATUS_TAG_BOND_PAID + SptConstant.SEP //1 已付保证金
                        + DealConstant.BONDPAYSTATUS_TAG_SELLOUT ;                   //9 买家已挂出
                upEntity.setBondPayStatusTagCondition(bondPayStatusTag);
                upEntity.setBuyerZoneStatusCondition( DealConstant.ORDER_NORMALITY);
                return Client.getService(IContractService.class).queryMyContractList(upEntity).getDataList();
            }

            @Override
            public void onDataSuccessfully(List<MyContractDownEntity> list) {
                if (list != null)
                {
                    if (currentPageNumber == 1)
                    {
                        myContractDownEntities.clear();
                    }
                    myContractDownEntities.addAll(list);
                    stockOrderAdapter.notifyDataSetChanged();
                }
                rlv_order.loadMoreComplete();
                rlv_order.refreshComplete();
            }
        });
    }

    private void findZoneMyOfferList() {
        SubAsyncTask.create().setOnDataListener("findZoneMyOfferList", new OnDataListener<List<ZoneMyOfferDownEntity>>() {
            @Override
            public List<ZoneMyOfferDownEntity> requestService() throws DBException, ApplicationException {
                QueryPageZoneOfferUpEntity upEntity = new QueryPageZoneOfferUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setProductType(productType);
                upEntity.setPageSize(numberPerPage);
                upEntity.setPageNo(currentPageNumber);
                upEntity.setBuySell(buySellStr);
                return Client.getService(IZoneRequestOfferService.class).findZoneMyOfferList(upEntity).getDataList();
            }

            @Override
            public void onDataSuccessfully(List<ZoneMyOfferDownEntity> list) {
                if (list != null)
                {
                    if (currentPageNumber == 1)
                    {
                        zoneMyOfferDownEntities.clear();
                    }
                    zoneMyOfferDownEntities.addAll(list);
                    realTimeOrderAdapter.notifyDataSetChanged();
                    String orderNum = "(" + zoneMyOfferDownEntities.size() + ")条挂单";
                    tv_ordernum.setText(orderNum);
                }
                rlv_order.refreshComplete();
                rlv_order.loadMoreComplete();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_selection) {
            arrowRoate(true);
            showPopwindow();
        }
    }

    private BottomPopSelect popItemSelect;

    /**
     * 单条弹框选择
     */
    private void showPopwindow() {
        if (popItemSelect == null) {
            popItemSelect = new BottomPopSelect(activity);
        }
        popItemSelect.updateList(selectList(), popItemSelect.getViewTag());
        popItemSelect.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Object object = popItemSelect.getViewTag();
                if (object != null) {
                    NameValueItem valueItem = (NameValueItem) object;
                    tv_select.setText(valueItem.getName());
                    tv_select.setTag(valueItem);
                    buySellStr = valueItem.getValue();

                    realTimeOrderAdapter.clear();
                    findZoneMyOfferList();
                }
                arrowRoate(false);
            }
        });
        popItemSelect.showAtBottom();

    }

    //    弹框数据
    private String[][] selectStrs = new String[][]{
            {"只看销售", SptConstant.BUYSELL_SELL}, {"查看全部", ""}, {"只看采购", SptConstant.BUYSELL_BUY}
    };

    private List<NameValueItem> selectList() {
        List<NameValueItem> selectList = new ArrayList<>();
        NameValueItem item;
        for (String[] s : selectStrs)
        {
            item = new NameValueItem();
            item.setName(s[0]);
            item.setValue(s[1]);
            selectList.add(item);
        }
        return selectList;
    }

    //旋转黑色箭头
    private void arrowRoate(boolean downward) {
        RotateAnimation animation;
        if (downward) {
            animation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        } else {
            animation = new RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        animation.setDuration(50);
        animation.setFillAfter(true);
        iv_arrow.startAnimation(animation);
    }

    @Override
    public void onRefresh() {
        currentPageNumber = 1;
        switchLoad();
    }

    @Override
    public void onLoadMore() {
        currentPageNumber++;
        switchLoad();
    }

    private void switchLoad()
    {
        if (ll_description.isShown())
        {
            findZoneMyOfferList();
        }
        else
        {
            queryMyContractList();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (popItemSelect != null && popItemSelect.isShowing())
        {
            popItemSelect.dismiss();
            popItemSelect = null;
        }
    }

    @Override
    public void itemClick(@NonNull Object obj, int position)
    {
        if (obj instanceof ZoneMyOfferDownEntity)
        {
            activity.switchMyOrderDetailFragment((ZoneMyOfferDownEntity) obj);
        }
        else if (obj instanceof MyContractDownEntity)
        {
            activity.switchContractDetailFragment((MyStockDownEntity) obj, true);
        }
    }
}
