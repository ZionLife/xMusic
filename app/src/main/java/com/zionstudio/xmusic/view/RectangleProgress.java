package com.zionstudio.xmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.zionstudio.xmusic.R;


/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class RectangleProgress extends View {
    private static final String TAG = "RectangleProgress";
    private int mProgress = 50;
    private int mCount = 100;
    private float mLineWidth;
    private float mLineHeight;
    private float mCircleRadius;
    private Paint mPaint = new Paint();
    private int mLineRadius;

    public RectangleProgress(Context context) {
        this(context, null);
    }

    public RectangleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectangleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RectangleProgress);
        mLineWidth = ta.getDimension(R.styleable.RectangleProgress_lineWidth, 0);
        mLineHeight = ta.getDimension(R.styleable.RectangleProgress_lineHeight, 0);
        mCircleRadius = ta.getDimension(R.styleable.RectangleProgress_circleRadius, 0);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mLineRadius = 5;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mCircleRadius == 0) {
            setMeasuredDimension((int) (mLineWidth + mCircleRadius * 2), (int) mLineHeight);
        } else {
            setMeasuredDimension((int) (mLineWidth + mCircleRadius * 2), (int) (mCircleRadius * 2));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float lineTop = (float) (getHeight() - mLineHeight) / 2;
        mPaint.setColor(getResources().getColor(R.color.colorItemDivider));
        mPaint.setStyle(Paint.Style.FILL);
        //设置透明度
        mPaint.setAlpha(70);
        RectF rectF = new RectF();
        rectF.set(0, lineTop, mLineWidth, lineTop + mLineHeight);
        canvas.drawRoundRect(rectF, mLineRadius, mLineRadius, mPaint);

        float nowPosition = (mProgress / (float) mCount) * mLineWidth;
        mPaint.setAlpha(255);
        rectF.set(0, lineTop, nowPosition, lineTop + mLineHeight);
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawRoundRect(rectF, mLineRadius, mLineRadius, mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(nowPosition, getHeight() / 2f, mCircleRadius, mPaint);
    }
}
