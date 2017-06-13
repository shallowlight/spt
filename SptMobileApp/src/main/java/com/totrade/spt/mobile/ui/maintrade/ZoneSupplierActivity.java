package com.totrade.spt.mobile.ui.maintrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.dto.QueryPageZoneCompanyDownEntity;
import com.autrade.spt.zone.dto.QueryPageZoneCompanyUpEntity;
import com.autrade.spt.zone.entity.TblZoneCompanyEntity;
import com.autrade.spt.zone.service.inf.IZoneCompanyService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.ui.maintrade.adapter.ZoneSupplierAdapter;

import java.util.Arrays;
import java.util.List;

public class ZoneSupplierActivity extends SptMobileActivityBase implements View.OnClickListener, RecyclerAdapterBase.ItemClickListener {

    @BindViewId(R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindViewId(R.id.btnConfirm)
    private Button btnConfirm;
    @BindViewId(R.id.iv_back)
    private ImageView iv_back;
    @BindViewId(R.id.title)
    private TextView title;

    ZoneSupplierAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_supplier);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnConfirm.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        title.setText("指定供应商");

        requestZoneSup();
    }

    private void requestZoneSup() {
        final String productType = getIntent().getStringExtra("productType");
        SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<PagesDownResultEntity<QueryPageZoneCompanyDownEntity>>() {
            @Override
            public PagesDownResultEntity<QueryPageZoneCompanyDownEntity> requestService() throws DBException, ApplicationException {
                QueryPageZoneCompanyUpEntity upEntity = new QueryPageZoneCompanyUpEntity();
                upEntity.setPageNo(1);
                upEntity.setPageSize(-1);       //不分页
                upEntity.setProductType(productType);
                upEntity.setType(TblZoneCompanyEntity.ZcType.C);
                return Client.getService(IZoneCompanyService.class).findZoneCompanyList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<QueryPageZoneCompanyDownEntity> pg) {
                if (pg != null && pg.getDataList() != null) {
                    String companyTags = getIntent().getStringExtra("TAGS");
                    String[] tags = null;
                    if (!TextUtils.isEmpty(companyTags)) {
                        tags = companyTags.split("\\" + SptConstant.SEP);
                    }
                    adapter = new ZoneSupplierAdapter(pg.getDataList(), tags == null ? null : Arrays.asList(tags));
                    adapter.setItemClickListener(ZoneSupplierActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnConfirm) {
            quit();
        } else if (v.getId() == R.id.iv_back) {
            finish();
        }
    }

    private void quit() {
        String json = "";
        if (adapter != null) {
            List<QueryPageZoneCompanyDownEntity> list = adapter.getSelectItems();
            if (list != null && !list.isEmpty()) {
                json = JsonUtility.toJSONString(list);
            }
        }
        Intent intent = new Intent();
        intent.putExtra("Json", json);
        setResult(0x0002, intent);
        finish();
    }

    @Override
    public void itemClick(@NonNull Object obj, int position) {
        adapter.selectItem((QueryPageZoneCompanyDownEntity) obj, position);
        quit();
    }
}
