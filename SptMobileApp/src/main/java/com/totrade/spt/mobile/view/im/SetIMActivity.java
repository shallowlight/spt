package com.totrade.spt.mobile.view.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.autrade.spt.common.entity.TblTemplateMasterEntity;
import com.autrade.spt.master.entity.QueryTemplateUpEntity;
import com.autrade.spt.master.service.inf.ITemplateService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.autrade.stage.utility.JsonUtility;
import com.google.gson.reflect.TypeToken;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.im.adapter.SetAdviserAdapter;
import com.totrade.spt.mobile.view.im.adapter.UserSetTeamAdapter;
import com.totrade.spt.mobile.bean.SetTeam;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.RecentContactSpt;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SubRequestCallback;
import com.totrade.spt.mobile.view.customize.ListEmptyView;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.customize.CommonTitle3;
import com.totrade.spt.mobile.view.customize.ScroListView;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
*
* 显示同一个组的多个群的界面
* @author huangxy
* @date 2017/1/23
*
*/
public class SetIMActivity extends SptMobileActivityBase implements UserSetTeamAdapter.OnDelClickListener,
        SetAdviserAdapter.OnRemarksClickListener, OnListItemClickListener<NimUserInfo> {

    private CommonTitle3 titleView;
    private TextView tv_set_title, tv_adviser_num;
    private ScroListView slv_adviser;
    private ListView slv_team;

    private UserSetTeamAdapter teamAdapter;
    private SetAdviserAdapter adviserAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_contract);
        initView();
        initSet();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerObservers(true);
        loadData();
        // 进入最近联系人列表界面，建议放在onResume中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void initView() {
        titleView = (CommonTitle3) findViewById(R.id.title);
        tv_set_title = (TextView) findViewById(R.id.tv_set_title);
        tv_adviser_num = (TextView) findViewById(R.id.tv_adviser_num);
        slv_team = (ListView) findViewById(R.id.lv_set);
        slv_adviser = (ScroListView) findViewById(R.id.slv_adviser);

        ListEmptyView teamEmptyView = new ListEmptyView(this, 100, 20, "暂未获取到数据");
        ListEmptyView adviserEmptyView = new ListEmptyView(this, 100, 20, "暂未获取到数据");
        ((ViewGroup) slv_team.getParent()).addView(teamEmptyView);
        ((ViewGroup) slv_adviser.getParent()).addView(adviserEmptyView);
        slv_team.setEmptyView(teamEmptyView);
        slv_adviser.setEmptyView(adviserEmptyView);
    }

    private void initSet() {
        teamAdapter = new UserSetTeamAdapter(this, mUserRecentTeams);
        teamAdapter.setOnDelClickListener(this);
        slv_team.setAdapter(teamAdapter);

        adviserAdapter = new SetAdviserAdapter(this, mAdviserList);
        adviserAdapter.setOnRemarksClickListener(this);
        adviserAdapter.setListItemClickListener(this);
        slv_adviser.setAdapter(adviserAdapter);
    }

    private void loadData() {
        findUserTeam();//取与用户有关所有群信息
        findGroupByProduct();
    }

    /**
     * 获取与用户相关所有群列表
     */
    private List<Team> mUserTeams;//与用户相关所有群信息

    public void findUserTeam() {
        NIMClient.getService(TeamService.class).queryTeamList().setCallback(new SubRequestCallback<List<Team>>(this) {
            @Override
            public void callBackSuccess(List<Team> listTeamReq) {
                mUserTeams = listTeamReq;
            }
        });
    }

    /**
     * 获取群组关系数据
     */
    private SetTeam mSetTeam = new SetTeam();//用于保存当前组以及其对应的集群

    private void findGroupByProduct() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<TblTemplateMasterEntity>() {
            @Override
            public TblTemplateMasterEntity requestService() throws DBException, ApplicationException {
                QueryTemplateUpEntity upEntity = new QueryTemplateUpEntity();
                upEntity.setTemplateCat("imGroupConfig");
                upEntity.setTemplateTag("IMGROUPCONFIG");
                upEntity.setTemplateId("IMGROUPCONFIG");
                return Client.getService(ITemplateService.class).getTemplateMaster(upEntity);
            }

            @Override
            public void onDataSuccessfully(TblTemplateMasterEntity obj) {
                if (null != obj) {
                    String s = obj.getTemplateContent().replace("\r\n", "");
                    Type type = new TypeToken<Map<String, List<String>>>() {
                    }.getType();
                    Map<String, List<String>> map = JsonUtility.toJavaObject(s, type);

                    //根据取回的组群关系，过滤出当前组的所有群
                    String tid = getIntent().getStringExtra("TEAM_ID");
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        if (entry.getValue().contains(tid)) {
                            mSetTeam.setName(entry.getKey());
                            mSetTeam.setTeams(entry.getValue());
                            titleView.setTitle(entry.getKey());     //设置标题
                            break;
                        }
                    }
                    //设置群的数量
                    tv_set_title.setText("群聊" + "(" + mSetTeam.getTeams().size() + ")");
                    //遍历当前组的所有群，取到对应群的详细信息
                    findTeamDetailByTeamid();
                }
            }
        });
    }

    /**
     * 获取当前组对应群的详细数据
     */
    private List<Team> mCurTeams = new ArrayList<>();

    private void findTeamDetailByTeamid() {
        if (null == mUserTeams || mUserTeams.isEmpty())
            return;
        List<String> ids = mSetTeam.getTeams();
        for (String id : ids) {
            for (Team team : mUserTeams) {
                if (team.getId().equals(id)) {
                    mCurTeams.add(team);
                    break;
                }
            }
        }
        tv_set_title.setText("群聊" + "(" + mCurTeams.size() + ")");
        //取出当前用户最近聊天记录,提取记录中的消息数据，组装成需要展示给用户的数据
        getRectentContact();
    }

    /**
     * 取最近联系的所有消息
     */
    private List<RecentContactSpt> mUserRecentTeams = new ArrayList<>();

    private void getRectentContact() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable e) {
                //初始化根据群组数量新建模型
                if (mUserRecentTeams.isEmpty()) {
                    RecentContactSpt spt;
                    for (Team team : mCurTeams) {
                        spt = new RecentContactSpt();
                        spt.setSessionId(team.getId());
                        spt.setTitle(team.getName());
                        spt.setSessionType(SessionTypeEnum.Team);
                        spt.setContent("暂无聊天记录");
                        mUserRecentTeams.add(spt);
                    }
                }

                if (!CollectionUtility.isNullOrEmpty(recents)) {
                    for (RecentContactSpt rcSpt : mUserRecentTeams) {
                        for (RecentContact rc : recents) {
                            //初始化的群，遇到有新消息的时候，要更新数据
                            if (rc.getContactId().equals(rcSpt.getSessionId())) {
                                rcSpt.setSessionId(rc.getContactId());
                                rcSpt.setUnReadCount(rc.getUnreadCount());
                                rcSpt.setSessionType(rc.getSessionType());
                                rcSpt.setMsgType(rc.getMsgType());
                                rcSpt.setTime(rc.getTime());
                                rcSpt.setFromNickName(rc.getFromNick());
                                rcSpt.setFromAccid(rc.getFromAccount());
                                rcSpt.setContent(rc.getFromNick() + ":" + rc.getContent());
                                if (rc.getMsgType().equals(MsgTypeEnum.custom)) {
                                    rcSpt.setContent("一条询价消息");
                                }
                                rcSpt.setTitle(rcSpt.getTitle());
                                break;
                            }
                        }
                    }
                }

                //刷新群组信息
                teamAdapter.notifyDataSetChanged();

                //由当前组的所有群ID获取所有群成员列表，并过滤出顾问成员的数据
                for (String s : mSetTeam.getTeams()) {
                    queryMemberListByTid(s);
                }
            }
        });
    }

    @Override
    public void onDelClick(View v, RecentContactSpt spt) {
        NIMClient.getService(MsgService.class).deleteRecentContact(spt);
        mUserRecentTeams.remove(spt);
        teamAdapter.notifyDataSetChanged();
    }

    private void queryMemberListByTid(final String tid) {
        NIMClient.getService(TeamService.class).queryMemberList(tid).setCallback(new SubRequestCallback<List<TeamMember>>(this) {
            @Override
            public void callBackSuccess(List<TeamMember> listMember) {
                List<TeamMember> mAdviserMember = new ArrayList<>();
                for (TeamMember member : listMember) {
                    // 过滤自己
                    if (LoginUserContext.getLoginUserDto().getImUserAccid().equalsIgnoreCase(member.getAccount())) {
                        continue;
                    }
                    // 过滤普通成员
                    if (member.getType() == TeamMemberType.Manager || member.getType() == TeamMemberType.Owner) {
                        mAdviserMember.add(member);
                    }
                }

                //多个群的顾问列表，有可能会重复，此处整理出无重复顾问的列表
                List<TeamMember> listMemberNew = new ArrayList<>();
                for (TeamMember member : mAdviserMember) {
                    boolean hasAdd = false;
                    for (TeamMember member2 : listMemberNew) {
                        if (member.getAccount().equals(member2.getAccount())) {
                            hasAdd = true;
                            break;
                        }
                    }
                    if (!hasAdd) {
                        listMemberNew.add(member);
                    }
                }
                //获取顾问的ID列表数据
                List<String> aids = new ArrayList<>();
                for (TeamMember member : listMemberNew) {
                    aids.add(member.getAccount());
                }
                fatchMemberIcon(aids);
            }
        });
    }

    /**
     * 获取当前群组的所有顾问的详细数据
     */
    private List<NimUserInfo> mAdviserList = new ArrayList<>();

    private void fatchMemberIcon(final List<String> uids) {

        NIMClient.getService(UserService.class).fetchUserInfo(uids).setCallback(new SubRequestCallback<List<NimUserInfo>>(this) {
            @Override
            public void callBackSuccess(List<NimUserInfo> list) {
                mAdviserList = list;

                //设置顾问数量
                tv_adviser_num.setText("顾问" + "(" + list.size() + ")");
                //刷新顾问列表数据
                adviserAdapter.updateListView(mAdviserList);
            }
        });

    }

    @Override
    public void onRemarksClick(View v, NimUserInfo userInfoEntity) {
        if (NIMClient.getService(FriendService.class).isMyFriend(userInfoEntity.getAccount())) {
            IntentUtils.startActivity(this, IMContractUserRemarkActivity.class, "TEAM_ACCOUNT", userInfoEntity.getAccount());
        } else {
            //非好友添加后跳转
            addFriend(userInfoEntity.getAccount());
        }
    }

    private void addFriend(final String accountId) {
        AddFriendData addFriendData = new AddFriendData(accountId, VerifyType.DIRECT_ADD, null);
        NIMClient.getService(FriendService.class).addFriend(addFriendData).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int i, Void aVoid, Throwable throwable) {
                if (i == ResponseCode.RES_SUCCESS) {
                    //添加成功
                    Intent intent = new Intent(SetIMActivity.this, IMContractUserRemarkActivity.class);
                    intent.putExtra("TEAM_ACCOUNT", accountId);
                    startActivity(intent);
                } else {
                    ToastHelper.showMessage("网络繁忙，请稍后再试");
                }
            }
        });
    }


    @Override
    public void finish() {
        startActivity(IMMainActivity.class);
        super.finish();
    }


    //    新消息观察者
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        private static final long serialVersionUID = 1L;

        @Override
        public void onEvent(List<IMMessage> messages) {
            loadData();
        }
    };

    /**
     * 添加消息接收观察者
     *
     * @param register .
     */
    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, register);
    }

    @Override
    public void onItemClick(View v, NimUserInfo data) {
        String contract = data.getAccount();
        String petName = LoginUserContext.getPickNameByAccid(contract);
        start2Chat(contract, petName, SessionTypeEnum.P2P.name());
    }

    private void start2Chat(String sessionId, String petName, String type) {
        ChatIMActivity.start(this, sessionId, petName, type);
    }
}
