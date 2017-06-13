package com.totrade.spt.mobile.ui.accountmanager;

import android.view.View;
import android.widget.Button;

import com.autrade.spt.master.entity.TblUserInfoMasterEntity;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
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
 * 更换邮箱
 * Created by Timothy on 2017/4/21.
 */

public class ModifyEmailFragment extends BaseSptFragment<UserAccMngActivity> {

    private ComTitleBar title;
    private ValidatorEditText vet_email;
    private Button btn_send_email;
    private String email;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_modify_email;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        vet_email = findView(R.id.vet_email);
        btn_send_email = findView(R.id.btn_send_email);
        vet_email.setSaveEnabled(false);
        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });
        btn_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) saveUserInfo();
            }
        });
    }

    private boolean checkData() {
        email = vet_email.getText().toString().trim();
        if (!vet_email.checkBody(ValidatorEditText.ValidatorType.EMAIL)) {
            ToastHelper.showMessage(ValidatorEditText.ValidatorType.EMAIL.getMsg());
            return false;
        }
        if (!StringUtility.isNullOrEmpty(LoginUserContext.getLoginUserDto().getEmail())){
            if (LoginUserContext.getLoginUserDto().getEmail().equals(email)){
                ToastHelper.showMessage("此邮箱号码已在使用");
                return false;
            }
        }
        return true;
    }

    /**
     * 保存手机号
     */
    private void saveUserInfo() {
        SubAsyncTask.create().setOnDataListener(mActivity, false, new OnDataListener() {
            @Override
            public Object requestService() throws DBException, ApplicationException {

                TblUserInfoMasterEntity upEntity = new TblUserInfoMasterEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setEmail(email);
                Client.getService(IUserService.class).updateUserInfoDetail(upEntity);
                return true;
            }

            @Override
            public void onDataSuccessfully(Object obj) {
                if (null != obj) {
                    ToastHelper.showMessage("操作成功");
                    mActivity.popBack();
                }else{
                    ToastHelper.showMessage("操作失败");
                }
            }
        });
    }
}
