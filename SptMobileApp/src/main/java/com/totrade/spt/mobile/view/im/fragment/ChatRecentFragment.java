package com.totrade.spt.mobile.view.im.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.autrade.spt.common.entity.TblTemplateMasterEntity;
import com.autrade.spt.master.entity.QueryTemplateUpEntity;
import com.autrade.spt.master.service.inf.ITemplateService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.google.gson.reflect.TypeToken;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.view.im.adapter.MainRecentChatAdapter;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.RecentContactSpt;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SubRequestCallback;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.view.customize.SilderListView;
import com.totrade.spt.mobile.view.im.ChatIMActivity;
import com.totrade.spt.mobile.view.im.SetIMActivity;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ChatRecentFragment extends SptMobileFragmentBase implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private MainRecentChatAdapter adapter;
    private List<RecentContactSpt> newMsgList;
    private SilderListView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ChatRecentFragment() {
        setContainerId(R.id.frameLayout);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    getRectentContact();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case 2:
                    getRectentContact();
                    break;
                case 3:
                    findGroupByProduct();
                    findUserTeam();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        getNotifyContent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_chat_fragment, container, false);
        recyclerView = (SilderListView) view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view;
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,
                R.color.swipe_color_2,
                R.color.swipe_color_3,
                R.color.swipe_color_4);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.swipe_background_color);
        swipeRefreshLayout.setProgressViewEndTarget(true, 100);
        swipeRefreshLayout.setOnRefreshListener(this);

        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 进入最近联系人列表界面，建议放在onResume中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        mHandler.sendEmptyMessageDelayed(3, 100);
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

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessage(1);
    }

    private void initData() {
        newMsgList = new ArrayList<>();
        adapter = new MainRecentChatAdapter(getActivity(), newMsgList);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener(this);
        registerObservers(true);

    }

    private void start2Chat(String sessionId, String petName, String type) {
        ChatIMActivity.start(getActivity(), sessionId, petName, type);
    }

    private void getRectentContact() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable e) {

                // recents参数即为最近联系人列表（最近会话列表）
                if (CollectionUtility.isNullOrEmpty(recents)) {
                    return;
                }
                List<RecentContactSpt> tempList = new ArrayList<RecentContactSpt>();
                //遍历云信返回的数据，经过筛选填入数据集
                for (RecentContact rece : recents) {
//                    过滤无效信息 和 自身
                    if (!isValidMsg(rece) || LoginUserContext.getLoginUserDto().getImUserAccid().equalsIgnoreCase(rece.getContactId())) {
                        continue;
                    }
                    //填充新的数据实体，并置入数据集
                    RecentContactSpt spt = new RecentContactSpt();
                    spt.setSessionId(rece.getContactId());
                    spt.setUnReadCount(rece.getUnreadCount());
                    spt.setSessionType(rece.getSessionType());
                    spt.setMsgType(rece.getMsgType());
                    spt.setTime(rece.getTime());
                    spt.setFromNickName(rece.getFromNick());
                    spt.setFromAccid(rece.getFromAccount());
                    spt.setContent(rece.getContent());

                    String sessionId = spt.getSessionId();
                    String name = LoginUserContext.getPickNameByAccid(sessionId);
                    if (TextUtils.isEmpty(name)
                            && !spt.getFromAccount().equalsIgnoreCase(LoginUserContext.getLoginUserAccId())) {
                        name = spt.getFromNickName();//显示昵称
                    }
                    //P2P显示备注名
                    if (spt.getSessionType().equals(SessionTypeEnum.P2P)) {
                        Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(sessionId);
                        if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                            name = friend.getAlias();
                        }
                    }
                    if (rece.getSessionType().name().equals(SessionTypeEnum.Team.name())) {
                        for (Team team : mTeam) {
                            if (team.getId().equals(spt.getSessionId()))
                                name = team.getName();
                        }
                        spt.setContent(rece.getFromNick() + ":" + rece.getContent());//设置显示内容
                        //为所有的群消息设置扩展数据，保存群id对应的产品List,此处设置用于判断群数据是否合并
                        spt.setExtensionsStr(ObjUtils.getKeyFromMap(mProductMap, spt.getSessionId()));
                    }
                    if (rece.getMsgType().equals(MsgTypeEnum.custom)) {
                        spt.setContent("一条询价消息");
                    }
                    spt.setTitle(name);

                    //判断是否有重复数据，此处消息id一样被认为是重复数据，
                    boolean hasRecn = false;
                    for (RecentContactSpt spt1 : tempList) {
                        if (rece.getContactId().equals(spt1.getSessionId())) {
                            hasRecn = true;
                            break;
                        }
                    }
                    if (!hasRecn) {
                        tempList.add(spt);
                    }
                }
                manageRecentData(tempList);
            }
        });
    }

    /**
     * 整理数据，合并
     *
     * @param tempList ..
     */
    private void manageRecentData(List<RecentContactSpt> tempList) {

        if (null != newMsgList && newMsgList.size() > 0)
            newMsgList.clear();
        List<RecentContactSpt> addGourpList = new ArrayList<>();//所有加入组群
        for (int i = 0; i < tempList.size(); i++) {
            if (StringUtility.isNullOrEmpty(tempList.get(i).getExtensionsStr())) {
                newMsgList.add(tempList.get(i));
            } else {
                addGourpList.add(tempList.get(i));
            }
        }
        List<RecentContactSpt> setList = new ArrayList<>();//合并后的组项
        for (int j = 0; j < addGourpList.size(); j++) {
            if (!setList.contains(addGourpList.get(j))) {
                setList.add(addGourpList.get(j));
                int s = setList.indexOf(addGourpList.get(j));//取出包含此项群消息的位置
                RecentContactSpt recentSpt = setList.get(s);//取出合并后的list中此消息项
                recentSpt.setTitle(recentSpt.getExtensionsStr());
                recentSpt.setImgId(R.drawable.im_group_head);
                Map<String, Object> extension = recentSpt.getExtension();
                if (null == recentSpt.getExtension())
                    extension = new HashMap();
                extension.put(addGourpList.get(j).getSessionId(), addGourpList.get(j));
                recentSpt.setExtension(extension);
            } else {
                int s = setList.indexOf(addGourpList.get(j));//取出包含此项群消息的位置
                RecentContactSpt recentSpt = setList.get(s);//取出合并后的list中此消息项
                recentSpt.setTitle(recentSpt.getExtensionsStr());
                recentSpt.setImgId(R.drawable.im_group_head);
                recentSpt.setContent((recentSpt.getTime() >= addGourpList.get(j).getTime() ? recentSpt.getContent() : addGourpList.get(j).getContent()));
                recentSpt.setTime(recentSpt.getTime() >= addGourpList.get(j).getTime() ? recentSpt.getTime() : addGourpList.get(j).getTime());
                recentSpt.setUnReadCount(recentSpt.getUnreadCount() + addGourpList.get(j).getUnreadCount() >= 99 ? 99 : recentSpt.getUnreadCount() + addGourpList.get(j).getUnreadCount());
                Map<String, Object> extension = recentSpt.getExtension();
                extension.put(addGourpList.get(j).getSessionId(), addGourpList.get(j));
                recentSpt.setExtension(extension);
            }
        }
        newMsgList.addAll(setList);
        adapter.notifyDataSetChanged();
        getUserAndTeamInfo();
    }

    /**
     * 获取群列表所有数据，目的取到群名称
     */
    private List<Team> mTeam;

    public void findUserTeam() {
        NIMClient.getService(TeamService.class).queryTeamList()
                .setCallback(new SubRequestCallback<List<Team>>(getActivity()) {
                    @Override
                    public void callBackSuccess(List<Team> listTeamReq) {
                        mTeam = listTeamReq;
                    }
                });
    }

    /**
     * 取所有群ID与产品关系数据
     */
    private Map<String, List<String>> mProductMap;

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
                    mProductMap = JsonUtility.toJavaObject(s, type);
                    mHandler.sendEmptyMessage(2);
                }
            }
        });
    }

    /**
     * 根据取回的新消息去取相应的用户信息和群信息详情
     */
    private void getUserAndTeamInfo() {
        List<String> accids = new ArrayList<String>();
        List<String> tids = new ArrayList<String>();
        String name;
        String icon;
        String contractId;
        for (RecentContactSpt recent : newMsgList) {
            contractId = recent.getSessionId();
            name = LoginUserContext.getPickNameByAccid(contractId);
            icon = LoginUserContext.getIconUrlByAccid(contractId);
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(icon)) {
                if (recent.getSessionType().equals(SessionTypeEnum.P2P)) {
                    accids.add(contractId);
                } else {
                    tids.add(contractId);
                }
            }
        }
        if (!accids.isEmpty()) {
            getUserInfo(accids);
        }
        if (!tids.isEmpty()) {
            getTeamInfo(tids);
        }
    }

    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        private static final long serialVersionUID = 1L;

        @Override
        public void onEvent(List<IMMessage> messages) {
            mHandler.sendEmptyMessageDelayed(1, 100);
        }
    };

    /**
     * 添加消息接收观察者
     *
     * @param register
     */
    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, register);
    }
//
//    /**
//     * 获取通知消息
//     */
//    private void getNotifyContent()
//    {
//        Intent intent = getActivity().getIntent();
//        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT))
//        {
//            @SuppressWarnings("unchecked")
//			ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
//            if (messages != null && messages.size() >= 1)
//            {
//                IMMessage message = messages.get(0);
//                start2Chat(message.getSessionId(), message.getFromNick(),message.getSessionType().name());
//            }
//        }
//    }

    public void updateUserIconAndName() {
        if (!CollectionUtility.isNullOrEmpty(newMsgList)
                && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * 从云信获取所有人的用户信息
     *
     * @param counts
     */
    private void getUserInfo(List<String> counts) {
        NIMClient.getService(UserService.class).fetchUserInfo(counts).setCallback(new SubRequestCallback<List<NimUserInfo>>(getActivity()) {
            @Override
            public void callBackSuccess(List<NimUserInfo> nimUserInfos) {
                //遍历出头像，昵称，用于会话
                Map<String, Map<String, String>> map = LoginUserContext.getMapIMUserInfo();
                if (map == null) map = new HashMap<>();
                Map<String, String> mapSub;
                for (NimUserInfo info : nimUserInfos) {
                    mapSub = new HashMap<>();
                    mapSub.put(AppConstant.ICON_URL, info.getAvatar());
                    mapSub.put(AppConstant.TAG_PETNAME, info.getName());
                    map.put(info.getAccount().toUpperCase(Locale.getDefault()), mapSub);
                }
                LoginUserContext.getMapIMUserInfo().putAll(map);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getTeamInfo(List<String> teamIds) {
        NIMClient.getService(TeamService.class).queryTeamListById(teamIds).setCallback(new SubRequestCallback<List<Team>>(getActivity()) {
            @Override
            public void callBackSuccess(List<Team> nimUserInfos) {
                //遍历出头像，昵称，用于会话
                Map<String, Map<String, String>> map = LoginUserContext.getMapIMUserInfo();
                if (map == null) map = new HashMap<>();
                Map<String, String> mapSub;
                for (Team team : nimUserInfos) {
                    mapSub = new HashMap<>();
                    mapSub.put(AppConstant.ICON_URL, team.getIcon());
                    mapSub.put(AppConstant.TAG_PETNAME, team.getName());
                    map.put(team.getId().toUpperCase(Locale.getDefault()), mapSub);
                }
                LoginUserContext.getMapIMUserInfo().putAll(map);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 退出群  变态点：会收到群系统消息...
     */
    public void delTeam(String tid) {
        if (CollectionUtility.isNullOrEmpty(newMsgList)) {
            return;
        }
        for (int i = newMsgList.size() - 1; i >= 0; i--) {
            if (newMsgList.get(i).getContactId().equalsIgnoreCase(tid)) {
                newMsgList.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 有效信息
     *
     * @param msg //
     * @return ..
     */
    private boolean isValidMsg(RecentContact msg) {
        if (!msg.getMsgType().equals(MsgTypeEnum.notification)) {
            return true;
        }
        //系统消息
        MsgAttachment msgAttachment = msg.getAttachment();
        if (msgAttachment instanceof MemberChangeAttachment) {
            MemberChangeAttachment attachment = (MemberChangeAttachment) msgAttachment;
            String type = attachment.getType().name();
            if (type.equals(NotificationType.MuteTeamMember.name())) {
                //禁言解禁
                return true;
            }
        }
        return false;
    }

//    public void updateUserTeam()
//    {
//    	List<Team> listTeam = null;
//    	
//    	for(int i = msgLst.size()-1;i>=0;i--)
//    	{
//    		for(Team team:listTeam)
//    		{
//    			if(team.getId().equals(msgLst.get(i).getContactId()))
//    			{
//    				msgLst.remove(i);
//    				break;
//    			}
//    		}
//    	}
//    }

    public void removeRecentChat(RecentContactSpt recentContact) {
        NIMClient.getService(MsgService.class).deleteRecentContact(recentContact);
        for (int i = newMsgList.size() - 1; i >= 0; i--) {
            if (newMsgList.get(i).getContactId()
                    .equalsIgnoreCase(recentContact.getSessionId())) {
                newMsgList.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecentContactSpt rec = newMsgList.get(position);
        if (rec.getImgId() != 0) {
            IntentUtils.startActivity(getActivity(), SetIMActivity.class, "TEAM_ID", rec.getSessionId());
            return;
        }
        String contract = rec.getContactId();
        String petName = LoginUserContext.getPickNameByAccid(contract);
        start2Chat(contract, petName, rec.getSessionType().name());
    }

}
