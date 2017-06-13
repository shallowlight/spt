package com.totrade.spt.mobile.ui.notifycenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autrade.spt.common.report.NotifyCategory;
import com.autrade.spt.master.entity.QueryNotifyUpEntity;
import com.autrade.spt.master.entity.TblNotifyMasterEntity;
import com.autrade.spt.report.dto.NotifyUnReadDownEntity;
import com.autrade.spt.report.entity.QueryNotifyHistoryUpEntity;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.MessageEntity;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息中心 - 商品通消息列表
 *
 * @author huangxy
 * @date 2017/5/2
 */
public class MessageFragment extends BaseFragment implements XRecyclerView.LoadingListener, OnListItemClickListener<MessageEntity> {

    private Activity activity;
    private XRecyclerView recyclerView;

    private UserNotiAdapter adapter;
    private List<MessageEntity> messageEntities;
    private String[] messageType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        recyclerView = new XRecyclerView(container.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        recyclerView.setLayoutParams(lp);
        recyclerView.init2LinearLayout();
        recyclerView.setLoadingListener(this);
        recyclerView.setLoadingMoreEnabled(false);

        return recyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onItemClick(View v, MessageEntity data) {
        NotiListActivity.start(activity, data);
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {
    }

    private void initData() {
        messageEntities = new ArrayList<>();
        messageType = getResources().getStringArray(R.array.notify_message_type_list_str);
        TypedArray ar = getResources().obtainTypedArray(R.array.notify_message_type_list_img);
        int[] messageTypeImg = new int[ar.length()];
        for (int i = 0; i < ar.length(); i++) {
            messageTypeImg[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();

        if (LoginUserContext.isFreeman()) {
            messageType = new String[]{messageType[0],};
            messageTypeImg = new int[]{messageTypeImg[0]};
        }

        for (int i = 0; i < messageType.length; i++) {
            MessageEntity entity = new MessageEntity();
            entity.setMsgType(messageType[i]);
            entity.setImgDrawable(messageTypeImg[i]);
            messageEntities.add(entity);
        }

        adapter = new UserNotiAdapter();
        adapter.setOnListItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged(messageEntities);
        getMessage();
    }

    private void getMessage() {
        SubAsyncTask.create().setOnDataListener(activity, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                getPublic();
                if (LoginUserContext.isTrader() || LoginUserContext.isCompanyMaster()) {
                    findNotifyUnReadNumList();
                }
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }

    //获取商品通公告
    private void getPublic() throws ApplicationException, DBException {
        Message msg = handler.obtainMessage(0);
        QueryNotifyUpEntity upEntity = new QueryNotifyUpEntity();
        upEntity.setCurrentPageNumber(1);
        upEntity.setNumberPerPage(1);
        upEntity.setNotifyType("1");
        msg.obj = Client.getService(com.autrade.spt.master.service.inf.INotifyService.class).queryNotifyList(upEntity);
        handler.sendMessage(msg);
    }

    //查询提醒类型消息未读数
    private void findNotifyUnReadNumList() throws DBException, ApplicationException {
        Message msg = handler.obtainMessage(6);
        QueryNotifyHistoryUpEntity upEntity = new QueryNotifyHistoryUpEntity();
        upEntity.setNotifyTo(LoginUserContext.getLoginUserId());
        upEntity.setNotifyChannel("jpush");
        upEntity.setPageSize(100);
        msg.obj = Client.getService(com.autrade.spt.report.service.inf.INotifyService.class).findNotifyUnReadNumList(upEntity);
        handler.sendMessage(msg);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    PagesDownResultEntity<TblNotifyMasterEntity> pagesDownEntity = (PagesDownResultEntity<TblNotifyMasterEntity>) msg.obj;
                    if (pagesDownEntity == null || pagesDownEntity.getDataList().size() == 0) break;
                    TblNotifyMasterEntity masterEntity = pagesDownEntity.getDataList().get(0);
                    messageEntities.get(0).setMsgType(messageType[0]);
                    messageEntities.get(0).setMsgContent(masterEntity.getSubject());
                    messageEntities.get(0).setLastTime(FormatUtil.getTimeShowString(masterEntity.getCreateTime().getTime(), false));
                    break;
                case 6:
                    setUnReadNum(msg);
                    break;
                default:
                    break;

            }
            adapter.notifyDataSetChanged(messageEntities);
        }
    };

    private void setUnReadNum(Message msg) {
        List<NotifyUnReadDownEntity> unReadNums = (List<NotifyUnReadDownEntity>) msg.obj;
        for (NotifyUnReadDownEntity unReadNum : unReadNums) {
            int p = 1;

            NotifyCategory notifyCategory;
            try {
                notifyCategory = NotifyCategory.valueOf(unReadNum.getCategory());
            } catch (IllegalArgumentException e) {
                continue;
            }
            switch (notifyCategory) {
                case trade:
                    p = 1;
                    break;
                case delivery:
                    p = 2;
                    break;
                case bank:
                    p = 3;
                    break;
                case breach:
                    p = 4;
                    break;
                case myStarted:
                    p = 5;
                    break;
                default:
                    break;
            }
            messageEntities.get(p).setUnread(unReadNum.getUnReadNum());
            messageEntities.get(p).setMsgContent(unReadNum.getLatestNotifyContent());
            messageEntities.get(p).setLastTime(FormatUtil.getTimeShowString(unReadNum.getLatestNotifyDate().getTime(), false));
        }
    }

}
