package com.totrade.spt.mobile.ui.notifycenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomViewPager;

/**
*
* 消息中心
* @author huangxy 
* @date 2017/4/21
* 
*/
public class NotifyActivity extends BaseActivity {

    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private TextView tv_more;
    private TextView title;
    private MessageFragment messageFragment;
    private TongtongFragment tongtongFragment;
    private NotifySettingFragment notifySettingFragment;

    private String[] notifyTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        initView();
    }

    private void initView() {
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        title = (TextView) findViewById(R.id.title);
        tv_more = (TextView) findViewById(R.id.tv_more);
        tv_more.setVisibility(View.VISIBLE);

        messageFragment = new MessageFragment();
        tongtongFragment = new TongtongFragment();
        notifySettingFragment = new NotifySettingFragment();

        notifyTab = getResources().getStringArray(R.array.notify_tab);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return messageFragment;
                else if (position == 1)
                    return tongtongFragment;
                else return new Fragment();
            }

            @Override
            public int getCount() {
                return notifyTab.length;
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        View tab = createTab(notifyTab[0], R.drawable.selector_notify_notify_tab_bg);
        tab1.setCustomView(tab);
        tab2.setCustomView(createTab(notifyTab[1], R.drawable.selector_notify_tongtong_tab_bg));
        tab.setSelected(true);      //  默认选中

        tv_more.setText("设置");
        title.setText("消息中心");
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchContent(notifySettingFragment, true);
            }
        });

        register();
    }

    private View createTab(String name, int iconID) {
        View newtab = LayoutInflater.from(this).inflate(R.layout.icon_view, null);
        TextView tv = (TextView) newtab.findViewById(R.id.tv_tab_text);
        tv.setText(name);
        ImageView im = (ImageView) newtab.findViewById(R.id.iv_tab_icon);
        im.setBackgroundResource(iconID);
        return newtab;
    }

    private void initData() {

    }

    private NotifyReceiver receiver;

    /**
     * 有新消息时刷新
     */
    private class NotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppConstant.INTENT_ACTION_NOTIFY_CENTER.equals(intent.getAction())) {
                messageFragment.onRefresh();

            }
        }
    }

    /**
     * 注册刷新广播接收者
     */
    private void register() {
        receiver = new  NotifyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.INTENT_ACTION_NOTI);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
