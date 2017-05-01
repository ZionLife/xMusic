package com.zionstudio.xmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.BoolRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.zionstudio.xmusic.R;

/**
 * Created by Administrator on 2017/5/1 0001.
 */

public class RoundProgress extends View {
    private static final String TAG = "RoundProgress";

    //定义一个注解
    @IntDef({PLAYING_STATE, PAUSED_STATE})
    public @interface PlayerState {
    }

    public static final int PLAYING_STATE = 0;
    public static final int PAUSED_STATE = 1;
    private int mRoundColor;
    private int mRoundProgressColor;

    private static int sPlayingRoundColor;
    private static int sPausedRoundColor;
    private static int sProgressColor;

    private float mStrokeWidth;
    private static Paint sPaint;
    @PlayerState
    private int mState;

    private float mProgress = 0;

    public RoundProgress(Context context) {
        this(context, null);
        Log.e(TAG, "111");
    }

    public RoundProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgress);
        mRoundColor = ta.getColor(R.styleable.RoundProgress_roundColor, context.getResources().getColor(R.color.colorBtnBottomLine));
        mRoundProgressColor = ta.getColor(R.styleable.RoundProgress_roundProgressColor, context.getResources().getColor(R.color.colorPrimary));
        mStrokeWidth = ta.getDimension(R.styleable.RoundProgress_strokeWidth, 5F);
        mState = ta.getInt(R.styleable.RoundProgress_state, 0) == 0 ? PLAYING_STATE : PAUSED_STATE;

        sPaint = new Paint();
        sPlayingRoundColor = context.getResources().getColor(R.color.colorBtnBottomLine);
        sPausedRoundColor = context.getResources().getColor(R.color.black);
        sProgressColor = context.getResources().getColor(R.color.colorPrimary);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mState) {
            case PLAYING_STATE:
                drawPlayingButton(canvas);
                break;
            case PAUSED_STATE:
                drawPausedButton(canvas);
        }
    }

    /**
     * 绘制暂停状态的按钮
     *
     * @param canvas
     */
    private void drawPausedButton(Canvas canvas) {
        drawCircleBg(canvas, PAUSED_STATE);
        //绘制三角形
        int center = getWidth() / 2;
        int radius = (int) (center - mStrokeWidth) / 2;
        float startX = center - radius / 2f;
        float startY = (float) (center - Math.sqrt(3) * radius / 2f);
        float firstMoveX = center + radius;
        float firstMoveY = center;
        float secondMoveX = startX;
        float secondMoveY = (float) (center + Math.sqrt(3) * radius / 2f);
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(firstMoveX, firstMoveY);
        path.lineTo(secondMoveX, secondMoveY);
        path.close();
        sPaint.setStrokeWidth(4);
//        sPaint.setStyle(Paint.Style.FILL);
        sPaint.setColor(sPausedRoundColor);
        canvas.drawPath(path, sPaint);
    }

    /**
     * 绘制播放状态的按钮
     *
     * @param canvas
     */
    private void drawPlayingButton(Canvas canvas) {
        drawCircleBg(canvas, PLAYING_STATE);
        //绘制双竖线
        int center = getWidth() / 2;
        int radius = (int) (center - mStrokeWidth);
        //绘制第一条
        float startX = center - radius / 4f;
        float startY = center - radius / 2;
        float endX = startX;
        float endY = center + radius / 2;
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        canvas.drawPath(path, sPaint);
        //绘制第二条
        startX = center + radius / 4;
        endX = startX;
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        canvas.drawPath(path, sPaint);
    }

    /**
     * 绘制通用的圆形Background,不同状态颜色不同
     *
     * @param canvas
     * @param type
     */
    private void drawCircleBg(Canvas canvas, int type) {
        switch (type) {
            case PLAYING_STATE:
                sPaint.setColor(sPlayingRoundColor);
                break;
            case PAUSED_STATE:
                sPaint.setColor(sPausedRoundColor);
                break;
        }

        //绘制按钮的背景
        int center = getWidth() / 2;
        sPaint.setStrokeWidth(mStrokeWidth);
        sPaint.setAntiAlias(true);
        sPaint.setStyle(Paint.Style.STROKE);
        int radius = (int) (center - mStrokeWidth);
        canvas.drawCircle(center, center, radius, sPaint);

        //绘制播放进度
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);
        sPaint.setColor(sProgressColor);
        canvas.drawArc(oval, 270, mProgress * 360, false, sPaint);
    }

    /**
     * 设置播放状态
     *
     * @param state
     */
    public void setState(@PlayerState int state) {
        mState = state;
    }

    /**
     * 获取播放状态
     *
     * @return
     */
    public int getState() {
        return mState;
    }

    public void setProgress(float progress) {
        if (progress >= 0 && progress <= 1) {
            mProgress = progress;
        }
//        Log.e(TAG, "" + progress);
        postInvalidate();
    }
}
