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

/**
 * Created by Administrator on 2017/5/5 0005.
 */


public class MyPlayerView extends View {
    private Bitmap mCD;
    private Bitmap mCover;
    private int mDrawableID;

    public int getCDSize() {
        return mCDSize;
    }

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
        drawCD(canvas);
        drawCover(canvas);
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
        mCover = BitmapFactory.decodeResource(getResources(), R.drawable.cover);
        loadCover();
        mBgWidth = 5;

    }

    /**
     * 加载专辑封面，专辑封面的半径约是CD机半径的2/3
     */
    private void loadCover() {
        Bitmap source = mCover;

        //裁剪成圆形
        int size = (int) ((2 / 3f) * mCDSize);
        size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squareBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squareBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap((int) ((2 / 3f) * mCDSize), (int) ((2 / 3f) * mCDSize), source.getConfig() == null ? source.getConfig() : Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();

        //设置着色器，当图片太小时拉伸
        BitmapShader shader = new BitmapShader(squareBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = (2 / 3f) * mCDSize / 2;
        //先画在一个Bitmap上
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(r, r, r, paint);
        if (mCover != null && mCover != bitmap) {
            mCover.recycle();
        }
        mCover = bitmap;
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
    }

    public void setCover(Bitmap bitmap) {
        if (mCover != null && mCover != bitmap) {
            mCover.recycle();
        }
        mCover = bitmap;
        loadCover();
        postInvalidate();
    }
}
