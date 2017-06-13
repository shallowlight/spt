package com.totrade.spt.mobile.view.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

public class Indicator extends LinearLayout {
    private Paint mPaint; // 画指示符的paint

    private int mTop; // 指示符的top
    private int mLeft; // 指示符的left
    private int mLineWidth; // 指示符的width
    private int mItemWidth;//条目总宽度
    private int mHeight = 3; // 指示符的高度，固定了
    private int mChildCount; // 子item的个数，用于计算单个条目的宽度
    private boolean isAdaptText = false;//是否自适应文本宽度
    private boolean isCanTouch = true;
    private ViewPager viewPager;
    private final static String TAG = "Indicator";

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性 指示符的颜色
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Indicator, 0, 0);
        int mColor = ta.getColor(R.styleable.Indicator_extracolor, getResources().getColor(R.color.blue));
        mLineWidth = ta.getDimensionPixelSize(R.styleable.Indicator_lineWidth, 0);
        isAdaptText = ta.getBoolean(R.styleable.Indicator_isAdaptText, false);
        ta.recycle();

        // 初始化paint
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChildCount = getChildCount(); // 获取子item的个数
        initLine();
    }

    /**
     * 初始化指示线
     */
    private void initLine() {
        caculateLineWidth(0);
        mLeft = (mItemWidth - mLineWidth) / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTop = getMeasuredHeight(); // 测量的高度即指示符的顶部位置
        int width = getMeasuredWidth(); // 获取测量的总宽度
        int height = mTop + mHeight; // 重新定义一下测量的高度
        mItemWidth = width / mChildCount;
        setMeasuredDimension(width, height);
    }


    /**
     * 指示符滚动
     *
     * @param position 现在的位置
     * @param offset   偏移量 0 ~ 1
     */
    public void scroll(int position, float offset) {
        if (!isShown()) return;
        mLeft = (int) ((position + offset) * mItemWidth) + (mItemWidth - mLineWidth) / 2;
        //线中心点横坐标
        int lineCenterX = (int) (mItemWidth * (position + offset + 0.5));
        //交界处横坐标
        int borderX = mItemWidth * (position + 1);
        if (lineCenterX > borderX) {
            caculateLineWidth(position + 1);//越界后，更新线的宽度
            setViewsTextBlueAt(position + 1);
        } else {
            caculateLineWidth(position);
            setViewsTextBlueAt(position);
        }
        invalidate();
    }

    /**
     * 计算线宽
     */
    private void caculateLineWidth(int positon) {
        if (isAdaptText)
            mLineWidth = getTxtWidth((TextView) getChildAt(positon));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isCanTouch) {
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            float rawX = ev.getX();
            float rawY = ev.getY();
            for (int i = 0; i < mChildCount; i++) {
                if (viewPager == null) {
                    break;
                }

                Rect outRect = new Rect();
                getChildAt(i).getHitRect(outRect);
                if (outRect.contains((int) rawX, (int) rawY)) {
                    ((TextView) getChildAt(i)).setTextColor(getResources().getColor(R.color.blue));
                    viewPager.setCurrentItem(i);
                } else {
                    ((TextView) getChildAt(i)).setTextColor(getResources().getColor(R.color.gray));
                }
            }
        }
        return true;
    }

    private int getTxtWidth(TextView textView) {
        return (int) textView.getPaint().measureText(textView.getText().toString());
    }

    private void setViewsTextBlueAt(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            if (i == position)
                ((TextView) getChildAt(i)).setTextColor(getResources().getColor(R.color.blue));
            else
                ((TextView) getChildAt(i)).setTextColor(getResources().getColor(R.color.gray));
        }
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public boolean isCanTouch() {
        return isCanTouch;
    }

    public void setCanTouch(boolean isCanTouch) {
        this.isCanTouch = isCanTouch;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect(mLeft, mTop, mLeft + mLineWidth, mTop + mHeight);
        canvas.drawRect(rect, mPaint);
        super.onDraw(canvas);
    }

}