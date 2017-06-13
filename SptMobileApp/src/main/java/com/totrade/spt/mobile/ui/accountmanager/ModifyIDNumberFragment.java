package com.totrade.spt.mobile.ui.accountmanager;

import android.view.View;
import android.widget.Button;

import com.autrade.spt.master.entity.TblUserInfoMasterEntity;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.ValidatorEditText;
import com.totrade.spt.mobile.base.BaseSptFragment;

/**
 * 账户管理
 * 设置身份证号码
 * Created by Timothy on 2017/5/10.
 */

public class ModifyIDNumberFragment extends BaseSptFragment<UserAccMngActivity> implements View.OnClickListener {

    private ComTitleBar title;
    private ValidatorEditText vet_id_number;
    private Button btn_confirm;
    private String idNumber;

    @Override
    public int setFragmentLayoutId ( ) {
        return R.layout.fragment_modify_id_number;
    }

    @Override
    protected void initView ( ) {
        title = findView ( R.id.title );
        vet_id_number = findView ( R.id.vet_id_number );
        btn_confirm = findView ( R.id.btn_confirm );

        btn_confirm.setOnClickListener ( this );
        title.setLeftViewClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                mActivity.popBack ( );
            }
        } );
    }

    @Override
    public void onClick ( View v ) {
        if ( v.getId ( ) == R.id.btn_confirm ) {
            if ( checkData ( ) ) {
                saveUserInfo ( );
            }
        }
    }

    private boolean checkData ( ) {
        idNumber = vet_id_number.getText ( ).toString ( ).trim ( );
        if ( ! vet_id_number.checkBody ( ValidatorEditText.ValidatorType.ID_CARD ) ) {
            showMessage ( ValidatorEditText.ValidatorType.ID_CARD.getMsg ( ) );
            return false;
        }
        return true;
    }

    /**
     * 保存手机号
     */
    private void saveUserInfo ( ) {
        SubAsyncTask.create ( ).setOnDataListener ( mActivity, false, new OnDataListener ( ) {
            @Override
            public Object requestService ( ) throws DBException, ApplicationException {
                TblUserInfoMasterEntity upEntity = new TblUserInfoMasterEntity ( );
                upEntity.setUserId ( LoginUserContext.getLoginUserDto ( ).getUserId ( ) );
                upEntity.setIdNumber ( idNumber );
                Client.getService ( IUserService.class ).updateUserInfoDetail ( upEntity );
                return true;
            }

            @Override
            public void onDataSuccessfully ( Object obj ) {
                if ( null != obj ) {
                    ToastHelper.showMessage ( "身份证号保存成功" );
                    mActivity.popBack ( );
                }
            }
        } );
    }
}
