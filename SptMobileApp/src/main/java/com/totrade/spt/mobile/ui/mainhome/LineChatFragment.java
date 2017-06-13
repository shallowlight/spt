package com.totrade.spt.mobile.ui.mainhome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autrade.spt.zone.dto.ZoneProductIndexDownEntity;
import com.autrade.spt.zone.service.inf.IZoneProductIndexService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.github.mikephil.charting.charts.LineChart;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.chart.MPChartHelper;
import com.totrade.spt.mobile.view.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy on 2017/4/5.
 */

public class LineChatFragment extends BaseFragment {

    private static String mProductType;
    private LineChart lineChart;

    public static LineChatFragment newInstance(String productType) {
        LineChatFragment lineChatFragment = new LineChatFragment();
        mProductType = productType;
        return lineChatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linechat, container, false);
        lineChart = (LineChart) view.findViewById(R.id.lineCharts);
        refreshLineChat(mProductType);
        return view;
    }

    /**
     * 获取指定品名当月的指数列表
     */
    private void findLatestProductIndexList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<ZoneProductIndexDownEntity>>() {
            @Override
            public List<ZoneProductIndexDownEntity> requestService() throws DBException, ApplicationException {
                return Client.getService(IZoneProductIndexService.class).findLatestProductIndexList(mProductType, 7);
            }

            @Override
            public void onDataSuccessfully(List<ZoneProductIndexDownEntity> obj) {
                List<String> xValues = new ArrayList<String>();
                List<Float> yValues = new ArrayList<Float>();
                if (!ObjUtils.isEmpty(obj)) {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd");
                    for (int i = obj.size() - 1; i >= 0; i--) {
                        yValues.add(obj.get(i).getIndexNumber().floatValue());
                        xValues.add(format.format(obj.get(i).getIndexTime()));
                    }
                    if (null != lineChart) lineChart.removeAllViews();
                }
                MPChartHelper.setLineChart(lineChart, xValues, yValues, null, false);
            }
        });
    }

    public void refreshLineChat(String productType) {
        if (!ObjUtils.isEmpty(productType)) {
            mProductType = productType;
            findLatestProductIndexList();
        } else {
            MPChartHelper.setLineChart(lineChart, null, null, null, false);
        }
    }

}
