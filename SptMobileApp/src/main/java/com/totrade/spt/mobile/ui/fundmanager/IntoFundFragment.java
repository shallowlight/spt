package com.totrade.spt.mobile.ui.fundmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.bank.constants.BankConstant;
import com.autrade.spt.bank.entity.AccountInfoBalanceEntity;
import com.autrade.spt.bank.service.inf.IAccountInfoBalanceService;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;

/**
 * 资金管理
 * 入金
 * Created by Timothy on 2017/4/13.
 */

public class IntoFundFragment extends BaseFragment {

    private TextView tv_pingan_hint;
    private TextView lblAccountName;
    private TextView lblAccountBank;
    private TextView lblBankCardNumber;
    private String paySystem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_into_fund, container, false);
        tv_pingan_hint = (TextView) view.findViewById(R.id.tv_pingan_hint);
        lblAccountName = (TextView) view.findViewById(R.id.lblAccountName);
        lblAccountBank = (TextView) view.findViewById(R.id.lblAccountBank);
        lblBankCardNumber = (TextView) view.findViewById(R.id.lblBankCardNumber);
        initView();
        return view;
    }

    private void initView() {
        paySystem = LoginUserContext.getLoginUserDto().getPaySystem();
        if (ObjUtils.isEmpty(paySystem)) return;
        tv_pingan_hint.setText(getString(R.string.funds_into_pingan_desc));
        getFunds();
    }

    /**
     * 按账户ID获得账户信息及其对应的资金信息、绑定用户
     */
    private void getAccountInfoBalanceByAccountId() {
        SubAsyncTask.create().setOnDataListener(getActivity(), true, new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                String id = BankConstant.CITIC_SPT_ACCOUNT_ID;
                String paySys = BankConstant.PAY_SYSTEM_CITIC;
                if (LoginUserContext.getLoginUserDto().getPaySystem().equals(BankConstant.PAY_SYSTEM_PINGAN)) {
                    id = BankConstant.PINGAN_SPT_ACCOUNT_ID;
                    paySys = BankConstant.PAY_SYSTEM_PINGAN;
                }

                AccountInfoBalanceEntity entity = Client.getService(IAccountInfoBalanceService.class).getAccountInfoBalanceByAccountId(id, paySys);
                return entity.getBankName();
            }

            @Override
            public void onDataSuccessfully(String obj) {
                if (!StringUtility.isNullOrEmpty(obj)) {
                    lblAccountBank.setText(obj);
                }
            }
        });
    }

    public void setDataForPingAn(AccountInfoBalanceEntity balanceEntity) {
        if (balanceEntity != null) {
            lblAccountName.setText(balanceEntity.getAccountName());
            lblAccountBank.setText(balanceEntity.getBankName());
            if (balanceEntity.getSubAccountId() != null)
                lblBankCardNumber.setText(balanceEntity.getSubAccountId().replaceAll("\\d{4}(?!$)", "$0 "));
        }
    }

    private void getFunds() {
        SubAsyncTask.create().setOnDataListener(getActivity(), true, new OnDataListener<AccountInfoBalanceEntity>() {
            @Override
            public AccountInfoBalanceEntity requestService() throws DBException, ApplicationException {
                LoginUserDto dto = LoginUserContext.getLoginUserDto();
                String paySys = dto.getPaySystem();
                return Client.getService(IAccountInfoBalanceService.class).getAccountInfoBalanceByUserId(dto.getUserId(), paySys);
            }

            @Override
            public void onDataSuccessfully(AccountInfoBalanceEntity obj) {
                if (obj != null) {
                    if (BankConstant.PAY_SYSTEM_PINGAN.equals(paySystem)) {
                        setDataForPingAn(obj);
                    }
                }
            }
        });
    }
}
