package com.totrade.spt.mobile.ui.focusproduct;

import android.os.Bundle;
import android.view.KeyEvent;

import com.autrade.spt.common.entity.TblNegoFocusMasterEntity;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.R;

/**
*
* 我关注的商品
* @author huangxy
* @date 2017/1/22
*
*/
public class FocusProductActivity extends SptMobileActivityBase {
//    private FocusListFragment focusListFragment;
    private FocusUpdateFragment focusUpdateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        focusUpdateFragment = new FocusUpdateFragment();
        switchUpdate(null);
        /*      TODO  修改我的关注用到
        String productType = getIntent().getStringExtra(this.getClass().getName());
        if (productType != null) {
            TblNegoFocusMasterEntity entity = new TblNegoFocusMasterEntity();
            entity.setProductType(productType);
            switchUpdate(entity);
        } else {
            finish();
        }
        */
    }

    /**
     * 修改我的关注
     */
    public void switchUpdate(TblNegoFocusMasterEntity entity) {
        focusUpdateFragment.setTblNegoFocusMasterEntity(entity);
        switchContent(focusUpdateFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        focusListFragment.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }
}
