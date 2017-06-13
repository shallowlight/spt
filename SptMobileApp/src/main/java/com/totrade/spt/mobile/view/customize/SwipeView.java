package com.totrade.spt.mobile.view.customize;

import com.totrade.spt.mobile.adapter.SwipeAdapter;
import com.totrade.spt.mobile.utility.FormatUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SwipeView extends LinearLayout {

    private LinearLayout contentView;
    private int MAX_Y;

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiView();
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        intiView();
    }

    public SwipeView(Context context) {
        super(context);
        intiView();
    }

    private void intiView() {
        MAX_Y = FormatUtil.dip2px(getContext(), 15f);
        openScroller = new Scroller(getContext());
        closeScroller = new Scroller(getContext());
    }

    int expandWid = -1;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initExpand();
    }


    void initExpand() {
        if (getChildCount() >= 2) {
            //计算需要划出
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            getChildAt(getChildCount() - 1).measure(w, h);
            expandWid = getChildAt(getChildCount() - 1).getMeasuredWidth();
        }

        contentView = (LinearLayout) getChildAt(0);
        menuView = (LinearLayout) getChildAt(1);
        ((LinearLayout) menuView.getParent()).removeView(menuView);
        LinearLayout.LayoutParams params2 = new LayoutParams(expandWid, LayoutParams.MATCH_PARENT);
        params2.leftMargin = contentView.getMeasuredWidth();
        menuView.setLayoutParams(params2);
        addView(menuView);
    }

    public void setExpandWid() {
        initExpand();
    }

    private Scroller openScroller;
    private Scroller closeScroller;
    private float downX;
    private float downY;
    private LinearLayout menuView;
    //	private int expandWid = 0;
    private final static int STATE_OPEN = 1;
    private final static int STATE_CLOSE = 2;
    private int state = STATE_CLOSE;
    private int baseX;
    private int downX2;
    private long downTime;
    private SwipeAdapter adapter;

    public void setAdapter(SwipeAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        非空 打开 不是自身
        if (adapter != null && adapter.getSpv() != null && adapter.getSpv().isOpen() && !adapter.getSpv().equals(this)) {
            adapter.getSpv().smoothCloseMenu();
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                downX = ev.getRawX();
                downX2 = (int) ev.getX();
                downY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = Math.abs((ev.getRawY() - downY));
                if (Math.abs(dy) > MAX_Y) {
                    super.onTouchEvent(ev);
                }

                float moveX = ev.getRawX();
                int disX = (int) (downX - moveX);
                if (state == STATE_OPEN) {
                    disX += menuView.getWidth();
                }
                swipe(disX);

                if (contentView.getLeft() < 0) {
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
//                up在指定范围内 move时间小于500
                long l = System.currentTimeMillis() - downTime;
                if (Math.abs(ev.getRawX() - downX) < 20 && Math.abs(ev.getRawY() - downY) < 20
                        && l < 500) {
                    if (onContentClickListener != null) {
                        onContentClickListener.onContentClick(contentView);
                    }
                    break;
                }
                if (((downX - ev.getRawX()) > (menuView.getWidth() / 4) && state == STATE_CLOSE) || ((ev.getRawX() - downX) < (menuView.getWidth() / 4) && state == STATE_OPEN)) {
                    smoothOpenMenu();
                } else if (Math.abs(downX2 - ev.getX()) < 15) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        this.callOnClick();
                    }
                } else {
                    smoothCloseMenu();
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (openScroller.computeScrollOffset()) {
                swipe(openScroller.getCurrX());
                postInvalidate();
            }
        } else {
            if (closeScroller.computeScrollOffset()) {
                swipe(baseX - closeScroller.getCurrX());
                postInvalidate();
            }
        }
    }

    private void swipe(int dis) {
        if (dis > menuView.getWidth()) {
            dis = menuView.getWidth();
        }
        if (dis < 0) {
            dis = 0;
        }
        contentView.layout(-dis, contentView.getTop(), contentView.getWidth() - dis, getMeasuredHeight());
        menuView.layout(contentView.getWidth() - dis, menuView.getTop(), contentView.getWidth() + menuView.getWidth() - dis, menuView.getBottom());

        if (dis == 0) {
            isOpen = false;
            callListener();
        }
        if (dis == menuView.getWidth()) {
            isOpen = true;
            callListener();
        }

    }

    private void callListener() {
        if (onOpenListener != null) {
            onOpenListener.onOpen(SwipeView.this, isOpen);
        }
    }

    private boolean isOpen = false;

    public boolean isOpen() {
        return isOpen;
    }

    public void smoothOpenMenu() {
        if (adapter != null && adapter.getSpv() != null && adapter.getSpv().isOpen() && !adapter.getSpv().equals(this)) {
            adapter.getSpv().smoothCloseMenu();
            return;
        }
        state = STATE_OPEN;
        openScroller.startScroll(-contentView.getLeft(), 0, menuView.getWidth(), 0, 350);
        postInvalidate();
    }

    public void smoothCloseMenu() {
        state = STATE_CLOSE;
        baseX = -contentView.getLeft();
        closeScroller.startScroll(0, 0, baseX, 0, 350);
        postInvalidate();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public interface OnOpenListener {
        void onOpen(View v, boolean isOpen);
    }

    private OnOpenListener onOpenListener;

    public void setOnOpenListener(OnOpenListener onOpenListener) {
        this.onOpenListener = onOpenListener;
    }

    public interface OnContentClickListener {
        void onContentClick(View v);
    }

    private OnContentClickListener onContentClickListener;

    public void setOnContentClickListener(OnContentClickListener onContentClickListener) {
        this.onContentClickListener = onContentClickListener;
    }
}
