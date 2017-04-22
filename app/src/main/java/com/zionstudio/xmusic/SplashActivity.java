package com.zionstudio.xmusic;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.fl_splash)
    FrameLayout mFlSplash;

    private static final int SPLASH_ANIM_DURA = 3 * 1000;
    private static float SPLASH_ANIM_FROM_SCALE = 1.0f;
    private static float SPLASH_ANIM_TO_SCALE = 1.05f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
//        skipToLoginActivity();
        startAnim();
    }

    private void startAnim() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator animX = ObjectAnimator.ofFloat(mFlSplash, "scaleX", SPLASH_ANIM_FROM_SCALE, SPLASH_ANIM_TO_SCALE);
        animX.setRepeatCount(ValueAnimator.INFINITE);
        animX.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator animY = ObjectAnimator.ofFloat(mFlSplash, "scaleY", SPLASH_ANIM_FROM_SCALE, SPLASH_ANIM_TO_SCALE);
        animY.setRepeatCount(ValueAnimator.INFINITE);
        animY.setRepeatMode(ValueAnimator.REVERSE);

        set.playTogether(
                animX, animY
        );
        set.setDuration(3 * 1000).start();

        //判断并跳转到登录界面
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                skipToLoginActivity();
                SplashActivity.this.finish();
            }
        }, 3000);
    }


    private void skipToMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        this.finish();
    }

    private void skipToLoginActivity(){
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        SplashActivity.this.finish();
    }
}
