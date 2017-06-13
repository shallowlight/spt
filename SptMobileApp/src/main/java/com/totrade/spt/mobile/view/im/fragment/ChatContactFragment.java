package com.totrade.spt.mobile.view.im.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.autrade.stage.utility.CollectionUtility;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.im.adapter.MainContractAdapter;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.SoftTeamMember;
import com.totrade.spt.mobile.utility.SubRequestCallback;
import com.totrade.spt.mobile.utility.lettersort.PinYinKit;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.view.customize.SideBar;
import com.totrade.spt.mobile.view.customize.SilderListView;
import com.totrade.spt.mobile.view.im.ChatIMActivity;
import com.totrade.spt.mobile.view.im.IMMainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A fragment of contract list
 * 联系人列表
 */
public class ChatContactFragment extends SptMobileFragmentBase implements AdapterView.OnItemClickListener {
    @BindViewId(R.id.sideBar)
    private SideBar sideBar;
    @BindViewId(R.id.listView)
    private SilderListView listView;
    private MainContractAdapter adapter;
    private List<Team> listTeam;                            //我的所有群列表
    private List<Object> listObject;                        //某个群状态下的联系人列表
    private Map<String, List<SoftTeamMember>> mapMember;        //群id与联系人列表
//    private Map<String,Map<String,String>> userInfo;	//存储头像和昵称

	public enum ListAction{
		REMARK,EXIT_TEAM
    }

    public ChatContactFragment() {
        setContainerId(R.id.frameLayout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_contact_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        findUserTeam();
    }

    private void initData() {
        listTeam = new ArrayList<>();
        listObject = new ArrayList<>();
        mapMember = new HashMap<>();
        adapter = new MainContractAdapter(getActivity(), listObject);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        String[] bars = {"群", "顾", "↑", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
        //user_center_setting sideBar
        sideBar.setB(bars);
        sideBar.setmTextDialog((TextView) getView().findViewById(R.id.tvShowLetter));
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public void onTouchingLetterChanged(String str) {
                int position = 0;
                if (str.equals("群")) {
                    position = 0;        //群数量不会太多,故直接返回0；
                } else if (str.equals("顾")) {
                    if (listTeam == null) {
                        position = 0;
                    } else {
                        position = listTeam.size();
                    }
                } else {
                    position = adapter.getPositionForSection(str.charAt(0));
                }
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });

    }

    public void removeTeam(final Team team) {
        if (team == null) return;
        NIMClient.getService(TeamService.class).quitTeam(team.getId())
                .setCallback(new SubRequestCallback<Void>(getActivity()) {
                    @Override
                    public void callBackSuccess(Void t) {
                        ToastHelper.showMessage("退出成功");
                        listTeam.remove(team);
                        if (listTeam.isEmpty()) {
                            listObject.clear();
                            adapter.setTeam(null);
                            adapter.notifyDataSetChanged();
                        } else {
                            if (team.getId().equals(adapter.getTeam().getId())) {
                                selectTeam(listTeam.get(0));
                            } else {
                                updateView();
                            }
                        }
                        ((IMMainActivity) getActivity()).recentChatFragment.delTeam(team.getId());
                    }
                });
    }

    public void selectTeam(Team team) {
        if (team != null && !team.getId().equals(adapter.getTeam().getId())) {
            adapter.setTeam(team);
            if (!mapMember.containsKey(team.getId())) {
                queryMemberList(team.getId());
            }
            updateView();
        }
        listView.setSelection(listTeam.size() + 1);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ListView listView = (ListView) adapterView;
        Object obj = listView.getItemAtPosition(position);
        if (obj == null) {
            return;
        }
        if (obj instanceof Team) {
            ChatIMActivity.start(getActivity(), ((Team) obj).getId(), ((Team) obj).getName(), SessionTypeEnum.Team.name());
        } else if (obj instanceof SoftTeamMember) {
            ChatIMActivity.start(getActivity(), ((SoftTeamMember) obj).teamMember.getAccount(), ((SoftTeamMember) obj).petName, SessionTypeEnum.P2P.name());
        } else {
            //String
            return;
        }
    }

    /**
     * 遍历出索引
     */
    private @NonNull ArrayList<String> loopIndex(@NonNull List<SoftTeamMember> list) {
//        if (CollectionUtility.isNullOrEmpty(list)) {
//            return null;
//        }
        ArrayList<String> array = new ArrayList<>();
        char c;
        for (SoftTeamMember member : list) {
            if (!member.teamMember.getType().name().equals(TeamMemberType.Normal.name())) {
                continue;
            }
            if(TextUtils.isEmpty(member.pinyin))
            {
                member.pinyin ="#";
            }
            c = member.pinyin.charAt(0);
            if (c > '0' && c < '9') {
                c = '↑';
            }
            if (!array.contains(String.valueOf(c))) {
                array.add(String.valueOf(c));
            }
        }
//    	ArrayList<String> arrBar
//    	sideBar.setB(bars);
        return array;
    }

    /**
     * 更新界面
     */
    public void updateView() {
        Team team = adapter.getTeam();
        listObject.clear();
        if (listTeam == null || listTeam.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }
        //添加群组
        listObject.add("群组");
        listObject.addAll(listTeam);
        //群成员添加索引
        List<SoftTeamMember> listMember = mapMember.get(team.getId());
        if (listMember != null && !listMember.isEmpty()) {
            listObject.add(getString(R.string.im_admin));
            ArrayList<String> indexs = loopIndex(listMember);
            List<Object> list2 = new ArrayList<>();
            list2.addAll(indexs);
            list2.addAll(listMember);
            //群成员排序
            Collections.sort(list2, comparator);
            //添加群成员
            listObject.addAll(list2);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取群列表
     */
    public void findUserTeam() {
        NIMClient.getService(TeamService.class).queryTeamList()
                .setCallback(new SubRequestCallback<List<Team>>(getActivity()) {
                    @Override
                    public void callBackSuccess(List<Team> listTeamReq) {
                        listTeam = listTeamReq;
                        Team team = listTeam.get(0);
                        adapter.setTeam(team);
                        updateView();
                        queryMemberList(team.getId());                //获取群成员
                    }
                });
    }

    /**
     * 根据群获取群成员列表
     */
    private void queryMemberList(final String tid) {
        NIMClient.getService(TeamService.class).queryMemberList(tid)
                .setCallback(new SubRequestCallback<List<TeamMember>>(getActivity()) {
                    @Override
                    public void callBackSuccess(List<TeamMember> listMember) {
                        List<SoftTeamMember> lst = member2SoftMember(listMember);
//                        for (SoftTeamMember member : lst) {
//                            // TODO: 2016/11/3
//                            Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(member.teamMember.getAccount());
//                            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
//                                member.petName = friend.getAlias();
//                            }
//                        }
                        mapMember.put(tid, lst);
                        fatchMemberIcon(tid);
//				需要添加 检索列表
//				updateView();
                    }
                });
    }

    /**
     * 数据格式之间转换 不带设置
     */
    private List<SoftTeamMember> member2SoftMember(List<TeamMember> listMember) {
        if (CollectionUtility.isNullOrEmpty(listMember)) return null;
        List<SoftTeamMember> softList = new ArrayList<>();
        SoftTeamMember member;
        String userAccid = LoginUserContext.getLoginUserDto().getImUserAccid();
        for (TeamMember m1 : listMember) {
            if (m1.getAccount().equalsIgnoreCase(userAccid)) {
                continue;
            }
            member = new SoftTeamMember();
            member.teamMember = m1;
            softList.add(member);
        }
        return softList;
    }

    private static final int maxFatchSize = 150;

    //获取用户头像id;分批，每次最多150个
    private void fatchMemberIcon(final String tid) {
        List<SoftTeamMember> list = mapMember.get(tid);
        if (list == null || list.isEmpty()) return;
        List<String> tids;
        int size = list.size();
        int times = (size - 1) / maxFatchSize + 1;        //超过150需要分批请求
        for (int i = 0; i < times; i++) {
            tids = new ArrayList<>();
            int maxNum = Math.min((i + 1) * maxFatchSize, size);
            for (int j = i * maxFatchSize; j < maxNum; j++) {
                tids.add(list.get(j).teamMember.getAccount());
            }
            //请求网络
            NIMClient.getService(UserService.class).fetchUserInfo(tids)
                    .setCallback(new SubRequestCallback<List<NimUserInfo>>(getActivity()) {
                        @Override
                        public void callBackSuccess(List<NimUserInfo> list) {
                            if (list != null && !list.isEmpty()) {
                                loopUserIconName(list);
                            }
                            else
                            {
                                return;
                            }
                            List<SoftTeamMember> lst = mapMember.get(tid);
                            String pinyin;
                            for (NimUserInfo info : list) {
                                for (SoftTeamMember member : lst) {

                                    if (member.teamMember.getAccount().equals(info.getAccount())) {
                                        member.iconUrl = info.getAvatar();
                                        member.petName = info.getName();

                                        if (!member.teamMember.getType().name().equals(TeamMemberType.Normal.name())) {
                                            pinyin = "顾";
                                        } else {
                                            try {
                                                pinyin = PinYinKit.getPingYin(member.getNameInT()).toUpperCase(Locale.getDefault());
                                            } catch (Exception e) {
                                                pinyin = "#";
                                            }
                                        }
                                        member.pinyin = pinyin;

                                        // TODO: 2016/11/3
                                        Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(member.teamMember.getAccount());
                                        if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                                            try {
                                                pinyin = PinYinKit.getPingYin(friend.getAlias()).toUpperCase();
                                            } catch (Exception e)
                                            {
                                                pinyin = "#";
                                            }
                                            member.pinyin = pinyin;
                                        }
                                        break;
                                    }
                                }
                            }

                            //更新头像界面
                            updateView();
                            if (tid.equals(adapter.getTeam().getId())) {
                                listView.setSelection(listTeam.size() + 1);
                            }
                        }
                    });
        }
    }


    private void loopUserIconName(List<NimUserInfo> list) {
        Map<String, Map<String, String>> userInfo = LoginUserContext.getMapIMUserInfo();
        Map<String, String> mapSub;
        for (NimUserInfo info : list) {
            mapSub = new HashMap<>();
            mapSub.put(AppConstant.ICON_URL, info.getAvatar());
            mapSub.put(AppConstant.TAG_PETNAME, info.getName());
            userInfo.put(info.getAccount(), mapSub);
        }
        LoginUserContext.setMapIMUserInfo(userInfo);

    }


    /**
     * 排序  顾问排第一，# 排第二，其余后面
     * String ,SoftTeamMember
     */
    private Comparator<Object> comparator = new Comparator<Object>() {
        String s1;
        String s2;

        @Override
        public int compare(Object lhs, Object rhs) {
            if (lhs instanceof SoftTeamMember) {
                if (!((SoftTeamMember) lhs).teamMember.getType().equals(TeamMemberType.Normal)) {
                    return -1;
                }
                s1 = ((SoftTeamMember) lhs).pinyin;
            } else {
                s1 = lhs.toString();
            }

            if (rhs instanceof SoftTeamMember) {
                if (!((SoftTeamMember) rhs).teamMember.getType().equals(TeamMemberType.Normal)) {
                    return 1;
                }
                s2 = ((SoftTeamMember) rhs).pinyin;
            } else {
                s2 = rhs.toString();
            }

            if (s1.startsWith("↑") && !s2.startsWith("↑")) {
                return -1;
            }
            if (!s1.startsWith("↑") && s2.startsWith("↑")) {
                return 1;
            }

            if (s1.startsWith("#") && !s2.startsWith("#")) {
                return 1;
            }
            if (!s1.startsWith("#") && s2.startsWith("#")) {
                return -1;
            }
            return s1.compareToIgnoreCase(s2);
        }

        //            备注名
        private void getAlias(SoftTeamMember lhs, String s) {
            Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(lhs.teamMember.getAccount().toLowerCase());
            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                Log.i(TAG, "getAlias: friend.getAlias() ------ " + friend.getAlias());
                s = friend.getAlias();
            }
        }
    };

    private final static String TAG = "ChatContactFragment";
}
