package com.zionstudio.xmusic.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.widget.FrameLayout;

import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.util.Utils;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_ANIM_DURA = 3 * 1000;
    private static float SPLASH_ANIM_FROM_SCALE = 1.0f;
    private static float SPLASH_ANIM_TO_SCALE = 1.08f;
    private int flag = 0;    //指示动画结束后跳转到哪个Activity 0：登录； 1：主页面
    @BindView(R.id.fl_splash)
    FrameLayout mFlSplash;

    private boolean doSkip = false;

    @Override
    protected void initData() {
        //初始化用户信息
        if (sApplication.mUserInfo == null) {
            flag = 0;
        } else {
            flag = 1;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        startAnim();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_splash;
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

        /**
         *         判断并跳转到登录界面
         */
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag == 0) {
                    Utils.skipToLoginActivity(SplashActivity.this);
                } else {
                    Utils.skipToMainActivity(SplashActivity.this);
                }
                SplashActivity.this.finish();
            }
        }, 1000);
    }
}
