package com.totrade.spt.mobile.view.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.utility.FormatUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PickerView extends View
{

	private List<String> mDataList;
	private int midPosition;		//绘图的中心
	private Paint mPaint;
	private int mTextSize = 80;
	private float lineSpacingMultiplier=1.2f;
	private int mMaxTextAlpha = 200;
	private int mMinTextAlpha = 20;
	private int mColorText = 0x000000;
	private int mViewHeight;
	private int mViewWidth;
	private float mLastDownY;
	private float mDownY;			//按下位置
	private int baseLine=0;			//距离中心位置
	private float mMoveLen = 0;
	private int moveSize;		//滚动数
	private long downTime;		//触摸按下时间
	private onSelectListener mSelectListener;
	private Timer timer;
	private MyTimerTask mTask;
	private boolean repeat=false;		//不重复
	private String textRight="";


	public String getTextRight()
	{
		return textRight;
	}

	public void setTextRight(String textRight)
	{
		this.textRight = textRight;
		postInvalidate();
	}

	/**设置是否首尾重复*/
	public void setRepeat(boolean repeat)
	{
		this.repeat = repeat;
	}


	@SuppressLint("HandlerLeak")
	Handler updateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (moveSize!=0)
			{
				if(!repeat)
				{
					if(-moveSize>midPosition)
					{
						moveSize=-midPosition;
					}
					else if(-moveSize<=midPosition-mDataList.size())
					{
						moveSize=-(midPosition-mDataList.size()+1);
					}

				}

				float fy=(float)mTextSize*lineSpacingMultiplier/2;
				if(moveSize<0)
				{
					if(baseLine>=fy)
					{
						baseLine=(int) -fy;
						moveSize++;
						moveTailToHead();
					}
					baseLine+=mTextSize/10;		//文字大小的整数倍
				}
				else
				{
					if(baseLine<=-fy)
					{
						baseLine=(int) fy;
						moveSize--;
						moveHeadToTail();
					}
					baseLine-=mTextSize/10;
				}
			}

			if (moveSize==0)
			{
				mMoveLen = 0;
				baseLine=0;
				if (mTask != null)
				{
					mTask.cancel();
					mTask = null;
					performSelect();
				}
			}
			invalidate();
		}

	};

	public PickerView(Context context)
	{
		super(context);
		init();
	}

	public PickerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public void setOnSelectListener(onSelectListener listener)
	{
		mSelectListener = listener;
	}

	private void performSelect()
	{
		if (mSelectListener != null)
		{
			mSelectListener.onSelect(mDataList.get(midPosition));
		}
	}

	public void setData(List<String> datas)
	{
		mDataList = datas;
		midPosition=0;
		postInvalidate();
	}

	public void setData(String[] datas)
	{
		List<String> list = new ArrayList<>();
		if(datas != null)
		{
			for (int i = 0; i < datas.length; i++)
			{
				list.add(datas[i]);
			}
		}

		setData(list);
	}

	public void setSelected(int selected)
	{
		midPosition = selected;
		postInvalidate();
	}

	public void setSelectedStr(String str)
	{
		for (int i = 0; i < mDataList.size(); i++)
		{
			if (mDataList.get(i).equals(str))
			{
				midPosition = i;
				return;
			}
		}
		postInvalidate();
	}

	public String getItem()
	{
		if(mDataList==null || mDataList.isEmpty())
		{
			return "";
		}
		return mDataList.get(midPosition);
	}

	private void moveHeadToTail()
	{
		midPosition++;
		if(midPosition>=mDataList.size())
		{
			midPosition=0;
		}
		if (mTask == null && moveSize==0)
		{
			performSelect();
		}

	}

	private void moveTailToHead()
	{
		midPosition--;
		if(midPosition<0)
		{
			midPosition+=mDataList.size();
		}
		if (mTask == null && moveSize==0)
		{
			performSelect();
		}
	}

	private void init()
	{
		mTextSize=FormatUtil.dip2px(getContext(), 20);
		timer = new Timer();
		mDataList = new ArrayList<String>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(mColorText);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(mDataList==null || mDataList.isEmpty())
		{
			return;
		}
		mViewHeight=getHeight();
		mViewWidth=getWidth();
		// 根据index绘制view
		drawData(canvas);
	}

	private void drawData(Canvas canvas)
	{
		// 先绘制选中的text再往上往下绘制其余的text
		int num=((int) (mViewHeight/(lineSpacingMultiplier*mTextSize)+0.4999)-1)/2;
		mPaint.setTextSize(mTextSize);
		// text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
		float x = (float) (mViewWidth / 2.0);
		if(!StringUtility.isNullOrEmpty(textRight))
		{
			x = (float) (mViewWidth / 3.0);
		}
		float y = (float) (mViewHeight / 2.0 + mMoveLen);
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
		float y2 = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(0xff000000);
		paint.setTextSize(mTextSize);

		canvas.drawText(mDataList.get(midPosition), x, y2+baseLine, paint);
		if(!StringUtility.isNullOrEmpty(textRight))
		{
			canvas.drawText(textRight, x*2, mViewHeight/2+mTextSize/2, paint);
		}
		paint.setColor(0xaf000000);
		canvas.drawLine(0, mViewHeight/2-mTextSize*0.6f, getWidth(), mViewHeight/2-mTextSize*0.6f, paint);
		canvas.drawLine(0, mViewHeight/2+mTextSize*0.6f, getWidth(), mViewHeight/2+mTextSize*0.6f, paint);
		// 绘制上方data
		for (int i = 1; i<num+2; i++)
		{
			drawOtherText(canvas, i, -1);
			drawOtherText(canvas, i, 1);
		}

	}

	/**
	 * @param canvas
	 * @param position
	 *            距离mCurrentSelected的差值
	 * @param type
	 *            1表示向下绘制，-1表示向上绘制
	 */
	private void drawOtherText(Canvas canvas, int position, int type)
	{
		if(!repeat)
		{
			int po=midPosition + type * position;
			if(po>=mDataList.size()|| po<0)
			{
				return;
			}
		}
		float d = mTextSize * position*lineSpacingMultiplier + type* mMoveLen;
		mPaint.setTextSize(mTextSize);
		float y = (float) (mViewHeight / 2.0 + type * d);
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
		float baseline2 = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
		int alphaChange=(int) ((float)(mMaxTextAlpha-mMinTextAlpha)*(baseline2+baseLine-mTextSize/2.4-mViewHeight/2)/(mViewHeight/2));
		int alpha=mMaxTextAlpha-Math.abs(alphaChange);
		if(alpha<10)
			alpha=10;
		mPaint.setAlpha(alpha);
		int textPosition=(midPosition + type * position)%mDataList.size();
		if(textPosition<0)
		{
			textPosition+=mDataList.size();
		}
		String text=mDataList.get(textPosition);
		float x = (float) (mViewWidth / 2.0);
		if(!StringUtility.isNullOrEmpty(textRight))
		{
			x = (float) (mViewWidth / 3.0);
		}
		canvas.drawText(text,x, baseline2+baseLine, mPaint);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				downTime=new Date().getTime();
				mDownY=event.getY();
				doDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				doMove(event);
				break;
			case MotionEvent.ACTION_UP:
				doUp(event);
				break;
		}
		return true;
	}

	private void doDown(MotionEvent event)
	{
		if (mTask != null)
		{
			mTask.cancel();
			mTask = null;
		}
		mLastDownY = event.getY();
	}

	private void doMove(MotionEvent event)
	{

		float fy=(float)mTextSize*lineSpacingMultiplier/2;
		mMoveLen += event.getY() - mLastDownY;

		if(!repeat)
		{
			if(mMoveLen>0 && midPosition==0)
			{
				mMoveLen=0;
				invalidate();
				return;
			}
			if(mMoveLen<0 && midPosition>=mDataList.size()-1)
			{
				mMoveLen=0;
				invalidate();
				return;
			}
		}

		if (mMoveLen > fy)
		{
			// 往下滑超过离开距离
			moveTailToHead();
			mMoveLen = -fy;
		}
		else if (mMoveLen < - fy)
		{
			// 往上滑超过离开距离
			moveHeadToTail();
			mMoveLen = fy;
		}

		mLastDownY = event.getY();
		invalidate();
	}

	private void doUp(MotionEvent event)
	{
		float moveLenth=event.getY()-mDownY;
		int timeChange=(int) (new Date().getTime()-downTime);
		if(timeChange<200 && Math.abs(moveLenth)>mViewHeight/4)
		{
			//快速移动
			mMoveLen=-moveLenth*200/timeChange;  //原值得基础上取负是和点击相反,点击正上滚，移动负上滚
		}
		else if(Math.abs(moveLenth)<10)
		{
			//点击
			mMoveLen=event.getY()-mViewHeight/2;
		}
		else
		{
			mMoveLen=0;
			postInvalidate();
		}
		if (mTask != null)
		{
			mTask.cancel();
			mTask = null;
		}
		moveSize=Math.round(mMoveLen/(lineSpacingMultiplier*mTextSize));
		mMoveLen=0;
		if(moveSize!=0)
		{
			mTask = new MyTimerTask(updateHandler);
			timer.schedule(mTask, 0, 10);
		}
	}

	class MyTimerTask extends TimerTask
	{
		Handler handler;

		public MyTimerTask(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run()
		{
			handler.sendMessage(handler.obtainMessage());
		}
	}

	public interface onSelectListener
	{
		void onSelect(String text);
	}
}
