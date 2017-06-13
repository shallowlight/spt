package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideBar extends View
{
	// touching event
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26 letters
	public String[] b =
	{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
	// if choosed
	private int choose = -1;
	private Paint paint = new Paint();

	public void setB(String[] b) 
	{
		this.b = b;
	}
	private TextView mTextDialog;

	public void setmTextDialog(TextView mTextDialog)
	{
		this.mTextDialog = mTextDialog;
	}

	public SideBar(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public SideBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public SideBar(Context context)
	{
		super(context);
		initView(context);
	}

	private void initView(Context context)
	{

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		textsize = (bottom - top) / 27;
	}

	int textsize;
	private int singleHeight;

	// override onDraw function
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// get the height
		int height = getHeight();
		// get the width
		int width = getWidth();
		singleHeight = height / b.length;

		for (int i = 0; i < b.length; i++)
		{
			paint.setColor(Color.parseColor("#5BADEC"));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize((int)(textsize/1.5));

			// if choosed
			if (i == choose)
			{
				paint.setColor(Color.parseColor("#3493e7"));
				paint.setTextSize(textsize);
				paint.setFakeBoldText(true);
			}

			// draw text
			float x = width / 2 - paint.measureText(b[i]) / 2;
			float y = (singleHeight / 2 - textsize / 2) + singleHeight * (i + 1);
			canvas.drawText(b[i], x, y, paint);
			paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		final int action = event.getAction();
		final float y = event.getY() + (singleHeight / 2 - textsize / 2);
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener changedListener = onTouchingLetterChangedListener;
		final int letterPos = (int) (y / singleHeight);

		switch (action)
		{
		case MotionEvent.ACTION_UP:
			setBackgroundColor(Color.TRANSPARENT);
			choose = -1;
			invalidate();
			if (mTextDialog != null)
				mTextDialog.setVisibility(View.INVISIBLE);
			break;

		default:
			setBackgroundColor(Color.TRANSPARENT);
			if (oldChoose != letterPos)
			{
				if (letterPos >= 0 && letterPos < b.length)
				{
					if (changedListener != null)
						changedListener.onTouchingLetterChanged(b[letterPos]);
					if (mTextDialog != null)
					{
						mTextDialog.setText(b[letterPos]);
						mTextDialog.setVisibility(View.VISIBLE);
					}

					choose = letterPos;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener changedListener)
	{
		this.onTouchingLetterChangedListener = changedListener;
	}

	public interface OnTouchingLetterChangedListener
	{
		void onTouchingLetterChanged(String str);
	}
}
