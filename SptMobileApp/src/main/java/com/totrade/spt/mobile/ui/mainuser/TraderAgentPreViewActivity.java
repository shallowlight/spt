package com.totrade.spt.mobile.ui.mainuser;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.entity.TblScfApplyDataEntity;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 贸易代理预览页面
 * Created by Timothy on 2017/4/26.
 */

public class TraderAgentPreViewActivity extends BaseActivity {

    private CustomGridView listView;
    private List <FormItemBean> data;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try {
            setContentView ( R.layout.activity_trader_agent_preveiw );
            listView = (CustomGridView) findViewById ( R.id.gridview );
            String entityString = getIntent ( ).getStringExtra ( "entity" );
            String buyCompanyName = getIntent ( ).getStringExtra ( "companyName" );
            TblScfApplyDataEntity dataEntity = JSON.parseObject ( entityString, TblScfApplyDataEntity.class );
            String deliveryModeValue;
            if ( dataEntity.getDeliveryMode ( ).equals ( "R" ) ) {
                deliveryModeValue = "转货权";
            } else {
                deliveryModeValue = DictionaryUtility.getValue ( SptConstant.SPTDICT_DELIVERY_MODE, dataEntity.getDeliveryMode ( ) );
            }
            data = new ArrayList <> ( );
            data.add ( new FormItemBean ( "", "申请方(销售方)", "采购方(购买方)" ) );
            data.add ( new FormItemBean ( "", LoginUserContext.getLoginUserDto ( ).getCompanyName ( ), buyCompanyName ) );
            data.add ( new FormItemBean ( "品名", dataEntity.getProductName ( ), dataEntity.getProductName ( ) ) );
            data.add ( new FormItemBean ( "质量标准", DictionaryUtility.getValue ( SptConstant.SPTDICT_PRODUCT_QUALITY, dataEntity.getProductQuality ( ) ), DictionaryUtility.getValue ( SptConstant.SPTDICT_PRODUCT_QUALITY, dataEntity.getProductQuality ( ) ) ) );
            data.add ( new FormItemBean ( "仓库", dataEntity.getWareHouse ( ), dataEntity.getWareHouse ( ) ) );
            data.add ( new FormItemBean ( "交货期", DispUtility.deliveryTimeToDisp ( dataEntity.getDeliveryTime ( ), "", "" ), DispUtility.deliveryTimeToDisp ( dataEntity.getDeliveryTime ( ), "", "" ) ) );
            data.add ( new FormItemBean ( "交货方式", deliveryModeValue, deliveryModeValue ) );
            data.add ( new FormItemBean ( "数量(吨)", dataEntity.getProductNumber ( ).toPlainString ( ), dataEntity.getProductNumber ( ).toPlainString ( ) ) );
            data.add ( new FormItemBean ( "单价(元)", dataEntity.getBuyPrice ( ).toPlainString ( ), dataEntity.getBuyPrice ( ).toPlainString ( ) ) );
            data.add ( new FormItemBean ( "总价(元)", dataEntity.getBuyPrice ( ).toPlainString ( ), dataEntity.getBuyPrice ( ).toPlainString ( ) ) );
            data.add ( new FormItemBean ( "结算方式", "定金:" + dataEntity.getBuyBond ( ) + "%", "定金:" + dataEntity.getBuyBond ( ) + "%" ) );

            TraderAgentPreAdapter adapter = new TraderAgentPreAdapter ( );
            listView.setAdapter ( adapter );
        } catch (Exception e) {
            Log.e ( "view:", e.getMessage ( ) );
        }

    }

    class TraderAgentPreAdapter extends BaseAdapter {

        @Override
        public int getCount ( ) {
            return data.size ( ) * 3;
        }

        @Override
        public Object getItem ( int position ) {
            return null;
        }

        @Override
        public long getItemId ( int position ) {
            return 0;
        }

        @Override
        public View getView ( int position, View convertView, ViewGroup parent ) {
            TextView textView = null;
            try {
                textView = new TextView ( TraderAgentPreViewActivity.this );
                LinearLayout.LayoutParams params;
                if ( position < 6 && position >= 3 ) {
                    params = new LinearLayout.LayoutParams ( ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px ( TraderAgentPreViewActivity.this, 80 ) );
                } else {
                    params = new LinearLayout.LayoutParams ( ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px ( TraderAgentPreViewActivity.this, 45 ) );
                }
                params.gravity = Gravity.CENTER_VERTICAL;
                textView.setPadding ( 20, 20, 20, 20 );
                textView.setGravity ( Gravity.CENTER_VERTICAL );
                textView.setLayoutParams ( params );

                if ( position > 3 && ( position % 3 == 1 || position % 3 == 2 ) ) {
                    textView.setBackgroundColor ( getResources ( ).getColor ( R.color.white ) );
                    textView.setTextSize ( 15 );
                    textView.setTextColor ( getResources ( ).getColor ( R.color.gray_txt_33 ) );
                } else {
                    textView.setBackgroundColor ( Color.parseColor ( "#f5f5f5" ) );
                    textView.setTextSize ( 12 );
                    textView.setTextColor ( Color.parseColor ( "#c6c6c6" ) );
                }
                if ( position % 3 == 0 ) {
                    textView.setText ( data.get ( position / 3 ).getName ( ) );
                } else if ( position % 3 == 1 ) {
                    textView.setText ( data.get ( position / 3 ).getBuyer ( ) );
                } else if ( position % 3 == 2 ) {
                    textView.setText ( data.get ( position / 3 ).getSeller ( ) );
                }
            } catch (Exception e) {

            }

            return textView;
        }
    }

    class FormItemBean {
        private String name;
        private String buyer;
        private String seller;

        public FormItemBean ( ) {
        }

        public FormItemBean ( String name, String buyer, String seller ) {
            this.name = name;
            this.buyer = buyer;
            this.seller = seller;
        }

        public String getName ( ) {
            return name;
        }

        public void setName ( String name ) {
            this.name = name;
        }

        public String getBuyer ( ) {
            return buyer;
        }

        public void setBuyer ( String buyer ) {
            this.buyer = buyer;
        }

        public String getSeller ( ) {
            return seller;
        }

        public void setSeller ( String seller ) {
            this.seller = seller;
        }
    }
}
