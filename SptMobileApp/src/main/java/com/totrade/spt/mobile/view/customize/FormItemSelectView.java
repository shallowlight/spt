package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.ui.maintrade.listener.IFormNameValue;

/**
 * 选择表单控件，此处暂未封装点击选择按钮以后的表现
 * （可能会有两种类型SelectPopupWindow,和WheelView）
 * 由于自定义属性代码量较多，此处暂未实现，内部元素请动态实现属性设置
 * Created by Administrator on 2016/11/24.
 */
public class FormItemSelectView extends LinearLayout implements IFormNameValue {

    private View itemView;
    private TextView name;
    private TextView value;
    private ImageView icon;

    public FormItemSelectView(Context context) {
        this(context, null);
    }

    public FormItemSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        itemView = LayoutInflater.from(context).inflate(R.layout.view_form_select, this, true);
        name = (TextView) itemView.findViewById(R.id.name);
        value = (TextView) itemView.findViewById(R.id.value);
        icon = (ImageView) itemView.findViewById(R.id.icon);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(params);
    }

    @Override
    public String getName() {
        return this.name.getText().toString();
    }

    @Override
    public String getValue() {
        return this.value.getText().toString();
    }

    public TextView getNameView() {
        return name;
    }

    public TextView getValueView() {
        return value;
    }

    public ImageView getIconView() {
        return icon;
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
    public void setHint(String hint) {

    }

    @Override
    public void setIfRequired(boolean ifRequired) {

    }

    @Override
    public boolean getIfRequired() {
        return false;
    }
}
