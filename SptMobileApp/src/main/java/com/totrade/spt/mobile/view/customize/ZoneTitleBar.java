package com.totrade.spt.mobile.view.customize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.IMMainActivity;

/**
 *
 * Created by iUserName on 16/12/07.
 */

public class ZoneTitleBar extends LinearLayout
{
    private ImageView imgBack;          //返回按钮
    private TextView lblTitle;          //标题栏
    private LinearLayout llBarMore;     //增加更多按钮

    public ZoneTitleBar(Context context)
    {
        super(context);
        initView(context);
    }

    public ZoneTitleBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }

    public ZoneTitleBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 设置标题
     * @param title 标题显示文字
     */
    public void setTitle(String title)
    {
        lblTitle.setText(title);
    }

    /**
     * 初始化标题栏
     * @param context 上下文
     */
    private void initView(Context context)
    {
        View title = LayoutInflater.from(context).inflate(R.layout.title_zone, null);
        lblTitle = (TextView) title.findViewById(R.id.lblTitle);
        imgBack = (ImageView) title.findViewById(R.id.imgBack);
        llBarMore = (LinearLayout) title.findViewById(R.id.llBarMore);
        if(context instanceof Activity)
        {
            lblTitle.setText(((Activity)context).getTitle());
        }
        title.findViewById(R.id.lbl2Chat).setOnClickListener(listener);
        imgBack.setOnClickListener(listener);
        title.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, FormatUtil.dip2px(context,42.3f)));
        this.addView(title);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FormatUtil.dip2px(context,42.3f)));
    }

    /**
     *
     * @return 返回按钮
     */
    public ImageView getImgBack()
    {
        return imgBack;
    }

    /**
     *
     * @return 右侧布局，可以自由增删按钮
     */
    public LinearLayout getBarMoreLayout()
    {
        return llBarMore;
    }

    /**
     * 返回按钮和通通按钮点击事件
     */
    View.OnClickListener listener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(v == imgBack)
            {
                ((Activity) v.getContext()).finish();
            }
            else if(v.getId() == R.id.lbl2Chat)
            {
                Intent intent = new Intent(v.getContext(),IMMainActivity.class);
                v.getContext().startActivity(intent);
            }
        }
    };
}
