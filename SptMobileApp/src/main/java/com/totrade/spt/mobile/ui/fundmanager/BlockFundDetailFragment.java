package com.totrade.spt.mobile.ui.fundmanager;

import android.view.View;
import android.widget.TextView;

import com.autrade.spt.bank.dto.BlockStatDownEntity;
import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.text.DecimalFormat;

/**
 * 资金管理
 * 冻结资金详情
 * Created by Timothy on 2017/4/14.
 */

public class BlockFundDetailFragment extends BaseSptFragment<FundManagerActivity> {

    private BlockStatDownEntity entity;
    private TextView tv_bussiness_type;
    private SuperTextView stv_block_price;
    private SuperTextView stv_other_company;
    private SuperTextView stv_deal_time;
    private SuperTextView stv_contractId;
    private ComTitleBar title;


    @Override
    public int setFragmentLayoutId ( ) {
        return R.layout.fragment_blocking_fund_detail;
    }

    @Override
    protected void initView ( ) {
        title = findView ( R.id.title );
        tv_bussiness_type = findView ( R.id.tv_bussiness_type );
        stv_block_price = findView ( R.id.stv_block_price );
        stv_other_company = findView ( R.id.stv_other_company );
        stv_deal_time = findView ( R.id.stv_deal_time );
        stv_contractId = findView ( R.id.stv_contractId );

        title.setLeftViewClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                mActivity.popBack ( );
            }
        } );

        DecimalFormat format = new DecimalFormat ( "###,###,###,##0.00" );
        if ( null != entity ) {
            tv_bussiness_type.setText ( DictionaryUtility.getValue ( AppConstant.DICT_BUSINESS_TYPE, entity.getBusinessType ( ) ) );
            stv_block_price.setRightString ( format.format ( entity.getBlockAmount ( ) ) );
            stv_deal_time.setRightString ( DateUtility.formatToStr ( entity.getBlockDate ( ) ) );
            stv_other_company.setRightString ( entity.getPayee ( ) );
            stv_contractId.setRightString ( entity.getBusinessId ( ) );
            if ( StringUtility.isNullOrEmpty ( entity.getPayee ( ) ) ) {
                stv_other_company.setVisibility ( View.GONE );
            }
            if ( StringUtility.isNullOrEmpty ( entity.getBusinessId ( ) ) ) {
                stv_contractId.setVisibility ( View.GONE );
            }
        }
    }

    public void setEntity ( BlockStatDownEntity entity ) {
        this.entity = entity;
    }

}
