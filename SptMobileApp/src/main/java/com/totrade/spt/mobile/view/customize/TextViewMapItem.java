package com.totrade.spt.mobile.view.customize;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

public class TextViewMapItem extends LinearLayout {

    private static int layoutHeigh;
    /**
     * 显示value，不可以选择
     */
    public static final int TYPE_SHOW = 1;
    /**
     * 显示value，可以选择
     */
    public static final int TYPE_SHOW_SELECT = 2;
    /**
     * 提示输入,不含单位
     */
    public static final int TYPE_INPUT = 3;
    /**
     * 提示输入,带单位
     */
    public static final int TYPE_INPUT_UNIT = 4;
    /**
     * 提示不可输入，可以选择
     */
    public static final int TYPE_HINT_SELECT = 5;

    private int currType = TYPE_SHOW_SELECT;

    public TextViewMapItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public TextViewMapItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextViewMapItem(Context context) {
        super(context);
        initView();
    }

    private TextView tv_key;
    private EditText et_value;
    private TextView tv_select;

    private void initView() {
        LinearLayout v = (LinearLayout) View.inflate(getContext(), R.layout.item_map_textview, null);
        tv_key = (TextView) v.findViewById(R.id.tv_key);
        tv_select = (TextView) v.findViewById(R.id.tv_select);
        et_value = (EditText) v.findViewById(R.id.et_value);
        if (layoutHeigh == 0) {
            layoutHeigh = FormatUtil.dip2px(getContext(), 40);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, layoutHeigh);
        v.setLayoutParams(lp);
        addView(v);
    }

    public void setCategory(@NonNull String key) {
        tv_key.setText(key);
    }

    public void setEditValue(@NonNull String value) {
        et_value.setText(value);
    }

    public void setTextValue(@NonNull CharSequence value) {
        tv_select.setText(value);
    }

    public void setUnitValue(@NonNull String unit) {
        tv_select.setText(unit);
    }

    public void setEditInputType(int inputType) {
        et_value.setInputType(inputType);
    }

    public void setHintValue(@NonNull String hint) {
        et_value.setHint(hint);
    }

    public void setCategoryAndType(String category, int type) {
        setCategory(category);
        setViewType(type);
    }

    public
    @NonNull
    String getTextValue() {
        return tv_select.getText().toString();
    }

    public TextView getTvSelect() {
        return tv_select;
    }

    public String getEditValue() {
        return et_value.getText().toString();
    }

    public void setViewType(int type) {
        switchType(type);
    }

    private void switchType(int type) {
        currType = type;
        switch (currType) {
            case TYPE_SHOW:
                et_value.setVisibility(View.GONE);
                tv_select.setVisibility(View.GONE);
                break;
            case TYPE_SHOW_SELECT:
                et_value.setVisibility(View.GONE);
                tv_select.setVisibility(View.VISIBLE);
                setDrawable(tv_select, R.drawable.arrow_right_gray);
                break;
            case TYPE_INPUT:
                et_value.setVisibility(View.VISIBLE);
                tv_select.setVisibility(View.GONE);
                break;
            case TYPE_INPUT_UNIT:
                et_value.setVisibility(View.VISIBLE);
                tv_select.setVisibility(View.VISIBLE);
                break;
            case TYPE_HINT_SELECT:
                et_value.setVisibility(View.VISIBLE);
                et_value.setEnabled(false);
                tv_select.setVisibility(View.VISIBLE);
                setDrawable(tv_select, R.drawable.arrow_right_gray);
                break;
            default:
                break;
        }
    }

    private void setDrawable(TextView tv, @DrawableRes int id) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // 设置边界
        tv.setCompoundDrawables(null, null, drawable, null);// 画在右边
        tv.setCompoundDrawablePadding(10);
    }
}
