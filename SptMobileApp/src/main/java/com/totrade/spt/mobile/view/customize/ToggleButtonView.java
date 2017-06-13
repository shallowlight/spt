package com.totrade.spt.mobile.view.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.totrade.spt.mobile.view.R;

public class ToggleButtonView extends View {

    public ToggleButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ToggleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ToggleButtonView(Context context) {
        super(context);
        initView();
    }

    private Scroller scroller;
    private Paint frontPaint;
    private Paint backgroundPaint;
    private Paint touchPaint; // 触摸时变色
    private int wid = dp2px(50);
    private int hei = dp2px(27);
    private int space = dp2px(1f); // 圆点与背景间隙
    private int maxLeftPadding = wid - hei - space; // 圆点最大左边距
    private boolean isOn = false; // 开关状态
    private int onColor;
    private int offColor;

    public void setOn(boolean isOn) {
        this.isOn = isOn;
    }

    public boolean isOn() {
        return isOn;
    }

    private void initView() {
        offColor = getContext().getResources().getColor(R.color.gray_txt_aa);
        onColor = getContext().getResources().getColor(R.color.colorPrimary);
        scroller = new Scroller(getContext());

        frontPaint = new Paint();
        frontPaint.setAntiAlias(true);
        frontPaint.setColor(0xffffffff);
        frontPaint.setStyle(Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(offColor);
        backgroundPaint.setStyle(Style.FILL);

        touchPaint = new Paint();
        touchPaint.setAntiAlias(true);
        touchPaint.setColor(0x00000000);
        touchPaint.setStyle(Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(wid, hei);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(hei / 2, hei / 2, hei / 2, backgroundPaint);
        canvas.drawCircle(wid - hei / 2, hei / 2, hei / 2, backgroundPaint);
        canvas.drawRect(new Rect(hei / 2, 0, wid - hei / 2, hei), backgroundPaint);

        if (leftPadding == space) {
            backgroundPaint.setColor(offColor);
        } else if (leftPadding == maxLeftPadding) {
            backgroundPaint.setColor(onColor);
        }
        canvas.drawCircle(hei / 2 + leftPadding, hei / 2, hei / 2 - space * 2, frontPaint);

        if (touchMode == MotionEvent.ACTION_DOWN) {
            // touchPaint.setColor(0x66666666);
            // frontPaint.setColor(0xffe6e6e6);
        } else if (touchMode == MotionEvent.ACTION_UP) {
            // touchPaint.setColor(0x00000000);
            // frontPaint.setColor(0xfffffff);
        }

        canvas.drawCircle(hei / 2, hei / 2, hei / 2, touchPaint);
        canvas.drawCircle(wid - hei / 2, hei / 2, hei / 2, touchPaint);
        canvas.drawRect(new Rect(hei / 2, 0, wid - hei / 2, hei), touchPaint);

        touchMode = -1;
    }

    private boolean isDrag = false; // 是否拖动事件
    private int leftPadding = space / 2;
    private int touchMode = -1;
    private float lastX, downX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        touchMode = event.getAction();
        switch (touchMode) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                downX = lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float delX = moveX - lastX;
                if (Math.abs(moveX - downX) > 2) {
                    isDrag = true;
                }
                leftPadding += delX;
                flushView();
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:

                if (isDrag) // 是拖动事件移动到临近端点
                {
                    if (leftPadding >= maxLeftPadding / 2) {
                        scroller.startScroll(leftPadding, 0, maxLeftPadding - leftPadding, 0);
                        setOn(true);
                    } else if (leftPadding < maxLeftPadding / 2) {
                        scroller.startScroll(leftPadding, 0, -leftPadding + space, 0);
                        setOn(false);
                    }
                } else// 不是拖动事件移动到相反端点
                {
                    if (leftPadding == space) {
                        scroller.startScroll(leftPadding, 0, maxLeftPadding - leftPadding, 0);
                        setOn(true);
                    } else if (leftPadding == maxLeftPadding) {
                        scroller.startScroll(leftPadding, 0, -leftPadding + space, 0);
                        setOn(false);
                    }
                }

                if (onClickToggleListener != null) {
                    onClickToggleListener.onClickToggle(this);
                }
                flushView();
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            leftPadding = scroller.getCurrX();
        }
        flushView();
    }

    /**
     * 限制拖动范围，并刷新位置
     */
    private void flushView() {
        if (leftPadding < space) {
            leftPadding = space;
        } else if (leftPadding > maxLeftPadding) {
            leftPadding = maxLeftPadding;
        }
        invalidate();
    }

    /**
     * 手动切换开关状态
     */
    public void switchOn(boolean switchOn) {
        setOn(switchOn);
        if (switchOn) {
            scroller.startScroll(leftPadding, 0, maxLeftPadding - leftPadding, 0);
        } else {
            scroller.startScroll(leftPadding, 0, -leftPadding + space, 0);
        }
        flushView();
    }

    /**
     * 点击监听事件
     */
    public interface OnClickToggleListener {
        void onClickToggle(ToggleButtonView view);
    }

    private OnClickToggleListener onClickToggleListener;

    public void setOnClickToggleListener(OnClickToggleListener onClickToggleListener) {
        this.onClickToggleListener = onClickToggleListener;
    }

    /**
     * 系统工具 dp2px
     *
     * @param dp
     * @return
     */
    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

}
