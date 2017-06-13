package com.totrade.spt.mobile.ui.fundmanager;

import android.view.View;

import com.autrade.spt.bank.dto.BlockStatDownEntity;
import com.autrade.spt.bank.dto.BlockStatUpEntity;
import com.autrade.spt.bank.entity.AccountInfoBalanceEntity;
import com.autrade.spt.bank.service.inf.IBlockStatService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.BlockingAdapter;
import com.totrade.spt.mobile.base.BaseBottomFlowFragment;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 资金管理
 * 冻结资金列表
 * Created by Timothy on 2017/4/14.
 */

public class BlockFundFragment extends BaseSptFragment<FundManagerActivity> implements XRecyclerView.LoadingListener {

    private ComTitleBar title;
    private AccountInfoBalanceEntity entity;
    private BlockingAdapter adapter;
    private List<BlockStatDownEntity> dataList = new ArrayList<>();

    private int curPageNumber = 1;
    private final int PAGE_NUMBER = 20;
    private XRecyclerView listView;

    private String selectTag;//选中的文本

    private BaseBottomFlowFragment popup;

    private int curSelect = 0;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_fund_blocking;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);

        listView = findView(R.id.lv_block_list);
        listView.init2LinearLayout();
        listView.setLoadingListener(this);
        adapter = new BlockingAdapter(dataList);
        listView.setAdapter(adapter);
        findBlockListByAcctId();
        title.setRightViewClickListener(onClickListener);
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
            popup = BaseBottomFlowFragment.newInstance("请选择冻结类型", Dictionary.keyList( Dictionary.FUND_BUSINESS_TYPE),curSelect);
            popup.setOnTagSelectListener(onTagSelectListener);
            popup.show(getChildFragmentManager(), "");
        }
    };

    private BaseBottomFlowFragment.OnTagSelectListener onTagSelectListener = new BaseBottomFlowFragment.OnTagSelectListener() {
        @Override
        public void getSelectText(String text,int position) {
            popup.dismiss();
            curPageNumber = 1;
            selectTag = Dictionary.keyToCode( Dictionary.FUND_BUSINESS_TYPE,text);
            curSelect = position;
            findBlockListByAcctId();
        }
    };

    private void findBlockListByAcctId() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<BlockStatDownEntity>>() {
            @Override
            public PagesDownResultEntity<BlockStatDownEntity> requestService() throws DBException, ApplicationException {
                BlockStatUpEntity upEntity = new BlockStatUpEntity();
                upEntity.setAccountId(entity.getAccountId());
                upEntity.setPageNo(curPageNumber);
                upEntity.setPageSize(PAGE_NUMBER);
                upEntity.setBusinessType(selectTag);
                return Client.getService(IBlockStatService.class).findBlockListByAcctId(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<BlockStatDownEntity> obj) {
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
                    if (obj.getDataList().size() < PAGE_NUMBER) {
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

    public void setEntity(AccountInfoBalanceEntity entity) {
        this.entity = entity;
    }

    @Override
    public void onRefresh() {
        curPageNumber = 1;
        findBlockListByAcctId();
    }

    @Override
    public void onLoadMore() {
        curPageNumber++;
        findBlockListByAcctId();
    }

}
