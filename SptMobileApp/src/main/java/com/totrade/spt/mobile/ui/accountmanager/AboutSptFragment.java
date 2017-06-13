package com.totrade.spt.mobile.ui.accountmanager;

import android.content.DialogInterface;
import android.view.View;

import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

/**
 * 账户管理
 * 关于商品通
 * Created by Timothy on 2017/4/10.
 */

public class AboutSptFragment extends BaseSptFragment<UserAccMngActivity> implements View.OnClickListener {

    private ComTitleBar title;
    private SuperTextView stv_product_info;
    private SuperTextView stv_company_introduce;
    private SuperTextView stv_contact_customer;

    @Override
    public int setFragmentLayoutId ( ) {
        return R.layout.fragment_about_spt;
    }

    @Override
    protected void initView ( ) {
        title = findView ( R.id.title );
        stv_product_info = findView ( R.id.stv_product_info );
        stv_company_introduce = findView ( R.id.stv_company_introduce );
        stv_contact_customer = findView ( R.id.stv_contact_customer );

        stv_product_info.setOnClickListener ( this );
        stv_company_introduce.setOnClickListener ( this );
        stv_contact_customer.setOnClickListener ( this );
        title.setLeftViewClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                mActivity.popBack ( );
            }
        } );
    }

    @Override
    public void onClick ( View v ) {
        switch ( v.getId ( ) ) {
            case R.id.stv_product_info:
                IntentUtils.startActivity ( mActivity, WebViewActivity.class, "titleString", "商品信息" );
                break;
            case R.id.stv_company_introduce:
                IntentUtils.startActivity ( mActivity, WebViewActivity.class, "titleString", "关于我们" );
                break;
            case R.id.stv_contact_customer:
                CustomDialog.Builder builder = new CustomDialog.Builder ( mActivity, "联系客服", "客服QQ：3061427009\n客服热线：021-32553868" );
                builder.setNegativeButton ( "关闭", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick ( DialogInterface dialog, int which ) {
                        dialog.dismiss ( );
                    }
                } );
                builder.create ( ).show ( );
                break;
        }
    }
}
