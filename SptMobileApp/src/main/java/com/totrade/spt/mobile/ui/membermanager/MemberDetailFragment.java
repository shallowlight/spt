package com.totrade.spt.mobile.ui.membermanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.autrade.spt.bank.service.inf.IAccountCreateAndBindService;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.master.dto.CompanyInviteInfo;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.dto.UserCompanyInviteUpEntity;
import com.autrade.spt.master.entity.UserAccountEntity;
import com.autrade.spt.master.entity.UserSubAcctUpEntity;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.bean.CodeItem;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.KeyTextView;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员管理详情页
 * Created by Timothy on 2017/4/24.
 */

public class MemberDetailFragment extends BaseSptFragment<MemberActivity> implements View.OnClickListener {

    private ComTitleBar title;
    private CircleImageView civ_user_img;
    private TextView tv_user_name;
    private KeyTextView ktv_mobile_phone;
    private SuperTextView stv_ID_card_number;
    private SuperTextView stv_email;
    private SuperTextView stv_permission;
    private TextView tv_agree;
    private TextView tv_refuse;

    private UserAccountEntity entity;//子账户列表实体
    private CompanyInviteInfo comEntity;//子账户申请实体

    private String type;

    public void setComEntity(CompanyInviteInfo comEntity) {
        this.comEntity = comEntity;
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_member_detail;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        civ_user_img = findView(R.id.civ_user_img);
        tv_user_name = findView(R.id.tv_user_name);
        ktv_mobile_phone = findView(R.id.ktv_mobile_phone);
        stv_ID_card_number = findView(R.id.stv_ID_card_number);
        stv_email = findView(R.id.stv_email);
        stv_permission = findView(R.id.stv_permission);
        tv_agree = findView(R.id.tv_agree);
        tv_refuse = findView(R.id.tv_refuse);

        initData();

        if (type.equals("sub_list")) {
            stv_permission.setVisibility(View.VISIBLE);
            tv_refuse.setVisibility(View.GONE);
            tv_agree.setText("解除绑定");
        } else if (type.equals("req_list")) {
            stv_permission.setVisibility(View.GONE);
            tv_refuse.setVisibility(View.VISIBLE);
            tv_agree.setText("同意");
        }

        stv_permission.setOnClickListener(this);
        tv_agree.setOnClickListener(this);
        tv_refuse.setOnClickListener(this);
        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.popBack();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;
        if (resultCode == Common.RESULT_CODE_0X0001) {
            String entity = data.getStringExtra(AccountManager.KEY_ENTITY_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT);
            if (!ObjUtils.isEmpty(entity)) {
                this.entity = JSON.parseObject(entity, UserAccountEntity.class);
            }
        }
        if (this.entity != null) {
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stv_permission:
                //Descripe:非自由人账户即为企业账户
                if (!entity.getUserRole().equals(SptConstant.CONFIGGROUPID_TAG_FREEMAN)) {
                    Intent intent = new Intent(mActivity, MemberPermissionActivity.class);
                    intent.putExtra(ActivityConstant.AccountManager.KEY_ENTITY_ACCOUNT_MANAGER_TO_ADD_SUBACCOUNT, entity.toString());
                    intent.putExtra(AccountManager.KEY_ACC_POWER_FROM_FRAGMENT, MemberDetailFragment.class.getSimpleName());
                    startActivityForResult(intent, Common.REQUEST_CODE_0X0001);
                }
                break;
            case R.id.tv_agree:
                if (type.equals("sub_list")) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(mActivity, "您确定与" + tv_user_name.getText().toString() + "解除一切关系吗?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            unBindSubAccount();
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else if (type.equals("req_list")) {
                    updateFreeAccount("1");
                }
                break;
            case R.id.tv_refuse:
                updateFreeAccount("0");
                break;
        }
    }

    /**
     * 解除子账户绑定
     */
    private void unBindSubAccount() {
        SubAsyncTask.create().setOnDataListener(mActivity, true, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IUserService.class).unBindSubAccount(LoginUserContext.getLoginUserId(), entity.getCompanyTag(), entity.getUserId());
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    Toast.makeText(mActivity, "解绑成功", Toast.LENGTH_SHORT).show();
                    mActivity.popBack();
                }
            }
        });
    }

    private void initData() {
        if (entity != null && type.equals("sub_list")) {
            loadNIMUserInfo(entity.getImUserAccid());
            tv_user_name.setText(entity.getUserName());
            ktv_mobile_phone.setKey("手机号码").setValue(entity.getMobileNumber());
            stv_ID_card_number.setRightString(entity.getIdNumber());
            stv_email.setRightString(entity.getEmail());
            stv_permission.setRightString(formatConfigGroupId(entity.getConfigGroupId()));
        } else if (type.equals("req_list")) {
            loadNIMUserInfo(comEntity.getUserId());
            tv_user_name.setText(comEntity.getRealName());
            ktv_mobile_phone.setKey("手机号码").setValue(comEntity.getMobileNumber());
            stv_ID_card_number.setRightString(comEntity.getIdNumber());
            stv_email.setRightString(comEntity.getEmail());
        }
    }

    /**
     * 用户身份tag转中文
     */
    private String formatConfigGroupId(String configid) {
        if (!StringUtility.isNullOrEmpty(entity.getConfigGroupId())) {
            for (CodeItem codeItem : Dictionary.MASTER_USER_DENTITY) {
                if (codeItem.getCode().equals(configid)) {
                    return Dictionary.CodeToKey(Dictionary.MASTER_USER_DENTITY, entity.getConfigGroupId());
                }
            }
        }
        return null;
    }


    public void setEntity(UserAccountEntity entity) {
        this.entity = entity;
    }

    /**
     * 加载头像
     */
    private void loadNIMUserInfo(String imUserId) {
        List<String> accounts = new ArrayList<>();
        accounts.add(imUserId);
        InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts);
        future.setCallback(new FutureRequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                super.onSuccess(param);
                if (param != null && param.size() > 0) {
                    NimUserInfo nimUserInfo = param.get(0);
                    String url = nimUserInfo.getAvatar();
                    String name = nimUserInfo.getName();

                    if (!TextUtils.isEmpty(url)) {
                        Picasso.with(mActivity)
                                .load(url)
                                .placeholder(R.drawable.main_home_portrait)
                                .error(R.drawable.main_home_portrait)
                                .into(civ_user_img);
                    }
                }
            }
        });
    }

    /**
     * 更新自由人账户
     * 1:表示同意 0标识拒绝
     */
    private void updateFreeAccount(final String optionTag) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                LoginUserDto dto = LoginUserContext.getLoginUserDto();
                UserCompanyInviteUpEntity upEntity = new UserCompanyInviteUpEntity();
                upEntity.setCompanyTag(dto.getCompanyTag());//管理员（自己）的companyTag
                upEntity.setUserId(comEntity.getUserId());//申请人用户id
                upEntity.setOptionTag(optionTag);//表示同意/拒绝的tag
                //更新自由账户信息
                Client.getService(IUserService.class).updateFreeAccount(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    bindUserToCompanyAccount();
                }
            }
        });
    }

    /**
     * 把一个用户绑定到企业的资金账户上
     */
    private void bindUserToCompanyAccount() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                //申请人的UserId,申请人的CompanyTag
                Client.getService(IAccountCreateAndBindService.class).bindUserToCompanyAccount(comEntity.getUserId(), comEntity.getCompanyTag());
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    updateBankAccountBinded();
                }
            }
        });
    }

    /**
     * 更新银行绑定状态
     */
    private void updateBankAccountBinded() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                UserSubAcctUpEntity userSubUpEntity = new UserSubAcctUpEntity();
                userSubUpEntity.setUserId(comEntity.getUserId());//申请人的userId
                Client.getService(IUserService.class).updateBankAccountBinded(userSubUpEntity);
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("操作成功");
                    mActivity.popBack();
                }
            }
        });
    }

    public void setType(String type) {
        this.type = type;
    }
}
