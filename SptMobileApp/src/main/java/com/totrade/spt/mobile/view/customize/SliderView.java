package com.totrade.spt.mobile.view.customize;

import com.totrade.spt.mobile.view.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SliderView extends LinearLayout 
{
	private static final int TAN = 2;
	private int mHolderWidth = 320;
	private float mLastX = 0;
	private float mLastY = 0;
	private LinearLayout mViewContent;
	private LinearLayout sliderHolder;
	private Scroller mScroller;

	public SliderView(Context context, Resources resources) 
	{
		super(context);
		initView();
	}

	public SliderView(Context context) 
	{
		super(context);
		initView();
	}

	public SliderView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initView();
	}

	private void initView()
	{
		setOrientation(LinearLayout.HORIZONTAL);
//		mContext = getContext();
		mScroller = new Scroller(getContext());
		View.inflate(getContext(), R.layout.slide_view_merge, this);
		mViewContent = (LinearLayout) findViewById(R.id.view_content);
		sliderHolder = (LinearLayout) findViewById(R.id.sliderHolder);
	}
	
	public void setSliderView(View view)
	{
		sliderHolder.removeAllViews();
		if(view == null)
		{
			mHolderWidth = 0;
			return;
		}
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int width = view.getMeasuredWidth();
		mHolderWidth = width;
		sliderHolder.setLayoutParams(new LayoutParams(width, LayoutParams.MATCH_PARENT));
		sliderHolder.addView(view);
	}
	
	
	public void setContentView(View view) 
	{
		mViewContent.addView(view);
	}

	public void shrink()
	{
		int offset = getScrollX();
		if (offset == 0) 
		{
			return;
		}
		scrollTo(0, 0);
	}

	public void reset() {
		int offset = getScrollX();
		if (offset == 0) {
			return;
		}
		smoothScrollTo(0, 0);
	}

	public void adjust(boolean left) {
		int offset = getScrollX();
		if (offset == 0) {
			return;
		}
		if (offset < 20) {
			this.smoothScrollTo(0, 0);
		} else if (offset < mHolderWidth - 20) {
			if (left) {
				this.smoothScrollTo(mHolderWidth, 0);
			} else {
				this.smoothScrollTo(0, 0);
			}
		} else {
			this.smoothScrollTo(mHolderWidth, 0);
		}
	}

	private boolean requestDisallow =false;
	float x;
	float y;
	float deltaX;
	float delatY;
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction()) 
		{
		case MotionEvent.ACTION_DOWN:
			requestDisallow = false;
			x = event.getX();
			y = event.getY();
			mLastX = x;
			mLastY = y;
			requestDisallowInterceptTouchEvent(true);
		case MotionEvent.ACTION_MOVE:
			x = event.getX();
			y = event.getY();
			deltaX = x - mLastX;
			delatY = y - mLastY;
			mLastX = x;
			mLastY = y;
			float scaleX =  getScrollX();
			if(deltaX > 0 && scaleX<10 && !requestDisallow)
			{
				requestDisallowInterceptTouchEvent(false);
			}
			else if(deltaX<0)
			{
				requestDisallow =true;
				requestDisallowInterceptTouchEvent(true);
			}
			if (Math.abs(deltaX) < Math.abs(delatY) * TAN) 
			{
				break;
			}
			if (deltaX != 0) 
			{
//				if(deltaX<0)
//				{
//					requestDisallowInterceptTouchEvent(true);
//				}
				float newScrollX = getScrollX() - deltaX;
				if (newScrollX < 0) {
					newScrollX = 0;
				} else if (newScrollX > mHolderWidth) {
					newScrollX = mHolderWidth;
				}
				this.scrollTo((int) newScrollX, 0);
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private void smoothScrollTo(int destX, int destY) {
		int scrollX = getScrollX();
		int delta = destX - scrollX;
		mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
		invalidate();
	}

	@Override
	public void computeScroll()
	{
		if (mScroller.computeScrollOffset()) 
		{
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
}
