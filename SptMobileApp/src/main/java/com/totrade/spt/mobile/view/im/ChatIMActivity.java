package com.totrade.spt.mobile.view.im;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.nego.entity.GetBuySellIntensionDetailUpEntity;
import com.autrade.spt.nego.entity.TblSubmitBSEntity;
import com.autrade.spt.nego.service.inf.IBuySellIntensionService;
import com.autrade.spt.report.entity.QueryIMUserLockUpEntity;
import com.autrade.spt.report.entity.TblIMUserLockEntity;
import com.autrade.spt.report.service.inf.IIMUserLockService;
import com.autrade.stage.droid.annotation.Injection;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.adapter.ChatAdapter;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.RecentContactSpt;
import com.totrade.spt.mobile.entity.nim.OrderAttachment;
import com.totrade.spt.mobile.helper.RequestIMUserInfoHelper;
import com.totrade.spt.mobile.receiver.NIMMessageReceiver;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FileUtils;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SubRequestCallback;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.utility.ViewExpandAnimation;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.SplashActivity;
import com.totrade.spt.mobile.view.customize.CommonTitle3;
import com.totrade.spt.mobile.view.customize.PopWindowShare;
import com.totrade.spt.mobile.view.customize.TouchImageView;
import com.totrade.spt.mobile.view.im.emoj.EmoticonPickerView;
import com.totrade.spt.mobile.view.im.emoj.IEmoticonSelectedListener;
import com.totrade.spt.mobile.view.im.emoj.MoonUtil;
import com.totrade.spt.mobile.view.im.record.AudioRecordCallback;
import com.totrade.spt.mobile.view.im.record.RecordHelper;
import com.totrade.spt.mobile.view.im.record.RecordView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatIMActivity extends SptMobileActivityBase
        implements OnClickListener, SwipeRefreshLayout.OnRefreshListener, IEmoticonSelectedListener {
    public final static String TAG = "ChatIMActivity";

    private static final int OPEN_CAMERA_CODE = 10;// 打开相机
    private static final int OPEN_PICTURE_CODE = 11;// 打开相册
    private File tempFile;
    @BindViewId(R.id.ll_send)
    private LinearLayout ll_send; // 包含文本输入，发送，图片，音频选择
    @BindViewId(R.id.lv_chat)
    private ListView lv_chat;
    @BindViewId(R.id.et_content)
    private EditText et_content;    // 文本输入框
    @BindViewId(R.id.ll_audio)
    private LinearLayout ll_audio;  // 录音布局
    @BindViewId(R.id.ll_emoj)
    private LinearLayout ll_emoj;  // 颜文字布局
    @BindViewId(R.id.fl_input_panel)
    private FrameLayout flInputPanel;
    @BindViewId(R.id.iv_record)
    private RecordView iv_record;   // 录音按钮
    @BindViewId(R.id.iv_emoj)
    private ImageView iv_emoj;     // 颜文字
    @BindViewId(R.id.iv_audio)
    private ImageView iv_audio;
    @BindViewId(R.id.tv_description)
    private TextView tv_description;
    @BindViewId(R.id.swipeLayout)
    private SwipeRefreshLayout swipeRefreshLayout;
    @BindViewId(R.id.title)
    private CommonTitle3 title;
    @BindViewId(R.id.tv_send)
    private TextView tv_send;
    @BindViewId(R.id.view_no_speech)
    private View viewMute;
    @BindViewId(R.id.emoticon_picker_view)
    private EmoticonPickerView emoticon_picker_view;

    private int valueHeight; // 弹出布局高度
    private List<IMMessage> msgList;
    private ChatAdapter adapter;
    private ViewExpandAnimation viewExpandAnimation;
    private int pageNum = 0;

    public static final String EXTRA_SESSION_TYPE = "EXTRA_SESSION_TYPE";
    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String EXTRA_SESSION_NAME = "EXTRA_SESSION_NAME";
    public static final String EXTRA_LAST_CLASS = "EXTRA_LAST_CLASS";

    public static void start(Context context, String sessionId, String petName, String type) {
        Intent intent = new Intent(context, ChatIMActivity.class);
        intent.putExtra(EXTRA_LAST_CLASS, context.getClass().getName());
        intent.putExtra(EXTRA_SESSION_TYPE, type);
        intent.putExtra(EXTRA_SESSION_ID, sessionId.toLowerCase(Locale.getDefault()));
        intent.putExtra(EXTRA_SESSION_NAME, petName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imchat);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_camera).setOnClickListener(this);
        findViewById(R.id.iv_picture).setOnClickListener(this);
        tv_send.setOnClickListener(this);
        iv_audio.setOnClickListener(this);
        iv_emoj.setOnClickListener(this);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1,
                R.color.swipe_color_2,
                R.color.swipe_color_3,
                R.color.swipe_color_4);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.swipe_background_color);
        swipeRefreshLayout.setProgressViewEndTarget(true, 100);
        swipeRefreshLayout.setOnRefreshListener(this);

        viewExpandAnimation = new ViewExpandAnimation();
        iv_record.setOnVisibilityListener(new RecordView.OnVisibilityListener() {

            @Override
            public void setVisi(boolean isVisi) {
                ll_send.setVisibility(isVisi ? View.VISIBLE : View.GONE);
                tv_description.setVisibility(isVisi ? View.GONE : View.VISIBLE);
            }
        });

        et_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                viewExpandAnimation.animateClose(flInputPanel);
            }
        });
        et_content.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.start = start;
                this.count = count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MoonUtil.replaceEmoticons(ChatIMActivity.this, s, start, count);
                int editEnd = et_content.getSelectionEnd();
                et_content.removeTextChangedListener(this);
                MoonUtil.identifyFaceExpression(ChatIMActivity.this, et_content, s.toString(), ImageSpan.ALIGN_BOTTOM);
                while (FormatUtil.counterChars(s.toString()) > 5000 && editEnd > 0) {
                    s.delete(editEnd - 1, editEnd);
                    editEnd--;
                }
                et_content.setSelection(editEnd);
                et_content.addTextChangedListener(this);

//                显示正在发送
            }
        });
    }

    private void initData() {
        msgList = new ArrayList<>();
        adapter = new ChatAdapter(this, msgList);
        lv_chat.setAdapter(adapter);
        try {
            parseIntent();
            getMsgStatus();
        } catch (Exception e) {
            if (DictionaryUtility.getDicMap().isEmpty()) {
                startActivity(SplashActivity.class);
            } else {
                startActivity(IMMainActivity.class);
            }
        }
    }

    private static PopupWindow popupWindow;

    public void showPopupWindow(String url, ImageAttachment attachment)
    {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(SptApplication.context).inflate(R.layout.pop_window_im, null);
        final TouchImageView iv_preview = (TouchImageView) contentView.findViewById(R.id.iv_preview);
        Picasso.with(SptApplication.context).load(url).placeholder(R.drawable.wait_bg).into(iv_preview);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);
        iv_preview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopWindow();
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAsDropDown(title);

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    pageNum += 1;
                    IMMessage anchor;
                    if (pageNum == 1 || CollectionUtility.isNullOrEmpty(msgList)) {
                        anchor = MessageBuilder.createEmptyMessage(sessionId, sessionType, 0);
                    } else {
                        // msgList 不为空
                        anchor = msgList.get(msgList.size() - 1);
                    }

                    pullMessageHistory(anchor);
                    swipeRefreshLayout.setRefreshing(false);
                    // swipeRefreshLayout.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    };

    private String sessionId;
    private SessionTypeEnum sessionType;
    private String petName;

    private void parseIntent()
    {
        sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        NIMMessageReceiver.setImSessionId(sessionId);
        if (!TextUtils.isEmpty(sessionId))
        {
            String sessionTypeStr = getIntent().getStringExtra(EXTRA_SESSION_TYPE);
            sessionType = sessionTypeStr.equals(SessionTypeEnum.P2P.name()) ? SessionTypeEnum.P2P : SessionTypeEnum.Team;
            petName = getIntent().getStringExtra(EXTRA_SESSION_NAME);
        }
        else
        {
            RecentContactSpt recentContact = (RecentContactSpt) getIntent().getSerializableExtra(AppConstant.RECENT_CONTACT);
            if (recentContact == null)
            {
                finish();
                return;
            }
            sessionId = recentContact.getSessionId();
            sessionType = recentContact.getSessionType().name().equals(SessionTypeEnum.P2P.name()) ? SessionTypeEnum.P2P : SessionTypeEnum.Team;
            petName = LoginUserContext.getPickNameByAccid(sessionId);
        }
        if (TextUtils.isEmpty(petName)) {
            if (sessionType.name().equals(SessionTypeEnum.P2P.name())) {
                List<String> list = new ArrayList<>();
                list.add(sessionId);
                fatchMemberIcon(list, false);
            } else {
                quetyTeamInfo(sessionId);
                queryMemberList(sessionId);    //获取群成员
            }
//			getUserEntity(sessionId);
        } else {
            title.setTitle(petName);
        }

        registerObservers(true);
        onRefresh();
        if (sessionType.equals(SessionTypeEnum.Team))
        {
            title.getTitleRightImg(R.drawable.im_team).setOnClickListener(this);
            title.getTitleRight2Img(R.drawable.ic_action_share).setOnClickListener(this);
            findPageIMUserLock();
        }
        else
        {
            title.getTitleRightImg(R.drawable.im_team).setVisibility(View.INVISIBLE);
            title.getTitleRight2Img(R.drawable.ic_action_share).setVisibility(View.INVISIBLE);
        }

        NotifyUtility.cancelIM(this, sessionId);    //按不同tag取消notify
    }

    // TODO: pullMessageHistory
    private void pullMessageHistory(final IMMessage anchor) {
        InvocationFuture<List<IMMessage>> pullMessageHistory = NIMClient.getService(MsgService.class).pullMessageHistory(anchor, 15, false);
        pullMessageHistory.setCallback(new RequestCallbackWrapper<List<IMMessage>>() {

            @Override
            public void onResult(int error, List<IMMessage> messages, Throwable throwable) {
                if (!CollectionUtility.isNullOrEmpty(messages)) {
                    for (IMMessage msg : messages) {
                        if (isValidMsg(msg)) {
                            msgList.add(msg);
                        }
                    }
                    try2QueryNegoStatus();


                    if (pageNum == 1) {
                        adapter.notifyDataChanged();
                        lv_chat.setSelection(msgList.size() - 1);
                    } else {
                        pullHistoryUpdateListView(anchor);
                    }
                }
            }
        });
    }

    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void onEvent(List<IMMessage> messages) {
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            for (IMMessage msg : messages) {
                if (msg.getSessionId().equals(sessionId)) {
                    if (isValidMsg(msg)) {
                        msgList.add(0, msg);
                    }
                }
            }
            try2QueryNegoStatus();
            adapter.notifyDataChanged();
            lv_chat.setSelection(msgList.size() - 1);
        }
    };

    private boolean isValidMsg(IMMessage msg) {
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


    /**
     * 添加消息接收观察者
     *
     * @param register
     */
    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgRight:
                Intent intent = new Intent(this, GroupMemberActivity.class);
                intent.putExtra(GroupMemberActivity.TEAM_MEMBER, GroupMemberActivity.TEAM_MEMBER);
                intent.putExtra(AppConstant.NIM_TEAMID, sessionId);
                intent.putExtra(AppConstant.NIM_NICKNAME, petName);
                startActivity(intent);
                break;
            case R.id.imgRight2:
                showSharePopWindow();
                break;
            case R.id.et_content:

                break;
            case R.id.iv_audio:
                switchAudioLayout();
                break;
            case R.id.iv_emoj:
                switchEmojLayout();
                break;
            case R.id.iv_camera:
                toCamera();
                break;
            case R.id.iv_picture:
                toPicture();
                break;
            case R.id.iv_record:
                // 自定义recordview处理点击事件
                break;
            case R.id.tv_send:
                String msg = et_content.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    sendText(msg);
                }
                et_content.setText("");
                adapter.notifyDataChanged();
                break;
            default:
                break;
        }
    }

    private void switchAudioLayout() {
        if (flInputPanel.isShown() && !ll_audio.isShown()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_audio.performClick();
                }
            }, 180);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ll_emoj.setVisibility(View.GONE);
                ll_audio.setVisibility(View.VISIBLE);
            }
        }, 150);
        valueHeight = FormatUtil.dip2px(this, 170);
        if (viewExpandAnimation.audioLayout(flInputPanel, valueHeight)) {
            if (ll_audio.isShown()) sendAudio();
        }
        hidenInput();
    }

    private void switchEmojLayout() {
        if (flInputPanel.isShown() && !ll_emoj.isShown()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_emoj.performClick();
                }
            }, 180);
        }
        valueHeight = FormatUtil.dip2px(this, 200);
        viewExpandAnimation.audioLayout(flInputPanel, valueHeight);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                emoticon_picker_view.show(ChatIMActivity.this);
                ll_emoj.setVisibility(View.VISIBLE);
                ll_audio.setVisibility(View.GONE);
            }
        }, 150);
        hidenInput();
    }

    private void toCamera() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        tempFile = new File(path, FileUtils.getFileName());
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri u = Uri.fromFile(tempFile);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, u);
        if (intentCamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentCamera, OPEN_CAMERA_CODE);
        }
    }

    private void toPicture() {
        Intent intentPick = new Intent(Intent.ACTION_PICK);
        intentPick.setType("image/*");
        startActivityForResult(intentPick, OPEN_PICTURE_CODE);
    }

    private void hidenInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); // 强制隐藏键盘
    }


    /**
     * 拉去历史后更新界面
     *
     * @param anchor
     */
    private void pullHistoryUpdateListView(IMMessage anchor) {
        adapter.notifyDataChanged();
        int p = msgList.size() - 1 - msgList.indexOf(anchor);
        lv_chat.setSelectionFromTop(p, 0);
    }

    private void sendText(String content) {
        msgList.add(0, MsgHelper.sendText(sessionId, sessionType, content));
        adapter.notifyDataChanged();
        lv_chat.setSelection(msgList.size() - 1);
    }

    private Map<String, IMMessage> photoMsgs = new HashMap<>();

    private void sendPhoto(File file) {
        IMMessage photoMsg = MsgHelper.sendPhoto(sessionId, sessionType, file);
        showDialog();
        photoMsgs.put(photoMsg.getUuid(), photoMsg);
        msgList.add(0, photoMsg);
        adapter.notifyDataChanged();
        lv_chat.setSelection(msgList.size() - 1);
    }

    private void sendAudio() {
        RecordHelper.setAudioRecordCallback(new AudioRecordCallback() {
            @Override
            public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
                super.onRecordSuccess(audioFile, audioLength, recordType);
                msgList.add(0, MsgHelper.sendAudio(sessionId, sessionType, audioFile, audioLength));
                adapter.notifyDataChanged();
                lv_chat.setSelection(msgList.size() - 1);
            }
        });
    }

    private void sendCustomMsg(String order) {
        String content = "custom message";
        OrderAttachment attachment = new OrderAttachment();
        msgList.add(0, MsgHelper.sendCustomMsg(sessionId, sessionType, content, attachment));
        adapter.notifyDataChanged();
        lv_chat.setSelection(msgList.size() - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // TODO 拿到图片文件
            if (requestCode == OPEN_CAMERA_CODE) {
                // File photoFile = tempFile;
                previewPhoto(tempFile);
            } else if (requestCode == OPEN_PICTURE_CODE) {
                Uri u = data.getData();
                // File photoFile = getRealPathFromUri(u);
                previewPhoto(FileUtils.getRealPathFromUri(u));
            }
        } else if (resultCode == 100) {
            // 拿到挂单数据
            String order = data.getStringExtra("order");
            sendCustomMsg(order);
            Log.i(TAG, "onActivityResult: resultCode" + resultCode);
        }
    }

    /**
     * 预览图片
     *
     * @param file
     */
    private void previewPhoto(File file) {
        // TODO preview
        sendPhoto(file);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
        dismissPopWindow();
        dismissDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (flInputPanel.isShown()) {
            viewExpandAnimation.audioLayout(flInputPanel, valueHeight);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessage(1);
    }

    // 监听消息发送状态的变化通知
    private void getMsgStatus() {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(new Observer<IMMessage>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(IMMessage imMessage) {
                int p = -1;
                for (int i = 0; i < msgList.size(); i++) {
                    if (msgList.get(i).getUuid().equals(imMessage.getUuid())) {
                        p = i;
                    }
                }

                if (p != -1) {
                    msgList.remove(p);
                    msgList.add(p, imMessage);
                }

                if (photoMsgs.containsKey(imMessage.getUuid())
                        && imMessage.getStatus().equals(MsgStatusEnum.success)) {
                    dismissDialog();
                }
                adapter.notifyDataChanged();
                lv_chat.setSelection(msgList.size() - 1);
            }
        }, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setAlias();
        // 进入聊天界面，建议放在onResume中
        NIMMessageReceiver.setImSessionId(sessionId);
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NIMMessageReceiver.setImSessionId(null);
        // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
    }

    /**
     * 显示备注名
     */
    private void setAlias() {
        if (sessionType.equals(SessionTypeEnum.P2P)) {
            Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(sessionId.toLowerCase());
            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                title.setTitle(friend.getAlias());
            }
        }
    }

    /**
     * 获取云信账户禁言/解禁列表
     */
    private void findPageIMUserLock() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<TblIMUserLockEntity>>() {
            String IMUserAccid = LoginUserContext.getLoginUserDto().getImUserAccid();

            @Override
            public List<TblIMUserLockEntity> requestService() throws DBException, ApplicationException {
                QueryIMUserLockUpEntity upEntity = new QueryIMUserLockUpEntity();
                upEntity.setTid(sessionId);
                upEntity.setAccid(IMUserAccid);
                upEntity.setMute("1");
                return Client.getService(IIMUserLockService.class).findPageIMUserLock(upEntity).getDataList();
            }

            @Override
            public void onDataSuccessfully(List<TblIMUserLockEntity> resultList) {
                if (resultList != null) {
                    for (TblIMUserLockEntity tblIMUserLockEntity : resultList) {
                        if (tblIMUserLockEntity.getAccid().equals(IMUserAccid)) {
                            Date date = tblIMUserLockEntity.getExpireTime();
                            isMute(true, date);
                            break;
                        }
                    }
                }
            }

        });
    }

    /**
     * 禁言或者解禁时更变状态
     *
     * @param ismute
     * @param date
     */
    public void isMute(boolean ismute, Date date) {
        String hint = new String();
        if (ismute) {
            viewMute.setVisibility(View.VISIBLE);
            tv_send.setVisibility(View.GONE);
            String time = FormatUtil.countDown(date);
            if (time == null) {
                hint = "您已经被禁言"; // time为null永久禁言
            } else if (time.equals("1")) {
                hint = "您已被主持人禁言"; // 暂时未获取time
            } else {
                hint = "您已被主持人禁言," + time + "后自动解除";
            }
            viewMute.setClickable(true);
            et_content.setEnabled(false);
        } else {
            viewMute.setVisibility(View.GONE);
            tv_send.setVisibility(View.VISIBLE);
            hint = getResources().getString(R.string.im_input_hint);
            et_content.setEnabled(true);
        }
        et_content.setHint(hint);
    }

    private void dismissPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private Dialog dialog;

    private void showDialog() {
        dialog = new Dialog(this, R.style.Custom_Progress);
        View view = View.inflate(this, R.layout.dialog_layout, null);
        ((TextView) view.findViewById(R.id.lblMessage)).setText(getResources().getString(R.string.uploading));
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        String sessionId2 = getIntent().getStringExtra(EXTRA_SESSION_ID);
        if(TextUtils.isEmpty(sessionId2) && !TextUtils.isEmpty(sessionId))
        {
            return;
        }
        setIntent(intent);
        initData();

        String close = intent.getStringExtra("CLOSE");
        if (!TextUtils.isEmpty(close) && close.equals("ChatIM"))
        {
            String lastClass = getIntent().getStringExtra(EXTRA_LAST_CLASS);
            intent.putExtra("CLOSE", "");
            intent.putExtra(EXTRA_LAST_CLASS, lastClass);
            if (lastClass != null && lastClass.equals(HomeActivity.class.getName())) {
                getIntent().putExtra(EXTRA_LAST_CLASS, "");
            }
            finish();
            this.startActivity(intent);
        }
    }

    /**
     * 查看order详情
     *
     * @param attachment .
     */
    public void showOrderDetail(final OrderAttachment attachment)
    {
        if (!LoginUserContext.canDoMatchBusiness())
        {
            showErrorUserPermissionMsg();
            return;
        }
        if (attachment.getNegoStatus().equals(SptConstant.NEGOSTATUS_D)
                || SptConstant.NEGOSTATUS_D.equals(bizId2NegoStatus.get(attachment.getBizId())))
        {
            ToastHelper.showMessage("该询价已成交");
            return;
        }
        if("CD".equals(bizId2NegoStatus.get(attachment.getBizId()))
                )
        {
            ToastHelper.showMessage("该询价已被撤销");
            return;
        }
        SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<String>()
        {
            @Override
            public String requestService() throws DBException, ApplicationException
            {
                GetBuySellIntensionDetailUpEntity upEntity = new GetBuySellIntensionDetailUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setSubmitId(attachment.getBizId());
                return Client.getService(IBuySellIntensionService.class).getBuySellIntensionDetailForForm(upEntity).getNegoStatus();
            }

            @Override
            public void onDataSuccessfully(String status)
            {
                if (status != null)
                {
                    if (!attachment.getNegoStatus().equals(SptConstant.NEGOSTATUS_D) && status.equals(SptConstant.NEGOSTATUS_D))
                    {
                        ToastHelper.showMessage("该询价已成交");
                        return;
                    }
                    Intent intent = new Intent();
                    if (!status.equals(SptConstant.NEGOSTATUS_D))
                    {
//                        intent = new Intent(ChatIMActivity.this, NegotiateActivity.class);
//                        intent.putExtra("comesFrom", "Chat");
//                        intent.putExtra(NegotiateActivity.class.getSimpleName(), NegotiateActivity.NegoType.NEGODETAIL);
                    }
                    else
                    {
                        intent = new Intent(ChatIMActivity.this, OrderInfoActivity.class);
                    }

                    intent.putExtra(AppConstant.TAG_NEGOSTATUS, status);
                    intent.putExtra(AppConstant.TAG_BIZID, attachment.getBizId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void quetyTeamInfo(String tid) {
        NIMClient.getService(TeamService.class).queryTeam(tid)
                .setCallback(new RequestCallbackWrapper<Team>() {
                    @Override
                    public void onResult(int code, Team t, Throwable exception) {
                        if (t != null) {
                            petName = t.getName();
                            title.setTitle(petName);
                            title.setTitleSubFormat(t.getMemberCount() + "人");
                        }
                    }
                });
    }


    /**
     * 根据群获取群成员列表
     */
    private void queryMemberList(final String tid) {
        NIMClient.getService(TeamService.class).queryMemberList(tid)
                .setCallback(new SubRequestCallback<List<TeamMember>>(this) {
                    @Override
                    public void callBackSuccess(List<TeamMember> listMember) {
                        List<String> accids = new ArrayList<>();
                        String accid;
                        for (TeamMember team : listMember)
                        {
                            accid = team.getAccount();
                            if (TextUtils.isEmpty(LoginUserContext.getPickNameByAccid(accid))
                                    || TextUtils.isEmpty(LoginUserContext.getIconUrlByAccid(accid)))
                            {
                                accids.add(accid);
                            }
                        }

                        fatchMemberIcon(accids, true);
                    }
                });
    }




    private void fatchMemberIcon(final List<String> accids, final boolean isTeam)
    {
        RequestIMUserInfoHelper helper = new RequestIMUserInfoHelper();
        helper.requestIMInfo(this,accids);
        helper.setRequestSuccess(new RequestIMUserInfoHelper.RequestIMInfoSuccess()
        {
            @Override
            public void onSuccess(List<NimUserInfo> list)
            {
                if (isTeam)
                {
                    adapter.notifyDataChanged();
                }
                else
                {
                    petName = LoginUserContext.getPickNameByAccid(accids.get(0));
                    title.setTitle(petName);
                }
            }
        });
    }

    @Override
    public void finish() {
        String lastClass = getIntent().getStringExtra(EXTRA_LAST_CLASS);
        NIMMessageReceiver.setImSessionId(null);
        if (lastClass != null
                && lastClass.equals(HomeActivity.class.getName())) {
//            IMMainActivity.start(this);
            IntentUtils.startActivity(this, SetIMActivity.class, "TEAM_ID", sessionId);
        }

        LogUtils.i(TAG, "finish");
        super.finish();
    }

    @Override
    public void onEmojiSelected(String key) {
        Editable mEditable = et_content.getText();
        if (key.equals("/DEL")) {
            et_content.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = et_content.getSelectionStart();
            int end = et_content.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            mEditable.replace(start, end, key);
        }
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName) {

    }

    /**
     * 分享弹窗
     */
    private void showSharePopWindow() {

        PopWindowShare ps = new PopWindowShare(this);
        Bitmap thmb = BitmapFactory.decodeResource(getResources(), R.drawable.im_share_logo);
        Bitmap thmBitmap = Bitmap.createScaledBitmap(thmb, 120, 120, true); //缩略图
        String url = AppConstant.isRelease ? AppConstant.SHARE_CHAT_RELEASE : AppConstant.SHARE_CHAT_TEST;
        ps.creat2ShareWeb(url + getIntent().getStringExtra(EXTRA_SESSION_ID), "商品通" +
                petName +
                "火热交流", "领先的工业原材料综合电商平台", thmBitmap);
        ps.showAtBottom();
    }


    private Map<String,String> bizId2NegoStatus = new HashMap<>();


    public String getNegoStatus(String bizid)
    {
        return bizId2NegoStatus.get(bizid);
    }

    public void try2QueryNegoStatus()
    {
        if(msgList==null)
        {
            return;
        }
        String strSubmmitId = "";
        OrderAttachment attachment;
        for (IMMessage msg:msgList)
        {
            if(!msg.getMsgType().equals(MsgTypeEnum.custom))
            {
                continue;
            }
            attachment= (OrderAttachment) msg.getAttachment();
            if(attachment.getNegoStatus().equals(SptConstant.NEGOSTATUS_D)
                    || SptConstant.NEGOSTATUS_D.equals(bizId2NegoStatus.get(attachment.getBizId()))
                    || "CD".equals(bizId2NegoStatus.get(attachment.getBizId()))
            )
            {
                continue;
            }
            strSubmmitId = strSubmmitId+"," + attachment.getBizId();
        }
        if(strSubmmitId.isEmpty())
        {
            return;
        }
        strSubmmitId = strSubmmitId.substring(1);
        queryNegoStatus(strSubmmitId);
    }


    @Injection(type = "com.autrade.spt.nego.stub.service.impl.BuySellIntensionServiceStub")
    private IBuySellIntensionService buySellIntensionService;
    private void queryNegoStatus(final String submitId)
    {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<TblSubmitBSEntity> >()
        {
            @Override
            public List<TblSubmitBSEntity>  requestService() throws DBException, ApplicationException
            {
                TblSubmitBSEntity entity = new TblSubmitBSEntity();
                entity.setSubmitId(submitId);
//                entity.setRequestUserId(LoginUserContext.getLoginUserId());
                return  buySellIntensionService.queryBuySellIntenStatusList(entity);
            }

            @Override
            public void onDataSuccessfully(List<TblSubmitBSEntity>  list)
            {
                if(list!=null)
                {
                    for (TblSubmitBSEntity entity:list)
                    {
                        bizId2NegoStatus.put(entity.getSubmitId(),entity.getNegoStatus());
                    }

                    boolean requested;
                    for(String str:submitId.split("[,]"))
                    {
                        requested = false;
                        for (TblSubmitBSEntity entity:list)
                        {
                            if(str.equals(entity.getSubmitId()))
                            {
                                requested = true;
                                break;
                            }
                        }
                        if(!requested)
                        {
                            //未获取到，已撤单
                            bizId2NegoStatus.put(str,"CD");
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
}
