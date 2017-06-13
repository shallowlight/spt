package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * Created by Timothy on 2017/1/20.
 */
public class EmptyView extends LinearLayout {

    private View emptyView;
    private ImageView imageView;
    private TextView textView;

    public EmptyView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        emptyView = LayoutInflater.from(context).inflate(R.layout.layout_empty_recy, this, true);
        setGravity(Gravity.CENTER);
        imageView = (ImageView) emptyView.findViewById(R.id.img_empty);
        textView = (TextView) emptyView.findViewById(R.id.lbl_empty);
    }

    public EmptyView setEmptyImage(int imgId) {
        imageView.setImageResource(imgId);
        return this;
    }

    public EmptyView setEmptyText(String str) {
        textView.setText(str);
        return this;
    }

    public EmptyView setEmptyText(int strId) {
        textView.setText(strId);
        return this;
    }
}
