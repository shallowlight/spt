package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.view.R;

/**
*
* Toast
* @author huangxy
* @date 2017/4/7
*
*/
public class ToastForSpt {
    private WindowManager wm;
    private int mDuration;
    private View mNextView;
    public static final int LENGTH_SHORT = 2200;
    public static final int LENGTH_LONG = 3200;

    public ToastForSpt() {
        wm = (WindowManager) SptApplication.context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static ToastForSpt makeText(Context context, CharSequence text, int duration) {
        ToastForSpt result = new ToastForSpt();
        LayoutInflater inflate = (LayoutInflater) context
                .getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.common_toast, null);
        TextView tv = (TextView) v.findViewById(R.id.toast_text);
        tv.setText(text);
        result.mNextView = v;
        result.mDuration = duration;
        return result;
    }

    public static ToastForSpt makeText(Context context, int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public void show() {
        if (mNextView != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = R.style.Animation_Toast;
            params.y = DensityUtil.dp2px(SptApplication.context, 64);
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            wm.addView(mNextView, params);
            SptApplication.appHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (mNextView != null) {
                        wm.removeView(mNextView);
                        mNextView = null;
                        wm = null;
                    }
                }
            }, mDuration);
        }
    }

}
