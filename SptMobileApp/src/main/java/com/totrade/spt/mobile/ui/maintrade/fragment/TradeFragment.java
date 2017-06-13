package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.QueryPageZoneRequestUpEntity;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.DrawerListenerWrapper;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity;
import com.totrade.spt.mobile.ui.maintrade.PlaceOrderActivity;
import com.totrade.spt.mobile.ui.maintrade.PowerHelper;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;
import com.totrade.spt.mobile.ui.maintrade.adapter.TradeOrderAdapter;
import com.totrade.spt.mobile.ui.notifycenter.NotifyActivity;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SysInfoUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 贸易专区
 *
 * @author huangxy
 * @date 2017/4/10
 */
public class TradeFragment extends BaseSptFragment<HomeActivity> implements View.OnClickListener, XRecyclerView.LoadingListener, RecyclerAdapterBase.ItemClickListener {

    private DrawerLayout drawer;
    private XRecyclerView recyclerView;
    private TextView iv_message;
    private TextView tv_edit_focus;
    private RadioGroup radioGroup;
    private int checkedButtonId;

    private QueryPageZoneRequestUpEntity tradeEntity;
    private QueryPageZoneRequestUpEntity focusEntity;
    private static String defaultType = null;
    private List<ZoneRequestDownEntity> entitys;
    private static final int numberPerPage = 20; // 每页显示条数
    private static int currentPageNumber = 1; // 当前页
    private static final int TIME_INTERVAL = 10*60*1000;//定时刷新时间间隔

    private TradeScreenFragment tradeScreenFragment;
    private TradeOrderAdapter tradeOrderAdapter;

    public TradeFragment() {
        setContainerId(R.id.fl_body);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_maintrade;
    }

    public void setScreenEntity(QueryPageZoneRequestUpEntity upEntity) {
        if (checkedButtonId == R.id.rb_trade) {
            this.tradeEntity = upEntity;
        } else {
            this.focusEntity = upEntity;
        }
        entitys.clear();

        tradeOrderAdapter.notifyDataSetChanged();
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        onRefresh();
    }

    public QueryPageZoneRequestUpEntity getTradeEntity() {
        if (checkedButtonId == R.id.rb_trade) {
            return tradeEntity;
        } else {
            String focusListStr = SharePreferenceUtility.spGetOut(mActivity, "focusList", "");
            String productType = focusEntity.getProductType();
            if (productType == null) {
                return focusEntity;
            }
            if (!"NONE".equals(productType) && !focusListStr.contains(productType)){
                focusEntity.setProductType("NONE");
            }
            return focusEntity;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshNoti();
    }

    @Override
    protected void initView() {
//        setTitleToIncludeStatusBar();
        radioGroup = findView(R.id.radioGroup);
        drawer = findView(R.id.drawer_layout);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.init2LinearLayout();
        iv_message = findView(R.id.iv_message);
        tv_edit_focus = findView(R.id.tv_edit_focus);
        setOnClick(R.id.iv_screen, R.id.iv_message, R.id.tv_place_order, R.id.tv_resell, R.id.tv_update_order, R.id.tv_edit_focus);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);     //关闭手势滑动

        entitys = new ArrayList<>();
        tradeOrderAdapter = new TradeOrderAdapter(entitys);
        tradeOrderAdapter.setItemClickListener(this);
        recyclerView.setAdapter(tradeOrderAdapter);

        recyclerView.setLoadingListener(this);
        recyclerView.refresh();
        tradeScreenFragment = new TradeScreenFragment();
        drawer.addDrawerListener(new DrawerListenerWrapper() {
            @Override
            public void onDrawerClosed(View drawerView) {
                getChildFragmentManager().beginTransaction().remove(tradeScreenFragment).commit();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    LogUtils.i(TradeFragment.class.getSimpleName(), "" + drawer.isDrawerOpen(GravityCompat.START));
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroupOnCheckedChangeListener());
        findView(R.id.rb_trade).performClick();
    }

    //RadioGroup选择监听
    class RadioGroupOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            checkedButtonId = checkedId;
            if (checkedId == R.id.rb_trade) {
                tv_edit_focus.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_focus) {
                if (LoginUserContext.isAnonymous()) {
                    IntentUtils.startActivity(mActivity, LoginActivity.class);
                    r();
                    return;
                }
                tv_edit_focus.setVisibility(View.VISIBLE);
            }
            onRefresh();
        }
    }

    //手动check页面
    public void r() {
        SptApplication.appHandler.post(new Runnable() {
            @Override
            public void run() {
                findView(R.id.rb_trade).performClick();
            }
        });
    }

    @Override
    public void setUnReadMsg(boolean unReadMsg) {
        if (mRootView != null)
            iv_message.setSelected(unReadMsg);
    }

    private void setOnClick(Integer... ids) {
        for (Integer id : ids) {
            findView(id).setOnClickListener(this);
        }
    }

    public void setTitleToIncludeStatusBar() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) findView(R.id.fl_title).getLayoutParams();
        lp.topMargin = SysInfoUtil.getStatusBarHeight();
        findView(R.id.fl_title).setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_screen:
                tradeScreenFragment.setFocus(checkedButtonId == R.id.rb_focus);
                if (!tradeScreenFragment.isAdded()) addFragments(tradeScreenFragment);
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_message:
                if (!isLogin()) return;
                startActivity(NotifyActivity.class);
                break;
            case R.id.tv_place_order:
                if (!PowerHelper.i().hasPower(mActivity)) return;
                startActivity(new Intent(mActivity, PlaceOrderActivity.class));
                break;
            case R.id.tv_resell:       //库存转卖/回补
                if (!PowerHelper.i().hasPower(mActivity)) return;
                Intent intent2 = new Intent(mActivity, TransferInventoryActivity.class);
                intent2.putExtra(TradeFragment.class.getName(), TradeFragment.class.getName());
                intent2.putExtra(Zone.KEY_ACTION, Zone.STOCK_RESELL_RECHARGE);
                intent2.putExtra(Zone.PRODUCT_TYPE, tradeEntity.getProductType());
                startActivity(intent2);
                break;
            case R.id.tv_update_order:       //修改挂单
                if (!PowerHelper.i().hasPower(mActivity)) return;
                Intent intent = new Intent(mActivity, TransferInventoryActivity.class);
                intent.putExtra(TradeFragment.class.getName(), TradeFragment.class.getName());
                intent.putExtra(Zone.KEY_ACTION, Zone.CHANGE_ORDER);
                intent.putExtra(Zone.PRODUCT_TYPE, tradeEntity.getProductType());
                startActivity(intent);
                break;
            case R.id.tv_edit_focus:
                mActivity.switchAddFocus();
                break;
            default:
                break;
        }
    }

    private boolean isLogin() {
        if (LoginUserContext.isAnonymous()) {
            IntentUtils.startActivity(mActivity, LoginActivity.class);
            return false;
        }
        return true;
    }

    /**
     * 贸易大厅列表
     */
    private void findZoneRequestList() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<List<ZoneRequestDownEntity>>() {

            @Override
            public List<ZoneRequestDownEntity> requestService() throws DBException, ApplicationException {
                if (tradeEntity == null) tradeEntity = new QueryPageZoneRequestUpEntity();
                if (TextUtils.isEmpty(tradeEntity.getProductType())) {
                    setDefProductType();            //初始化默认值
                    tradeEntity.setProductType(defaultType);
                } else if ("NONE".equals(tradeEntity.getProductType())) {
                    tradeEntity.setProductType("HG");
                }
                tradeEntity.setPageNo(currentPageNumber);
                tradeEntity.setPageSize(numberPerPage);
                tradeEntity.setRequestStatusCondition(ZoneConstant.OFFER_STATUS_PENDING);
                return Client.getService(IZoneRequestService.class).findZoneRequestList(tradeEntity).getDataList();
            }

            @Override
            public void onDataSuccessfully(List<ZoneRequestDownEntity> resultList) {
                if (resultList != null) {
                    if (currentPageNumber == 1) {
                        entitys.clear();
                    }
                    entitys.addAll(resultList);
                    tradeOrderAdapter.notifyDataSetChanged();
                }
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }

    /**
     * 获取我关注列表
     */
    private void findMyFocusZoneRequestList() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<PagesDownResultEntity<ZoneRequestDownEntity>>() {
            @Override
            public PagesDownResultEntity<ZoneRequestDownEntity> requestService() throws DBException, ApplicationException {
                if (focusEntity == null) focusEntity = new QueryPageZoneRequestUpEntity();
                QueryPageZoneRequestUpEntity upEntity = new QueryPageZoneRequestUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                upEntity.setPageNo(currentPageNumber);
                upEntity.setPageSize(numberPerPage);
                upEntity.setProductPlace(focusEntity.getProductPlace());
                upEntity.setProductQuality(focusEntity.getProductQuality());
                upEntity.setDeliveryPlace(focusEntity.getDeliveryPlace());
                upEntity.setDeliveryTime(focusEntity.getDeliveryTime());
                upEntity.setUpOrDown(focusEntity.getUpOrDown());
                upEntity.setProductType("NONE".equals(focusEntity.getProductType()) ? null : focusEntity.getProductType());
                return Client.getService(IZoneRequestService.class).findMyFocusZoneRequestList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<ZoneRequestDownEntity> obj) {
                if (obj != null && obj.getDataList() != null) {
                    if (currentPageNumber == 1) {
                        entitys.clear();
                    }
                    entitys.addAll(obj.getDataList());
                    tradeOrderAdapter.notifyDataSetChanged();
                }
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }

    //设置默认值
    private void setDefProductType() {
        if (TextUtils.isEmpty(defaultType)) {
            defaultType = "HG";
//            List<ProductTypeDto> lst = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_ZONE, "HG", 2);
//            if (lst != null && !lst.isEmpty()) {
//                defaultType = lst.get(0).getProductType();  //2级类第一个
//            } else {
//                defaultType = "HG_CL_YE";
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        iv_message.setOnClickListener(this);
        setUnReadMsg(mActivity.getUnReadNum());
        if (LoginUserContext.isAnonymous()) {
            iv_message.setSelected(false);
        }
        onRefresh();

        //页面显示出来10分钟以后执行定时刷新任务
        mHandler.postDelayed(mRefreshRunable,TIME_INTERVAL);
    }

    @Override
    public void onRefresh() {
        currentPageNumber = 1;
        loadList();
    }

    @Override
    public void onLoadMore() {
        currentPageNumber += 1;
        loadList();
    }

    void loadList() {
        if (checkedButtonId == R.id.rb_trade) {
            findZoneRequestList();
        } else {
            findMyFocusZoneRequestList();
        }
    }

    @Override
    public void itemClick(@NonNull Object obj, int position) {
        Intent intent = new Intent(getActivity(), OrderDeatilActivity.class);
        intent.putExtra(ZoneRequestDownEntity.class.getName(), JsonUtility.toJSONString(obj));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 刷新界面
     */
    private void refreshNoti() {
        receiver = new NewQuestReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.INTENT_ACTION_REFRESH_ON_PRICE);
        filter.addAction(AppConstant.INTENT_ACTION_FOUCS_REFRESH);
        filter.addAction(AppConstant.INTENT_ACTION_TRADE_REFRESH);
        mActivity.registerReceiver(receiver, filter);
    }

    private NewQuestReceiver receiver;

    /**
     * 有新消息时刷新
     */
    private class NewQuestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.INTENT_ACTION_REFRESH_ON_PRICE.equals(intent.getAction())) {
                String receiveMsg = intent.getStringExtra("receiveMsg");
                for (int i = 0; i < entitys.size(); i++) {
                    if (entitys.get(i).getRequestId().equals(NotifyUtility.getValue(receiveMsg, "requestId"))) {
                        tradeOrderAdapter.setChangedPrice(i, receiveMsg);
                        break;
                    }
                }

                //TODO  刷一下就好了
//                onRefresh();
            } else if (AppConstant.INTENT_ACTION_FOUCS_REFRESH.equals(intent.getAction())
                    || AppConstant.INTENT_ACTION_TRADE_REFRESH.equals(intent.getAction())) {
                onRefresh();
            }
        }
    }

    /**
     * 10分钟刷一次列表
     */
    private Handler mHandler = new Handler();
    Runnable mRefreshRunable = new Runnable() {
        @Override
        public void run() {
            onRefresh();
            mHandler.postDelayed(this,TIME_INTERVAL);
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(receiver);
        mHandler.removeCallbacks(mRefreshRunable);
    }
}
