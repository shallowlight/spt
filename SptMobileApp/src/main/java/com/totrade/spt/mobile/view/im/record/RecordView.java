package com.totrade.spt.mobile.view.im.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RecordView extends ImageView
{

	String TAG = "RecordView";

	public RecordView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public RecordView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RecordView(Context context)
	{
		super(context);
	}

	private int recordState = -1;
	private static final int RECORD_START = 1;
	private static final int RECORD_STOP = 2;
	private static final int RECORD_CANCEL = 3;
	private long downTime;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				downTime = SystemClock.currentThreadTimeMillis();
				recordState = RECORD_START;
				break;
			case MotionEvent.ACTION_MOVE:
				if (!isTouched((View) getParent(), (int) event.getRawX(), (int) event.getRawY()))
				{
					recordState = RECORD_CANCEL;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (SystemClock.currentThreadTimeMillis() - downTime < 160){
					recordState = RECORD_CANCEL;
				}
				if (isTouched(this, (int) event.getRawX(), (int) event.getRawY()))
				{
					recordState = RECORD_STOP;
				} else
				{
					recordState = RECORD_CANCEL;
				}
				break;

			default:
				break;
		}
		record(recordState);
		return true;
	}

	private boolean isStart = false;

	private void record(int State)
	{
		switch (State)
		{
			case RECORD_START:
				// ready start
				if (isStart) {
					break;
				}
				isStart = true;
				com.totrade.spt.mobile.utility.LogUtils.i(TAG, "RecordHelper.ready(); : : : ");	//	XXX
				RecordHelper.ready();

				setVisi(false);

				break;
			case RECORD_STOP:
				// complete
				isStart = false;
				setVisi(true);
				RecordHelper.stopRecord();
				break;
			case RECORD_CANCEL:
				// cancel
				isStart = false;
				RecordHelper.cancelRecord();
				setVisi(true);
				break;

			default:
				break;
		}

	}

	/**
	 * 是否在View的范围内
	 * @param v
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isTouched(View v, int x, int y)
	{
		int[] location = new int[2];
		v.getLocationOnScreen(location);

		Rect rect = new Rect(location[0], location[1], location[0] + v.getWidth(), location[1] + v.getHeight());
		return rect.contains(x, y);
	}

	private void setVisi(boolean isVisi)
	{
		if (onVisibilityListener != null) {
			onVisibilityListener.setVisi(isVisi);
		}
	}

	public interface OnVisibilityListener {
		void setVisi(boolean isVisi);
	}

	private OnVisibilityListener onVisibilityListener;

	public void setOnVisibilityListener(OnVisibilityListener onVisibilityListener) {
		this.onVisibilityListener = onVisibilityListener;
	}
}
