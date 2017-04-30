package com.zionstudio.xmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.util.Utils;

import java.io.IOException;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class PlayMusicService extends Service {
    public static final int PLAY_MUSIC = 0;
    public static final int PAUSE_MUSIC = 1;
    public static final int STOP_MUSIC = 2;

    private boolean isStop = true;
    private static final String TAG = "PlayMusicService";
    private static MediaPlayer mPlayer;
    private static String playingPath = "";

    private final IBinder mBinder = new PlayMusicBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "on PlayMusicService create");
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    Intent intent = new Intent();
//                    intent.setAction("com.complete");
//                    sendBroadcast(intent);
                    Utils.makeToast("音乐播放结束");
                    playingPath = "";
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 播放音乐
     *
     * @param path 音乐文件的路径
     */
    public void playMusic(String path) {
        if (!mPlayer.isPlaying() || !path.equals(playingPath)) {
            mPlayer.reset();
            try {
                mPlayer.setDataSource(path);
                mPlayer.prepare();
                mPlayer.setLooping(false);
                mPlayer.start();
                Log.e(TAG, "on playing Music:" + path);
                playingPath = path;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停播放音乐
     */
    public void pauseMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    /**
     * 停止播放音乐
     */
    public void stopMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    /**
     * 继续播放音乐
     */
    public void startMusic() {
        mPlayer.start();
    }

    /**
     * 判断是否正在播放音乐
     *
     * @return 正在播放音乐返回true，否则返回false
     */
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    /**
     * 获取正在播放的音乐文件路径
     *
     * @return 正在播放的音乐文件路径
     */
    public String getPlayingPath() {
        return playingPath;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放MediaPlayer
        mPlayer.release();
    }

    public class PlayMusicBinder extends Binder {
        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }
}
