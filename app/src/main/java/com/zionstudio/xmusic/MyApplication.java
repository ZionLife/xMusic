package com.zionstudio.xmusic;

import android.app.Application;
import android.content.Context;


import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.model.user.UserInfo;

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
    }
}
