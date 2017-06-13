package com.totrade.spt.mobile.ui.mainhome;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.datacentre.dto.ContractDataStatisticforKlineUpEntity;
import com.autrade.spt.datacentre.dto.ZoneDealNumberTopDownEntity;
import com.autrade.spt.datacentre.service.inf.IContractDataService;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.github.mikephil.charting.charts.CombinedChart;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.DecimalUtils;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.chart.MPChartHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CPopupWindow;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.RelativePopupWindow;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Timothy on 2017/4/6.
 */

public class DealRankDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_product_name;
    private ImageView iv_select_year;
    private CombinedChart comBinedChart;
    private CPopupWindow popupWindow;
    private ComTitleBar title;
    private String curProductType;
    private String curYear;

    private ArrayList<String> years = new ArrayList<>();
    private ArrayList<String> xValues = new ArrayList<>();
    private ArrayList<Float> yLeftValues = new ArrayList<>();
    private ArrayList<Float> yRightValue = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_deal_detail);
        initView();
        initData();
        initChart();
    }

    private void initView() {
        title = (ComTitleBar) findViewById(R.id.title);
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        iv_select_year = (ImageView) findViewById(R.id.iv_select_year);
        comBinedChart = (CombinedChart) findViewById(R.id.comBinedChart);

        iv_select_year.setOnClickListener(this);
    }

    private void initData() {
        clearChartData();

        curProductType = getIntent().getStringExtra("PRODUCT_TYPE");
        tv_product_name.setText(ProductTypeUtility.getProductName(curProductType));

        List<View.OnClickListener> selectListeners = new ArrayList<>();
        View.OnClickListener onSelectClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                curYear = ((TextView) v).getText().toString();
                initChart();
            }
        };

        //从现在的年份往前推三年
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        curYear = String.valueOf(year);
        for (int i = year; i > year - 3; i--) {
            years.add(String.valueOf(i));
            selectListeners.add(onSelectClick);
        }
        popupWindow = new CPopupWindow(this, years);
        popupWindow.setOnPopupItemClickListener(popupItemClickListener);

        findDealNumberTopDetailList(curProductType);
    }

    private void clearChartData() {
        xValues.clear();
        yLeftValues.clear();
        yRightValue.clear();
    }

    private void initChart() {
        MPChartHelper.setCombineChart(comBinedChart, xValues, yRightValue, yLeftValues, "", "");
    }

    private void findDealNumberTopDetailList(final String productType) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<ZoneDealNumberTopDownEntity>>() {
            @Override
            public List<ZoneDealNumberTopDownEntity> requestService() throws DBException, ApplicationException {
                ContractDataStatisticforKlineUpEntity upEntity = new ContractDataStatisticforKlineUpEntity();
                upEntity.setProductType(curProductType);
                upEntity.setTradeDate(curYear);
                return Client.getService(IContractDataService.class).findDealNumberTopDetailList(upEntity);
            }

            @Override
            public void onDataSuccessfully(List<ZoneDealNumberTopDownEntity> obj) {
                clearChartData();
                if (!ObjUtils.isEmpty(obj)) {
                    for (ZoneDealNumberTopDownEntity entity : obj) {
                        if (entity != null) {
                            try {
                                xValues.add(entity.getDateStr());
                                yLeftValues.add(Float.valueOf(entity.getTotalDealNumber().longValue()));
                                yRightValue.add(Float.valueOf(DecimalUtils.decimalToTenThousandWithUnit(entity.getTotalDealPrice(), "")));
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                initChart();
            }
        });
    }

    private CPopupWindow.OnPopupItemClickListener popupItemClickListener = new CPopupWindow.OnPopupItemClickListener() {
        @Override
        public void onPopupItemClick(View text, int position) {
            curYear = years.get(position);
            findDealNumberTopDetailList(curProductType);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_select_year) {
            popupWindow.showOnAnchor(v, RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, false);
        }
    }
}
