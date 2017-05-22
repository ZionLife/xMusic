package com.zionstudio.xmusic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.util.Log;


import com.zionstudio.xmusic.model.playlist.Song;
import com.zionstudio.xmusic.model.user.UserInfo;
import com.zionstudio.xmusic.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class MyApplication extends Application {
    public UserInfo mUserInfo = null;
    public static Context sContext;
    public List<Song> mPlayingList;
    public List<Song> mLocalSongs;
    public List<Song> mRecentlyPlayedList;
    public int mPlayingIndex = 0;
    private List<Activity> mActivities = new ArrayList<Activity>();
    public static MyApplication sMyApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        sMyApplication = this;
        if (mUserInfo == null) {
            SharedPreferences userSP = getSharedPreferences("userSP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userSP.edit();
            String userInfo = userSP.getString("userInfo", null);
            if (userInfo != null) {
                mUserInfo = (UserInfo) Utils.String2Object(userInfo);
            }
        }

        SharedPreferences sp = getSharedPreferences("PlayingInfo", Context.MODE_PRIVATE);
        String playingList = sp.getString("PlayingList", null);
        //取出播放列表
        if (playingList != null) {
            mPlayingList = (List<Song>) Utils.String2Object(playingList);
        } else {
            mPlayingList = new ArrayList<Song>();
        }
        //取出播放歌曲索引
        mPlayingIndex = sp.getInt("PlayingIndex", -1);
        //取出最近播放列表
        String recentlyPlayedList = sp.getString("RecentlyPlayedList", null);
        if (recentlyPlayedList != null) {
            mRecentlyPlayedList = (List<Song>) Utils.String2Object(recentlyPlayedList);
        } else {
            mRecentlyPlayedList = new ArrayList<Song>();
        }
        //取出本地歌曲
        String localSongs = sp.getString("LocalSongs", null);
        if (localSongs != null) {
            mLocalSongs = (List<Song>) Utils.String2Object(localSongs);
        } else {
            mLocalSongs = new ArrayList<Song>();
        }
    }

    /**
     * 添加Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    /**
     * 移除指定activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        mActivities.remove(activity);
        activity = null;
    }

    /**
     * 退出程序
     */
    public void exitApplication() {
        for (Activity activity : mActivities) {
            activity.finish();
        }
        savePlayInfo();
        //杀死进程，让MyApplication被回收
        Process.killProcess(Process.myPid());
    }

    public void savePlayInfo() {
        SharedPreferences sp = getSharedPreferences("PlayingInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("PlayingList", Utils.Object2String(mPlayingList));
        editor.putInt("PlayingIndex", mPlayingIndex);
        editor.putString("RecentlyPlayedList", Utils.Object2String(
                mRecentlyPlayedList == null ? new ArrayList<Song>() : mRecentlyPlayedList));

//        SharedPreferences sp = getSharedPreferences("PlayingInfo", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
        editor.putString("LocalSongs", Utils.Object2String(mLocalSongs));
        editor.commit();
        Log.e("MyApplication", "savePlayInfo");
    }

    public void savePlayIndex() {
        SharedPreferences sp = getSharedPreferences("PlayingInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("PlayingIndex", mPlayingIndex);
        editor.commit();
    }

    /**
     * 取得MyApplication实例
     *
     * @return
     */
    public static MyApplication getMyApplication() {
        return sMyApplication;
    }
}
