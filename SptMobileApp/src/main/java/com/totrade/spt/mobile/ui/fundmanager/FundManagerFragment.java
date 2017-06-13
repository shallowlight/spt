package com.totrade.spt.mobile.ui.fundmanager;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.autrade.spt.bank.entity.AccountInfoBalanceEntity;
import com.autrade.spt.bank.entity.Cfs;
import com.autrade.spt.bank.entity.TblAccountBalanceEntity;
import com.autrade.spt.bank.service.inf.IAccountInfoBalanceService;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.math.BigDecimal;

/**
 * 资金管理
 * Created by Timothy on 2017/4/13.
 */

public class FundManagerFragment extends BaseSptFragment<FundManagerActivity> implements View.OnClickListener {

    private ComTitleBar title;
    private TextView tv_account_statement;
    private TextView tv_outinto_urrency;
    private SuperTextView stv_usable_fund;
    private SuperTextView stv_fund_blocking;
    private SuperTextView stv_tsb;
    private TextView tv_fund_ID;
    private TextView tv_bank_card_number;
    private TextView tv_company_account_name;

    private AccountInfoBalanceEntity balanceEntity;

    @Override
    public int setFragmentLayoutId ( ) {
        return R.layout.fragment_fund_manager;
    }

    @Override
    protected void initView ( ) {
        title = findView ( R.id.title );
        tv_company_account_name = findView ( R.id.tv_company_account_name );
        tv_fund_ID = findView ( R.id.tv_fund_ID );
        tv_bank_card_number = findView ( R.id.tv_bank_card_number );
        stv_usable_fund = findView ( R.id.stv_usable_fund );
        stv_fund_blocking = findView ( R.id.stv_fund_blocking );
        stv_tsb = findView ( R.id.stv_tsb );
        tv_account_statement = findView ( R.id.tv_account_statement );
        tv_outinto_urrency = findView ( R.id.tv_outinto_urrency );

        tv_account_statement.setOnClickListener ( this );
        tv_outinto_urrency.setOnClickListener ( this );
        stv_fund_blocking.setOnClickListener ( this );

        title.setLeftViewClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                mActivity.finish ( );
            }
        } );

        getFunds ( );
    }

    @Override
    public void onClick ( View v ) {
        switch ( v.getId ( ) ) {
            case R.id.tv_account_statement:
                mActivity.switchASF ( );
                break;
            case R.id.tv_outinto_urrency:
                mActivity.switchOICF ( );
                break;
            case R.id.stv_fund_blocking:
//                mActivity.switchBFF ( balanceEntity );    TODO  next version
                break;
        }
    }

    private void getFunds ( ) {
        SubAsyncTask.create ( ).setOnDataListener ( new OnDataListener <AccountInfoBalanceEntity> ( ) {
            @Override
            public AccountInfoBalanceEntity requestService ( ) throws DBException, ApplicationException {
                LoginUserDto dto = LoginUserContext.getLoginUserDto ( );
                String paySys = dto.getPaySystem ( );
                return Client.getService ( IAccountInfoBalanceService.class ).getAccountInfoBalanceByUserId ( dto.getUserId ( ), paySys );
            }

            @Override
            public void onDataSuccessfully ( AccountInfoBalanceEntity obj ) {
                if ( obj != null ) {
                    balanceEntity = obj;
                    loadView ( obj );
                }
            }
        } );
    }

    private void loadView ( AccountInfoBalanceEntity balanceEntity ) {
        if ( balanceEntity != null ) {
            tv_fund_ID.setText ( balanceEntity.getAccountId ( ).toString ( ).toUpperCase ( ) );
            TblAccountBalanceEntity accountBalanceEntity = balanceEntity.getAccountBalanceEntity ( );
            if ( ! TextUtils.isEmpty ( balanceEntity.getSubAccountId ( ) ) )
                tv_bank_card_number.setText ( balanceEntity.getSubAccountId ( ).replaceAll ( "\\d{4}(?!$)", "$0 " ) ); //虚拟子账户ID

            if ( ! TextUtils.isEmpty ( balanceEntity.getAccountName ( ) ) )
                tv_company_account_name.setText ( balanceEntity.getAccountName ( ) );

            if ( accountBalanceEntity != null ) {
                Cfs tempCfs = accountBalanceEntity.getAccountBalanceCfs ( );
                stv_usable_fund.setRightString ( StringUtils.parseMoney ( tempCfs.getBalance ( ) ) );
                stv_tsb.setRightString ( StringUtils.parseMoney ( tempCfs.getCredit ( ) ) );
                BigDecimal blockAmountTotal = accountBalanceEntity.getBlockFundCfs ( ).getBalance ( )
                        .add ( accountBalanceEntity.getMatchBondCfs ( ).getBalance ( ) )
                        .add ( accountBalanceEntity.getMatchBondCfs ( ).getSptpoint ( ) )
                        .add ( accountBalanceEntity.getMatchBlockFeeCfs ( ).getBalance ( ) )
                        .add ( accountBalanceEntity.getMatchBlockBondCfs ( ).getBalance ( ) );
                stv_fund_blocking.setRightString ( StringUtils.parseMoney ( blockAmountTotal ) );
            }
        }
    }

}
