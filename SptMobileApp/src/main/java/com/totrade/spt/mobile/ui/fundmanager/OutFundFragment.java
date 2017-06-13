package com.totrade.spt.mobile.ui.fundmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.bank.constants.BankConstant;
import com.autrade.spt.bank.entity.AccountInfoBalanceEntity;
import com.autrade.spt.bank.entity.Cfs;
import com.autrade.spt.bank.entity.TblAccountBalanceEntity;
import com.autrade.spt.bank.service.inf.IAccountInfoBalanceService;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;

/**
 * 资金管理
 * 出金
 * Created by Timothy on 2017/4/13.
 */

public class OutFundFragment extends BaseFragment {

    private AccountInfoBalanceEntity balanceEntity;
    private TextView tv_pingan_hint;
    private TextView tv_pingan_outfunds_number;
    private String paySystem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_out_fund, container, false);
        tv_pingan_hint = (TextView) view.findViewById(R.id.tv_pingan_hint);
        tv_pingan_outfunds_number = (TextView) view.findViewById(R.id.tv_pingan_outfunds_number);
        initView();
        return view;
    }

    private void initView() {

        paySystem = LoginUserContext.getLoginUserDto().getPaySystem();
        if (ObjUtils.isEmpty(paySystem)) return;
        tv_pingan_hint.setText(getString(R.string.funds_outto_pingan_desc));
        getFunds();
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
                    balanceEntity = obj;
                    if (BankConstant.PAY_SYSTEM_PINGAN.equals(paySystem)) {
                        setDataForPingAn(balanceEntity);
                    }
                }
            }
        });
    }

    /**
     * 设置平安数据
     *
     * @param balanceEntity
     */
    private void setDataForPingAn(AccountInfoBalanceEntity balanceEntity) {
        if (balanceEntity == null) return;
        TblAccountBalanceEntity accountBalanceEntity = balanceEntity.getAccountBalanceEntity();
        if (accountBalanceEntity == null) return;
        Cfs tempCfs = accountBalanceEntity.getAccountBalanceCfs();
        tv_pingan_outfunds_number.setText(StringUtils.parseMoney(tempCfs.getBalance()));
    }
}
