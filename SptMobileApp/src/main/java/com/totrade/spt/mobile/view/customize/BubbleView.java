package com.totrade.spt.mobile.view.customize;

import com.totrade.spt.mobile.view.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 消息气泡
 */
public class BubbleView extends TextView
{

	public BubbleView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public BubbleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public BubbleView(Context context)
	{
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		setMeasuredDimension(dip2Px(33), dip2Px(23));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		wid = getWidth();
		hei = getHeight();
		super.onLayout(changed, left, top, right, bottom);
	}

	int wid, hei;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		Paint paint = new Paint();
		paint.setColor(getContext().getResources().getColor(R.color.red));

		float radius = dip2Px(getHeight() / 4 );
		canvas.drawCircle(getHeight() / 2 , getHeight() / 2 , radius, paint);
		canvas.drawCircle(getWidth() - getHeight() / 2 , getHeight() / 2 , radius, paint);
		Rect rect = new Rect(getHeight() / 2, 0, getWidth() - getHeight() / 2 , getHeight());
		canvas.drawRect(rect, paint);

		canvas.drawCircle(wid / 2, hei / 2, radius, paint);
		super.onDraw(canvas);
	}

	/*
	 * converts dip to px
	 */
	private int dip2Px(float dip)
	{
		return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
	}
}
