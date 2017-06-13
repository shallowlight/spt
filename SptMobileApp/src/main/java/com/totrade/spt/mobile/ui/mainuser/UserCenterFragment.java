package com.totrade.spt.mobile.ui.mainuser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autrade.spt.activity.entity.TblPotUserEntity;
import com.autrade.spt.activity.service.inf.IPointService;
import com.autrade.spt.bank.entity.AccountInfoBalanceEntity;
import com.autrade.spt.bank.entity.Cfs;
import com.autrade.spt.bank.entity.TblAccountBalanceEntity;
import com.autrade.spt.bank.service.inf.IAccountInfoBalanceService;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.pot.PotIncreaseVo;
import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.MyContractNumberDownEntity;
import com.autrade.spt.deal.entity.QueryMyContractUpEntity;
import com.autrade.spt.deal.service.inf.IContractService;
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
import com.totrade.spt.mobile.adapter.UserCenterMenuAdapter;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.bean.Menu;
import com.totrade.spt.mobile.bean.MenuNavigation;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.accountmanager.UserAccMngActivity;
import com.totrade.spt.mobile.ui.fundmanager.FundManagerActivity;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.ui.login.RegistActivity;
import com.totrade.spt.mobile.ui.login.fragment.CompanyBindFragment;
import com.totrade.spt.mobile.ui.membermanager.MemberActivity;
import com.totrade.spt.mobile.ui.notifycenter.NotifyActivity;
import com.totrade.spt.mobile.ui.ordermanager.OrderManagerActivity;
import com.totrade.spt.mobile.ui.pointshop.PointShopActivity;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.utility.EncryptUtility;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.StatusBarUtil;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SysInfoUtil;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.BadgeView;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.CommonTextView;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.CustomGridView;
import com.totrade.spt.mobile.view.customize.GradationScrollView;
import com.totrade.spt.mobile.view.customize.KeyTextView;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 * Created by Timothy on 2017/4/6.
 */

public class UserCenterFragment extends BaseSptFragment<HomeActivity> implements AdapterView.OnItemClickListener,
        GradationScrollView.ScrollViewListener, View.OnClickListener {

    private RelativeLayout rl_title;
    private RelativeLayout rl_user_info;
    private LinearLayout ll_block_fund;
    private FrameLayout fl_title;
    private ImageView iv_set;
    private TextView ctv_msg;
    private GradationScrollView scrollView;
    private CustomGridView gv_menu;
    private CircleImageView civ_user_img;
    private TextView tv_user_name_key;
    private TextView tv_user_name;
    private TextView tv_company_name;
    private KeyTextView ktv_sum_score;
    private CommonTextView ctv_order_manager;
    private TextView tv_available_balance;
    private TextView tv_available_balance_unit;
    private TextView tv_block_fund;
    private TextView tv_block_fund_unit;
    private TextView tv_tsb_count;
    private TextView tv_tsb_count_unit;
    private TextView tv_not_login_msg;
    private TextView tv_declaredelivery;
    private TextView tv_bond_paid;
    private TextView tv_all_paid;
    private TextView tv_tag_sent;
    private TextView tv_unpayremain;

    private UserCenterMenuAdapter menuAdapter;

    private String[] titles;

    private int[] menuDrawIds = new int[]{R.drawable.icon_business_analysis, R.drawable.icon_funds_managements,
            R.drawable.icon_trade_agent, R.drawable.icon_apply_c_level,
            R.drawable.icon_integral_mall, R.drawable.icon_industry_apply,
            R.drawable.icon_member_manager, R.drawable.icon_supplier_set};

    private String[] premissions = new String[]{
            SptConstant.USER_ROLE_COMPANYMASTER + SptConstant.CONFIGGROUPID_TAG_TRADE,//企业管理者，交易员
            SptConstant.USER_ROLE_COMPANYMASTER + SptConstant.CONFIGGROUPID_TAG_FUND,//企业管理者，财务
            SptConstant.USER_ROLE_COMPANYMASTER + SptConstant.CONFIGGROUPID_TAG_TRADE + SptConstant.CONFIGGROUPID_TAG_FUND + SptConstant.CONFIGGROUPID_TAG_DELIVE,//企业账户
            SptConstant.USER_ROLE_COMPANYMASTER + SptConstant.CONFIGGROUPID_TAG_TRADE + SptConstant.CONFIGGROUPID_TAG_FUND + SptConstant.CONFIGGROUPID_TAG_DELIVE,
            SptConstant.USER_ROLE_COMPANYMASTER + SptConstant.CONFIGGROUPID_TAG_TRADE + SptConstant.CONFIGGROUPID_TAG_FUND + SptConstant.CONFIGGROUPID_TAG_DELIVE + SptConstant.CONFIGGROUPID_TAG_FREEMAN,
            SptConstant.USER_ROLE_COMPANYMASTER + SptConstant.CONFIGGROUPID_TAG_TRADE,
            SptConstant.USER_ROLE_COMPANYMASTER,//企业管理者
            SptConstant.USER_ROLE_COMPANYMASTER + SptConstant.CONFIGGROUPID_TAG_TRADE};//企业管理者，交易员

    private Class<?>[] clazzs = new Class[]{null, FundManagerActivity.class,
            TraderAgentRequestActivity.class, null,
            PointShopActivity.class, IndustryRequestActivity.class,
            MemberActivity.class, null};

    private String[] hint3s = new String[]{
            "您不是管理者/交易账号",
            "您不是管理者/财务账号",
            "您还未绑定企业，不能使用相关服务",
            "您还未绑定企业，不能使用相关服务",
            "您还未登录，不能使用相关服务",
            "您不是管理者/交易账号",
            "您不是管理者账号",
            "您不是管理者/交易账号"};


    private BadgeView[] badgeViews = new BadgeView[5];

    public UserCenterFragment() {
        setContainerId(R.id.fl_body);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_user_center;
    }

    @Override
    protected void initView() {
        fl_title = findView(R.id.fl_title);
        rl_title = findView(R.id.rl_title);
        ctv_msg = findView(R.id.ctv_msg);
        iv_set = findView(R.id.iv_set);
        gv_menu = findView(R.id.gv_menu);
        scrollView = findView(R.id.scrollView);
        ctv_order_manager = findView(R.id.ctv_order_manager);
        rl_user_info = findView(R.id.rl_user_info);
        civ_user_img = findView(R.id.civ_user_img);
        tv_user_name_key = findView(R.id.tv_user_name_key);
        tv_user_name = findView(R.id.tv_user_name);
        tv_company_name = findView(R.id.tv_company_name);
        tv_not_login_msg = findView(R.id.tv_not_login_msg);
        ktv_sum_score = findView(R.id.ktv_sum_score);
        tv_available_balance = findView(R.id.tv_available_balance);
        tv_block_fund = findView(R.id.tv_block_fund);
        tv_tsb_count = findView(R.id.tv_tsb_count);
        tv_available_balance_unit = findView(R.id.tv_available_balance_unit);
        tv_block_fund_unit = findView(R.id.tv_block_fund_unit);
        tv_tsb_count_unit = findView(R.id.tv_tsb_count_unit);
        ll_block_fund = findView(R.id.ll_block_fund);
        tv_declaredelivery = findView(R.id.tv_declaredelivery);
        tv_bond_paid = findView(R.id.tv_bond_paid);
        tv_all_paid = findView(R.id.tv_all_paid);
        tv_tag_sent = findView(R.id.tv_tag_sent);
        tv_unpayremain = findView(R.id.tv_unpayremain);

        initBadgeViews();

        setStatusBar();
        setGridMenu();
        setListener();
    }

    private void initBadgeViews() {
        for (int i = 0; i < badgeViews.length; i++) {
            badgeViews[i] = new BadgeView(mActivity);
            badgeViews[i].setLayoutParams(new FrameLayout.LayoutParams(DensityUtil.dp2px(mActivity, 12), DensityUtil.dp2px(mActivity, 12)));
            badgeViews[i].setPadding(0, 0, 0, 0);
            badgeViews[i].setGravity(Gravity.CENTER);
            badgeViews[i].setBackground(3, Color.parseColor("#DC5C5D"));
            badgeViews[i].setTextSize(6);
            badgeViews[i].setHideOnNull(true);
            badgeViews[i].setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
            badgeViews[i].setBadgeMargin(0, 0, 15, 0);
        }
        badgeViews[0].setTargetView(tv_declaredelivery);
        badgeViews[1].setTargetView(tv_bond_paid);
        badgeViews[2].setTargetView(tv_all_paid);
        badgeViews[3].setTargetView(tv_tag_sent);
        badgeViews[4].setTargetView(tv_unpayremain);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initData();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUnReadMsg(boolean unReadMsg) {
        if (mRootView != null)
            ctv_msg.setSelected(unReadMsg);
    }

    /**
     * 透明状态栏
     */
    private void setStatusBar() {
        StatusBarUtil.setImgTransparent(mActivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.topMargin = SysInfoUtil.getStatusBarHeight();
            rl_title.setLayoutParams(params);
        }
    }

    /**
     * 装载Grid菜单
     */
    private void setGridMenu() {
        titles = getResources().getStringArray(R.array.UserCenterMenus);
        menuAdapter = new UserCenterMenuAdapter(mActivity);
        List<Menu> menuList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            menuList.add(new Menu(menuDrawIds[i], titles[i], premissions[i],
                    new MenuNavigation(getString(R.string.user_center_not_login_hint), LoginActivity.class),
                    new MenuNavigation(getString(R.string.user_center_freeman_hint), RegistActivity.class),
                    new MenuNavigation(hint3s[i], clazzs[i])));
        }
        menuAdapter.setData(menuList);
        gv_menu.setAdapter(menuAdapter);
    }

    /**
     * 设置监听
     */
    private void setListener() {
        scrollView.setScrollViewListener(this);
        gv_menu.setOnItemClickListener(this);
        iv_set.setOnClickListener(this);
        ctv_order_manager.setOnClickListener(this);
        ctv_msg.setOnClickListener(this);
        ctv_msg.setSaveEnabled(false);
        tv_declaredelivery.setOnClickListener(this);
        tv_bond_paid.setOnClickListener(this);
        tv_all_paid.setOnClickListener(this);
        tv_tag_sent.setOnClickListener(this);
        tv_unpayremain.setOnClickListener(this);
        civ_user_img.setOnClickListener(this);
        ll_block_fund.setOnClickListener(this);
    }

    /**
     * 获取数据
     */
    private void initData() {
        setUnReadMsg(mActivity.getUnReadNum());
        if (LoginUserContext.isAnonymous()) {//匿名用户(未登录)
            tv_not_login_msg.setVisibility(View.VISIBLE);
            ctv_msg.setSelected(false);
            civ_user_img.setImageResource(R.drawable.main_home_portrait);
            ktv_sum_score.setText("积分:—");
            tv_user_name_key.setText("");
            tv_user_name.setText("");
            tv_company_name.setText("");
            tv_available_balance.setText("— — —");
            tv_tsb_count.setText("— — —");
            tv_block_fund.setText("— — —");

            for (BadgeView badgeView : badgeViews) {
                badgeView.setBadgeCount(0);
            }

            rl_user_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginUserContext.isAnonymous()) {
                        IntentUtils.startActivity(getActivity(), LoginActivity.class);
                    }
                }
            });
            civ_user_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.startActivity(getActivity(), LoginActivity.class);
                }
            });
        } else if (LoginUserContext.isFreeman()) {//自由人
            tv_not_login_msg.setVisibility(View.GONE);
            civ_user_img.setImageResource(R.drawable.main_home_portrait);
            tv_available_balance.setText("— — —");
            tv_tsb_count.setText("— — —");
            tv_block_fund.setText("— — —");
            for (BadgeView badgeView : badgeViews) {
                badgeView.setBadgeCount(0);
            }
            loadNIMUserInfo();//从云信读取头像
            getUserScore();//获取用户积分
            queryUserAcctDetailByUserName();//根据用户名获取用户详细信息
        } else {
            //其它身份数据都可以浏览，不同的是页面上某些操作有不同的限
            tv_not_login_msg.setVisibility(View.GONE);
            getAccountInfoBalanceByUserId();//资金数据
            loadNIMUserInfo();//从云信读取头像
            getUserScore();//获取用户积分
            queryUserAcctDetailByUserName();//根据用户名获取用户详细信息
            findMyContractNumber();//设置未处理的订单数量提醒
        }
    }

    /**
     * 按用户ID获得账户信息及其对应的资金信息
     */
    private void getAccountInfoBalanceByUserId() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<AccountInfoBalanceEntity>() {
            @Override
            public AccountInfoBalanceEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IAccountInfoBalanceService.class).getAccountInfoBalanceByUserId(LoginUserContext.getLoginUserDto().getUserId(), LoginUserContext.getLoginUserDto().getPaySystem());
            }

            @Override
            public void onDataSuccessfully(AccountInfoBalanceEntity result) {
                if (result != null) {
                    loadDataToView(result);
                }
            }
        });
    }

    private void loadDataToView(AccountInfoBalanceEntity entity) {
        TblAccountBalanceEntity accountBalanceEntity = entity.getAccountBalanceEntity();
        if (null != accountBalanceEntity) {
            Cfs cfs = accountBalanceEntity.getAccountBalanceCfs();
            if (null != cfs) {
                formatAccountMoney(cfs.getBalance(), tv_available_balance, tv_available_balance_unit, "可用余额", "元", 2);
                formatAccountMoney(cfs.getCredit(), tv_tsb_count, tv_tsb_count_unit, "通商宝", "个", 2);
                // 冻结真钱+冻结保证金+冻结定金贷款+撮合冻结手续费+撮合假冻结保证金
                BigDecimal blockAmountTotal = accountBalanceEntity.getBlockFundCfs().getBalance()
                        .add(accountBalanceEntity.getMatchBondCfs().getBalance())
                        .add(accountBalanceEntity.getMatchBondCfs().getSptpoint())
                        .add(accountBalanceEntity.getMatchBlockFeeCfs().getBalance())
                        .add(accountBalanceEntity.getMatchBlockBondCfs().getBalance());
                tv_block_fund.setText(StringUtils.double2String(blockAmountTotal.doubleValue(), 2));
                formatAccountMoney(blockAmountTotal, tv_block_fund, tv_block_fund_unit, "冻结资金", "元", 2);
            }
        }
    }

    private void formatAccountMoney(BigDecimal bigDecimal, TextView tv1, TextView tv2, String des, String unit, int num) {
        if (bigDecimal.doubleValue() >= 10000) {
            tv1.setText(StringUtils.double2String(bigDecimal.doubleValue(), 10000, num, "0.00"));
            tv2.setText(des + "(" + "万" + unit + ")");
        } else {
            if (des.equals("通商宝")) {
                tv1.setText(StringUtils.double2String(bigDecimal.doubleValue(), 0));
            } else {
                tv1.setText(StringUtils.double2String(bigDecimal.doubleValue(), num));
            }
            tv2.setText(des + "(" + unit + ")");
        }
    }

    /**
     * 加载头像
     */
    private void loadNIMUserInfo() {
        List<String> accounts = new ArrayList<>();
        accounts.add(LoginUserContext.getLoginUserDto().getImUserAccid());
        InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts);
        future.setCallback(new FutureRequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                super.onSuccess(param);
                if (param != null && param.size() > 0) {
                    NimUserInfo nimUserInfo = param.get(0);
                    String url = nimUserInfo.getAvatar();

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
                    LoginUserContext.setLoginUserDto(obj);//刷新内存中的用户上下文
                    tv_user_name_key.setText(obj.getDisplayName());
                    tv_user_name.setText(Dictionary.CodeToKey(Dictionary.MASTER_USER_DENTITY, obj.getConfigGroupId()));
                    if (StringUtility.isNullOrEmpty(obj.getCompanyName())) {
                        tv_company_name.setText("-----------");
                    } else {
                        tv_company_name.setText(obj.getCompanyName());
                    }
                }
            }
        });
    }

    /**
     * 取合同状态的未读消息
     */
    private void findMyContractNumber() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<MyContractNumberDownEntity>>() {
            @Override
            public List<MyContractNumberDownEntity> requestService() throws DBException, ApplicationException {
                QueryMyContractUpEntity entity = new QueryMyContractUpEntity();
                entity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                return Client.getService(IContractService.class).findMyContractNumber(entity);
            }

            @Override
            public void onDataSuccessfully(List<MyContractNumberDownEntity> obj) {
                if (null != obj) {
                    setUnHandleContractNum(obj);
                }
            }
        });
    }

    /**
     * 取用户积分
     */
    private void getUserScore() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<TblPotUserEntity>() {
            @Override
            public TblPotUserEntity requestService() throws DBException, ApplicationException {
                PotIncreaseVo vo = new PotIncreaseVo();
                vo.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                return Client.getService(IPointService.class).findUserAcctPoint(vo);
            }

            @Override
            public void onDataSuccessfully(TblPotUserEntity obj) {
                if (null != obj && !ObjUtils.isEmpty(obj.getPotBalance())) {
                    ktv_sum_score.setText("积分:" + obj.getPotBalance());
                }
            }
        });
    }


    /**
     * 设置没有处理的合同消息数量提醒
     */
    private void setUnHandleContractNum(List<MyContractNumberDownEntity> obj) {
        for (MyContractNumberDownEntity entity : obj) {
            String statusTag = entity.getOrderStatus();
            if (!ObjUtils.isEmpty(statusTag)) {
                if (statusTag.equals(DealConstant.ORDER_STATUS_DECLAREDELIVERY)) {//待宣港
                    badgeViews[0].setBadgeCount(entity.getMsgNum());
                } else if (statusTag.equals(DealConstant.ORDER_STATUS_WAIT_PAYALL)) {//待付款
                    badgeViews[1].setBadgeCount(entity.getMsgNum());
                } else if (statusTag.equals("3")) {//待发货
                    badgeViews[2].setBadgeCount(entity.getMsgNum());
                } else if (statusTag.equals("4")) {//待收货
                    badgeViews[3].setBadgeCount(entity.getMsgNum());
                } else if (statusTag.equals("X")) {//余款违约
                    badgeViews[4].setBadgeCount(entity.getMsgNum());
                }
            }
        }
    }

    @Override
    public void onScrollChanged(GradationScrollView scrollView, int x, int y, int oldx, int oldy) {
        int trans = y <= 0 ? 0 : 255;
        fl_title.setBackgroundColor(Color.argb(trans, 48, 169, 222));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Menu menu = menuAdapter.getItem(position);
        if (LoginUserContext.isAnonymous()) {
            ToastHelper.showMessage(menu.getMenuHint().getHint());
            return;
        }
        if (LoginUserContext.isFreeman()) {
            //积分管理 不受二级权限控制,故此处需排除
            if (!menu.getTitle().equals(titles[4])) {
                toBindCompanyActivity(mActivity);
                return;
            }
        }
        if (menu.getPremissions().contains(LoginUserContext.getLoginUserDto().getConfigGroupId())) {
            if (ObjUtils.isEmpty(menu.getMenuHint3().getClazz())) {
                ToastHelper.showMessage("此功能正在开发中");
            } else {
                IntentUtils.startActivity(mActivity, menu.getMenuHint3().getClazz());
            }
        } else {
            ToastHelper.showMessage(menu.getMenuHint3().getHint());
        }
    }

    @Override
    public void onClick(View v) {
        if (LoginUserContext.isAnonymous()) return;
        switch (v.getId()) {
            case R.id.iv_set:
                IntentUtils.startActivity(mActivity, UserAccMngActivity.class);
                break;
            case R.id.ctv_msg:
                startActivity(NotifyActivity.class);
                break;
            case R.id.ctv_order_manager:
                IntentUtils.startActivity(mActivity, OrderManagerActivity.class, "STATUS", Dictionary.CONSTRACT_STATUS.get(0).getCode());
                break;
            case R.id.tv_declaredelivery:
                IntentUtils.startActivity(mActivity, OrderManagerActivity.class, "STATUS", Dictionary.CONSTRACT_STATUS.get(1).getCode());
                break;
            case R.id.tv_bond_paid:
                IntentUtils.startActivity(mActivity, OrderManagerActivity.class, "STATUS", Dictionary.CONSTRACT_STATUS.get(2).getCode());
                break;
            case R.id.tv_all_paid:
                IntentUtils.startActivity(mActivity, OrderManagerActivity.class, "STATUS", Dictionary.CONSTRACT_STATUS.get(3).getCode());
                break;
            case R.id.tv_tag_sent:
                IntentUtils.startActivity(mActivity, OrderManagerActivity.class, "STATUS", Dictionary.CONSTRACT_STATUS.get(4).getCode());
                break;
            case R.id.tv_unpayremain:
                IntentUtils.startActivity(mActivity, OrderManagerActivity.class, "STATUS", Dictionary.CONSTRACT_STATUS.get(8).getCode());
                break;
            case R.id.ll_block_fund:
//                IntentUtils.startActivity ( mActivity,FundManagerActivity.class, AccountInfoBalanceEntity.class.getName (), JsonUtility.toJSONString ( balanceEntity ));
                break;
        }
    }

    private void toBindCompanyActivity(final Context context) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context, "您还未升级至企业账号， 不能\n使用相关服务");
        builder.setPositiveButton("稍后再说", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("去升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, RegistActivity.class);
                intent.putExtra(CompanyBindFragment.class.getName(), CompanyBindFragment.class.getName());
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();
        builder.positiveButton.setSelected(false);
        builder.negativeButton.setSelected(true);
    }
}
