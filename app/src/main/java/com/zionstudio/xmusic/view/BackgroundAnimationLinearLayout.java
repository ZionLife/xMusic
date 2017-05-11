package com.zionstudio.xmusic.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zionstudio.xmusic.R;

/**
 * 播放详情页提供背景切换过度动画的布局。
 * Created by Administrator on 2017/5/11 0011.
 */
public class BackgroundAnimationLinearLayout extends LinearLayout {
    private final int DURATION_ANIMATION = 500;
    private final int INDEX_BACKGROUND = 0;
    private final int INDEX_FOREGROUND = 1;

    private LayerDrawable layerDrawable;
    private ObjectAnimator objectAnimator;
    private int musicPicRes = -1;

    public BackgroundAnimationLinearLayout(Context context) {
        this(context, null);
    }

    public BackgroundAnimationLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackgroundAnimationLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayerDrawable();
        initObjectAnimator();
    }

    private void initObjectAnimator() {
        objectAnimator = ObjectAnimator.ofFloat(this, "number", 0, 1.0f);
        objectAnimator.setDuration(DURATION_ANIMATION);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //把foreground图片设置上去之后，执行动画，让其从完全透明开始显示
                int foregroundAlpha = (int) ((float) animation.getAnimatedValue() * 255);

                layerDrawable.getDrawable(INDEX_FOREGROUND).setAlpha(foregroundAlpha);
                BackgroundAnimationLinearLayout.this.setBackground(layerDrawable);
            }
        });

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //完全显示之后，把它设置成background
                layerDrawable.setDrawableByLayerId(layerDrawable.getId(INDEX_BACKGROUND), layerDrawable.getDrawable(INDEX_FOREGROUND));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    layerDrawable.setDrawable(INDEX_BACKGROUND, layerDrawable.getDrawable(INDEX_FOREGROUND));
//                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setForeground(Drawable drawable) {
        layerDrawable.setDrawableByLayerId(layerDrawable.getId(INDEX_FOREGROUND), drawable);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            layerDrawable.setDrawable(INDEX_FOREGROUND, drawable);
//        }
    }

    public void beginAnimation() {
        objectAnimator.start();
    }

    public boolean isNeed2UpdateBackground(int musicPicRes) {
        if (this.musicPicRes == -1) {
            return true;
        }
        if (musicPicRes != this.musicPicRes) {
            return true;
        }
        return false;
    }

    private void initLayerDrawable() {
        Drawable backgroundDrawable = getContext().getDrawable(R.drawable.defaultbg_detail);
        Drawable[] drawables = new Drawable[2];

        drawables[INDEX_BACKGROUND] = backgroundDrawable;
        drawables[INDEX_FOREGROUND] = backgroundDrawable;

        layerDrawable = new LayerDrawable(drawables);
        //设置ID，为了兼容23以前的版本
        layerDrawable.setId(INDEX_BACKGROUND, INDEX_BACKGROUND);
        layerDrawable.setId(INDEX_FOREGROUND, INDEX_FOREGROUND);
        BackgroundAnimationLinearLayout.this.setBackground(layerDrawable);
    }
}
