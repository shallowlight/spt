package com.totrade.spt.mobile.utility;

import java.util.Random;



import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * 验证码生成工具
 *
 * @author Administrator
 *
 */
public class CaptchaCodeUtility
{
	private static final char[] CHARS =
			{
					'2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
					'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
					'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
					'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
			};

	private static CaptchaCodeUtility bmpCode;

	public static CaptchaCodeUtility getInstance()
	{
		if(bmpCode == null)
		{
			bmpCode = new CaptchaCodeUtility();
		}
		return bmpCode;
	}

	//default settings
	private static final int DEFAULT_CODE_LENGTH = 4;
	private static final int DEFAULT_FONT_SIZE = 25;
	private static final int DEFAULT_LINE_NUMBER = 2;
	private static final int BASE_PADDING_LEFT = 15, RANGE_PADDING_LEFT = 5, BASE_PADDING_TOP = 22, RANGE_PADDING_TOP = 5;
	private static final int DEFAULT_WIDTH = 100, DEFAULT_HEIGHT = 40;

	//settings decided by the layout xml
	//canvas width and height
	private int width = DEFAULT_WIDTH-10, height = DEFAULT_HEIGHT-10;

	//random word space and pading_top
	private int base_padding_left = BASE_PADDING_LEFT, range_padding_left = RANGE_PADDING_LEFT,
			base_padding_top = BASE_PADDING_TOP, range_padding_top = RANGE_PADDING_TOP;

	//number of chars, lines; font size
	private int codeLength = DEFAULT_CODE_LENGTH, line_number = DEFAULT_LINE_NUMBER, font_size = DEFAULT_FONT_SIZE;

	//variables
	private String code;
	private int padding_left, padding_top;
	private Random random = new Random();
	//验证码图片
	public Bitmap createBitmap()
	{
		padding_left = 0;

		Bitmap bp = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		Canvas c = new Canvas(bp);
		code = createCode();  					//生成随机数
		c.drawColor(0xfff5f5f5);

		Paint paint = new Paint();
		paint.setTextSize(font_size);
		for (int i = 0; i < code.length(); i++)
		{
			randomTextStyle(paint);
			randomPadding();
			c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);
		}

		for (int i = 0; i < line_number; i++)
		{
			drawLine(c, paint);
		}

		c.save( Canvas.ALL_SAVE_FLAG );//保存
		c.restore();//
		return bp;
	}

	public String getCode() {
		return code;
	}

	//验证码
	private String createCode() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < codeLength; i++)
		{
			buffer.append(CHARS[random.nextInt(CHARS.length)]);
		}
		return buffer.toString();
	}

	//画干扰线
	private void drawLine(Canvas canvas, Paint paint)
	{
		int color = randomColor();
		int startX = random.nextInt(width);
		int startY = random.nextInt(height);
		int stopX = random.nextInt(width);
		int stopY = random.nextInt(height);
		paint.setStrokeWidth(1);
		paint.setColor(color);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
	}

	private int randomColor()
	{
		return randomColor(1);
	}

	//生成字体颜色
	private int randomColor(int rate)
	{
		int red = random.nextInt(256) / rate;
		int green = random.nextInt(256) / rate;
		int blue = random.nextInt(256) / rate;
		return Color.rgb(red, green, blue);
	}

	private void randomTextStyle(Paint paint)
	{
		int color = randomColor();
		paint.setColor(color);							//设置字体颜色
		paint.setFakeBoldText(true);  					//设置是粗体
		float skewX = random.nextInt(20) / 24;
		skewX = random.nextBoolean() ? skewX : -skewX;
		paint.setTextSkewX(skewX); 						//设置倾斜因子，负数表示右斜，整数左斜
	}

	private void randomPadding()
	{
		padding_left += base_padding_left + random.nextInt(range_padding_left);
		padding_top = base_padding_top + random.nextInt(range_padding_top);
	}
	public boolean isCodeRight(String strCode)
	{
		if(CaptchaCodeUtility.getInstance().getCode()==null)
		{
			return false;
		}
		return strCode.equalsIgnoreCase(CaptchaCodeUtility.getInstance().getCode());
	}
}
