package com.totrade.spt.mobile.ui.accountmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.totrade.spt.mobile.app.SystemUtil;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.FileUtils;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.Temp;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * 账户管理
 * 设置
 * Created by Timothy on 2017/4/7.
 */

public class SetFragment extends BaseSptFragment<UserAccMngActivity> implements View.OnClickListener{

    private ComTitleBar title;
    private SuperTextView stv_account_set;
    private SuperTextView stv_message_set;
    private SuperTextView stv_site_rule;
    private SuperTextView stv_use_help;
    private SuperTextView stv_version_info;
    private SuperTextView stv_abount_spt;
    private SuperTextView stv_clear_cache;
    private TextView tv_exit;

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_set;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        stv_account_set = findView(R.id.stv_account_set);
        stv_message_set = findView(R.id.stv_message_set);
        stv_site_rule = findView(R.id.stv_site_rule);
        stv_use_help = findView(R.id.stv_use_help);
        stv_version_info = findView(R.id.stv_version_info);
        stv_abount_spt = findView(R.id.stv_abount_spt);
        stv_clear_cache = findView(R.id.stv_clear_cache);
        tv_exit = findView(R.id.tv_exit);
        setListener(stv_abount_spt,stv_account_set,stv_message_set,stv_site_rule,stv_use_help,stv_version_info,stv_abount_spt,stv_clear_cache);
        tv_exit.setOnClickListener(this);

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        initData();
    }

    private void initData() {
        stv_version_info.setRightString(SystemUtil.getVersion());
        stv_clear_cache.setRightString(getCacheSize());
    }

    private void setListener(SuperTextView... views){
        for (SuperTextView superTextView : views) {
            superTextView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.stv_account_set:
                mActivity.switchAMF();
                break;
            case R.id.stv_message_set:
                mActivity.switchMSF();
                break;
            case R.id.stv_site_rule:
                break;
            case R.id.stv_use_help:
                break;
            case R.id.stv_version_info:
                break;
            case R.id.stv_abount_spt:
                mActivity.switchASF();
                break;
            case R.id.stv_clear_cache:
                clearCache(FileUtils.getPhotoCacheRoot(), new File(NIMClient.getSdkStorageDirPath()));
                stv_clear_cache.setRightString(getCacheSize());
                break;
            case R.id.tv_exit:
                CustomDialog.Builder builder = new CustomDialog.Builder(mActivity, "确定退出当前登录吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.create().show();
                break;
        }
    }

    /**
     * 清除缓存
     *
     * @param files
     */
    private void clearCache(File... files) {
        for (File file : files) {
            FileUtils.deleteDir(file.getPath());
        }
        ToastHelper.showMessage("清除成功!");
    }

    /**
     * 取缓存大小
     * @return
     */
    private String getCacheSize(){
        return FileUtils.getCacheSize(FileUtils.getPhotoCacheRoot(), new File(NIMClient.getSdkStorageDirPath()));
    }

    /**
     * 注销
     */
    private void logout() {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", "");
        editor.putString("passWord", "");
        editor.putString("userId", "");
        editor.putString(SharePreferenceUtility.COMPANYTAG, "");
        editor.apply();
        ProductTypeUtility.setLstUserFocusDto(null);
        LoginUserContext.setLoginUserDto(LoginUserContext.getAnonymousDto());
        ProductTypeUtility.setOpenFocus(false);

        JPushInterface.setAliasAndTags(mActivity, LoginUserContext.getLoginUserId(), null);
        NIMClient.getService(AuthService.class).logout();
        NotifyUtility.cancelAll(mActivity);

        Temp.i().put(SetFragment.class, AppConstant.ACTION_TYPE_LOGOUT);
        mActivity.finish();
    }

}
