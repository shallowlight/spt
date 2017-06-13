package com.totrade.spt.mobile.ui.fundmanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.bank.entity.RunningHistoryUpEntity;
import com.autrade.spt.bank.entity.RunningReportDownEntity;
import com.autrade.spt.bank.service.inf.IRunningAccountService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.StringUtility;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.FundAccountStatementAdapter;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 资金管理
 * 对账单列表
 * Created by Timothy on 2017/4/14.
 */

public class FundStatementListFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private int currentPageNumber = 1;
    private final int numberPerPage = 20;
    private Date mStartDate;
    private Date mEndDate;
    private String mBusinessType="";
    private XRecyclerView xRecyclerView;
    private List<RunningReportDownEntity> resultList = new ArrayList<>();
    private FundAccountStatementAdapter fundAdapter;

    public static FundStatementListFragment newInstance(int type) {
        FundStatementListFragment fundStatementListFragment = new FundStatementListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fundStatementListFragment.setArguments(bundle);
        return fundStatementListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        xRecyclerView = new XRecyclerView(getActivity());
        xRecyclerView.init2LinearLayout();
        xRecyclerView.addHeaderView(setTitleHead());
        xRecyclerView.setLoadingListener(this);
        xRecyclerView.setLoadingMoreEnabled(true);
        fundAdapter = new FundAccountStatementAdapter(resultList);
        xRecyclerView.setAdapter(fundAdapter);
        ((AccountStatementFragment) getParentFragment()).setOnRefreshFromDateAndBusinessTypeListener(refreshListener);
        mStartDate = DateUtility.parseToDate(FormatUtil.date2String(HostTimeUtility.getDate(), "yyyy/MM/dd"), "yyyy/MM/dd");
        Calendar c = FormatUtil.string2Calendar(FormatUtil.date2String(HostTimeUtility.getDate(), "yyyy/MM/dd"), "yyyy/MM/dd");
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.SECOND, -1);
        mEndDate = FormatUtil.calendar2Date(c);
        return xRecyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @NonNull
    private LinearLayout setTitleHead() {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.white));

        TextView textView = new TextView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(getActivity(), 36));
        textView.setLayoutParams(params);
        textView.setPadding(DensityUtil.dp2px(getActivity(), 15), DensityUtil.dp2px(getActivity(), 12), 0, 0);
        textView.setText("账单列表");
        textView.setTextColor(getResources().getColor(R.color.gray_txt_87));
        textView.setTextSize(12);
        linearLayout.addView(textView);

        View view = new View(getActivity());
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundResource(R.drawable.activity_driver);
        linearLayout.addView(view);
        return linearLayout;
    }

    @Override
    public void onRefresh() {
        currentPageNumber = 1;
        loadData();
    }

    @Override
    public void onLoadMore() {
        currentPageNumber++;
        loadData();
    }

    private void loadData() {
        if (getArguments().getInt("type") == 0) {
            queryFundBillList();
            fundAdapter.setBao(false);
        } else {
            fundAdapter.setBao(true);
            queryTSBList();
        }
    }

    /**
     * 查询银行账户对账单
     */
    private void queryFundBillList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<RunningReportDownEntity>>() {

            @Override
            public PagesDownResultEntity<RunningReportDownEntity> requestService() throws DBException, ApplicationException {
                return Client.getService(IRunningAccountService.class).queryFundBillList(fromUpEntity());
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<RunningReportDownEntity> pg) {
                notifyData(pg);
            }
        });
    }

    /**
     * 查询通商宝账户对账单
     */
    private void queryTSBList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<RunningReportDownEntity>>() {

            @Override
            public PagesDownResultEntity<RunningReportDownEntity> requestService() throws DBException, ApplicationException {
                return Client.getService(IRunningAccountService.class).queryTSBList(fromUpEntity());
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<RunningReportDownEntity> pg) {
                notifyData(pg);
            }
        });
    }

    /**
     * 填充数据到上行entity
     *
     * @return
     */
    private RunningHistoryUpEntity fromUpEntity() {
        RunningHistoryUpEntity entity = new RunningHistoryUpEntity();
        entity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
        entity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
        entity.setCurrentPageNumber(currentPageNumber);
        entity.setNumberPerPage ( numberPerPage );
        entity.setTradeType(mBusinessType);
        if (!ObjUtils.isEmpty(mStartDate)) {
            entity.setStartTime(mStartDate);
        }
        if (!ObjUtils.isEmpty(mEndDate)) {
            entity.setEndTime(mEndDate);
        }
        return entity;
    }

    private void notifyData(PagesDownResultEntity<RunningReportDownEntity> pg) {
        xRecyclerView.refreshComplete();
        xRecyclerView.loadMoreComplete();
        if (pg == null) {
            return;
        }
        if (currentPageNumber == 1) {
            resultList.clear();
        }
        if (pg.getDataList() != null) {
            resultList.addAll(pg.getDataList());
            if (pg.getDataList().size() < numberPerPage) {
                xRecyclerView.setNoMore(currentPageNumber > 1);
            } else {
                xRecyclerView.setLoadingMoreEnabled(true);
                xRecyclerView.setNoMore(false);
            }
        }
        fundAdapter.notifyDataSetChanged();
    }

    private AccountStatementFragment.OnRefreshFromDateAndBusinessTypeListener refreshListener = new AccountStatementFragment.OnRefreshFromDateAndBusinessTypeListener() {
        @Override
        public void OnNoticeRefresh(String startDate, String endDate, String businessType) {
//            if (!ObjUtils.isEmpty(resultList)) resultList.clear();
            mStartDate = DateUtility.parseToDate(startDate, "yyyy/MM/dd");
            Calendar c = FormatUtil.string2Calendar(endDate, "yyyy/MM/dd");
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.SECOND, -1);
            mEndDate = FormatUtil.calendar2Date(c);
            mBusinessType = businessType;
            onRefresh();
        }
    };

}
