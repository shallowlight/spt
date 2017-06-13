package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * Created by Administrator on 2016/11/24.
 */
public class FormItemToggleView extends RelativeLayout {

    private View itemView;
    private TextView name;
    private ToggleButtonView toggle;

    public FormItemToggleView(Context context) {
        this(context,null);
    }

    public FormItemToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        itemView = LayoutInflater.from(context).inflate(R.layout.view_form_toggle,this,true);
        name = (TextView) itemView.findViewById(R.id.name);
        toggle = (ToggleButtonView) itemView.findViewById(R.id.toggle);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutParams(params);
    }
}
