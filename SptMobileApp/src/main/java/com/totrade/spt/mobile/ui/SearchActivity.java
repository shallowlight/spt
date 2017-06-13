package com.totrade.spt.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.autrade.spt.master.dto.CompanyListUpEntity;
import com.autrade.spt.master.entity.TblCompanyMasterEntity;
import com.autrade.spt.master.service.inf.ICompanyService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.CompanyListAdapter;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业模糊查询
 *
 * @author huangxy
 * @date 2017/4/18
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener,  XRecyclerView.LoadingListener, RecyclerAdapterBase.ItemClickListener {

    private EditText searchView;
    private XRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();

        initData();
    }

    private void initData() {
        entityList = new ArrayList<>();
        adapter = new CompanyListAdapter(entityList);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
        onRefresh();
    }

    private void initView() {
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerView);
        searchView = (EditText) findViewById(R.id.searchView);
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);

    }

    private List<TblCompanyMasterEntity> entityList;

    /**
     * 根据企业名称查询企业列表(分页)
     * <p>新版企业绑定使用</p>
     *
     **/
     private void findCompanyList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<TblCompanyMasterEntity>>() {
            @Override
            public PagesDownResultEntity<TblCompanyMasterEntity> requestService() throws DBException, ApplicationException {
                CompanyListUpEntity upEntity = new CompanyListUpEntity();
                upEntity.setPageSize(pageSize);
                upEntity.setPageNo(currentPage);
                upEntity.setRegistName(searchView.getText().toString());
                return Client.getService(ICompanyService.class).findCompanyList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<TblCompanyMasterEntity> obj) {

                if (obj != null && obj.getDataList() != null) {
                    if (currentPage == 1) {
                        entityList.clear();
                        entityList.addAll(obj.getDataList());
                    } else {
                        entityList.addAll(obj.getDataList());
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_search:
                onRefresh();
                break;
        }
    }

    private CompanyListAdapter adapter;
    private int currentPage = 1;
    private static final int pageSize = 20;

    @Override
    public void onRefresh() {
        currentPage = 1;
        findCompanyList();
    }

    @Override
    public void onLoadMore() {
        currentPage += 1;
        findCompanyList();
    }

    @Override
    public void itemClick(@NonNull Object obj, int position) {
        TblCompanyMasterEntity entity = (TblCompanyMasterEntity) obj;
        Intent i = new Intent();
        i.putExtra("company", entity.toString());
        setResult(0x01, i);
        finish();
    }

    // 搜索文本监听器
/*
    private class QueryListener implements SearchView.OnQueryTextListener {
        // 当内容被提交时执行
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        // 当搜索框内内容发生改变时执行
        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                listView.clearTextFilter();  // 清楚ListView的过滤
            } else {
                listView.setFilterText(newText); // 设置ListView的过滤关键词
            }
            return true;
        }
    }
*/

}
