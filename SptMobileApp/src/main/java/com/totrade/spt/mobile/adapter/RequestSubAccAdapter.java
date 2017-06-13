package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autrade.spt.bank.service.inf.IAccountCreateAndBindService;
import com.autrade.spt.master.dto.CompanyInviteInfo;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.dto.UserCompanyInviteUpEntity;
import com.autrade.spt.master.entity.UserCompanyInviteDownEntity;
import com.autrade.spt.master.entity.UserSubAcctUpEntity;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.KeyTextView;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy on 2017/4/26.
 */

public class RequestSubAccAdapter extends SwipeAdapter<CompanyInviteInfo> {


    public RequestSubAccAdapter(Context context, List<CompanyInviteInfo> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_request,parent,false);
            holder.civ_user_img = (CircleImageView) convertView.findViewById(R.id.civ_user_img);
            holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.ktv_mobile_phone = (KeyTextView) convertView.findViewById(R.id.ktv_mobile_phone);
            holder.tv_agree = (TextView) convertView.findViewById(R.id.tv_agree);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CompanyInviteInfo itemObj = (CompanyInviteInfo) getItem(position);
        loadNIMUserInfo(itemObj, holder.civ_user_img);
        holder.tv_user_name.setText(itemObj.getRealName());
        holder.ktv_mobile_phone.setKey("手机号码").setValue(itemObj.getMobileNumber());
        holder.tv_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFreeAccount(itemObj.getCompanyTag(),"1");
            }
        });
        return convertView;
    }

    public class ViewHolder {
        CircleImageView civ_user_img;
        TextView tv_user_name;
        KeyTextView ktv_mobile_phone;
        TextView tv_agree;
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
    public void updateFreeAccount(final String companyTag, final String optionTag) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                LoginUserDto dto = LoginUserContext.getLoginUserDto();
                UserCompanyInviteUpEntity upEntity = new UserCompanyInviteUpEntity();
                upEntity.setCompanyTag(companyTag);
                upEntity.setUserId(dto.getUserId());
                upEntity.setOptionTag(optionTag);
                //更新自由账户信息
                Client.getService(IUserService.class).updateFreeAccount(upEntity);
                // step1 把一个用户绑定到企业的资金账户上
                boolean b = Client.getService(IAccountCreateAndBindService.class).bindUserToCompanyAccount(dto.getUserId(), companyTag);
                if (b) {
                    // step2 更改银行账户绑定状态
                    UserSubAcctUpEntity userSubUpEntity = new UserSubAcctUpEntity();
                    userSubUpEntity.setUserId(dto.getUserId());
                    userSubUpEntity.setCompanyTag(companyTag);
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
