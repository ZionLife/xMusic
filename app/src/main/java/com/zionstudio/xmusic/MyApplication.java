package com.zionstudio.xmusic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.model.user.UserInfo;
import com.zionstudio.xmusic.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class MyApplication extends Application {
    public static UserInfo sUserInfo = null;
    public static Context sContext;
    public static List<Song> sPlayingList;
    public static List<Song> sRecentlyPlayedList;
    public static int sPlayingIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        Log.e("MyApplication", "on MyApplication Create");
        if (sUserInfo == null) {
            SharedPreferences userSP = getSharedPreferences("userSP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userSP.edit();
            String userInfo = userSP.getString("userInfo", null);
            if (userInfo != null) {
                MyApplication.sUserInfo = (UserInfo) Utils.String2Object(userInfo);
            }
        }
    }
}
