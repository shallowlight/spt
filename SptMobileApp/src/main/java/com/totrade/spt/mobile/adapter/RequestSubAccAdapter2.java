package com.totrade.spt.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.autrade.spt.bank.service.inf.IAccountCreateAndBindService;
import com.autrade.spt.master.dto.CompanyInviteInfo;
import com.autrade.spt.master.dto.UserCompanyInviteUpEntity;
import com.autrade.spt.master.entity.UserSubAcctUpEntity;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.ItemHelpter;
import com.totrade.spt.mobile.view.customize.KeyTextView;
import com.totrade.spt.mobile.view.customize.SwipeLayout;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy on 2017/5/5.
 */

public class RequestSubAccAdapter2 extends RecyclerAdapterBase<CompanyInviteInfo, RequestSubAccAdapter2.ViewHolder> implements ItemHelpter.Callback {

    private RecyclerView recyclerView;

    public RequestSubAccAdapter2(List<CompanyInviteInfo> list) {
        super(list);
    }

    @Override
    public ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public SwipeLayout getSwipLayout(float x, float y) {
        try {
            return (SwipeLayout) recyclerView.findChildViewUnder(x, y);
        }catch (Exception e){
            //当列表没有数据时，EmptyView跟目录是LinearLayout,会报异常
            return null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        if (null == context){
            context = recyclerView.getContext();
        }
        recyclerView.addOnItemTouchListener(new ItemHelpter(context, this));
    }

    class ViewHolder extends ViewHolderBase<CompanyInviteInfo> {
        @BindViewId(R.id.btn_delete)
        TextView btn_delete;
        @BindViewId(R.id.civ_user_img)
        CircleImageView civ_user_img;
        @BindViewId(R.id.tv_user_name)
        TextView tv_user_name;
        @BindViewId(R.id.ktv_mobile_phone)
        KeyTextView ktv_mobile_phone;
        @BindViewId(R.id.tv_agree)
        TextView tv_agree;

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_member_request);
        }

        @Override
        public void initItemData() {
            loadNIMUserInfo(itemObj, civ_user_img);
            tv_user_name.setText(itemObj.getRealName());
            ktv_mobile_phone.setKey("手机号码").setValue(itemObj.getMobileNumber());
            tv_agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateFreeAccount(LoginUserContext.getLoginUserDto ().getCompanyTag (),itemObj.getUserId (),"1");
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateFreeAccount(LoginUserContext.getLoginUserDto ().getCompanyTag (),itemObj.getUserId (),"0");
                }
            });
        }

        /**
         * 加载头像
         */
        private void loadNIMUserInfo(CompanyInviteInfo itemObj, final CircleImageView civ_user_img) {
            List<String> accounts = new ArrayList<>();
            accounts.add(itemObj.getUserId());
            InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts);
            future.setCallback(new FutureRequestCallback<List<NimUserInfo>>() {
                @Override
                public void onSuccess(List<NimUserInfo> param) {
                    super.onSuccess(param);
                    if (param != null && param.size() > 0) {
                        NimUserInfo nimUserInfo = param.get(0);
                        String url = nimUserInfo.getAvatar();

                        if (!TextUtils.isEmpty(url)) {
                            Picasso.with(context)
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
        public void updateFreeAccount(final String companyTag,final String userid, final String optionTag) {
            SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
                @Override
                public Boolean requestService() throws DBException, ApplicationException {
                    UserCompanyInviteUpEntity upEntity = new UserCompanyInviteUpEntity();
                    upEntity.setCompanyTag(companyTag);
                    upEntity.setUserId(userid );
                    upEntity.setOptionTag(optionTag);
                    //更新自由账户信息
                    Client.getService(IUserService.class).updateFreeAccount(upEntity);
                    // step1 把一个用户绑定到企业的资金账户上
                    boolean b = Client.getService(IAccountCreateAndBindService.class).bindUserToCompanyAccount(userid, itemObj.getCompanyTag ());
                    if (b) {
                        // step2 更改银行账户绑定状态
                        UserSubAcctUpEntity userSubUpEntity = new UserSubAcctUpEntity();
                        userSubUpEntity.setUserId(userid);
                        Client.getService(IUserService.class).updateBankAccountBinded(userSubUpEntity);
                    }
                    return Boolean.TRUE;
                }

                @Override
                public void onDataSuccessfully(Boolean obj) {
                    if (null != obj && obj) {
                        Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
