package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.ui.maintrade.listener.IFormNameValue;

/**
 * 输入框表单控件
 * Created by Administrator on 2016/11/24.
 */
public class FormItemInputView extends LinearLayout implements IFormNameValue {

    private View itemView;
    private TextView name;
    private EditText value;
    private ImageView icon;
    private TextView right_text;
    private String hint;
    private boolean ifRequired;

    public FormItemInputView(Context context) {
        this(context, null);
    }

    public FormItemInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FormItemInputView);
        ifRequired = a.getBoolean(R.styleable.FormItemInputView_if_required, false);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        itemView = LayoutInflater.from(context).inflate(R.layout.view_form_input, this, true);
        name = (TextView) itemView.findViewById(R.id.tv_form_name);
        value = (EditText) itemView.findViewById(R.id.et_form_value);
        icon = (ImageView) itemView.findViewById(R.id.iv_form_edit);
        right_text = (TextView) itemView.findViewById(R.id.tv_form_edit);

        value.setSaveEnabled(false);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(params);

        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != mFormInputTextWatcherListener)
                    mFormInputTextWatcherListener.afterTextChange(s);
            }
        });
    }

    public FormItemInputView setIconVisible() {
        icon.setVisibility(View.VISIBLE);
        return this;
    }

    public FormItemInputView setIconGone() {
        icon.setVisibility(View.GONE);
        return this;
    }

    public TextView getNameView() {
        return name;
    }

    public EditText getValueView() {
        return value;
    }

    public ImageView getIconView() {
        right_text.setVisibility(View.GONE);
        return icon;
    }

    public TextView getRightTextView() {
        icon.setVisibility(View.GONE);
        return right_text;
    }

    @Override
    public void setName(String name) {
        this.name.setText(name);
    }

    @Override
    public void setValue(String value) {
        this.value.setText(value);
    }

    @Override
    public String getName() {
        return name.getText().toString();
    }

    @Override
    public String getValue() {
        return value.getText().toString();
    }

    @Override
    public void setHint(String hint) {
        value.setHint(hint);
    }

    @Override
    public void setIfRequired(boolean ifRequired) {
        this.ifRequired = ifRequired;
    }

    @Override
    public boolean getIfRequired() {
        return ifRequired;
    }

    private FormInputTextWatcherListener mFormInputTextWatcherListener;

    public interface FormInputTextWatcherListener {
        void afterTextChange(Editable editable);
    }

    public void setFormInputTextWatcherListener(FormInputTextWatcherListener textWatcherListener) {
        this.mFormInputTextWatcherListener = textWatcherListener;
    }

}
