package com.totrade.spt.mobile.view.im;

import android.os.Bundle;
import android.view.KeyEvent;

import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.im.fragment.TeamMemberFragment;

/**
 * 管理群成员列表 组成员列表
 *
 * @author huangxy
 * @date 2016/11/30
 */
public class GroupMemberActivity extends SptMobileActivityBase {

    public static String TEAM_MEMBER = "teamMember";    //IM聊天界面
    public static String GROUP_MEMBER = "groupMember";    //最近联系人界面
    private TeamMemberFragment teamMemberFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_group_member);

        initView();
        initData();
    }

    private void initView() {
        teamMemberFragment = new TeamMemberFragment();
    }

    private void initData() {
        // 根据参数切换界面
        if (TEAM_MEMBER.equals(getIntent().getStringExtra(TEAM_MEMBER))) {
            teamMemberFragment.setTeamId(getIntent().getStringExtra(AppConstant.NIM_TEAMID),
                    getIntent().getStringExtra(AppConstant.NIM_NICKNAME));
            switchContent(teamMemberFragment);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            teamMemberFragment.onKeyDown(keyCode, event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
