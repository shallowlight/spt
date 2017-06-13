package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.Executor;

/**
 * @param <K>
 * @param <T>
 * @param <M>
 * @author wangzq
 *         运行时会弹出Dialog，结束后Dialog 会消失
 *         适合在Activity中使用，不适用与TabHost和ViewPage中使用
 */
@Deprecated
public abstract class DialogAsyncTask<K, T, M> extends AsyncTask<K, T, M> {
    private Context context;
    private NewDialog dialog;
    private boolean dialogCancelable = false;
    private static volatile Executor sDefaultExecutor = THREAD_POOL_EXECUTOR;

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public final AsyncTask execute(Context context, K... params) {
        this.context = context;
        return executeOnExecutor(sDefaultExecutor, params);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public final AsyncTask execute(boolean dialogCancelable, Context context, K... params) {
        this.dialogCancelable = dialogCancelable;
        this.context = context;
        return executeOnExecutor(sDefaultExecutor, params);
    }

    @Override
    protected void onPreExecute() {
        if (context != null) {
            if (dialog == null) {
                dialog = NewDialog.create(context, "请稍候...", dialogCancelable, null);
            }
            dialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(M result) {
        dismissDialog();
        super.onPostExecute(result);
    }
}
