package com.example.nomansanaullah.assignment3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class GraphView extends View {
    private static final float RATIO = 4f / 3f;
    private RectF rect;
    private Paint paint, paint2;
    double graphArray[] = null;
    double m_maxY = 0;

    /**
     *
     * @param context
     */
    public GraphView(Context context) {
        super(context);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public GraphView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     *
     * Initialize rounded rectangle and paint values
     */
    public void init(){
        rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.darkmodal));
        paint2.setColor(getResources().getColor(R.color.lightblue));
        paint2.setStrokeWidth(7);

    }

    /**
     *
     * @param Xi_graphArray
     * @param Xi_maxY
     */
    public void setGraphArray(double Xi_graphArray[], double Xi_maxY)
    {
        graphArray = Xi_graphArray;
        m_maxY = Xi_maxY;
    }

    /**
     * Takes an array
     * @param Xi_graphArray
     */
    public void setGraphArray(double Xi_graphArray[])
    {
        double maxY = 0;
        for(int i = 0; i < Xi_graphArray.length; i++)
        {
            if(Xi_graphArray[i] > maxY)
            {
                maxY = Xi_graphArray[i];
            }
        }
        setGraphArray(Xi_graphArray, maxY);
    }

    /**
     * Draw the graph
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.drawColor(getResources().getColor(R.color.darkmodal));
        canvas.drawRoundRect(rect, 20, 20, paint);
        if(graphArray == null)
        {
            return;
        }

        int maxX = graphArray.length;

        double factorX = getWidth() / (double)maxX-5;
        double factorY = getHeight() / (double)m_maxY-7;

        for(int i = 1; i < graphArray.length; i++) {
            int j = i-1;
            int x0 = i - 1;
            double y0 = graphArray[i-1];
            int x1 = i;
            double y1 = graphArray[i];

            int sx = (int)(x0 * factorX);
            int sy = getHeight() - (int)(y0* factorY);
            int ex = (int)(x1*factorX);
            int ey = getHeight() - (int)(y1* factorY);
            canvas.drawLine(sx, sy, ex, ey, paint2);

            paint2.setTextSize(35f);
            canvas.drawText("" + String.format("%.03f",graphArray[j]*10), sx, sy, paint2);
        }

        canvas.drawText("Altitude",30,30,paint2);



    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        int maxWidth = (int) (heigthWithoutPadding * RATIO);
        int maxHeight = (int) (widthWithoutPadding / RATIO);

        if (widthWithoutPadding > maxWidth) {
            width = maxWidth + getPaddingLeft() + getPaddingRight();
        } else {
            height = maxHeight + getPaddingTop() + getPaddingBottom();
        }

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = getMeasuredWidth();
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = getMeasuredHeight();
        }

        setMeasuredDimension(width, height);
    }



}
