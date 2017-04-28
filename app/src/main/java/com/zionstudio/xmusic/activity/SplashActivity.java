package com.zionstudio.xmusic.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.user.UserInfo;
import com.zionstudio.xmusic.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class SplashActivity extends BaseActivity {


    private static final int SPLASH_ANIM_DURA = 3 * 1000;
    private static float SPLASH_ANIM_FROM_SCALE = 1.0f;
    private static float SPLASH_ANIM_TO_SCALE = 1.08f;
    private int flag = 0;    //指示动画结束后跳转到哪个Activity 0：登录； 1：主页面
    @BindView(R.id.fl_splash)
    FrameLayout mFlSplash;

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
        init();
    }

    private void init() {
        //初始化用户信息
        SharedPreferences userSP = getSharedPreferences("userSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSP.edit();
        String userInfo = userSP.getString("userInfo", null);
        if (userInfo == null) {
            flag = 0;
        } else {
            flag = 1;
            MyApplication.sUserInfo = (UserInfo) Utils.String2Object(userInfo);
        }
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
