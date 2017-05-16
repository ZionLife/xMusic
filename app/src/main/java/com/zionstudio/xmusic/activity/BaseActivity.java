package com.zionstudio.xmusic.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zionstudio.xmusic.MyApplication;

import butterknife.ButterKnife;

/**
 * Base activity for all activities.
 * Created by Administrator on 2017/4/26 0026.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static MyApplication sApplication = MyApplication.getMyApplication();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        ButterKnife.bind(this);
        sApplication.addActivity(this);
        initData();
        initView();
    }

    protected void initView() {
        //实现状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * 获取contentView的资源ID
     *
     * @return
     */
    protected abstract int getLayoutResID();

    protected abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放MyApplication中对当前Activity对象的引用。
        sApplication.removeActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sApplication.savePlayInfo();
    }
}
