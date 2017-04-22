package com.zionstudio.xmusic;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class MyApplication extends Application {
    public static  Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
