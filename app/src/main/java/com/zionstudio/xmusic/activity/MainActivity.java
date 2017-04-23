package com.zionstudio.xmusic.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.view.CircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.rl_drawer_bg)
    RelativeLayout mRlDrawerBg;
    @BindView(R.id.sdv_avatar)
    ImageView mSdvAvatar;
    @BindView(R.id.iv_bg)
    ImageView mIvBg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.e(TAG, "TEST");
        initView();
    }

    private void initView() {
        //实现状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        Picasso.with(this).load(MyApplication.sUserInfo.profile.avatarUrl)
                .transform(new CircleTransform())
                .into(mSdvAvatar);

        Picasso.with(this).load(MyApplication.sUserInfo.profile.backgroundUrl)
                .into(mIvBg);
    }
}
