package com.totrade.spt.mobile.view.customize;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * 自定义dialog
 * 从构造函数传入的view就是将要弹出的view
 *
 * @author huangxy
 * @date 2017/6/8
 */
public class SimpleDialog extends AlertDialog {
    private View view;

    public SimpleDialog(Context context, View view) {
        super(context);
        this.view = view;
    }

    public SimpleDialog(Context context, int theme, View view) {
        super(context, theme);
        this.view = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
    }

}
