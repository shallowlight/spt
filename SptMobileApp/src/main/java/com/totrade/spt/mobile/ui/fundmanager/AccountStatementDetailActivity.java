package com.totrade.spt.mobile.ui.fundmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.autrade.spt.bank.entity.RunningReportDownEntity;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.google.gson.reflect.TypeToken;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.SuperTextView;

/**
 * 资金管理
 * 对账单详情页面
 * Created by Timothy on 2017/5/4.
 */

public class AccountStatementDetailActivity extends BaseActivity {

    private ComTitleBar title;
    private SuperTextView stv_pay_amount;
    private SuperTextView stv_other_company;
    private SuperTextView stv_cur_status;
    private SuperTextView stv_deal_time;
    private SuperTextView stv_constract_number;
    private RunningReportDownEntity runningReportDownEntity;
    private boolean isTSB;
    private TextView tv_statement_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_account_statement_detail);
        initView();
    }

    protected void initView() {
        title = (ComTitleBar) findViewById(R.id.title);
        tv_statement_name = (TextView) findViewById(R.id.tv_statement_name);
        stv_pay_amount = (SuperTextView) findViewById(R.id.stv_pay_amount);
        stv_other_company = (SuperTextView) findViewById(R.id.stv_other_company);
        stv_cur_status = (SuperTextView) findViewById(R.id.stv_cur_status);
        stv_deal_time = (SuperTextView) findViewById(R.id.stv_deal_time);
        stv_constract_number = (SuperTextView) findViewById(R.id.stv_constract_number);

        runningReportDownEntity = JsonUtility.toJavaObject(getIntent().getStringExtra("entity"), new TypeToken<RunningReportDownEntity>(){}.getType());
        isTSB = getIntent().getBooleanExtra("isBao",false);
        if (runningReportDownEntity != null) {
            tv_statement_name.setText(runningReportDownEntity.getBusinessTag1());
            getContractDetail(runningReportDownEntity.getBizId());
        }
    }

    /**
     * 获取详情并显示
     *
     * @param contractId 合同id
     */
    public void getContractDetail(final String contractId) {
        SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<ContractDownEntity>() {
            @Override
            public ContractDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).getContractDetail(contractId, LoginUserContext.getLoginUserDto().getUserId());
            }

            @Override
            public void onDataSuccessfully(ContractDownEntity entity) {
                if (entity != null) {
                    stv_pay_amount.setRightString(StringUtils.parseMoney(runningReportDownEntity.getAcctBalance()));
                    stv_other_company.setRightString(runningReportDownEntity.getObjAccountName1());
                    String contractStatus =  DictionaryUtility.getValue(SptConstant.SPTDICT_BONDPAYSTATUSTAG, entity.getBondPayStatusTag());
                    stv_cur_status.setRightString(contractStatus);
                    String deliveryTime = FormatUtil.date2String(entity.getContractTime(), "yyyy/MM/dd HH:mm:ss");
                    stv_deal_time.setRightString(deliveryTime);
                    stv_constract_number.setRightString(entity.getContractId ());
                }
            }
        });
    }
}
