package com.zionstudio.xmusic.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.util.BitmapUtils;

/**
 * Created by Administrator on 2017/5/5 0005.
 */


public class MyPlayerView extends View {
    private Bitmap mCD;
    private Bitmap mCover;
    private int mDrawableID;

    private int mCDSize;
    private boolean hasInit = false;
    private int mBgWidth;

    private float mCenter;

    public MyPlayerView(Context context) {
        this(context, null);
    }

    public MyPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCDSize + 2 * mBgWidth, mCDSize + 2 * mBgWidth);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        mCenter = getWidth() / 2f;
        drawBg(canvas);
        drawCover(canvas);
        drawCD(canvas);

    }

    /**
     * 绘制黑色的CD背景
     */
    private void loadCD() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cd_bg);
        mCDSize = bitmap.getWidth();
        mCD = bitmap;
    }

    private void init() {
        loadCD();
        mCover = BitmapFactory.decodeResource(getResources(), R.drawable.cover_square);
        loadCover();
        mBgWidth = 5;

    }

    /**
     * 加载专辑封面，专辑封面的半径约是CD机半径的2/3
     */
    private void loadCover() {
        Bitmap source = mCover;
        int expectedSize = (int) ((2 / 3f) * mCDSize);
        mCover = BitmapUtils.getScaleBitmap(source, expectedSize, expectedSize);
    }

    /**
     * 绘制周边半透明背景
     */
    private void drawBg(Canvas canvas) {
        Paint paint = new Paint();
        paint.setARGB(30, 255, 255, 255);
        float radius = mCDSize / 2 + mBgWidth;
        paint.setAntiAlias(true);
        canvas.drawCircle(mCenter, mCenter, radius, paint);
    }

    public void drawCD(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(mCD, mCenter - mCD.getWidth() / 2f, mCenter - mCD.getWidth() / 2f, paint);
    }

    private void drawCover(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(mCover, mCenter - mCover.getWidth() / 2f, mCenter - mCover.getWidth() / 2f, paint);
//        canvas.drawBitmap(mCover, 0, 0, paint);
    }

    public void setCover(Bitmap bitmap) {
        if (mCover != null && mCover != bitmap) {
            mCover.recycle();
        }
        if (bitmap == null) {
            mCover = BitmapFactory.decodeResource(getResources(), R.drawable.cover_square);
        } else {
            mCover = bitmap;
        }
        loadCover();
        postInvalidate();
    }

    public Bitmap getCover() {
        return mCover;
    }

    public int getCDSize() {
        return mCDSize;
    }
}
