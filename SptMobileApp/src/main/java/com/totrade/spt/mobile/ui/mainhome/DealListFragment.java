package com.totrade.spt.mobile.ui.mainhome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.autrade.spt.datacentre.dto.ZoneDealNumberTopDownEntity;
import com.autrade.spt.datacentre.service.inf.IContractDataService;
import com.autrade.spt.master.service.inf.IUserRegisterApplyService;
import com.autrade.stage.droid.annotation.Injection;
import com.autrade.stage.droid.helper.InjectionHelper;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.adapter.DealTopAdapter;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ScroListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy on 2017/4/5.
 */

public class DealListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ScroListView listView;
    private DealTopAdapter adapter;
    private List<ZoneDealNumberTopDownEntity> data;

    public DealListFragment() {
        setContainerId(R.id.fl_deal_list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_deallist, container, false);
        listView = (ScroListView) view.findViewById(R.id.slv_deal_list);
        adapter = new DealTopAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        initData();
        return view;
    }

    private void initData() {
        findZoneDealNumberTop();
    }

    private void findZoneDealNumberTop() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<ZoneDealNumberTopDownEntity>>() {
            @Override
            public List<ZoneDealNumberTopDownEntity> requestService() throws DBException, ApplicationException {
                return Client.getService(IContractDataService.class).findZoneDealNumberTop();
            }

            @Override
            public void onDataSuccessfully(List<ZoneDealNumberTopDownEntity> obj) {
                if (!ObjUtils.isEmpty(obj)) {
                    List<ZoneDealNumberTopDownEntity> disEntity = new ArrayList<ZoneDealNumberTopDownEntity>();
                    //过滤出前三条数据
                    for (int i = 0; i < obj.size(); i++) {
                        if (i < 3) disEntity.add(obj.get(i));
                        else break;
                    }
                    adapter.refreshData(disEntity);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ZoneDealNumberTopDownEntity entity = adapter.getItem(position);
        IntentUtils.startActivity(getActivity(), DealRankDetailActivity.class, "PRODUCT_TYPE", entity.getProductType());
    }
}
