package com.totrade.spt.mobile.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.Toast;

import com.autrade.spt.master.service.inf.ILoginService;
import com.autrade.stage.constants.CommonErrorId;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.exception.SystemException;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;

@SuppressWarnings("rawtypes")
public class SubAsyncTask<T> {
    public final static String TAG = "SubAsyncTask";
    private OnDataListener onDatafinishedListener;
    private Dialog dialog;
    private Context context;

    private SubAsyncTask() { }

    /**
     * 没有dialog 线程不可取消
     *
     * @param onDatafinishedListener
     */
    public void setOnDataListener(OnDataListener onDatafinishedListener) {
        setOnDataListener(null, onDatafinishedListener);
    }

    /**
     * 填写taskTag，没有dialog
     *
     * @param taskTag                线程标签 用来取消线程
     * @param onDatafinishedListener
     */
    public void setOnDataListener(String taskTag, OnDataListener onDatafinishedListener) {
        setOnDataListener(null, null, true, onDatafinishedListener);
    }

    /**
     * 不填写taskTag，在activity弹出dialog是否可取消
     *
     * @param cxt
     * @param doCancelDialog
     * @param onDatafinishedListener
     */
    public void setOnDataListener(Context cxt, boolean doCancelDialog, OnDataListener onDatafinishedListener) {
        setOnDataListener(null, cxt, doCancelDialog, onDatafinishedListener);
    }

    /**
     * 填写taskTag，在activity弹出dialog是否可取消
     *
     * @param taskTag                线程标签 用来取消线程
     * @param cxt                    上下文
     * @param doCancelDialog         运行时是否可以取消dialog
     * @param onDatafinishedListener 数据回调监听
     */
    public void setOnDataListener(String taskTag, Context cxt, boolean doCancelDialog, OnDataListener onDatafinishedListener) {
        this.onDatafinishedListener = onDatafinishedListener;
        this.context = cxt;
        if (null != cxt) {
            dialog = new Dialog(cxt, R.style.Custom_Progress);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setCancelable(doCancelDialog);
        }
        createTask(taskTag);
    }

    public static SubAsyncTask create() {
        return new SubAsyncTask();
    }

    private void createTask(String tag) {
        if (isInMainThread()) {
            mAsyncTask<T> masync = new mAsyncTask<T>();
            masync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tag);

            TaskCache.put(tag, masync);
        }
    }

    @SuppressWarnings("hiding")
    class mAsyncTask<T> extends AsyncTask<String, SystemException, T> {

        public mAsyncTask() {
            if (null != dialog && !dialog.isShowing()) {
                dialog.show();
            }
        }

        String taskTag = new String();

        @SuppressWarnings("unchecked")
        protected T doInBackground(String... params) {
            taskTag = params[0];
            T obj = null;
            try {
                obj = (T) onDatafinishedListener.requestService();
            } catch (DBException | ApplicationException e) {
                com.totrade.spt.mobile.utility.LogUtils.e(TAG, "\r\n\t\t\t" + e.getClass().getSimpleName() + " ErrorId : " + e.getErrorId() + " Message : " + e.getMessage()); // XXX
                publishProgress(e);
            } catch (Exception e) {
                com.totrade.spt.mobile.utility.LogUtils.e(TAG, "Exception : " + e);
            }
            return obj;
        }

        @Override
        protected void onProgressUpdate(SystemException... values) {
            super.onProgressUpdate(values);
            if (context == null || values[0] == null) {
                return;
            }
            if (context instanceof SptMobileActivityBase) {
                if (!((SptMobileActivityBase) context).isFinishing()) {
                    ((SptMobileActivityBase) context).showExceptionMessage(values[0]);
                }
            } else {
                ToastHelper.showMessage(getErrorMsg(values[0]));
            }
        }

        @SuppressWarnings("unchecked")
        protected void onPostExecute(T obj) {
            TaskCache.remove(taskTag);
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }
            onDatafinishedListener.onDataSuccessfully(obj);
        }

    }

    /**
     * 是否主线程
     *
     * @return
     */
    public static boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 显示异常消息
     *
     * @param e
     */
    public String getErrorMsg(SystemException e) {
        if (e == null) {
            return "";
        }
        int errorId = e.getErrorId();
        if (errorId == CommonErrorId.ERROR_REMOTE_CALL_TIMEOUT) // 远程调用超时
        {
            return "请您检查网络连接!";
        } else {
            return ErrorCodeUtility.getMessage(errorId);
        }
    }

    public void cancleTask(String... tags) {
        for (String tag : tags) {
            mAsyncTask task = TaskCache.getTaskmap().get(tag);
            if (task != null && !task.isCancelled() && task.getStatus() == AsyncTask.Status.RUNNING) {
                TaskCache.remove(tag);
                task.cancel(true);
            }
        }
    }
}
