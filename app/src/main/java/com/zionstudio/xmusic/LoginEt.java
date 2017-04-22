package com.zionstudio.xmusic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import org.w3c.dom.Attr;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class LoginEt extends android.support.v7.widget.AppCompatEditText {

    private DrawableListener mDrawableLeftListener;
    private DrawableListener mDrawableRightListener;

    public LoginEt(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoginEt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

//        setPadding((int)(getX() + getCompoundDrawables()[0].getBounds().width() + 50), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    public void setDrawableRightListener(DrawableListener l) {
        mDrawableRightListener = l;
    }

    public void setDrawableLeftListener(DrawableListener l) {
        mDrawableLeftListener = l;
    }

    public interface DrawableListener {
        public void onDrawableClick();
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (!TextUtils.isEmpty("+86")) {
//            canvas.drawText("+86", getLeft() + getCompoundDrawables()[0].getBounds().width(), (getMeasuredHeight() - getTextSize()) / 2 + getTextSize(), getPaint());
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable right = getCompoundDrawables()[2];  //获取右边图片
                Drawable left = getCompoundDrawables()[0];  //获取左边图片

                if ((right != null) && (event.getRawX() >= (getX() + (getWidth() - right.getBounds().width()))) && mDrawableRightListener != null) {
                    mDrawableRightListener.onDrawableClick();
                    return true;
                }

                if ((left != null) && (event.getRawX() <= (getX() + left.getBounds().width())) && mDrawableLeftListener != null) {
                    mDrawableLeftListener.onDrawableClick();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
