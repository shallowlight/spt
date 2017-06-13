package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * key:value形式的TextView
 * Created by Administrator on 2016/11/24.
 */
public class KeyTextView extends TextView {

    private String[] mStrings;

    public KeyTextView(Context context) {
        this(context,null);
    }

    public KeyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyTextView);
        mStrings = new String[2];
        mStrings[0] = a.getString(R.styleable.KeyTextView_text_key);
        mStrings[1] = a.getString(R.styleable.KeyTextView_text_value);
        loadText();
        a.recycle();
    }

    public KeyTextView setKey(@NonNull String key) {
        mStrings[0] = key;
        loadText();
        return this;
    }

    public KeyTextView setValue(String value)
    {
        if(value==null)
        {
            value = "";
        }
        mStrings[1] = value;
        loadText();
        return this;
    }

    private void loadText() {

        if (TextUtils.isEmpty(mStrings[0]) && TextUtils.isEmpty(mStrings[1]))
            return;
        if (!TextUtils.isEmpty(mStrings[0]) && TextUtils.isEmpty(mStrings[1])) {
            setText(mStrings[0] + ":");
            return;
        }
        if (TextUtils.isEmpty(mStrings[0]) && !TextUtils.isEmpty(mStrings[1])) {
            setText(mStrings[1]);
            return;
        } else {
            setText(mStrings[0] + ":" + mStrings[1]);
        }

    }

}
