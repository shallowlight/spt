package com.totrade.spt.mobile.ui.notifycenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.utility.ErrorCodeUtility;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.SplashActivity;
import com.totrade.spt.mobile.view.im.fragment.ChatContactFragment;
import com.totrade.spt.mobile.view.im.fragment.ChatRecentFragment;

/**
 * 消息中心 - 通通
 *
 * @author huangxy
 * @date 2017/5/2
 */
public class TongtongFragment extends BaseFragment {
    private Activity activity;
    private View rootView;
    public ChatContactFragment contactListFragment;
    public ChatRecentFragment recentChatFragment;
    private RadioGroup rgMainTab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_im, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ErrorCodeUtility.isErrorMapEmpty()) {
            IntentUtils.startActivity(activity, SplashActivity.class);
            activity.finish();
        } else if (LoginUserContext.isAnonymous()) {
            IntentUtils.startActivity(activity, LoginActivity.class);
            activity.finish();
        }
    }

    private void initView() {
        rgMainTab = (RadioGroup) rootView.findViewById(R.id.rgMainTab);
        recentChatFragment = new ChatRecentFragment();
        contactListFragment = new ChatContactFragment();
        addFragments(contactListFragment, recentChatFragment);
        rgMainTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                showFragment(id == R.id.rbChatMsg ? recentChatFragment : contactListFragment);
            }
        });

        showFragment(recentChatFragment);

        friendChangedObserve(true);
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
            recentChatFragment.updateUserIconAndName();
            contactListFragment.findUserTeam();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        friendChangedObserve(false);
    }
}
