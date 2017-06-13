package com.totrade.spt.mobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.bank.entity.RunningReportDownEntity;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.ui.fundmanager.AccountStatementDetailActivity;
import com.totrade.spt.mobile.ui.fundmanager.FundManagerActivity;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CommonTextView;

import java.util.List;

/**
 * Created by Timothy on 2017/4/14.
 */

public class FundAccountStatementAdapter extends RecyclerAdapterBase <RunningReportDownEntity, FundAccountStatementAdapter.ViewHolder> {

    private boolean isBao = false;  //是否通商宝

    public FundAccountStatementAdapter ( List <RunningReportDownEntity> list ) {
        super ( list );
    }

    public void setBao ( boolean bao ) {
        isBao = bao;
        notifyDataSetChanged ( );
    }

    @Override
    public FundAccountStatementAdapter.ViewHolder createViewHolderUseData ( ViewGroup parent, int viewType ) {
        return new FundAccountStatementAdapter.ViewHolder ( LayoutInflater.from ( context ).inflate ( R.layout.item_fund_account_statement, parent, false ) );
    }

    class ViewHolder extends ViewHolderBase <RunningReportDownEntity> {
        CommonTextView ctv;

        public ViewHolder ( View view ) {
            super ( view );
            ctv = (CommonTextView) itemView.findViewById ( R.id.ctv_account_statement );
        }

        @Override
        public void initItemData ( ) {

            final RunningReportDownEntity entity = itemObj;
            if ( null != entity ) {
                isIncome ( ctv.getRightTopTextView ( ), entity );
                ctv.setLeftTopTextString ( DictionaryUtility.getValue ( SptConstant.SPTDICT_BUSINESS_TYPE, entity.getBusinessType ( ) ) );
                ctv.setLeftTopTextString ( entity.getBusinessTag1 ( ) );
                ctv.setLeftBottomTextString ( DateUtility.formatToStr ( entity.getSubmitTime ( ), "yyyy/MM/dd" ) );
                ctv.setRightBottomTextString ( isBao ? entity.getBizId ( ) : entity.getObjAccountName1 ( ) );
            }
            ctv.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {
                    if ( ! StringUtility.isNullOrEmpty ( entity.getBizId ( ) ) ) {
                        Intent intent = new Intent ( context, AccountStatementDetailActivity.class );
                        intent.putExtra ( "entity", JsonUtility.toJSONString ( entity ) );
                        intent.putExtra ( "isBao", isBao );
                        context.startActivity ( intent );
                    }
                }
            } );
        }
    }

    private void isIncome ( TextView tv, RunningReportDownEntity entity ) {
        String text = StringUtils.parseMoney ( entity.getFeeTotal ( ) );
        if ( "+".equals ( entity.getDirect ( ) ) ) {
            text = "+" + text;
        } else if ( "-".equals ( entity.getDirect ( ) ) ) {
            text = "-" + text;
        }
        tv.setText ( text );
    }
}
