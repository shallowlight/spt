package com.totrade.spt.mobile.ui.mainmatch;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.dto.BsAdditionalRecordUpEntity;
import com.autrade.spt.nego.entity.GetBuySellIntensionDownEntity;
import com.autrade.spt.nego.service.inf.IBsAdditionalRecordService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.mainmatch.adapter.MatchListAdapter;
import com.totrade.spt.mobile.ui.mainmatch.adapter.PopupWindowHelper;
import com.totrade.spt.mobile.ui.maintrade.PowerHelper;
import com.totrade.spt.mobile.ui.notifycenter.NotifyActivity;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SysInfoUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 撮合集市页面
 *
 * @author huangxy
 * @date 2017/4/6
 */
public class MatchFragment extends BaseSptFragment<HomeActivity> implements XRecyclerView.LoadingListener, View.OnClickListener, MatchSelection.OnSelcetionListener {

    private XRecyclerView recyclerView;
    private List<GetBuySellIntensionDownEntity> downEntityList;
    private MatchListAdapter adapter;
    private PopupWindow popupWindow;
    private MatchSelection matchSelection;
    private TextView iv_message;
    private TextView tv_account_name;
    private CircleImageView civ_img_left;

    private BsAdditionalRecordUpEntity upEntity;

    public MatchFragment() {
        setContainerId(R.id.fl_body);
        upEntity = new BsAdditionalRecordUpEntity();
//        upEntity.setProductType("HG_CL_EG");
        upEntity.setProductType("HG");
        PopupWindowHelper.i().setUpEntity(upEntity);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_mainmatch;
    }

    @Override
    protected void initView() {
        setTitleToIncludeStatusBar();
        iv_message = findView(R.id.iv_message);
        tv_account_name = findView(R.id.tv_account_name);
        civ_img_left = findView(R.id.civ_img_left);

        recyclerView = findView(R.id.recyclerView);
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);
        downEntityList = new ArrayList<>();
        adapter = new MatchListAdapter(downEntityList);
        recyclerView.setAdapter(adapter);

        findView(R.id.iv_message).setOnClickListener(this);
        findView(R.id.ll_selection).setOnClickListener(this);
        findView(R.id.tv_account_name).setOnClickListener(this);
        matchSelection = new MatchSelection();
        matchSelection.setView((LinearLayout) findView(R.id.ll_selection));
        matchSelection.setOnSelcetionListener(this);

        onRefresh();
    }

    public void setUnReadMsg(boolean unReadMsg) {
        if (mRootView != null)
            iv_message.setSelected(unReadMsg);
    }

    private int currentPage = 1;
    private static final int pageSize = 10;

    private void getBsAdditionalRecordList() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener<PagesDownResultEntity>() {
            @Override
            public PagesDownResultEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IBsAdditionalRecordService.class).getBsAdditionalRecordList(getUpEntity());
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity obj) {
                if (obj != null && obj.getDataList() != null) {
                    if (currentPage == 1) {
                        downEntityList.clear();
                        downEntityList.addAll(obj.getDataList());
                    } else {
                        downEntityList.addAll(obj.getDataList());
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }

    @NonNull
    private BsAdditionalRecordUpEntity getUpEntity() {
        upEntity.setUserId(LoginUserContext.getLoginUserId());
        upEntity.setCurrentPageNumber(currentPage);
        upEntity.setNumberPerPage(pageSize);
        upEntity.setBuySell(buySell);
        upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
        upEntity.setProductType(PopupWindowHelper.i().filterEntity.getProductType());

        return upEntity;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUnReadMsg(mActivity.getUnReadNum());
        if (LoginUserContext.isAnonymous()) {
            iv_message.setSelected(false);
        }
        buySell = null;
        getPortrait();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getBsAdditionalRecordList();
    }

    @Override
    public void onLoadMore() {
        currentPage += 1;
        getBsAdditionalRecordList();
    }

    public void setTitleToIncludeStatusBar() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) findView(R.id.fl_title_inner).getLayoutParams();
        lp.topMargin = SysInfoUtil.getStatusBarHeight();
        findView(R.id.fl_title_inner).setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_message:
                startActivity(NotifyActivity.class);
                break;
            case R.id.ll_selection:
                break;
            case R.id.tv_account_name:
                PowerHelper.i().hasPower(mActivity);
                break;

        }
    }

    /**
     * 获取登录名
     * 获取用户头像
     */
    private void getPortrait() {
        List<String> accounts = new ArrayList<>();
        if (LoginUserContext.isAnonymous()) {
            tv_account_name.setText("请登录");
            civ_img_left.setImageResource(R.drawable.home_portrait);
        } else {
            //设置用户昵称
            String account = LoginUserContext.getLoginUserDto().getImUserAccid();
            tv_account_name.setText(LoginUserContext.getLoginUserDto().getUserName());
            accounts.add(account);
            InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts);
            future.setCallback(mFutureRequestCallback);
        }
    }

    private FutureRequestCallback<List<NimUserInfo>> mFutureRequestCallback = new FutureRequestCallback<List<NimUserInfo>>() {
        @Override
        public void onSuccess(List<NimUserInfo> param) {
            if (param != null && param.size() > 0) {
                NimUserInfo nimUserInfo = param.get(0);
                //设置用户头像
                if (ObjUtils.isEmpty(nimUserInfo.getAvatar()))
                    civ_img_left.setImageResource(R.drawable.home_portrait);
                else Picasso.with(getActivity())
                        .load(nimUserInfo.getAvatar())
                        .placeholder(R.drawable.home_portrait)
                        .error(R.drawable.home_portrait)
                        .into(civ_img_left);
            }
        }
    };

    private String buySell;

    @Override
    public void selction(int code) {
        switch (code) {
            case 0:

                break;
            case 1:
                showPop(1);
                break;
            case 2:
                showPop(2);
                break;
            case 3:
                buySell = SptConstant.BUYSELL_BUY;
                onRefresh();
                break;
            case 4:
                buySell = SptConstant.BUYSELL_SELL;
                onRefresh();
                break;
            default:
                break;
        }

    }

    private void showPop(int type) {
        View view = type == 1 ? PopupWindowHelper.i().initSelectProductSecondType() : PopupWindowHelper.i().initDeliveryTime();
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popupWindow.showAsDropDown(findView(R.id.ll_selection));

        view.findViewById(R.id.view_outter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDismiss(false);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popDismiss(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (matchSelectionFragment.isAdded()) {
//                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                transaction.remove(matchSelectionFragment);
//            }
            if (popDismiss(false)) return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean popDismiss(boolean isDestroy) {
        if (popupWindow != null && popupWindow.isShowing()) {
            matchSelection.clearSelect();
            popupWindow.dismiss();
            popupWindow = null;

            if (!isDestroy) onRefresh();

            return true;
        }
        return false;
    }
}
