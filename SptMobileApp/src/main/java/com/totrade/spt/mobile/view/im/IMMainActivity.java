package com.totrade.spt.mobile.view.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.utility.ErrorCodeUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.SplashActivity;
import com.totrade.spt.mobile.view.customize.CommonTitle3;
import com.totrade.spt.mobile.view.im.fragment.ChatContactFragment;
import com.totrade.spt.mobile.view.im.fragment.ChatRecentFragment;

public class IMMainActivity extends SptMobileActivityBase {
    @BindViewId(R.id.rgMainTab)
    private RadioGroup rgMainTab;
    @BindViewId(R.id.title)
    private CommonTitle3 title;
    public ChatContactFragment contactListFragment;
    public ChatRecentFragment recentChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatmain);
        recentChatFragment = new ChatRecentFragment();
        contactListFragment = new ChatContactFragment();
        title.setTitle("通通");

        addFragments(contactListFragment, recentChatFragment);
        rgMainTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                showFragment(id == R.id.rbChatMsg ? recentChatFragment : contactListFragment);
            }
        });

        showFragment(recentChatFragment);

        friendChangedObserve(true);

        title.getImgBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(HomeActivity.class);
            }
        });

    }

    public void requestUserSuccess() {
        recentChatFragment.updateUserIconAndName();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, IMMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ErrorCodeUtility.isErrorMapEmpty()) {
            startActivity(SplashActivity.class);
            finish();
        } else if (LoginUserContext.isAnonymous()) {
            startActivity(LoginActivity.class);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        friendChangedObserve(false);
    }

    /**
     * 好友变化监听
     */
    private void friendChangedObserve(boolean regist) {
        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, regist);
    }

    private Observer<FriendChangedNotify> friendChangedNotifyObserver = new Observer<FriendChangedNotify>() {
        @Override
        public void onEvent(FriendChangedNotify friendChangedNotify) {
//			List<Friend> addedOrUpdatedFriends = friendChangedNotify.getAddedOrUpdatedFriends(); // 新增的好友
//			List<String> deletedFriendAccounts = friendChangedNotify.getDeletedFriends(); // 删除好友或者被解除好友
            recentChatFragment.updateUserIconAndName();
            contactListFragment.findUserTeam();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(HomeActivity.class);
        }
        return super.onKeyDown(keyCode, event);
    }
}
