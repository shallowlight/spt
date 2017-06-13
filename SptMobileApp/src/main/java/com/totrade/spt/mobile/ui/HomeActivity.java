package com.totrade.spt.mobile.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.report.dto.NotifyUnReadDownEntity;
import com.autrade.spt.report.entity.QueryNotifyHistoryUpEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.accountmanager.SetFragment;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.ui.login.fragment.AddFocusFragment;
import com.totrade.spt.mobile.ui.mainhome.HomeFragment;
import com.totrade.spt.mobile.ui.mainmatch.MatchFragment;
import com.totrade.spt.mobile.ui.maintrade.IndicatorHelper;
import com.totrade.spt.mobile.ui.maintrade.fragment.TradeFragment;
import com.totrade.spt.mobile.ui.mainuser.UserCenterFragment;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.StatusBarUtil;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.Temp;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.ChatIMActivity;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author huangxy
 * @date 2017/4/1
 */
public class HomeActivity extends BaseActivity {

    private android.support.design.widget.TabLayout tabLayout;
    private int selectP;
    private String[] mainTab;
    private boolean unReadNum;

    public static final String EXTRA_NEST = "extraNext";

    private HomeFragment homeFragment;
    private TradeFragment tradeFragment;
    private MatchFragment matchFragment;
    private UserCenterFragment userCenterFragment;
    private AddFocusFragment addFocusFragment;
    private List<BaseSptFragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImgTransparent(this);
        setContentView(R.layout.activity_home);

        initView();
        refreshNoti();
    }

    private void initView() {
        homeFragment = new HomeFragment();
        tradeFragment = new TradeFragment();
        matchFragment = new MatchFragment();
        userCenterFragment = new UserCenterFragment();
        addFocusFragment = new AddFocusFragment();
        addFragments(homeFragment, tradeFragment, matchFragment, userCenterFragment);
        fragmentList = new ArrayList<>();
        fragmentList.add(homeFragment);
        fragmentList.add(tradeFragment);
        fragmentList.add(matchFragment);
        fragmentList.add(userCenterFragment);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mainTab = getResources().getStringArray(R.array.main_tab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (LoginUserContext.isAnonymous()) {
                    if (tab.getPosition() == 3) {
                        new IndicatorHelper().getTabView(tabLayout, tab.getPosition()).setSelected(false);
                        IntentUtils.startActivity(HomeActivity.this, LoginActivity.class);
                    } else {
                        selectP = tab.getPosition();
                        showFragment(fragmentList.get(tab.getPosition()));
                    }
                } else {
                    selectP = tab.getPosition();
                    showFragment(fragmentList.get(tab.getPosition()));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        addViewTab();
    }

    private void addViewTab() {
        TabLayout.Tab tab1 = tabLayout.newTab();
        tab1.setCustomView(createTab(mainTab[0], R.drawable.selector_home_bg));
        tabLayout.addTab(tab1);

        TabLayout.Tab tab2 = tabLayout.newTab();
        tab2.setCustomView(createTab(mainTab[1], R.drawable.selector_home_trade_bg));
        tabLayout.addTab(tab2);

        TabLayout.Tab tab3 = tabLayout.newTab();
        tab3.setCustomView(createTab(mainTab[2], R.drawable.selector_home_match_bg));
        tabLayout.addTab(tab3);

        TabLayout.Tab tab4 = tabLayout.newTab();
        tab4.setCustomView(createTab(mainTab[3], R.drawable.selector_home_user_bg));
        tabLayout.addTab(tab4);

    }

    private View createTab(String name, int iconID) {
        View newtab = LayoutInflater.from(this).inflate(R.layout.icon_view, null);
        TextView tv = (TextView) newtab.findViewById(R.id.tv_tab_text);
        tv.setText(name);
        ImageView im = (ImageView) newtab.findViewById(R.id.iv_tab_icon);
        im.setBackgroundResource(iconID);
        return newtab;
    }

    public void switchAddFocus() {
        addFocusFragment.setStatusBar(true);
        switchContent(addFocusFragment, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        receivedPushMsg();
    }

    private void receivedPushMsg() {
        NotifyUtility.cancelJpush(this);
        Intent intent = getIntent();
        String nextClsName = intent.getStringExtra(EXTRA_NEST);
        if (TextUtils.isEmpty(nextClsName)) {
            return;
        }
        //有需要跳转
        if (nextClsName.equals(ChatIMActivity.class.getName())) {
            String sessid = intent.getStringExtra(ChatIMActivity.EXTRA_SESSION_ID);
            if (TextUtils.isEmpty(sessid)) {
                return;
            }
            String petName = intent.getStringExtra(ChatIMActivity.EXTRA_SESSION_NAME);
            String type = intent.getStringExtra(ChatIMActivity.EXTRA_SESSION_TYPE);
            ChatIMActivity.start(this, sessid, petName, type);
        }

        if (nextClsName.equals(TradeFragment.class.getName())) {
            setCurrItem(2);
        }
    }

    public void setCurrItem(int i) {
        showFragment(fragmentList.get(i));
    }

    /**
     * 刷新界面
     */
    private void refreshNoti() {
        receiver = new NotifyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.INTENT_ACTION_NOTI);
        filter.addAction(AppConstant.INTENT_ACTION_NOTIFY_CENTER);
        registerReceiver(receiver, filter);
    }

    private NotifyReceiver receiver;

    public boolean getUnReadNum() {
        return unReadNum;
    }

    /**
     * 有新消息时刷新
     */
    private class NotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.INTENT_ACTION_NOTIFY_CENTER.equals(intent.getAction())) {
                tradeFragment.onRefresh();
                unReadNum = true;
                unReadMessage(true);
            }
        }
    }

    /**
     * 查询未读消息
     */
    public void getNotifyUnReadNum() {
        if (LoginUserContext.isAnonymous()) return;
        if (LoginUserContext.isFreeman()) {
            unReadMessage(false);
            return;
        }
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<NotifyUnReadDownEntity>>() {
            @Override
            public List<NotifyUnReadDownEntity> requestService() throws DBException, ApplicationException {
                QueryNotifyHistoryUpEntity upEntity = new QueryNotifyHistoryUpEntity();
                upEntity.setNotifyTo(LoginUserContext.getLoginUserId());
                upEntity.setNotifyChannel("jpush");
                upEntity.setPageSize(100);
                return Client.getService(com.autrade.spt.report.service.inf.INotifyService.class).findNotifyUnReadNumList(upEntity);

            }

            @Override
            public void onDataSuccessfully(List<NotifyUnReadDownEntity> obj) {
                if (obj != null) {
                    int num = 0;
                    for (NotifyUnReadDownEntity notifyUnReadDownEntity : obj) {
                        if (notifyUnReadDownEntity.getCategory().equals("uncategory")) continue;
                        num += notifyUnReadDownEntity.getUnReadNum();
                    }
                    unReadNum = num > 0;
                    unReadMessage(unReadNum);
                }
            }
        });
    }

    public void unReadMessage(boolean b) {
        homeFragment.setUnReadMsg(b);
        tradeFragment.setUnReadMsg(b);
        matchFragment.setUnReadMsg(b);
        userCenterFragment.setUnReadMsg(b);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstant.ACTION_TYPE_LOGOUT.equals(Temp.i().getAndClear(SetFragment.class))) {
            showFragment(fragmentList.get(0));
            tabLayout.getTabAt(0).select();
        } else {
            tabLayout.getTabAt(selectP).select();
        }
        getNotifyUnReadNum();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (matchFragment.onKeyDown(keyCode, event))
            return true;
        if (tradeFragment.onKeyDown(keyCode, event))
            return true;
        if (addFocusFragment.onKeyDown(keyCode, event))
            return true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }

    private static Boolean isExit = false;

    /**
     * 双击退出
     */
    private void exit() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            ToastHelper.showMessage("再按一次退出商品通");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            exitApp();
        }
    }

}
