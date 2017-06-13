package com.totrade.spt.mobile.ui.fundmanager;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.autrade.spt.bank.dto.BlockStatDownEntity;
import com.autrade.spt.bank.entity.AccountInfoBalanceEntity;
import com.autrade.spt.bank.entity.RunningReportDownEntity;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.view.R;

/**
 * 资金管理
 * Created by Timothy on 2017/4/13.
 */

public class FundManagerActivity extends BaseActivity {

    private FundManagerFragment fundManagerFragment;
    private AccountStatementFragment accountStatementFragment;
    private OutIntoCurrencyFragment outIntoCurrencyFragment;
    private AccountStatementDetailFragment accountStatementDetailFragment;
    private BlockFundFragment blockFundFragment;
    private BlockFundDetailFragment blockFundDetailFragment;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_layout );

        initFragments ( );
        String entity = getIntent ( ).getStringExtra ( AccountInfoBalanceEntity.class.getName ( ) );
        AccountInfoBalanceEntity balanceEntity = JSON.parseObject ( entity, AccountInfoBalanceEntity.class );
        if ( ! ObjUtils.isEmpty ( balanceEntity ) ) {
            switchBFF ( balanceEntity );
        } else {
            switchFMF ( );
        }
    }

    private void initFragments ( ) {
        fundManagerFragment = new FundManagerFragment ( );
        accountStatementFragment = new AccountStatementFragment ( );
        outIntoCurrencyFragment = new OutIntoCurrencyFragment ( );
        accountStatementDetailFragment = new AccountStatementDetailFragment ( );
        blockFundFragment = new BlockFundFragment ( );
        blockFundDetailFragment = new BlockFundDetailFragment ( );

//        addFragments(fundManagerFragment,accountStatementFragment,outIntoCurrencyFragment,accountStatementDetailFragment,blockFundFragment,blockFundDetailFragment);
    }

    public void switchFMF ( ) {
        switchContent ( fundManagerFragment );
//        addFragment(fundManagerFragment,false);
//        showFragment(fundManagerFragment);
    }

    public void switchASF ( ) {
        switchContent ( accountStatementFragment, true );
//        addFragment(accountStatementFragment,true);
//        showFragment(accountStatementFragment);
    }

    public void switchOICF ( ) {
        switchContent ( outIntoCurrencyFragment, true );
//        addFragment(outIntoCurrencyFragment,true);
//        showFragment(outIntoCurrencyFragment);
    }

    public void switchASDF ( RunningReportDownEntity entity, boolean isBao ) {
        accountStatementDetailFragment.setRunningReportDownEntity ( entity );
        accountStatementDetailFragment.setIsTSB ( isBao );
        switchContent ( accountStatementDetailFragment, true );
//        addFragment(accountStatementDetailFragment,false);
//        showFragment(accountStatementDetailFragment);
    }

    public void switchBFF ( AccountInfoBalanceEntity balanceEntity ) {
        blockFundFragment.setEntity ( balanceEntity );
        switchContent ( blockFundFragment, true );
//        addFragment(blockFundFragment,true);
//        showFragment(blockFundFragment);
    }

    public void switchBFDF ( BlockStatDownEntity itemObj ) {
        blockFundDetailFragment.setEntity ( itemObj );
        switchContent ( blockFundDetailFragment, true );
//        addFragment(blockFundDetailFragment,true);
//        showFragment(blockFundDetailFragment);
    }

}
