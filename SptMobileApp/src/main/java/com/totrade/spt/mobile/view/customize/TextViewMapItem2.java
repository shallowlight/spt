package com.totrade.spt.mobile.view.customize;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

public class TextViewMapItem2 extends FrameLayout {

    public static final int TYPE_SHOW = 1;

    public static final int TYPE_INPUT = 2;

    public TextViewMapItem2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public TextViewMapItem2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextViewMapItem2(Context context) {
        super(context);
        initView();
    }

    private TextView tv_key;
    private EditText et_value;
    private TextView tv_unit1;
    private TextView tv_unit2;

    private void initView() {
        LinearLayout v = (LinearLayout) View.inflate(getContext(), R.layout.item_map_textview2, null);
        tv_key = (TextView) v.findViewById(R.id.tv_key);
        tv_unit1 = (TextView) v.findViewById(R.id.tv_unit1);
        tv_unit2 = (TextView) v.findViewById(R.id.tv_unit2);
        et_value = (EditText) v.findViewById(R.id.et_value);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        v.setLayoutParams(lp);
        addView(v);
    }

    public TextView setKey(String key) {
        tv_key.setText(key);
        return tv_key;
    }

    public EditText setHint(String hint) {
        et_value.setHint(hint);
        return et_value;
    }

    public void showText(int type, String key, String value, String unit1, String unit2) {
        setKey(key);
        if (unit1 == null) {
            tv_unit1.setVisibility(View.GONE);
        } else {
            tv_unit1.setText(unit1);
        }
        tv_unit2.setText(unit2);
        et_value.setText(value);
        et_value.setEnabled(type == TYPE_INPUT);
    }

}
