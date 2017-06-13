package com.totrade.spt.mobile.ui.maintrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.google.gson.reflect.TypeToken;
import com.totrade.spt.mobile.adapter.ZoneChooseRequestAdapter;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;

import java.lang.reflect.Type;

/**
*
* 可转卖库存列表
* @author huangxy
* @date 2017/4/13
*
*/
public class ResellStockActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ZoneChooseRequestAdapter adapter;
    private QueryMyStockUpEntity upEntity;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_resell_stock);

        initView();
        parseIntent();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.title)).setText("可转卖库存");
    }

    private void initData() {
        requestOfficeList(upEntity);
    }

    private void parseIntent() {
        String upEntityJson = getIntent().getStringExtra("UPENTITYJSON");
        if (!TextUtils.isEmpty(upEntityJson)) {
            Type type = new TypeToken<QueryMyStockUpEntity>() {
            }.getType();
            upEntity = JsonUtility.toJavaObject(upEntityJson, type);
        }
        if (upEntity == null) {
            String productType = getIntent().getStringExtra("PRODUCTTYPE");
            String buySell = getIntent().getStringExtra("BUYSELL");
            upEntity = formUpEntity(buySell, productType);
        }
    }


    private QueryMyStockUpEntity formUpEntity(String buySell, String productType) {
        QueryMyStockUpEntity upEntity = new QueryMyStockUpEntity();
        upEntity.setNumberPerPage(50);
        upEntity.setCurrentPageNumber(1);
        upEntity.setProductType(productType);
//        upEntity.setBuySell(buySell);
        upEntity.setUserId(LoginUserContext.getLoginUserId());
//        upEntity.setDealType(SptConstant.DEAL_TYPE_ZONE);
//        upEntity.setMode("A");
//        String bondPayStatusTag = DealConstant.BONDPAYSTATUS_TAG_BOND_PAID + SptConstant.SEP //1 已付保证金
//                + DealConstant.BONDPAYSTATUS_TAG_SELLOUT ;                   //9 买家已挂出
//        upEntity.setBondPayStatusTagCondition(bondPayStatusTag);
//        String buyerZoneStatus = DealConstant.ORDER_NORMALITY + SptConstant.SEP              // O 正常
//                + DealConstant.ORDER_HANGINGOUT;                                     //G 挂出
//        upEntity.setBuyerZoneStatusCondition(buyerZoneStatus);
        upEntity.setContractZoneStatus(ActivityConstant.Zone.CONTRACT_ZONE_STATUS_NOT_A_DELIVERY);
        return upEntity;
    }

    private void requestOfficeList(final QueryMyStockUpEntity upEntity) {
        SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<PagesDownResultEntity<MyStockDownEntity>>() {
            @Override
            public PagesDownResultEntity<MyStockDownEntity> requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).findMyStockList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<MyStockDownEntity> downResultEntity) {
                recyclerView.setLayoutManager(new LinearLayoutManager(ResellStockActivity.this));
                adapter = new ZoneChooseRequestAdapter(downResultEntity.getDataList());
                recyclerView.setAdapter(adapter);
            }
        });
    }


    @Override
    public void finish() {
        if (adapter != null && adapter.getSelectEntity() != null) {
            String json = JsonUtility.toJSONString(adapter.getSelectEntity());
            Intent intent = getIntent();
            intent.putExtra("JSON", json);
            setResult(0x0002, intent);
        }
        super.finish();
    }
}
