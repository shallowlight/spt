package com.totrade.spt.mobile.ui.fundmanager;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.StringUtility;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.totrade.spt.mobile.base.BaseBottomFlowFragment;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.entity.TabEntity;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.CalendarActivity;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 对账单
 * Created by Timothy on 2017/4/13.
 */

public class AccountStatementFragment extends BaseSptFragment<FundManagerActivity> implements View.OnClickListener {

    private ComTitleBar title;
    private CommonTabLayout ctl_tab;
    private RelativeLayout rl_fund_statement_list;
    private TextView tv_date_from;
    private TextView tv_date_to;
    private String[] tabTitles = new String[]{"资金对账单", "通商宝对账单"};

    private final static int REQUEST_CODE_DATE_FROM = 0x0001;
    private final static int REQUEST_CODE_DATE_TO = 0x0002;
    private final static String DATE_FORMAT = "yyyy/MM/dd";

    private FundStatementListFragment fundStatementListFragment;

    private BaseBottomFlowFragment popup;

    private int curSelect;
    private String selectTag;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_account_statement;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        title.setRightViewClickListener(onClickListener);
        ctl_tab = findView(R.id.ctl_tab);
        rl_fund_statement_list = findView(R.id.rl_fund_statement_list);
        tv_date_from = findView(R.id.tv_date_from);
        tv_date_to = findView(R.id.tv_date_to);

        fundStatementListFragment = FundStatementListFragment.newInstance(ctl_tab.getCurrentTab());
        getChildFragmentManager().beginTransaction().replace(R.id.rl_fund_statement_list, fundStatementListFragment).commitAllowingStateLoss();

        ArrayList<CustomTabEntity> tabEntitys = new ArrayList<>();
        for (String tabTitle : tabTitles) {
            tabEntitys.add(new TabEntity(tabTitle, 0, 0));
        }
        ctl_tab.setTabData(tabEntitys);
        ctl_tab.setOnTabSelectListener(mOnTabSelectListener);
        ctl_tab.setCurrentTab(0);
        ctl_tab.setSaveEnabled ( false );
        tv_date_from.setText(FormatUtil.date2String(HostTimeUtility.getDate(), DATE_FORMAT));
        tv_date_to.setText(FormatUtil.date2String(HostTimeUtility.getDate(), DATE_FORMAT));
        tv_date_from.setOnClickListener(this);
        tv_date_to.setOnClickListener(this);

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });
    }

    protected View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popup = BaseBottomFlowFragment.newInstance("请选择交易类型", Dictionary.keyList( Dictionary.FUND_TRADE_TYPE),curSelect);
            popup.setOnTagSelectListener(onTagSelectListener);
            popup.show(getChildFragmentManager(), "");
        }
    };

    private BaseBottomFlowFragment.OnTagSelectListener onTagSelectListener = new BaseBottomFlowFragment.OnTagSelectListener() {
        @Override
        public void getSelectText(String text,int position) {
            popup.dismiss();
            selectTag = Dictionary.keyToCode( Dictionary.FUND_TRADE_TYPE,text);
            curSelect = position;
            refreshList();
        }
    };

    private OnTabSelectListener mOnTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            fundStatementListFragment.getArguments().putInt("type", position);
            refreshList();
        }

        @Override
        public void onTabReselect(int position) {
            fundStatementListFragment.getArguments().putInt("type", position);
            refreshList();
        }
    };

    /**
     * 刷新对账单列表
     */
    private void refreshList() {
        if (onRefreshFromDateAndBusinessTypeListener != null) {
            onRefreshFromDateAndBusinessTypeListener.OnNoticeRefresh(
                    tv_date_from.getText().toString(),
                    tv_date_to.getText().toString(), selectTag);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_date_from:
                intent = new Intent(getActivity(), CalendarActivity.class);
                intent.putExtra("datastr", tv_date_from.getText().toString());
                intent.putExtra("dateformat", DATE_FORMAT);
                startActivityForResult(intent, REQUEST_CODE_DATE_FROM);
                break;
            case R.id.tv_date_to:
                intent = new Intent(getActivity(), CalendarActivity.class);
                intent.putExtra("datastr", tv_date_from.getText().toString());
                intent.putExtra("dateformat", DATE_FORMAT);
                startActivityForResult(intent, REQUEST_CODE_DATE_TO);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_DATE_FROM) {
            String startTime = data.getStringExtra("dateselect");
            if (!TextUtils.isEmpty(startTime)) {
                Date date = FormatUtil.string2Date(startTime, DATE_FORMAT);
                tv_date_from.setText(FormatUtil.date2String(date, DATE_FORMAT));
            }
        } else if (requestCode == REQUEST_CODE_DATE_TO) {
            String endTime = data.getStringExtra("dateselect");
            if (!TextUtils.isEmpty(endTime)) {
                Date date = FormatUtil.string2Date(endTime, DATE_FORMAT);
                tv_date_to.setText(FormatUtil.date2String(date, DATE_FORMAT));
            }
        }

        checkRequestData();//检查时间

        refreshList();

    }

    /**
     * 检查日期
     *
     * @return
     */
    private boolean checkRequestData() {
        Date dStart = null;
        Date dEnd = null;
        if (!StringUtility.isNullOrEmpty(tv_date_from.getText().toString())) {
            dStart = DateUtility.parseToDate(tv_date_from.getText().toString(), DATE_FORMAT);
        }
        if (!StringUtility.isNullOrEmpty(tv_date_to.getText().toString())) {
            Calendar c = FormatUtil.string2Calendar(tv_date_to.getText().toString(), DATE_FORMAT);
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.SECOND, -1);
            dEnd = c.getTime();
        }
        if (dStart != null && dStart.after(new Date())) {
            ToastHelper.showMessage("查询起始日期不能早于当前日期");
            return false;
        } else if (dStart != null && dEnd != null && dStart.after(dEnd)) {
            ToastHelper.showMessage("查询起始日期不能早于终止日期");
            return false;
        }
        return true;
    }

    private OnRefreshFromDateAndBusinessTypeListener onRefreshFromDateAndBusinessTypeListener;

    public void setOnRefreshFromDateAndBusinessTypeListener(OnRefreshFromDateAndBusinessTypeListener listener) {
        this.onRefreshFromDateAndBusinessTypeListener = listener;
    }

    public interface OnRefreshFromDateAndBusinessTypeListener {
        void OnNoticeRefresh(String startDate, String endDate, String businessType);
    }

}
