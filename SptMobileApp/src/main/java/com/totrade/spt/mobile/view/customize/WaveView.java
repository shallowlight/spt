package com.totrade.spt.mobile.view.customize;

import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("WrongCall")
public class WaveView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{

	private SurfaceHolder holder;
	private boolean isRunning;

	// 波纹颜色
	private static final int WAVE_PAINT_COLOR = 0x8859ADEE;
	// y = Asin(wx+b)+h
	private static final float STRETCH_FACTOR_A = 10;
	private static int OFFSET_Y;
	// 第一条水波移动速度
	private static final int TRANSLATE_X_SPEED_ONE = 5;
	// 第二条水波移动速度
	private static final int TRANSLATE_X_SPEED_TWO = 3;

	private int mTotalWidth, mTotalHeight;
	private float[] mOneYPositions;
	private float[] mResetOneYPositions;
	private float[] mResetTwoYPositions;
	private int mXOffsetSpeedOne;
	private int mXOffsetSpeedTwo;
	private int mXOneOffset;
	private int mXTwoOffset;

	private Paint mWavePaint;
	private DrawFilter mDrawFilter;
	private float[] mTwoYPositions;
	private Canvas canvas;

	public WaveView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public WaveView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public WaveView(Context context)
	{
		super(context);
		initView(context);
	}

	private void initView(Context context)
	{
		holder = getHolder();
		holder.addCallback(this);

		// 将dp转化为px，用于控制不同分辨率上移动速度基本一致
		mXOffsetSpeedOne = FormatUtil.dip2px(context, TRANSLATE_X_SPEED_ONE);
		mXOffsetSpeedTwo = FormatUtil.dip2px(context, TRANSLATE_X_SPEED_TWO);
		OFFSET_Y = FormatUtil.dip2px(context, 40);

		// 初始绘制波纹的画笔
		mWavePaint = new Paint();
		// 去除画笔锯齿
		mWavePaint.setAntiAlias(true);
		// 设置风格为实线
		mWavePaint.setStyle(Style.FILL);
		// 设置画笔颜色
		mWavePaint.setColor(WAVE_PAINT_COLOR);
		mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	}

	@Override
	public void run()
	{
		handler.sendEmptyMessageDelayed(1, 30);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == 1)
			{
				onDraw();
				if (isRunning)
				{
					handler.sendEmptyMessageDelayed(1, 30);
				}
			}
		}
	};

	@SuppressLint("WrongCall")
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{

		isRunning = true;
		onSizeChanged();
		new Thread(this).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		isRunning = false;
	}

	protected void onSizeChanged()
	{
		// 记录下view的宽高
		mTotalWidth = getWidth();
		mTotalHeight = getHeight();
		// 用于保存原始波纹的y值
		mOneYPositions = new float[mTotalWidth];
		mTwoYPositions = new float[mTotalWidth];
		// 用于保存波纹一的y值
		mResetOneYPositions = new float[mTotalWidth];
		// 用于保存波纹二的y值
		mResetTwoYPositions = new float[mTotalWidth];

		// 将周期定为view总宽度
		float mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

		// 根据view总宽度得出所有对应的y值
		for (int i = 0; i < mTotalWidth; i++)
		{
			mOneYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
		}
		for (int i = 0; i < mTotalWidth; i++)
		{
			mTwoYPositions[i] = (float) (STRETCH_FACTOR_A * Math.cos(mCycleFactorW * i) + OFFSET_Y);
		}
	}

	protected void onDraw()
	{
		try
		{
			resetPositonY();
			canvas = holder.lockCanvas();
			// 从canvas层面去除绘制时锯齿
			canvas.setDrawFilter(mDrawFilter);
			canvas.drawColor(getResources().getColor(color.blue));
			for (int i = 0; i < mTotalWidth; i++)
			{
				// 减400只是为了控制波纹绘制的y的在屏幕的位置，大家可以改成一个变量，然后动态改变这个变量，从而形成波纹上升下降效果
				// 绘制第一条水波纹
				canvas.drawLine(i, mTotalHeight - mResetOneYPositions[i] - mTotalHeight / 2, i, mTotalHeight, mWavePaint);

				// 绘制第二条水波纹
				canvas.drawLine(i, mTotalHeight - mResetTwoYPositions[i] - mTotalHeight / 2, i, mTotalHeight, mWavePaint);
			}
		}
		catch (Exception e)
		{
			//
		}
		finally
		{
			if (canvas != null)
			{
				holder.unlockCanvasAndPost(canvas);
			}
		}

		// 改变两条波纹的移动点
		mXOneOffset += mXOffsetSpeedOne;
		mXTwoOffset += mXOffsetSpeedTwo;

		// 如果已经移动到结尾处，则重头记录
		if (mXOneOffset >= mTotalWidth)
		{
			mXOneOffset = 0;
		}
		if (mXTwoOffset > mTotalWidth)
		{
			mXTwoOffset = 0;
		}
	}

	private void resetPositonY()
	{
		// mXOneOffset代表当前第一条水波纹要移动的距离
		int yOneInterval = mOneYPositions.length - mXOneOffset;
		// 使用System.arraycopy方式重新填充第一条波纹的数据
		System.arraycopy(mOneYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
		System.arraycopy(mOneYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

		int yTwoInterval = mTwoYPositions.length - mXTwoOffset;
		System.arraycopy(mTwoYPositions, mXTwoOffset, mResetTwoYPositions, 0, yTwoInterval);
		System.arraycopy(mTwoYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
	}

}
