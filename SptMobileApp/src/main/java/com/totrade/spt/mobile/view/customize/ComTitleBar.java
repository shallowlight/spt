package com.totrade.spt.mobile.view.customize;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * Created by Timothy on 2017/3/23.
 */

public class ComTitleBar extends LinearLayout{

    private Boolean isLeftBtnVisible;
    private int leftResId;

    private Boolean isLeftTvVisible;
    private String leftTvText;

    private Boolean isRightBtnVisible;
    private int rightResId;

    private Boolean isRightTvVisible;
    private String rightTvText;

    private Boolean isTitleVisible;
    private String titleText;
    private int titleColor;

    private int backgroundResId;

    private TextView leftTv,rightTv;
    private ImageView leftBtn,rightBtn;

    private Context mContext;

    public ComTitleBar(Context context) {
        this(context,null);
    }

    public ComTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ComTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ComTitleBar);
        isLeftBtnVisible = typedArray.getBoolean(R.styleable.ComTitleBar_left_btn_visible, false);
        leftResId = typedArray.getResourceId(R.styleable.ComTitleBar_left_btn_src, -1);
        isLeftTvVisible = typedArray.getBoolean(R.styleable.ComTitleBar_left_tv_visible, false);
        if(typedArray.hasValue(R.styleable.ComTitleBar_left_tv_text)){
            leftTvText = typedArray.getString(R.styleable.ComTitleBar_left_tv_text);
        }
        isRightBtnVisible = typedArray.getBoolean(R.styleable.ComTitleBar_right_btn_visible, false);
        rightResId = typedArray.getResourceId(R.styleable.ComTitleBar_right_btn_src, -1);
        isRightTvVisible = typedArray.getBoolean(R.styleable.ComTitleBar_right_tv_visible, false);
        if(typedArray.hasValue(R.styleable.ComTitleBar_right_tv_text)){
            rightTvText = typedArray.getString(R.styleable.ComTitleBar_right_tv_text);
        }
        isTitleVisible = typedArray.getBoolean(R.styleable.ComTitleBar_title_visible, false);
        if(typedArray.hasValue(R.styleable.ComTitleBar_title_text)){
            titleText = typedArray.getString(R.styleable.ComTitleBar_title_text);
        }
        titleColor = typedArray.getResourceId(R.styleable.ComTitleBar_title_color,R.color.white);
        backgroundResId = typedArray.getResourceId(R.styleable.ComTitleBar_barBackground, -1);

        typedArray.recycle();

        View barLayoutView = View.inflate(getContext(), R.layout.view_com_title_bar, null);
        leftBtn = (ImageView)barLayoutView.findViewById(R.id.toolbar_left_btn);
        leftTv = (TextView)barLayoutView.findViewById(R.id.toolbar_left_tv);
        TextView titleTv = (TextView)barLayoutView.findViewById(R.id.toolbar_title_tv);
        rightBtn = (ImageView)barLayoutView.findViewById(R.id.toolbar_right_btn);
        rightTv = (TextView)barLayoutView.findViewById(R.id.toolbar_right_tv);
        RelativeLayout barRlyt = (RelativeLayout)barLayoutView.findViewById(R.id.toolbar_content_rlyt);

        if(isLeftBtnVisible){
            leftBtn.setVisibility(VISIBLE);
        }
        if(isLeftTvVisible){
            leftTv.setVisibility(VISIBLE);
        }
        if(isRightBtnVisible){
            rightBtn.setVisibility(VISIBLE);
        }
        if(isRightTvVisible){
            rightTv.setVisibility(VISIBLE);
        }
        if(isTitleVisible){
            titleTv.setVisibility(VISIBLE);
        }
        leftTv.setText(leftTvText);
        rightTv.setText(rightTvText);
        titleTv.setText(titleText);
        titleTv.setTextColor(getResources().getColor(titleColor));

        if(leftResId != -1){
            leftBtn.setImageResource(leftResId);
        }
        if(rightResId != -1){
            rightBtn.setImageResource(rightResId);
        }
        if(backgroundResId != -1){
            barRlyt.setBackgroundColor(getResources().getColor(backgroundResId));
        }
        addView(barLayoutView, 0);

        setDefaultLeftViewClick(leftBtn,leftTv);
    }


    public void setLeftViewClickListener(OnClickListener viewClickListener){
        if (null != viewClickListener){
            leftTv.setOnClickListener(viewClickListener);
            leftBtn.setOnClickListener(viewClickListener);
        }else{
            setDefaultLeftViewClick(leftBtn,leftTv);
        }
    }

    public void setRightViewClickListener(OnClickListener onClickListener){
        if (null != onClickListener){
            rightBtn.setOnClickListener(onClickListener);
            rightTv.setOnClickListener(onClickListener);
        }else{
            throw new NullPointerException("请注册点击标题点击事件");
        }
    }

    private void setDefaultLeftViewClick(View... views){
        for (View view : views) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof Activity){
                        ((Activity) mContext).finish();
                    }
                }
            });
        }
    }

    public ImageView getRightBtn ( ) {
        return rightBtn;
    }

    public ImageView getLeftBtn ( ) {
        return leftBtn;
    }

    public TextView getRightTv ( ) {
        return rightTv;
    }

    public TextView getLeftTv ( ) {
        return leftTv;
    }
}
