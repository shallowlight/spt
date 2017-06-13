package com.totrade.spt.mobile.view.customize;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * 底部弹出popup选择器，支持1到4项数据的选择，并设有回传数据的接口
 * Created by Administrator on 2016/11/23.
 */
public class SelectBottomPopupWindow extends PopupWindow implements View.OnClickListener {

    public enum BottomPopupType {
        ONE, TWO, THREE, FOUR
    }

    private Context mContext;
    private View popupView;
    private TextView tvOne, tvTwo, tvThree, tvFour, tvCancel;
    private Window mWindow;
    private OnItemSelectClickListener mOnItemSelectClickListener;

    public SelectBottomPopupWindow(Context context) {
        this.mContext = context;
    }

    public SelectBottomPopupWindow create(BottomPopupType type) {
        init();
        initSet(type);
        return this;
    }

    /**
     * 设置初始化数据
     *
     * @param data 不可为空，此数据规定类型为String，为设置到控件上的数据
     * @param t    不为空时，回传数据，为此实体，为空时，回传数据为data内文本
     * @param <T>  泛型实体数据
     * @return
     */
    public <T> SelectBottomPopupWindow setData(@NonNull String[] data, T[] t) {
        initData(data, t);
        return this;
    }

    private void init() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.view_popup_bottom_select, null);
        tvOne = (TextView) popupView.findViewById(R.id.tvOne);
        tvTwo = (TextView) popupView.findViewById(R.id.tvTwo);
        tvThree = (TextView) popupView.findViewById(R.id.tvThree);
        tvFour = (TextView) popupView.findViewById(R.id.tvFour);
        tvCancel = (TextView) popupView.findViewById(R.id.tvCancel);

        mWindow = ((Activity) mContext).getWindow();
    }

    private void initSet(BottomPopupType type) {
        setOutsideTouchable(true);
        setFocusable(true);
        setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        switch (type) {
            case ONE:
                tvTwo.setVisibility(View.GONE);
                tvThree.setVisibility(View.GONE);
                tvFour.setVisibility(View.GONE);
                break;
            case TWO:
                tvThree.setVisibility(View.GONE);
                tvFour.setVisibility(View.GONE);
                break;
            case THREE:
                tvFour.setVisibility(View.GONE);
                break;
            case FOUR:
                break;
            default:
                break;
        }
    }

    private <T> void initData(String[] data, T[] t) {
        for (int i = 0; i < data.length; i++) {
            switch (i) {
                case 0:
                    initWeight(tvFour, data[i], t[i]);
                    break;
                case 1:
                    initWeight(tvFour, data[i], t[i]);
                    break;
                case 2:
                    initWeight(tvFour, data[i], t[i]);
                    break;
                case 3:
                    initWeight(tvFour, data[i], t[i]);
                    break;
                default:
                    break;
            }
        }
    }

    private <T> void initWeight(TextView tv, String text, T tag) {
        tv.setText(text);
        if (null != tag) {
            tv.setTag(tag);
        }
    }

    //处理点击事件以及数据回传的接口，Activity若要取得点击后的回传数据，需要实现此接口
    public interface OnItemSelectClickListener {
        <T> void OnItemSelectClick(View v, String s, T t);
    }

    public void setOnItemSelectClickListener(OnItemSelectClickListener l) {
        this.mOnItemSelectClickListener = l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvOne:
                setCallData(tvOne);
            case R.id.tvTwo:
                setCallData(tvTwo);
                break;
            case R.id.tvThree:
                setCallData(tvThree);
                break;
            case R.id.tvFour:
                setCallData(tvFour);
                break;
        }
    }

    private void setCallData(TextView tv) {
        if (null != mOnItemSelectClickListener) {
            mOnItemSelectClickListener.OnItemSelectClick(tv, tv.getText().toString(), tv.getTag());
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.alpha = 1f;
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow.setAttributes(lp);
    }

    public void showAtBottom() {
        super.showAtLocation(mWindow.getDecorView(), Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.alpha = 0.5f;
        int flag = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mWindow.addFlags(flag);
        mWindow.setAttributes(lp);
    }
}
