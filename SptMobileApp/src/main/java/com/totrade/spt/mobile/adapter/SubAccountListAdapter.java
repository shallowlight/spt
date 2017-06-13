package com.totrade.spt.mobile.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.master.entity.UserAccountEntity;
import com.autrade.stage.utility.StringUtility;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.bean.CodeItem;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.KeyTextView;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy on 2017/4/24.
 */

public class SubAccountListAdapter extends RecyclerAdapterBase <UserAccountEntity, SubAccountListAdapter.ViewHolder> {

    public SubAccountListAdapter ( List <UserAccountEntity> list ) {
        super ( list );
    }

    @Override
    public SubAccountListAdapter.ViewHolder createViewHolderUseData ( ViewGroup parent, int viewType ) {
        return new ViewHolder ( LayoutInflater.from ( context ).inflate ( R.layout.item_member_list, parent, false ) );
    }

    class ViewHolder extends ViewHolderBase <UserAccountEntity> {

        @BindViewId(R.id.civ_user_img)
        private CircleImageView civ_user_img;
        @BindViewId(R.id.tv_user_name)
        private TextView tv_user_name;
        @BindViewId(R.id.tv_user_identity)
        private TextView tv_user_identity;
        @BindViewId(R.id.ktv_mobile_phone)
        private KeyTextView ktv_mobile_phone;

        public ViewHolder ( View view ) {
            super ( view );
        }

        @Override
        public void initItemData ( ) {
            loadNIMUserInfo ( );
            tv_user_name.setText ( itemObj.getUserName ( ) );
            tv_user_identity.setText ( formatConfigGroupId ( itemObj, itemObj.getConfigGroupId ( ) ) );
            ktv_mobile_phone.setKey ( "手机号码" ).setValue ( itemObj.getMobileNumber ( ) );
        }

        /**
         * 用户身份tag转中文
         */
        private String formatConfigGroupId ( UserAccountEntity entity, String configid ) {
            if ( ! StringUtility.isNullOrEmpty ( entity.getConfigGroupId ( ) )) {
                if ( configid.equals ( SptConstant.CONFIGGROUPID_TAG_TRADE )||
                        configid.equals ( SptConstant.CONFIGGROUPID_TAG_FUND )||
                        configid.equals ( SptConstant.CONFIGGROUPID_TAG_DELIVE )){
                    return Dictionary.CodeToKey ( Dictionary.MASTER_USER_DENTITY, entity.getConfigGroupId ( ) );
                }else{
                    return "无权限";
                }
            }
            return "无权限";
        }

        /**
         * 加载头像
         */
        private void loadNIMUserInfo ( ) {
            List <String> accounts = new ArrayList <> ( );
            accounts.add ( itemObj.getImUserAccid ( ) );
            InvocationFuture <List <NimUserInfo>> future = NIMClient.getService ( UserService.class ).fetchUserInfo ( accounts );
            future.setCallback ( new FutureRequestCallback <List <NimUserInfo>> ( ) {
                @Override
                public void onSuccess ( List <NimUserInfo> param ) {
                    super.onSuccess ( param );
                    if ( param != null && param.size ( ) > 0 ) {
                        NimUserInfo nimUserInfo = param.get ( 0 );
                        String url = nimUserInfo.getAvatar ( );
                        String name = nimUserInfo.getName ( );

                        if ( ! TextUtils.isEmpty ( url ) ) {
                            Picasso.with ( context )
                                    .load ( url )
                                    .placeholder ( R.drawable.main_home_portrait )
                                    .error ( R.drawable.main_home_portrait )
                                    .into ( civ_user_img );
                        }
                    }
                }
            } );
        }
    }
}
