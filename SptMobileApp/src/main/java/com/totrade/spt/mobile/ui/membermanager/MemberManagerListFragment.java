package com.totrade.spt.mobile.ui.membermanager;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.autrade.spt.master.dto.CompanyInviteInfo;
import com.autrade.spt.master.dto.UserCompanyInviteUpEntity;
import com.autrade.spt.master.entity.UserAccountEntity;
import com.autrade.spt.master.entity.UserSubAcctUpEntity;
import com.autrade.spt.master.service.inf.ICompanyInviteService;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.SubAccountListAdapter;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.BadgeView;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员管理列表（子账户列表）
 * Created by Timothy on 2017/4/24.
 */

public class MemberManagerListFragment extends BaseSptFragment<MemberActivity> {
    private ComTitleBar title;
    private XRecyclerView recyclerView;

    private List<UserAccountEntity> dataList = new ArrayList<>();
    private SubAccountListAdapter adapter;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_member_manager;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.init2LinearLayout();
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);

        adapter = new SubAccountListAdapter(dataList);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new RecyclerAdapterBase.ItemClickListener() {
            @Override
            public void itemClick(@NonNull Object obj, int position) {
                mActivity.switchMDF(((UserAccountEntity) obj), null, "sub_list");
            }
        });

        title.setRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.switchMALF();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getTradeSubAcctListByCompanyTag();
        findUserBindCompanyApplyList();
    }

    /**
     * 获取交易子账户列表
     */
    private void getTradeSubAcctListByCompanyTag() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<UserAccountEntity>>() {
            @Override
            public List<UserAccountEntity> requestService() throws DBException, ApplicationException {
                UserSubAcctUpEntity upEntity = new UserSubAcctUpEntity();
                upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
                return Client.getService(IUserService.class).getTradeSubAcctListByCompanyTag(upEntity);
            }

            @Override
            public void onDataSuccessfully(List<UserAccountEntity> resultList) {
                if (resultList != null) {
                    dataList.clear();
                    dataList.addAll(resultList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void findUserBindCompanyApplyList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<CompanyInviteInfo>>() {
            @Override
            public PagesDownResultEntity<CompanyInviteInfo> requestService() throws DBException, ApplicationException {
                UserCompanyInviteUpEntity upEntity = new UserCompanyInviteUpEntity();
                upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
                upEntity.setPageSize(10);
                upEntity.setPageNo(1);
                return Client.getService(ICompanyInviteService.class).findUserBindCompanyApplyList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<CompanyInviteInfo> obj) {
                if (null != obj) {
                    int count = obj.getTotalCount();
                    BadgeView badgeView = new BadgeView(mActivity);
                    badgeView.setLayoutParams(new FrameLayout.LayoutParams(DensityUtil.dp2px(mActivity, 12), DensityUtil.dp2px(mActivity, 12)));
                    badgeView.setPadding(0, 0, 0, 0);
                    badgeView.setGravity(Gravity.CENTER);
                    badgeView.setBackground(3, Color.parseColor("#DC5C5D"));
                    badgeView.setBadgeMargin(0, 2, 2, 0);
                    badgeView.setTextSize(6);
                    badgeView.setBadgeCount(count);
                    badgeView.setHideOnNull(true);
                    badgeView.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
                    badgeView.setTargetView(title.getRightBtn());
                }
            }
        });
    }
}
