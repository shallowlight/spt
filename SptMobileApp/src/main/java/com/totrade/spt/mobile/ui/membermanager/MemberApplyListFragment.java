package com.totrade.spt.mobile.ui.membermanager;

import android.support.annotation.NonNull;
import android.view.View;

import com.autrade.spt.master.dto.CompanyInviteInfo;
import com.autrade.spt.master.dto.UserCompanyInviteUpEntity;
import com.autrade.spt.master.service.inf.ICompanyInviteService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.RequestSubAccAdapter2;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员申请列表
 * Created by Timothy on 2017/4/24.
 */
public class MemberApplyListFragment extends BaseSptFragment<MemberActivity> implements XRecyclerView.LoadingListener, RequestSubAccAdapter2.ItemClickListener {

    private ComTitleBar title;
    private XRecyclerView listView;
    private int curPageNumber = 1;
    private final int PAGE_SIZE = 20;
    private RequestSubAccAdapter2 adapter;
    private List<CompanyInviteInfo> dataList = new ArrayList<>();

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_member_apply_list;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        listView = findView(R.id.listView);

        adapter = new RequestSubAccAdapter2(dataList);
        adapter.setItemClickListener(this);
        listView.init2LinearLayout();
        listView.setAdapter(adapter);
        listView.setLoadingListener(this);

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        findUserBindCompanyApplyList();
    }

    private void findUserBindCompanyApplyList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<CompanyInviteInfo>>() {
            @Override
            public PagesDownResultEntity<CompanyInviteInfo> requestService() throws DBException, ApplicationException {
                UserCompanyInviteUpEntity upEntity = new UserCompanyInviteUpEntity();
                upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
                upEntity.setPageSize(PAGE_SIZE);
                upEntity.setPageNo(curPageNumber);
                return Client.getService(ICompanyInviteService.class).findUserBindCompanyApplyList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<CompanyInviteInfo> obj) {
                listView.refreshComplete();
                listView.loadMoreComplete();
                if (obj == null) {
                    dataList.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }
                if (curPageNumber == 1) {
                    dataList.clear();
                }
                if (obj.getDataList() != null) {
                    dataList.addAll(obj.getDataList());
                    if (obj.getDataList().size() < PAGE_SIZE) {
                        listView.setNoMore(curPageNumber > 1);
                    } else {
                        listView.setLoadingMoreEnabled(true);
                        listView.setNoMore(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRefresh() {
        curPageNumber = 1;
        findUserBindCompanyApplyList();
    }

    @Override
    public void onLoadMore() {
        curPageNumber++;
        findUserBindCompanyApplyList();
    }

    @Override
    public void itemClick(@NonNull Object obj, int position) {
        mActivity.switchMDF(null, (CompanyInviteInfo) obj, "req_list");
    }

}
