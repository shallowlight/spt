package com.totrade.spt.mobile.view.customize;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
*
* drawable底部边框线 短的线
* @author huangxy
* @date 2017/1/20
*
*/
public class LineDrawable extends Drawable {
    private Paint mPaint;
    private Rect rect;
    private final int LENGTH = 50;
    private int space;

    public LineDrawable() {
        mPaint = new Paint();
        mPaint.setColor(0xff0177f0);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(25f);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        rect = new Rect(left, top, right, bottom);

        space = (right - LENGTH) / 2;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawLine(space, rect.bottom, rect.right - space, rect.bottom, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.RGB_888;
    }
}
