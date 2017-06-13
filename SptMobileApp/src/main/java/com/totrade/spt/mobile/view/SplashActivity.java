package com.totrade.spt.mobile.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.TblSptDictionaryEntity;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.spt.master.entity.GetProductTypeUpEntity;
import com.autrade.spt.master.entity.TblErrorCodeMasterEntity;
import com.autrade.spt.master.service.inf.IDictionaryService;
import com.autrade.spt.master.service.inf.IErrorCodeService;
import com.autrade.spt.master.service.inf.ILoginService;
import com.autrade.spt.master.service.inf.IProductTypeService;
import com.autrade.spt.report.entity.IMUserDownEntity;
import com.autrade.spt.report.service.inf.IIMUserService;
import com.autrade.spt.zone.dto.QueryPageZoneFocusUpEntity;
import com.autrade.spt.zone.entity.TblZoneRequestFocusEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestFocusService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.totrade.spt.mobile.adapter.SplashPagerAdapter;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.bean.Address;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.FocusContext;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.helper.UpdateManager;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.utility.DictionaryTools;
import com.totrade.spt.mobile.utility.EncryptUtility;
import com.totrade.spt.mobile.utility.ErrorCodeUtility;
import com.totrade.spt.mobile.utility.HttpURLConnectionUtility;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.im.ChatIMActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class SplashActivity extends SptMobileActivityBase {
    private TextView tvTrynow;
    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_splash);

        getLastIPCfg(); // 获取最新Ip地址
    }

    private void checkUpdate() {
        UpdateManager updateManager = new UpdateManager(this);
        updateManager.setUpdateListener(new UpdateManager.UpdateListener() {
            @Override
            public void update(Boolean b) {
//                不需要更新,开始加载数据
                if (null == b) downloadConfigTask();
//                需要更新,用户点击取消
                if (Boolean.FALSE == b) finish();
            }
        });
        updateManager.checkUpdate();
    }

    private void getLastIPCfg() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                try {
                    return HttpURLConnectionUtility.HttpURLConnectionRequest(AppConstant.ALIYUN, null, false);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onDataSuccessfully(String json) {
                if (!StringUtility.isNullOrEmpty(json)) {
                    Map<String, Address> addressMap = new Gson().fromJson(json, new TypeToken<Map<String, Address>>() {
                    }.getType());
                    Address androiAddress = addressMap.get(Address.android);
                    Address iosAddress = addressMap.get(Address.ios);
                    Client.setAddress(androiAddress);
                    //获取ios的配置以便推送中使用
                    SharePreferenceUtility.spSaveIOSNetInfo(SplashActivity.this, iosAddress.getIP(), iosAddress.getPort());
                }

                checkUpdate();
                NotifyUtility.cancelAll(SplashActivity.this);
            }
        });
    }

    /**
     * 下载字典值，错误字，匿名登录(任意项失败则启动失败)，获取关注的产品
     */
    private void downloadConfigTask() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                boolean login = loginOnAsy();
                boolean productMatch = getAllProductTypeDto(SptConstant.BUSINESS_MATCH);
                boolean productZone = getAllProductTypeDto(SptConstant.BUSINESS_ZONE);
                if (!(login && productMatch && productZone)) // 匿名登录或者获取产品列表失败
                {
                    return false;
                }

                // 获取字典
                List<TblSptDictionaryEntity> lst = Client.getService(IDictionaryService.class).getDictionaryDiff(0);
                // 初始化字典工具
                DictionaryTools.i().initDict(lst);
                // 获取错误字列表
                List<TblErrorCodeMasterEntity> lst2 = Client.getService(IErrorCodeService.class).getAllErrorCodeList();
                // 初始化ErrorCode工具
                ErrorCodeUtility.init(lst2);
                // 获取关注产品(非必须)
                getUserFocus();

                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean b) {
                if (b != null && b) {
                    boolean firstTime = SharePreferenceUtility.spGetOut(SplashActivity.this, SharePreferenceUtility.IS_FIRST, true);

                    if (firstTime) {
                        initRollView();
                    } else {
                        if (LoginUserContext.isAnonymous()) {
                            toMain();
                        } else {
                            loginIM();
                        }
                    }
                } else {

                    CustomDialog.Builder builder = new CustomDialog.Builder(SplashActivity.this, "请检查网络或稍后再试");
                    builder.setNegativeButton("关闭", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog = builder.create();
                    dialog.setOnDismissListener(new OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }


    /**
     * 引导页轮播图
     */
    private void initRollView() {
        findViewById(R.id.SplashView).setVisibility(View.GONE);
        findViewById(R.id.SplashView2).setVisibility(View.VISIBLE);
        SharePreferenceUtility.spSave(this, SharePreferenceUtility.IS_ALLOW_PUSH_NOT_SUPPLIERS, true); // 默认开启非供应商推送
        SharePreferenceUtility.spSave(this, SharePreferenceUtility.PUSH_ON_TIME, false); // 默认时段推送关闭
        SharePreferenceUtility.spSave(this, SharePreferenceUtility.IS_FIRST, false);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        tvTrynow = (TextView) findViewById(R.id.tv_trynow);
        LinearLayout llPoint = (LinearLayout) findViewById(R.id.llPoint);

        final Integer[] ids = new Integer[]{R.drawable.splash_1, R.drawable.splash_2, R.drawable.splash_3};
        final SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(ids, viewpager, llPoint);
        viewpager.setAdapter(splashPagerAdapter);
        splashPagerAdapter.selectPoint(0);
        splashPagerAdapter.startRoll();
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if ((ids.length - 1) == position) {
                    tvTrynow.setVisibility(View.VISIBLE);
                    splashPagerAdapter.stopRoll();
                } else {
                    tvTrynow.setVisibility(View.GONE);
                    splashPagerAdapter.startRoll();
                }

                splashPagerAdapter.selectPoint(position);
            }
        });

        tvTrynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });
    }

    /**
     * 登录,需要在AsyncTask中调用
     */
    private boolean loginOnAsy() throws DBException, ApplicationException {
//        try {
        String anonymousStr = SharePreferenceUtility.spGetOut(SplashActivity.this, "anonymousStr", "");
        LoginUserDto dto = null;
        if (!TextUtils.isEmpty(anonymousStr)) {
            try {
                dto = JsonUtility.toJavaObject(anonymousStr, new TypeToken<LoginUserDto>() {
                }.getType());
            } catch (Exception e) {
                //
            }
        }
        if (dto == null) {
            dto = Client.getService(ILoginService.class).login("anonymous", "anonymous");
            SharePreferenceUtility.spSave(this, "anonymousStr", JsonUtility.toJSONString(dto));
        }

        LoginUserContext.setLoginUserDto(dto);
        LoginUserContext.setAnonymousDto(dto);
        String userName = SharePreferenceUtility.spGetOut(SplashActivity.this, SharePreferenceUtility.USERNAME, "");
        String passWord = SharePreferenceUtility.spGetOut(SplashActivity.this, SharePreferenceUtility.PASSWORD, "");
        if (!StringUtility.isNullOrEmpty(userName) && !StringUtility.isNullOrEmpty(passWord)) {
            userName = EncryptUtility.decrypt(userName);
            passWord = EncryptUtility.decrypt(passWord);
            LoginUserDto dto2 = Client.getService(ILoginService.class).login(userName, passWord);
            SharePreferenceUtility.spSave(SplashActivity.this, SharePreferenceUtility.USERID, dto2.getUserId());
            SharePreferenceUtility.spSave(SplashActivity.this, SharePreferenceUtility.COMPANYTAG, dto2.getCompanyTag());

            LoginUserContext.setLoginUserDto(dto2);
        }
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }

        return LoginUserContext.getLoginUserDto() != null;
    }

    /**
     * 获取产品列表
     */
    private boolean getAllProductTypeDto(String businessIndicator) throws DBException, ApplicationException {
//        try {
        List<ProductTypeDto> listPDtos = Client.getService(IProductTypeService.class).getAllProductTypeDtoWithAccessright(formUpEntity(businessIndicator));
        List<ProductTypeDto> listEnd = new ArrayList<>(); // 存储遍历的所有元素
        List<ProductTypeDto> list2;
        List<ProductTypeDto> list3;
        listEnd.addAll(listPDtos);                                      //添加 level =0;
        for (ProductTypeDto topTypedto : listPDtos) // 迭代出商品名称列表
        {
            list2 = topTypedto.getChildNodes();
            listEnd.addAll(list2);                                      //添加 level =1;
            list3 = new ArrayList<>();
            for (ProductTypeDto midTypedto : list2) {
                list3.addAll(midTypedto.getChildNodes());
            }
            listEnd.addAll(sortProductDto(list3));                      //对3级小类排序,添加 level =2;
        }

        if (businessIndicator.equals(SptConstant.BUSINESS_MATCH)) {
            ProductTypeUtility.setListProductDto(listEnd);
        } else {
            ProductTypeUtility.setListZoneProduct(listEnd);
        }

//        } catch (Exception e) {
//            return false;
//        }

        return true;
    }

    // 获取用户关注产品
    private void getUserFocus() {
        try {
            // 更新用户关注
            if (!LoginUserContext.isAnonymous()) {
                QueryPageZoneFocusUpEntity upEntity = new QueryPageZoneFocusUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                List<TblZoneRequestFocusEntity> focusList = Client.getService(IZoneRequestFocusService.class).findZoneRequestFocusList(upEntity).getDataList();
                List<String> focusTypes = new ArrayList<>();
                for (TblZoneRequestFocusEntity entity : focusList) {
                    focusTypes.add(entity.getProductType());
                }
                setAliaAndTag(focusTypes);
                FocusContext.update(this, focusTypes);
            }
        } catch (Exception e) {
            //
        }
    }

    //设置标签与别名
    private void setAliaAndTag(List<String> group) {
        final HashSet<String> hashSet = new HashSet<>();
        // 标签
        for (String type : group) {
            hashSet.add(type);
        }
        // 设置别名 UserId
        String alias = LoginUserContext.getLoginUserDto().getUserId();
        JPushInterface.setAliasAndTags(this, alias, hashSet, new TagAliasCallback() {
            @Override
            public void gotResult(int responseCode, String alias, Set<String> tags) {
                // 0 表示调用成功。 其他返回码请参考错误码定义。
            }
        });

    }


    /**
     * 启动主页
     */
    private void toMain() {
        Intent intent = getIntent();
        if (intent == null) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent.setClass(this, HomeActivity.class);
        }
        String seeid = intent.getStringExtra(ChatIMActivity.EXTRA_SESSION_ID);
        if (!TextUtils.isEmpty(seeid)) {
            intent.putExtra(HomeActivity.EXTRA_NEST, ChatIMActivity.class.getName());
        }

        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {

            ArrayList<IMMessage> lst = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            if (lst != null && !lst.isEmpty()) {
                IMMessage mess = lst.get(0);
                String nick = null;
                if (mess.getSessionId().equals(mess.getFromAccount())) {
                    nick = mess.getFromNick();
                }
                intent.putExtra(ChatIMActivity.EXTRA_SESSION_ID, mess.getSessionId());
                intent.putExtra(ChatIMActivity.EXTRA_SESSION_NAME, nick);
                intent.putExtra(ChatIMActivity.EXTRA_SESSION_TYPE, mess.getSessionType().name());
                intent.putExtra(HomeActivity.EXTRA_NEST, ChatIMActivity.class.getName());
            }
        }
        startActivity(intent);
        finish();
    }

//	@Override
//	protected void onNewIntent(Intent intent)
//	{
//		super.onNewIntent(intent);
//		finish();
//	}

    /**
     * 启动主页 对产品列表排序
     */
    private List<ProductTypeDto> sortProductDto(List<ProductTypeDto> list) {
        Collections.sort(list, new Comparator<ProductTypeDto>() {
            @Override
            public int compare(ProductTypeDto lhs, ProductTypeDto rhs) {
                return lhs.getSortIndicator() - (rhs.getSortIndicator());
            }
        });
        return list;
    }

    private GetProductTypeUpEntity formUpEntity(String businessIndicator) {
        GetProductTypeUpEntity entity = new GetProductTypeUpEntity();
        entity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
        entity.setBusinessIndicator(businessIndicator);
        entity.setShowInvalid(false);
        return entity;
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private void loginIM() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<IMUserDownEntity>() {

            @Override
            public IMUserDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IIMUserService.class).findIMUserById(LoginUserContext.getLoginUserDto().getImUserAccid()); // 获取云信账户信息
            }

            @SuppressLint("DefaultLocale")
            @SuppressWarnings("unchecked")
            @Override
            public void onDataSuccessfully(IMUserDownEntity imUserDownEntity) {
                if (imUserDownEntity == null) {
                    toMain();
                    return;
                }

                if (!TextUtils.isEmpty(imUserDownEntity.getAccid()) && !TextUtils.isEmpty(imUserDownEntity.getToken())) {
                    LoginInfo info = new LoginInfo(imUserDownEntity.getAccid().toLowerCase(), imUserDownEntity.getToken().toLowerCase());
                    NIMClient.getService(AuthService.class).login(info).setCallback(new RequestCallbackWrapper<LoginInfo>() {

                        @Override
                        public void onResult(int arg0, LoginInfo arg1, Throwable arg2) {
                            toMain();
                        }
                    });
                } else {
                    toMain();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
