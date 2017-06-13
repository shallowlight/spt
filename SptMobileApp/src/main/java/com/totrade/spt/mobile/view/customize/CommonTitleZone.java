package com.totrade.spt.mobile.view.customize;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.IMMainActivity;

/**
 * 专区通用标题栏
 * Created by Administrator on 2016/11/25.
 */
public class CommonTitleZone extends LinearLayout {

    private View titleView;
    private ImageView iv_back;
    private TextView title, tv_apply, tv_tongtong;

    public CommonTitleZone(Context context) {
        this(context, null);
    }

    public CommonTitleZone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        titleView = LayoutInflater.from(context).inflate(R.layout.view_title_zone, this,false);
        iv_back = (ImageView) titleView.findViewById(R.id.tv_screen);
        title = (TextView) titleView.findViewById(R.id.title);
        tv_apply = (TextView) titleView.findViewById(R.id.tv_apply);
        tv_tongtong = (TextView) titleView.findViewById(R.id.tv_tongtong);
        addView(titleView);

        iv_back.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                ((Activity) v.getContext()).finish();
            }

        });

        tv_tongtong.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (!LoginUserContext.canDoMatchBusiness()) {
                    return;
                }
                IMMainActivity.start(v.getContext());
            }

        });
    }

    public TextView getTitleView() {
        return this.title;
    }

    public ImageView getImgBack() {
        return this.iv_back;
    }

    public TextView getTvApply() {
        return this.tv_apply;
    }

    public TextView getTvTongTong() {
        return this.tv_tongtong;
    }

    public TextView setTitleSize(int resId) {
        this.title.setTextSize(getResources().getDimension(resId));
        return this.title;
    }

    public CommonTitleZone setTitle(String title) {
        this.title.setText(title);
        return this;
    }

    public CommonTitleZone setTvApplyGone() {
        tv_apply.setVisibility(View.GONE);
        return this;
    }
}
