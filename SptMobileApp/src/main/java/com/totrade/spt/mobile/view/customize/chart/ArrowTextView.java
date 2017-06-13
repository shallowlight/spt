package com.totrade.spt.mobile.view.customize.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * 带左边箭头的文本框。
 */

public class ArrowTextView extends TextView {

    private float radius;
    private float arrowWidth;
    private float arrowHeight;
    private int color;

    public ArrowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ini(context, attrs);
    }

    private void ini(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArrowTextView);
        radius = typedArray.getDimension(R.styleable.ArrowTextView_radius, 0);
        arrowWidth = typedArray.getDimension(R.styleable.ArrowTextView_arrowWidth, 0);
        arrowHeight = typedArray.getDimension(R.styleable.ArrowTextView_arrowHeight, 0);
        color = typedArray.getColor(R.styleable.ArrowTextView_bg, Color.BLACK);
    }

    public ArrowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ini(context, attrs);
    }


    public ArrowTextView(Context context) {
        super(context);
    }


    /**
     * @param arrowWidth 三角形箭头的宽度.......
     */
    public void setArrowWidth(float arrowWidth) {
        this.arrowWidth = arrowWidth;
        invalidate();

    }

    /**
     * @param arrowHeight 三角形箭头的高度......
     */
    public void setArrowHeight(float arrowHeight) {
        this.arrowHeight = arrowHeight;
        invalidate();
    }

    /**
     * @param radius 矩形四角圆角的半径..........
     */
    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();

    }

    /**
     * @param color 箭头矩形的背景色.........
     */
    public void setBgColor(int color) {
        this.color = color;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color == 0 ? Color.RED : color);
        paint.setAntiAlias(true);
        if (radius == 0) {
            radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 5, getResources().getDisplayMetrics());
        }
        if (arrowWidth == 0) {
            arrowWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics());
        }
        if (arrowHeight == 0) {
            arrowHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 15, getResources().getDisplayMetrics());
        }

        //画三角形
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        float yMiddle = getHeight() / 2;
        float yTop = yMiddle - arrowHeight / 2;
        float yBottom = yMiddle + arrowHeight / 2;
        path.moveTo(0, yMiddle);
        path.lineTo(arrowWidth, yTop);
        path.lineTo(arrowWidth, yBottom);
        path.close();
        canvas.drawPath(path, paint);

        //圆角画矩形
        float x = arrowWidth - 1;
        float width = getWidth() - arrowWidth;
        int height = getHeight();
        canvas.drawRoundRect(new RectF(x, 0, width, height), radius, radius, paint);

        super.onDraw(canvas);
    }
}
