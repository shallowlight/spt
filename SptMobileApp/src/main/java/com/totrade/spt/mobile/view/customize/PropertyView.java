package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.view.R;


/**
 * 用于商品属性的View。
 * 提供设置标题，属性值，单位等方法
 */
public class PropertyView extends RelativeLayout {

    private TextView mTitleView, mUnitView, mDisplayView;
    private EditText mValueView;
    private ImageView ivSelect;

    public PropertyView(Context context) {
        this(context, null);
    }

    public PropertyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_property_view, this, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PropertyView);

        init(a);
        initSet();
    }

    private void init(TypedArray a) {

        mTitleView = (TextView) findViewById(R.id.title);
        mValueView = (EditText) findViewById(R.id.value);
        mDisplayView = (TextView) findViewById(R.id.display);
        mUnitView = (TextView) findViewById(R.id.unit);
        ivSelect = (ImageView) findViewById(R.id.ivSelect);

        mValueView.setSaveEnabled(false);
        mDisplayView.setSaveEnabled(false);

        String title = a.getString(
                R.styleable.PropertyView_property_title_text);
        setTitleText(title);

        String value = a.getString(
                R.styleable.PropertyView_property_value_text);
        int type = a.getInt(R.styleable.PropertyView_type, 1);
        setValueText(value);
        if (type == 1) {
            mDisplayView.setVisibility(View.VISIBLE);
            mValueView.setVisibility(View.GONE);
            if (TextUtils.isEmpty(value)) {
                mUnitView.setVisibility(View.GONE);
                ivSelect.setVisibility(View.VISIBLE);
            }
        } else if (type == 2) {
            mDisplayView.setVisibility(View.GONE);
            mValueView.setVisibility(View.VISIBLE);
        }

        String unit = a.getString(
                R.styleable.PropertyView_property_unit_text);
        setUnitText(unit);


        mValueView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != mAfterTextChangeListener)
                    mAfterTextChangeListener.afterTextChange(s);
            }
        });

        a.recycle();

    }

    private void initSet() {
        mDisplayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnPropertyClickListener)
                    mOnPropertyClickListener.OnPropertyClick(PropertyView.this);
            }
        });
    }


    public void setTitleText(String title) {
        mTitleView.setText(title);
    }

    public void setValueText(String value) {
        mValueView.setText(value);
    }

    public void setUnitText(String unit) {
        mUnitView.setText(unit);
    }

    public String getUnitText() {
        return mUnitView.getText().toString();
    }

    public TextView getDisplayView() {
        return mDisplayView;
    }

    public void setDisplayViewHint(String hint) {
        mDisplayView.setText(hint);
        mDisplayView.setTextColor(getResources().getColor(R.color.ltgray));
    }

    public void setDisplayViewTextSmall(String text){
        mDisplayView.setText(text);
        mDisplayView.setTextColor(getResources().getColor(R.color.ltBlack));
        mDisplayView.setTextSize(14);
    }

    public EditText getmValueView() {
        return mValueView;
    }

    public void setValueViewHint(String hint) {
        mValueView.setHint(hint);
    }

    public void setDisplayText(String text) {
        mDisplayView.setText(text);
    }

    /**
     * 控件点击事件监听接口
     */
    private OnPropertyClickListener mOnPropertyClickListener;

    public interface OnPropertyClickListener {
        void OnPropertyClick(PropertyView v);
    }

    public void setOnPropertyClickListener(OnPropertyClickListener listener) {
        this.mOnPropertyClickListener = listener;
    }

    /**
     * 文本输入监听接口
     */
    private AfterTextChangeListener mAfterTextChangeListener;

    public interface AfterTextChangeListener {
        void afterTextChange(Editable editable);
    }

    public void setAfterTextChangeListener(AfterTextChangeListener listener) {
        this.mAfterTextChangeListener = listener;
    }

}
