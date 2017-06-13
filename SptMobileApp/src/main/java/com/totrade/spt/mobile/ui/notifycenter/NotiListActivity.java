package com.totrade.spt.mobile.ui.notifycenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.report.NotifyCategory;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.master.entity.QueryNotifyUpEntity;
import com.autrade.spt.master.entity.TblNotifyMasterEntity;
import com.autrade.spt.master.entity.UserPushMessageDownEntity;
import com.autrade.spt.master.service.inf.INotifyService;
import com.autrade.spt.master.service.inf.IUserPushMessageService;
import com.autrade.spt.report.dto.NotifyHistoryDownEntity;
import com.autrade.spt.report.entity.QueryNotifyHistoryUpEntity;
import com.autrade.spt.report.entity.QueryNotifyReadUpEntity;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.adapter.NoticeListAdapter;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.MessageEntity;
import com.totrade.spt.mobile.entity.NotiEntity;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.RefreshAndLoadListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 单一类型消息列表界面
 *
 * @author huangxy
 * @date 2017/2/8
 */
public class NotiListActivity extends SptMobileActivityBase implements XRecyclerView.LoadingListener, OnClickListener, RecyclerAdapterBase.ItemClickListener {
    private XRecyclerView recyclerView;
    private ImageView ivBack;

    private MessageEntity messageEntity;
    private NoticeListAdapter adapter;

    private static final int numberPerPage = 20;
    private int currentPageNumber = 1;

    public static void start(Context context, MessageEntity messageEntity) {
        Intent i = new Intent(context, NotiListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MessageEntity.class.getName(), messageEntity);
        i.putExtras(bundle);
        context.startActivity(i);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
        initView();
        parseIntent();
    }

    private void initView() {
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerView);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        notiList = new ArrayList<>();
        msgIdList = new ArrayList<>();
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);
    }

    private void parseIntent() {
        Bundle extras = getIntent().getExtras();
        messageEntity = (MessageEntity) extras.getSerializable(MessageEntity.class.getName());
        if (messageEntity != null) {
            String msgType = messageEntity.getMsgType();
            ((TextView) findViewById(R.id.tv_title)).setText("消息-" + msgType);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    /**
     * 根据推送消息ID选择消息
     */
    private void selectPushMessageById(final String notiId) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                UserPushMessageDownEntity userPushMessageDownEntity = Client.getService(IUserPushMessageService.class).selectPushMessageById(notiId, LoginUserContext.getLoginUserDto().getUserId());
                if (userPushMessageDownEntity != null) {
                    NotiEntity notiEntity = new NotiEntity(
                            userPushMessageDownEntity.getUserId(),
                            userPushMessageDownEntity.getMsgId(),
                            userPushMessageDownEntity.getMessageTitle(),
                            userPushMessageDownEntity.getMessageDispText(),
                            userPushMessageDownEntity.getMsgTag(),
                            userPushMessageDownEntity.getUpdateTime(),
                            userPushMessageDownEntity.getReadFlg(), "0");
                    MessageDetailActivity.start(NotiListActivity.this, notiEntity, messageEntity.getMsgType());
                }
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {

            }
        });
    }

    public void findNotifyDetail(final String msgId) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                NotifyHistoryDownEntity notifyHistoryDownEntity = Client.getService(com.autrade.spt.report.service.inf.INotifyService.class).findNotifyDetail(Long.valueOf(msgId));
                if (notifyHistoryDownEntity != null) {
                    NotiEntity entity = new NotiEntity();
//                  entity.setUerId();
                    entity.setMsgId(String.valueOf(notifyHistoryDownEntity.getHistoryId()));
                    entity.setMsgTitle(notifyHistoryDownEntity.getNotifyTitle());
                    entity.setMsgContent(notifyHistoryDownEntity.getNotifyContent());
                    entity.setMsgType("提醒");
                    entity.setDate(notifyHistoryDownEntity.getUpdateTime());
                    entity.setReadFlag(notifyHistoryDownEntity.isReadFlag() ? "1" : "0");
                    entity.setCheck("0");
                    MessageDetailActivity.start(NotiListActivity.this, entity, entity.getMsgType());
                    setRead(msgId);
                }
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
            }
        });
    }

    private void setRead(String msgId) throws ApplicationException, DBException {
        QueryNotifyReadUpEntity upEntity = new QueryNotifyReadUpEntity();
        upEntity.setUserId(LoginUserContext.getLoginUserId());
        upEntity.setHistoryId(msgId);
        Client.getService(com.autrade.spt.report.service.inf.INotifyService.class).read(upEntity);
    }

    private List<NotiEntity> notiList;
    private List<String> msgIdList;
    private int isRefresh;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            List list = null;
            if (isRefresh == RefreshAndLoadListView.REFRESH) {
                notiList.clear();
                msgIdList.clear();
            }

            switch (msg.what) {
                case 0:
                    List<TblNotifyMasterEntity> resultList0 = (List<TblNotifyMasterEntity>) msg.obj;
                    for (TblNotifyMasterEntity tblNotifyMasterEntity : resultList0) {
                        if (!msgIdList.contains(tblNotifyMasterEntity.getNotifyId())) {
                            String type = "1".equals(tblNotifyMasterEntity.getNotifyType()) ? "商品通公告" : "新闻";
                            NotiEntity entity = new NotiEntity(tblNotifyMasterEntity.getAuthor(),
                                    tblNotifyMasterEntity.getNotifyId(),
                                    type, tblNotifyMasterEntity.getSubject(),
                                    tblNotifyMasterEntity.getNotifyType(),
                                    tblNotifyMasterEntity.getCreateTime(),
                                    "1", "0");
                            msgIdList.add(entity.getMsgId());
                            notiList.add(entity);
                        }
                    }
                    list = (List) msg.obj;
                    break;
                case 1:
                    List<NotiEntity> resultList1 = new ArrayList<>();
                    for (NotifyHistoryDownEntity notifyHistoryDownEntity : (List<NotifyHistoryDownEntity>) msg.obj) {
                        NotiEntity entity = new NotiEntity(
                                "",
                                String.valueOf(notifyHistoryDownEntity.getHistoryId()),
                                notifyHistoryDownEntity.getNotifyTitle(),
                                notifyHistoryDownEntity.getNotifyContent(),
                                notifyHistoryDownEntity.getCategory().name(),
                                notifyHistoryDownEntity.getUpdateTime(),
                                notifyHistoryDownEntity.isReadFlag() ? "1" : "0",
                                "0");
                        msgIdList.add(entity.getMsgId());
                        resultList1.add(entity);

                    }
                    notiList.addAll(resultList1);
                    list = (List) resultList1;
                    break;
                default:
                    break;
            }

            if (adapter == null) {
                adapter = new NoticeListAdapter(notiList);
                recyclerView.setAdapter(adapter);
                adapter.setItemClickListener(NotiListActivity.this);
            } else {
                adapter.notifyDataSetChanged();
            }

            if (msg.obj != null) {
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        }
    };

    @Override
    public void onRefresh() {
        currentPageNumber = 1;
        isRefresh = RefreshAndLoadListView.REFRESH;
        requestServer();
    }

    @Override
    public void onLoadMore() {
        currentPageNumber++;
        isRefresh = RefreshAndLoadListView.LOAD;
        requestServer();
    }

    @Override
    public void itemClick(@NonNull Object obj, int position) {
        NotiEntity entity = (NotiEntity) obj;
        MessageDetailActivity.start(this, entity, messageEntity.getMsgType());
        if (entity != null) {
/*            switch (messageEntity.getMsgType()) {
                case "商品通公告":
                    MessageDetailActivity.start(this, entity, "公告");
                    break;
                case "交易公告":
                case "交收处理":
                case "资金管理":
                case "违约处理":
                    toOrderManager(entity.getMsgId());
                    break;
                case "我的关注":
                    toTrade(entity.getMsgId());
                    break;
                default:
                    break;
            }
            */
            batchUpdatePushMessageReadFlg(entity.getMsgId());
        }
    }

    /**
     * 按类别请求列表
     */
    private void requestServer() {
        switch (messageEntity.getMsgType()) {
            case "商品通公告":
                queryPublicList();
                break;
            case "交易公告":
                findPageNotifyHistory(NotifyCategory.trade);
                break;
            case "交收处理":
                findPageNotifyHistory(NotifyCategory.delivery);
                break;
            case "资金管理":
                findPageNotifyHistory(NotifyCategory.bank);
                break;
            case "违约处理":
                findPageNotifyHistory(NotifyCategory.breach);
                break;
            case "我的关注":
                findPageNotifyHistory(NotifyCategory.myStarted);
                break;
            default:
                break;
        }
    }

    /**
     * 公告消息列表
     */
    private void queryPublicList() {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<PagesDownResultEntity<TblNotifyMasterEntity>>() {
            @Override
            public PagesDownResultEntity<TblNotifyMasterEntity> requestService() throws DBException, ApplicationException {
                QueryNotifyUpEntity upEntity = new QueryNotifyUpEntity();
                upEntity.setCurrentPageNumber(currentPageNumber);
                upEntity.setNumberPerPage(numberPerPage);
                upEntity.setNotifyType("1");        // 1 是公告
                return Client.getService(INotifyService.class).queryNotifyList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<TblNotifyMasterEntity> obj) {
                if (obj != null) {
                    Message msg = handler.obtainMessage(0);
                    msg.obj = obj.getDataList();
                    handler.sendMessage(msg);
                }
                recyclerView.loadMoreComplete();
                recyclerView.refreshComplete();
            }
        });
    }

    /**
     * 获取订单管理类消息列表
     */
    private void findPageNotifyHistory(final NotifyCategory category) {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<PagesDownResultEntity<NotifyHistoryDownEntity>>() {
            @Override
            public PagesDownResultEntity<NotifyHistoryDownEntity> requestService() throws DBException, ApplicationException {
                QueryNotifyHistoryUpEntity upEntity = new QueryNotifyHistoryUpEntity();
                upEntity.setNotifyTo(LoginUserContext.getLoginUserId());
                upEntity.setPageSize(numberPerPage);
                upEntity.setPageNo(currentPageNumber);
                upEntity.setNotifyChannel("jpush");
                upEntity.setCategory(category);
                return Client.getService(com.autrade.spt.report.service.inf.INotifyService.class).findPageNotifyHistory(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<NotifyHistoryDownEntity> obj) {
                if (obj != null) {
                    Message msg = handler.obtainMessage(1);
                    msg.obj = obj.getDataList();
                    handler.sendMessage(msg);
                }
                recyclerView.loadMoreComplete();
                recyclerView.refreshComplete();
            }
        });
    }

    /**
     * 批量设置已读
     * @param msgIds
     */
    private void batchUpdatePushMessageReadFlg(final String msgIds) {

        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                QueryNotifyReadUpEntity upEntity = new QueryNotifyReadUpEntity();
                upEntity.setHistoryId(msgIds);
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                Client.getService(com.autrade.spt.report.service.inf.INotifyService.class).read(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {

            }
        });
    }

    public void toOrderManager(final String contractId) {
        Intent intent = new Intent(NotiListActivity.this, com.totrade.spt.mobile.ui.ordermanager.OrderDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ContractDownEntity.class.getName(), contractId);
        startActivity(intent);
    /* SubAsyncTask.create().setOnDataListener(new OnDataListener<ContractDownEntity>() {
            @Override
            public ContractDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IContractService.class).getContractDetail(contractId, LoginUserContext.getLoginUserId());
            }

            @Override
            public void onDataSuccessfully(ContractDownEntity entity) {
                if (entity != null) {
                    Intent intent = new Intent(NotiListActivity.this, com.totrade.spt.mobile.ui.ordermanager.OrderDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ContractDownEntity.class.getName(), JsonUtility.toJSONString(entity));
                    startActivity(intent);
                }
            }
        });*/
    }

    private void toTrade(final String requestId) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<ZoneRequestDownEntity>() {

            @Override
            public ZoneRequestDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IZoneRequestService.class).getZoneRequestDetailByRequestId(requestId);
            }

            @Override
            public void onDataSuccessfully(ZoneRequestDownEntity entity) {
                if (entity != null) {
                    Intent intent = new Intent(NotiListActivity.this, com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity.class);
                    intent.putExtra(ZoneRequestDownEntity.class.getName(), entity.toString());
                    startActivity(intent);
                }
            }
        });
    }

}
