package com.totrade.spt.mobile.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autrade.spt.master.entity.TblVersionMasterEntity;
import com.autrade.spt.master.service.inf.IVersionService;
import com.autrade.spt.master.stub.service.impl.VersionServiceStub;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 检查是否有更新
 *
 * @author Administrator
 */

public class UpdateManager {
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 保存解析的XML信息 */
    private String mSavePath;
    private int progress;
    private String lastName;
    private boolean cancelUpdate = false;
    private Context mContext;
    private ProgressBar mProgress;
    private TextView lblSchedule;
    private Dialog updateDialog;
    private boolean showNoUpdate = false;
    private String appUrl;
    private int lastVersion = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD:
                    mProgress.setProgress(progress);
                    lblSchedule.setText("请稍等..." + progress + "%");
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public UpdateManager(Context context, boolean showNoUpdate) {
        this.mContext = context;
        this.showNoUpdate = showNoUpdate;
    }

    /**
     * 检测软件更新
     */
    public void checkUpdate() {
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        // 构造对话框
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext, "检测到新版本，立即更新吗?");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (updateListener != null) {
                    updateListener.update(Boolean.FALSE);
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(mContext, "");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.update, null);
        mProgress = (ProgressBar) v.findViewById(R.id.updateProgress);
        lblSchedule = (TextView) v.findViewById(R.id.lblSchedule);
        builder.setContentView(v);
        builder.isNega(false);
        builder.setPositiveButton("取消更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
                cancelUpdate = true;
            }
        });

        updateDialog = builder.create();
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.show();
        downloadApk();// 下载文件
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(appUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, lastName);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (updateDialog != null) {
                updateDialog.dismiss();
            }
            if (updateListener != null) {
                updateListener.update(Boolean.FALSE);
            }
        }
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, lastName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);// 通过Intent安装APK文件
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    /**
     * 检查软件是否有更新
     *
     * @author Administrator
     */
    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            int versionCode = getVersionCode(mContext);// 获取当前软件版本

            try        //获取服务器端
            {
//                IVersionService versionService = (IVersionService) ObjectFactory
//                        .getObject("com.autrade.spt.master.stub.service.impl.VersionServiceStub"); // 注入DXO字段
//                InjectionHelper.injectAllInjectionFields(versionService);// 注入DXO字段
//                TblVersionMasterEntity entity = versionService.getLastVersion("android");
                TblVersionMasterEntity entity = Client.getService(IVersionService.class).getLastVersion("android");
                appUrl = entity.getUpdateUrl();
                lastVersion = Integer.valueOf(entity.getVersion());
                lastName = "spt.apk";
                if (!StringUtility.isNullOrEmpty(appUrl) && lastVersion >= versionCode) {
                    return null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                int versionCode = getVersionCode(mContext);// 获取当前软件版本
                if (versionCode < lastVersion) {
                    showNoticeDialog();
                    if (updateListener != null) {
                        updateListener.update(Boolean.TRUE);
                    }
                    return;
                } else {
                    LogUtils.log("不用更新updateListener.update(null)");   //TODO
                    if (updateListener != null) {
                        updateListener.update(null);
                    }
                }

            } catch (Exception e) {
            }
        }
    }

    /**
     * 显示信息
     *
     * @param msg
     */
    protected void showMessage(String msg) {
        TextView toastView = new TextView(mContext);
        toastView.setBackgroundResource(R.drawable.borderline_toast);
        toastView.setTextColor(0xffffffff);
        int padding = FormatUtil.dip2px(mContext, 12);
        toastView.setPadding(padding, padding, padding, padding);
        toastView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        toastView.setText(msg);
        toastView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(toastView);
        toast.show();
    }

    private UpdateListener updateListener;

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public interface UpdateListener {
        void update(Boolean hasUpdate);
    }
}
