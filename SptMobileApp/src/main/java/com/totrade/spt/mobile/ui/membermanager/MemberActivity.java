package com.totrade.spt.mobile.ui.membermanager;

import android.os.Bundle;

import com.autrade.spt.master.dto.CompanyInviteInfo;
import com.autrade.spt.master.entity.UserAccountEntity;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.view.R;

/**
 * 成员管理
 * Created by Timothy on 2017/4/24.
 */

public class MemberActivity extends BaseActivity {

    private MemberManagerListFragment memberManagerListFragment;//成员管理列表页
    private MemberDetailFragment memberDetailFragment;//成员详情
    private MemberApplyListFragment memberApplyListFragment;//成员申请列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        initFragments();
        switchMMLF();
    }

    private void initFragments() {
        memberManagerListFragment = new MemberManagerListFragment();
        memberDetailFragment = new MemberDetailFragment();
        memberApplyListFragment = new MemberApplyListFragment();
    }

    public void switchMMLF() {
        switchContent(memberManagerListFragment);
    }

    public void switchMDF(UserAccountEntity obj, CompanyInviteInfo info, String type) {
        memberDetailFragment.setType(type);
        memberDetailFragment.setEntity(obj);
        memberDetailFragment.setComEntity(info);
        switchContent(memberDetailFragment, true);
    }

    public void switchMALF() {
        switchContent(memberApplyListFragment, true);
    }
}
