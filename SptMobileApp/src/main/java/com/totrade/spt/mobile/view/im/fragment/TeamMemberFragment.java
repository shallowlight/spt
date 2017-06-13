package com.totrade.spt.mobile.view.im.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.autrade.stage.utility.CollectionUtility;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.SubRequestCallback;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.utility.lettersort.PinYinKit;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileFragmentBase;
import com.totrade.spt.mobile.view.customize.CommonTitle3;
import com.totrade.spt.mobile.view.customize.SideBar;
import com.totrade.spt.mobile.view.im.ChatIMActivity;
import com.totrade.spt.mobile.view.im.GroupMemberActivity;
import com.totrade.spt.mobile.view.im.IMContractUserRemarkActivity;
import com.totrade.spt.mobile.view.im.IMUserInfoEntity;
import com.totrade.spt.mobile.view.im.adapter.TeamMemberAdapter;
import com.totrade.spt.mobile.view.im.common.OnExtendClickListener;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 群成员列表
 *
 * @author huangxy
 * @date 2016/11/30
 */
public class TeamMemberFragment extends SptMobileFragmentBase implements OnExtendClickListener<IMUserInfoEntity> {
    private final static String TAG = "TeamMemberFragment";
    private GroupMemberActivity activity;
    private View rootView;
    private CommonTitle3 title;
    private ListView listView;
    private TextView tvShowLetter;
    private SideBar sideBar;
    private TeamMemberAdapter adapter;
    private List<IMUserInfoEntity> imUserInfoEntityList;

    private String teamId;
    private String petName;

    public TeamMemberFragment() {
        setContainerId(R.id.fl_group_member);
    }

    public void setTeamId(String teamId, String petName) {
        this.teamId = teamId;
        this.petName = petName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (GroupMemberActivity) getActivity();
        rootView = View.inflate(activity, R.layout.fragment_team_member, null);

        listView = (ListView) rootView.findViewById(R.id.listView);
        tvShowLetter = (TextView) rootView.findViewById(R.id.tvShowLetter);
        sideBar = (SideBar) rootView.findViewById(R.id.sideBar);
        title = (CommonTitle3) rootView.findViewById(R.id.title);
        title.getImgBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toIM();
            }
        });

        initSideBar();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initSideBar() {
        String[] bars = {"顾", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
        //user_center_setting sideBar
        sideBar.setB(bars);
        sideBar.setmTextDialog((TextView) rootView.findViewById(R.id.tvShowLetter));
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public void onTouchingLetterChanged(String str) {
                int position = adapter.getPositionForSection(str.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
    }

    private void initData() {
        String teamId = activity.getIntent().getStringExtra(AppConstant.NIM_TEAMID);

        imUserInfoEntityList = new ArrayList<>();

        queryTeamMember(teamId);
    }

    /**
     * 查询群成员
     * 区分管理员普通成员
     *
     * @param tid
     */
    private void queryTeamMember(final String tid) {
        NIMClient.getService(TeamService.class).queryMemberList(tid).setCallback(new SubRequestCallback<List<TeamMember>>(activity) {
            @Override
            public void callBackSuccess(List<TeamMember> teamMembers)
            {
                if (CollectionUtility.isNullOrEmpty(teamMembers))
                {
                    return;
                }
                List<String> accountList = new ArrayList<String>();
                for (TeamMember member : teamMembers)
                {
//                    //                    过滤自己
//                    if (member.getAccount().equalsIgnoreCase(LoginUserContext.getLoginUserDto().getImUserAccid()))
//                    {
//                        continue;
//                    }
                    IMUserInfoEntity imUserInfoEntity = new IMUserInfoEntity();
                    imUserInfoEntity.setLetter("");
                    imUserInfoEntity.setAccount(member.getAccount());
                    imUserInfoEntity.setName(member.getTeamNick());
                    imUserInfoEntity.setType(member.getType());
                    imUserInfoEntityList.add(imUserInfoEntity);
                    accountList.add(member.getAccount());
                }

                int size = imUserInfoEntityList.size();
                int maxFatchSize = 150;
                int times = (size - 1) / maxFatchSize + 1;        //超过150需要分批请求
                List<String> tids;
                for (int i = 0; i < times; i++) {
                    tids = new ArrayList<>();
                    int maxNum = Math.min((i + 1) * maxFatchSize, size);
                    for (int j = i * maxFatchSize; j < maxNum; j++)
                    {
                        tids.add(accountList.get(j));
                    }
                    queryMemberInfo(tids);
                }

            }
        });
    }

    /**
     * 查询群成员资料
     * 获取头像
     *
     * @param lst
     */
    private void queryMemberInfo(List<String> lst)
    {
        NIMClient.getService(UserService.class).fetchUserInfo(lst).setCallback(
                new SubRequestCallback<List<NimUserInfo>>(activity)
                {
                    @Override
                    public void callBackSuccess(List<NimUserInfo> nimUserInfos)
                    {

                        List<IMUserInfoEntity> totalList = new ArrayList<IMUserInfoEntity>();
                        List<IMUserInfoEntity> managerList = new ArrayList<IMUserInfoEntity>();     //管理员list
                        List<IMUserInfoEntity> normalList = new ArrayList<IMUserInfoEntity>();     //普通成员list

                        for (IMUserInfoEntity imUserInfoEntity : imUserInfoEntityList) {
//                            区分管理员和普通成员
                            if (imUserInfoEntity.getType().equals(TeamMemberType.Owner) || imUserInfoEntity.getType().equals(TeamMemberType.Manager)) {
                                imUserInfoEntity.setLetter("顾问");
                                managerList.add(imUserInfoEntity);
                            } else {
                                normalList.add(imUserInfoEntity);
                            }
//                            头像和名称
                            for (NimUserInfo userInfo : nimUserInfos) {
                                if (imUserInfoEntity.getAccount().equalsIgnoreCase(userInfo.getAccount())) {
                                    imUserInfoEntity.setAvatar(userInfo.getAvatar());
                                    if (TextUtils.isEmpty(imUserInfoEntity.getName())) {
                                        imUserInfoEntity.setName(userInfo.getName());
                                    }

//                                    备注名
                                    Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(imUserInfoEntity.getAccount());
                                    if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                                        imUserInfoEntity.setName(friend.getAlias());
                                    }
                                }
                            }
                        }

                        sortList(normalList);
                        totalList.addAll(normalList);
                        totalList.addAll(0, managerList);
                        if (adapter == null) {
                            adapter = new TeamMemberAdapter(activity, totalList);
                            adapter.setOnExtendClickListener(TeamMemberFragment.this);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.updateListView(totalList);
                        }
                    }
                });
    }

    private void sortList(List<IMUserInfoEntity> normalList) {
        List<IMUserInfoEntity> infoList = new ArrayList<>();
        try {
            infoList = filledData(normalList);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        Collections.sort(infoList, comparator);
    }

    private Comparator comparator = new Comparator<IMUserInfoEntity>() {

        @Override
        public int compare(IMUserInfoEntity lhs, IMUserInfoEntity rhs) {
            if (!lhs.getLetter().equals("#") && rhs.getLetter().equals("#"))
                return -1;
            else if (lhs.getLetter().equals("#") && !rhs.getLetter().equals("#"))
                return 1;
            else
                return lhs.getLetter().compareTo(rhs.getLetter());
        }
    };

    private List<IMUserInfoEntity> filledData(List<IMUserInfoEntity> list) throws BadHanyuPinyinOutputFormatCombination {
        for (int i = 0; i < list.size(); i++) {
            IMUserInfoEntity sortModel = list.get(i);
            String pinyin = PinYinKit.getPingYin(sortModel.getName());
            if(TextUtils.isEmpty(pinyin))
            {
                pinyin = "#";
            }
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setLetter(sortString.toUpperCase());
            } else {
                sortModel.setLetter("#");
            }
        }
        return list;

    }

    @Override
    public void onExtendClick(View v, IMUserInfoEntity userInfoEntity) {
        if (NIMClient.getService(FriendService.class).isMyFriend(userInfoEntity.getAccount())) {
            IntentUtils.startActivity(activity, IMContractUserRemarkActivity.class, "TEAM_ACCOUNT", userInfoEntity.getAccount());
        } else {
//            非好友添加后跳转
            addFriend(userInfoEntity.getAccount());
        }
    }

    private void addFriend(final String accountId) {
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(accountId, VerifyType.DIRECT_ADD, null))
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int i, Void aVoid, Throwable throwable) {
                        if (i == ResponseCode.RES_SUCCESS) {
//                            添加成功
                            IntentUtils.startActivity(activity, IMContractUserRemarkActivity.class, "TEAM_ACCOUNT", accountId);
                        } else {
                            ToastHelper.showMessage("网络繁忙，请稍后再试");
                        }
                    }
                });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toIM();
            return true;
        }
        return false;
    }

    private void toIM() {
        ChatIMActivity.start(activity, teamId, petName, SessionTypeEnum.Team.name());
        activity.finish();
    }

}
