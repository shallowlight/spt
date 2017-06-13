package com.totrade.spt.mobile.service;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SysInfoUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.SplashActivity;

import java.util.concurrent.atomic.AtomicLong;

import cn.jpush.android.api.JPushInterface;

/**
 * @author haungxy
 */
public class MessageService extends Service {

    private WindowManager wManager;
    private WindowManager.LayoutParams mParams;
    private NotificationManager nm;
    private View myView;
    private final int SHOW_NOTI = 1;
    private int notifyId = AppConstant.JPUSH_NOTIFY_ID;

    /**
     * 判断是否是登录用用户
     */
    private boolean isLoginUser() {
        String userId = getUserId();
        return !TextUtils.isEmpty(userId) && !userId.equals(SptConstant.USER_ID_ANONYMOUS);
    }

    /**
     * 获取用户Id
     */
    private String getUserId() {
        return SharePreferenceUtility.spGetOut(this, "userId", "");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
        mParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;// 窗口的宽和高
        height = FormatUtil.dip2px(this, 40);
        mParams.height = height;
        mParams.gravity = Gravity.TOP;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initService(intent);
        return START_REDELIVER_INTENT;
    }

    /**
     * 初始化
     */
    private void initService(Intent intent) {
        //未登录不接收推送
        if (intent == null || intent.getExtras() == null || !isLoginUser()) {
            return;
        }
        notifyId = (int) System.currentTimeMillis();
        String receiveNotify = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
        String receiveMsg = NotifyUtility.getValue(receiveNotify, "message");
        String category = NotifyUtility.getValue(receiveMsg, "category");

        if (TextUtils.isEmpty(category)) return;
        refreshBroadcast(AppConstant.INTENT_ACTION_NOTIFY_CENTER);
        Intent tradeIntent = new Intent();
        switch (category) {
            case "news":              //商品通公告

                break;
            case "trade":             //交易通知
            case "bank":              //资金管理
            case "breach":            //违约处理
            case "delivery":          //交收处理
                toOrderManager(receiveMsg);
                break;
            case "myStarted":         //新订单
                toTrade(receiveMsg);
                break;
            case "newRequest":        //旧订单有新价格变动
                tradeIntent.setAction(AppConstant.INTENT_ACTION_REFRESH_ON_PRICE);
            case "deal":              //报价成交
            case "cancelOffer":       //报价取消
                tradeIntent.putExtra("receiveMsg", receiveMsg);
                tradeIntent.setAction(AppConstant.INTENT_ACTION_TRADE_REFRESH);
                sendBroadcast(tradeIntent);
                break;
            default:
                break;
        }

    }

    /**
     * 到订单管理模块的订单详情
     *
     * @param receiveMsg
     */
    public void toOrderManager(final String receiveMsg) {
        String contractId = NotifyUtility.getValue(receiveMsg, "businessId");
        if (TextUtils.isEmpty(contractId)) return;
        if (!contractId.startsWith("CTR")) return;
        Intent intent = new Intent(MessageService.this, com.totrade.spt.mobile.ui.ordermanager.OrderDetailActivity.class);
        intent.putExtra(ContractDownEntity.class.getName(), contractId);

        String title = NotifyUtility.getValue(receiveMsg, "title");
        String content = NotifyUtility.getValue(receiveMsg, "content");
        showNoti(content, intent, title);
    }

    /**
     * 到贸易大厅订单详情
     *
     * @param receiveMsg
     */
    private void toTrade(final String receiveMsg) {
        if (twice()) return;
        refreshBroadcast(AppConstant.INTENT_ACTION_TRADE_REFRESH);
        SubAsyncTask.create().setOnDataListener("findZoneRequestList", new OnDataListener<ZoneRequestDownEntity>() {

            String content = NotifyUtility.getValue(receiveMsg, "content");
            String title = NotifyUtility.getValue(receiveMsg, "title");
            String requestId = NotifyUtility.getValue(receiveMsg, "requestId");
            String businessId = NotifyUtility.getValue(receiveMsg, "businessId");

            @Override
            public ZoneRequestDownEntity requestService() throws DBException, ApplicationException {
                String id = null;
                if (requestId == null) id = businessId;
                if (businessId == null) id = requestId;
                return Client.getService(IZoneRequestService.class).getZoneRequestDetailByRequestId(id);
            }

            @Override
            public void onDataSuccessfully(ZoneRequestDownEntity entity) {
                if (entity != null) {
                    Intent intent = new Intent(MessageService.this, com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity.class);
                    intent.putExtra(ZoneRequestDownEntity.class.getName(), entity.toString());
                    showNoti(content, intent, title);
                }
            }
        });
    }

    private AtomicLong l;

    //两次间隔是否小于30秒
    public boolean twice() {
        if (l == null) {
            l = new AtomicLong(System.currentTimeMillis());
            return false;
        }

        if (System.currentTimeMillis() - l.get() < 30000) {
            return true;
        } else {
            l.set(System.currentTimeMillis());
            return false;
        }
    }

    /**
     * 显示弹窗与通知栏
     *
     * @param content      通知内容
     * @param intent       预跳转的意图
     * @param contentTitle 通知标题
     */
    public void showNoti(String content, final Intent intent, String contentTitle) {
        showNoti(content, intent, contentTitle, true);
    }

    /**
     * 显示弹窗与通知栏
     *
     * @param content      通知内容
     * @param intent       预跳转的意图
     * @param contentTitle 通知标题
     * @param isHandle     是否做处理通知点击事件
     */
    public void showNoti(String content, final Intent intent, final String contentTitle, final boolean isHandle) {
        LogUtils.i(MessageService.class.getSimpleName(), "SysInfoUtil.isAppRunning()" + SysInfoUtil.isAppRunning());
        if (!SysInfoUtil.isAppRunning() || LoginUserContext.getLoginUserDto() == null) {
            intent.setClass(this, SplashActivity.class);
        }
        intent.putExtra(AppConstant.NOTIFY_TAG, String.valueOf(notifyId));
        intent.putExtra(AppConstant.NOTIFY_ID, notifyId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifyId, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification noti = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentIntent(pendingIntent).setContentTitle(contentTitle)
                .setContentText(content).build();
        noti.defaults |= Notification.DEFAULT_LIGHTS;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.defaults |= Notification.DEFAULT_SOUND;

        if (!isHandle) {    //点击后消失，不做处理
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
        }

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(String.valueOf(notifyId), notifyId, noti);

        Message msg = handler.obtainMessage();
        msg.what = SHOW_NOTI;
        handler.sendMessageDelayed(msg, 3000);

        if (myView == null) {
            myView = View.inflate(getApplicationContext(), R.layout.notifycation, null);
            myView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.equals(myView)) {
                        // 通知窗口点击后移除
                        if (myView.getParent() != null && myView.isShown()) {
                            wManager.removeView(myView);
                        }
                    }
                    // 跳转至指定界面
                    if (isHandle) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    stopSelf();
                }
            });
        }
        if (!myView.isShown()) {
            TextView tv_title = (TextView) myView.findViewById(R.id.title);
            TextView tv_content = (TextView) myView.findViewById(R.id.tv_content);
            tv_title.setText(contentTitle);
            tv_content.setText(content);
            ll = (LinearLayout) myView.findViewById(R.id.ll_inside);
            ObjectAnimator.ofFloat(ll, "translationY", 0, -height).setDuration(0).start();
            wManager.addView(myView, mParams);
            ObjectAnimator.ofFloat(ll, "translationY", -height, 0).setDuration(500).start();

        }
    }

    /**
     * 发出刷新广播
     *
     * @param refreshAction
     */
    private void refreshBroadcast(String refreshAction) {
        Intent refreshIntent = new Intent();
        refreshIntent.setAction(refreshAction);
        sendBroadcast(refreshIntent);
    }

    private int height;
    private LinearLayout ll;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SHOW_NOTI) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(ll, "translationY", 0, -height)
                        .setDuration(500);
                animator.start();
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(android.animation.Animator animation) {
                        if (myView != null && myView.isShown()) {
                            myView.setVisibility(View.GONE);
                            wManager.removeView(myView);
                            myView = null;
                        }
                        stopSelf();
                    }
                });
            }
        }
    };

}
