package com.totrade.spt.mobile.ui.accountmanager;

import android.text.TextUtils;
import android.view.View;

import com.autrade.spt.master.constants.MasterConstant;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.service.inf.ILoginService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.base.BaseCameraFragment;
import com.totrade.spt.mobile.bean.Attatch;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.EncryptUtility;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.PicUtility;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CommonTextView;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户管理
 * Created by Timothy on 2017/4/10.
 */

public class AccountManagerFragment extends BaseCameraFragment<UserAccMngActivity> implements View.OnClickListener {

    private ComTitleBar title;
    private CircleImageView civ_user_img;
    private CommonTextView ctv_modify_password;
    private CommonTextView ctv_user_telphone;
    private SuperTextView stv_identity_card;
    private CommonTextView ctv_user_email;
    private SuperTextView stv_company_name;
    private SuperTextView stv_access_permission;
    private SuperTextView stv_user_name;

    private LoginUserDto mLoginUserDto;

    /**
     * 用户权限枚举
     */
    public enum USER_PERMISSION {

        T("交易账户"), F("资金账户"), D("物管账户");

        private String descripe;

        USER_PERMISSION(String descripe) {
            this.descripe = descripe;
        }

        private String getDescripe() {
            return descripe;
        }
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_account_manager;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        civ_user_img = findView(R.id.civ_user_img);
        stv_user_name = findView(R.id.stv_user_name);
        ctv_modify_password = findView(R.id.ctv_modify_password);
        ctv_user_telphone = findView(R.id.ctv_user_telphone);
        stv_identity_card = findView(R.id.stv_identity_card);
        ctv_user_email = findView(R.id.ctv_user_email);
        stv_company_name = findView(R.id.stv_company_name);
        stv_access_permission = findView(R.id.stv_access_permission);

        civ_user_img.setOnClickListener(this);
        ctv_modify_password.setOnClickListener(this);
        ctv_user_email.setOnClickListener(this);
        ctv_user_telphone.setOnClickListener(this);
        stv_identity_card.setOnClickListener ( this );
        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfoFromIM();
        queryUserAcctDetailByUserName();
    }

    /**
     * 从云信载入用户信息
     */
    private void getUserInfoFromIM() {
        List<String> accounts = new ArrayList<>();
        accounts.add(LoginUserContext.getLoginUserDto().getImUserAccid());
        InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts); // 请求服务器
        future.setCallback(new FutureRequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                super.onSuccess(param);
                if (param != null && param.size() > 0) {
                    String url = param.get(0).getAvatar();
                    if (!TextUtils.isEmpty(url)) {
                        loadViewFromIM(param);
                    }
                }
            }
        });
    }

    /**
     * 根据用户名获取用户详情信息
     */
    private void queryUserAcctDetailByUserName() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<LoginUserDto>() {

            @Override
            public LoginUserDto requestService() throws DBException, ApplicationException {
                String userName = EncryptUtility.decrypt(SharePreferenceUtility.spGetOut(mActivity, SharePreferenceUtility.USERNAME, ""));
                return Client.getService(ILoginService.class).getUserAcctDetailByUserName(userName);
            }

            @Override
            public void onDataSuccessfully(LoginUserDto obj) {
                if (null != obj) {
                    loadViewFromWeb(obj);
                }
            }
        });
    }

    private void loadViewFromWeb(LoginUserDto obj) {
        mLoginUserDto = obj;
        stv_user_name.setRightString(StringUtility.isNullOrEmpty(obj.getRealName()) ? obj.getUserName(): obj.getRealName());
        ctv_user_telphone.setRightTextString(obj.getPhoneNumber());
        stv_company_name.setRightString(obj.getCompanyName());
        stv_identity_card.setRightString(obj.getIdNumber());//身份证号码
//        是否待激活邮箱:A:已激活，U-未激活，W-等待激活
        if (null != obj.getActivateStatus()) {
            if (obj.getActivateStatus().equals(MasterConstant.ACTIVE_MAILBOX_TYPE.A.name())) {
                ctv_user_email.setRightTextString(obj.getEmail());
                ctv_user_email.setRightTextColor(getResources().getColor(R.color.black_txt_34));
            } else if (obj.getActivateStatus().equals(MasterConstant.ACTIVE_MAILBOX_TYPE.U.name())) {
                ctv_user_email.setRightTextString(MasterConstant.ACTIVE_MAILBOX_TYPE.U.value);
                ctv_user_email.setRightTextColor(getResources().getColor(R.color.red_txt_d56));
            } else if (obj.getActivateStatus().equals(MasterConstant.ACTIVE_MAILBOX_TYPE.W.name())) {
                ctv_user_email.setRightTextString(MasterConstant.ACTIVE_MAILBOX_TYPE.W.value);
                ctv_user_email.setRightTextColor(getResources().getColor(R.color.red_txt_d56));
            }
        }

        StringBuffer buffer = new StringBuffer();
        if (ObjUtils.isEmpty(obj.getConfigGroupId())) return;
        if (obj.getConfigGroupId().contains(USER_PERMISSION.D.name())) {
            buffer.append("|" + USER_PERMISSION.D.getDescripe());
        } else if (obj.getConfigGroupId().contains(USER_PERMISSION.F.name())) {
            buffer.append("|" + USER_PERMISSION.F.getDescripe());
        } else if (obj.getConfigGroupId().contains(USER_PERMISSION.T.name())) {
            buffer.append("|" + USER_PERMISSION.T.getDescripe());
        }
        if (buffer.length() > 3) {
            stv_access_permission.setRightString(buffer.deleteCharAt(0).toString());
        }
    }

    private void loadViewFromIM(List<NimUserInfo> params) {
        Picasso.with(mActivity).load(params.get(0).getAvatar()).placeholder(R.drawable.img_headpic_def).error(R.drawable.img_headpic_def).into(civ_user_img);
    }


    @Override
    public void getCameraData(Attatch t) {
        //载入从相册上传的图像,从本地获取
        if (!ObjUtils.isEmpty(t.getFilePath())) {
            civ_user_img.setImageBitmap(PicUtility.getimage(t.getFilePath()));
        } else {
            Picasso.with(mActivity).load(t.getFilePath()).placeholder(R.drawable.img_headpic_def).error(R.drawable.img_headpic_def).into(civ_user_img);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_user_img:
                showCameraPopWindow(false, true);
                break;
            case R.id.ctv_user_email:
                mActivity.switchMEF();
                break;
            case R.id.ctv_modify_password:
                mActivity.switchMPF();
                break;
            case R.id.ctv_user_telphone:
                mActivity.switchMTPF();
                break;
            case R.id.stv_identity_card:
                if ( StringUtility.isNullOrEmpty ( mLoginUserDto.getIdNumber () )){
                    mActivity.switchMDIDC ();
                }
                break;
        }
    }
}
