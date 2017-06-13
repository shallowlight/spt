package com.totrade.spt.mobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.dto.TongTongLoginUpEntity;
import com.autrade.spt.master.entity.QuerySupplierUpEntity;
import com.autrade.spt.master.entity.SupplierMasterDownEntity;
import com.autrade.spt.master.service.inf.ILoginService;
import com.autrade.spt.master.service.inf.ISupplierService;
import com.autrade.spt.report.entity.IMUserDownEntity;
import com.autrade.spt.report.service.inf.IIMUserService;
import com.autrade.spt.zone.dto.QueryPageZoneFocusUpEntity;
import com.autrade.spt.zone.entity.TblZoneRequestFocusEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestFocusService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.FocusContext;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.Company;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.utility.EncryptUtility;
import com.totrade.spt.mobile.utility.FileUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.StatusBarUtil;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.utility.lettersort.PinYinKit;
import com.totrade.spt.mobile.utility.lettersort.PinyinComparator;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登录页面
 *
 * @author huangxy
 * @date 2017/4/7
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private ImageView iv_clear;
    private ImageView iv_pwd_visible;
    private EditText et_account;
    private EditText et_pwd;
    private TextView tv_forget_pwd;
    private TextView tv_login;
    private TextView tv_regist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        StatusBarUtil.setImgTransparent(this);
        setContentView(R.layout.activity_user_login);

        initView();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        iv_pwd_visible = (ImageView) findViewById(R.id.iv_pwd_visible);
        et_account = (EditText) findViewById(R.id.et_account);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_regist = (TextView) findViewById(R.id.tv_regist);

        iv_pwd_visible.setSelected(true);
        setOnclick(iv_back, iv_clear, iv_pwd_visible, tv_forget_pwd, tv_login, tv_regist);
    }

    private void setOnclick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_clear:
                et_account.setText(null);
                break;
            case R.id.iv_pwd_visible:
                if (iv_pwd_visible.isSelected()) {
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_pwd.setSelection(et_pwd.getText().length());
                } else {
                    et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_pwd.setSelection(et_pwd.getText().length());
                }
                iv_pwd_visible.setSelected(!iv_pwd_visible.isSelected());
                break;
            case R.id.tv_forget_pwd:
                startActivity(new Intent(LoginActivity.this, ForgetPwdActivity.class));
                break;
            case R.id.tv_login:
                if (!validation()) {
                    return;
                }
                login(et_account.getText().toString(), et_pwd.getText().toString());
                break;
            case R.id.tv_regist:
                startActivity(new Intent(this, RegistActivity.class));
                break;
            default:
                break;
        }
    }

    private String[] hints = new String[]{"手机号不能为空", "请输入正确的手机号", "密码不能为空", "密码不正确，请重新输入",};

    /**
     * 校验输入内容
     *
     * @return
     */
    private boolean validation() {
        String phone = et_account.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastHelper.showMessage(hints[0]);
            return false;
        } else if (!phone.matches(AppConstant.RE_EVERYTHING)) {    //TODO 账户 RE_PHONE
            ToastHelper.showMessage(hints[1]);
            return false;
        }
        String pwd = et_pwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            ToastHelper.showMessage(hints[2]);
            return false;
        } else if (!pwd.matches(AppConstant.RE_EVERYTHING)) {    //TODO 密码 RE_PWD
            ToastHelper.showMessage(hints[3]);
            return false;
        }
        return true;
    }

    /**
     * 登录商品通
     * @param account
     * @param password
     */
    private void login(final String account, final String password) {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                LoginUserDto userDto = Client.getService(ILoginService.class).login(account, password);
                LoginUserContext.setLoginUserDto(userDto);
                // 缓存
                SharePreferenceUtility.spSave(LoginActivity.this, SharePreferenceUtility.USERNAME, EncryptUtility.encrypt(account));
                SharePreferenceUtility.spSave(LoginActivity.this, SharePreferenceUtility.PASSWORD, EncryptUtility.encrypt(password));
                SharePreferenceUtility.spSave(LoginActivity.this, SharePreferenceUtility.USERID, userDto.getUserId());
                SharePreferenceUtility.spSave(LoginActivity.this, SharePreferenceUtility.COMPANYTAG, userDto.getCompanyTag());
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    if (!StringUtility.isNullOrEmpty(LoginUserContext.getLoginUserDto().getImUserAccid())) {
                        findSupCompanyList();
                        NIMLogin(LoginUserContext.getLoginUserDto());
                    } else {
                        finish();
                    }
                    findZoneRequestFocusList();
                }
            }
        });
    }

    /**
     * 查询关注
     */
    private void findZoneRequestFocusList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<TblZoneRequestFocusEntity>>() {
            @Override
            public PagesDownResultEntity<TblZoneRequestFocusEntity> requestService() throws DBException, ApplicationException {
                QueryPageZoneFocusUpEntity upEntity = new QueryPageZoneFocusUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                upEntity.setPageNo(1);
                upEntity.setPageSize(100);
                return Client.getService(IZoneRequestFocusService.class).findZoneRequestFocusList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<TblZoneRequestFocusEntity> obj) {
                if (obj != null && obj.getDataList() != null) {
                    List<String> typeList = new ArrayList<>() ;
                    for (TblZoneRequestFocusEntity tblZoneRequestFocusEntity : obj.getDataList()) {
                        typeList.add(tblZoneRequestFocusEntity.getProductType());
                    }
                    FocusContext.update(LoginActivity.this, typeList);
                    setAliaAndTag(typeList);
                }
            }
        });
    }

    /**
     * 登录云信
     *
     * @param dto
     */
    private void NIMLogin(final LoginUserDto dto) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<IMUserDownEntity>() {
            @Override
            public IMUserDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IIMUserService.class).findIMUserById(dto.getImUserAccid()); // 获取云信账户信息;
            }

            @SuppressLint("DefaultLocale")
            @SuppressWarnings("unchecked")
            @Override
            public void onDataSuccessfully(final IMUserDownEntity entity) {
                if (entity == null || TextUtils.isEmpty(entity.getAccid())) {
                    // startActivity(MainActivity.class);
                    finish();
                    return;
                }
                final LoginInfo info = new LoginInfo(entity.getAccid().toLowerCase(),
                        entity.getToken().toLowerCase());
                NIMClient.getService(AuthService.class).login(info)
                        .setCallback(new FutureRequestCallback<LoginInfo>() {
                            @Override
                            public void onSuccess(LoginInfo loginInfo) {
                                super.onSuccess(loginInfo);
                                SharePreferenceUtility.spSave(LoginActivity.this,
                                        SharePreferenceUtility.NIM_ACCID, loginInfo.getAccount());
                                SharePreferenceUtility.spSave(LoginActivity.this,
                                        SharePreferenceUtility.NIM_TOKEN, loginInfo.getToken());
                                SharePreferenceUtility.spSave(LoginActivity.this,
                                        SharePreferenceUtility.NIM_APPKEY, loginInfo.getAppKey());
                                // GroupInfoProvider.queryTeamList();
                                // 启动主Activity
                                // startActivity(MainActivity.class);
                                sendTongTongLoginMsg();
                                finish();
                            }
                        });
            }
        });
    }

    private void sendTongTongLoginMsg() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                TongTongLoginUpEntity loginUpEntity = new TongTongLoginUpEntity();
                loginUpEntity.setUserId(LoginUserContext.getLoginUserId());
                Client.getService(ILoginService.class).sendTongTongLoginMsg(loginUpEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {

            }
        });
    }

    /**
     * 设置标签与别名
     */
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

        // 切换用户后刷新消息界面
        Intent i = new Intent(this, HomeActivity.class);
        i.setAction(AppConstant.INTENT_ACTION_NOTIFY_CENTER);
        sendBroadcast(i);
    }

    /**
     * 获取所有企业列表
     */
    public void findSupCompanyList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<Company>>() {
            @Override
            public List<Company> requestService() throws DBException, ApplicationException {
                QuerySupplierUpEntity upEntity = new QuerySupplierUpEntity();
                upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
                upEntity.setPageNo(1);
                upEntity.setPageSize(10000);
                List<SupplierMasterDownEntity> lst = Client.getService(ISupplierService.class).findSupplierList(upEntity)
                        .getDataList();
                List<Company> lstCompany = new ArrayList<Company>();
                Company company;
                char pinyin;
                for (SupplierMasterDownEntity entity : lst) {
                    pinyin = PinYinKit.getPingYin(entity.getSupCompanyName().charAt(0));
                    pinyin = Character.toUpperCase(pinyin);
                    pinyin = Character.isUpperCase(pinyin) ? pinyin : '#';
                    company = new Company(pinyin, entity.getSupCompanyTag(),
                            entity.getSupCompanyName(), entity.getSupCompanyIndustry(),
                            entity.getSupLevel());
                    boolean isSup = !TextUtils.isEmpty(entity.getCompanyTag())
                            && entity.getCompanyTag().contains(LoginUserContext.getLoginUserDto().getCompanyTag());
                    company.setUserSupplier(isSup);
                    lstCompany.add(company);
                }
                Collections.sort(lstCompany, new PinyinComparator());
                return lstCompany;
            }

            @Override
            public void onDataSuccessfully(List<Company> requestList) {
                List<Company> lstAllCompany = new ArrayList<>();
                if (!CollectionUtility.isNullOrEmpty(requestList)) {
                    for (Company com : requestList) {
                        if (!com.getCompanyTag().equals(LoginUserContext.getLoginUserDto().getCompanyTag())) {
                            lstAllCompany.add(com);
                        }
                    }

                    FileUtils.saveCache(JsonUtility.list2JSONString(lstAllCompany),
                            LoginUserContext.getLoginUserDto().getCompanyTag());
//					FileUtils.saveCache(JsonUtility.list2JSONString(lstAllCompany),
//							FileUtils.FILETYPE_COMPANY);

                }
            }
        });
    }
}
