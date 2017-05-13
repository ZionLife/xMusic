package com.zionstudio.xmusic.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.util.Utils;

import java.io.IOException;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class PlayMusicService extends Service {
    public static final int PLAY_MUSIC = 0;
    public static final int PAUSE_MUSIC = 1;
    public static final int STOP_MUSIC = 2;
    public static final int END_MUSIC = 3;

    private static boolean isStop = true;
    private static boolean isPaused = false;
    private static final String TAG = "PlayMusicService";
    private static MediaPlayer sPlayer;
    private static String playingPath = "";
    private Song playingSong;
    private final IBinder mBinder = new PlayMusicBinder();
    private static Bitmap sCover;

    private static PlayMusicService sPlayMusicService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        if (sPlayMusicService == null) {
//            sPlayMusicService = this;
//        }

        Log.e(TAG, "on PlayMusicService create");
        if (sPlayer == null) {
            sPlayer = new MediaPlayer();
            sPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playingPath = "";
                    Intent intent = new Intent("com.zionstudio.xmusic.playstate");
                    intent.putExtra("type", "end");
                    sendBroadcast(intent);
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
     * @param song 要播放的音乐
     */
    public void playMusic(Song song) {
        String path = song.path;
        if (!sPlayer.isPlaying() || !path.equals(playingPath)) {
            sPlayer.reset();
            try {
                sPlayer.setDataSource(path);
                sPlayer.prepare();
                sPlayer.setLooping(false);
                sPlayer.start();
                //回收Bitmap
                if (sCover != null) {
                    sCover.recycle();
                }
                sCover = null;
                isPaused = false;
                playingSong = song;
                playingPath = path;
                loadCover();
                Intent intent = new Intent("com.zionstudio.xmusic.playstate");
                intent.putExtra("type", "start");
                sendBroadcast(intent);
            } catch (IOException e) {
                e.printStackTrace();
                Intent intent = new Intent("com.zionstudio.xmusic.playstate");
                intent.putExtra("type", "end");
                sendBroadcast(intent);
            }
        }
    }

    /**
     * 暂停播放音乐
     */
    public void pauseMusic() {
        if (sPlayer.isPlaying()) {
            sPlayer.pause();
            isPaused = true;
            Intent intent = new Intent("com.zionstudio.xmusic.playstate");
            intent.putExtra("type", "paused");
            sendBroadcast(intent);
        }
    }

    /**
     * 停止播放音乐
     */
    public void stopMusic() {
        if (sPlayer.isPlaying()) {
            sPlayer.stop();
            Intent intent = new Intent("com.zionstudio.xmusic.playstate");
            intent.putExtra("type", "stop");
            sendBroadcast(intent);
        }
    }

    /**
     * 继续播放音乐
     */
    public void continueMusic() {
        sPlayer.start();
        isPaused = false;
        Intent intent = new Intent("com.zionstudio.xmusic.playstate");
        intent.putExtra("type", "continue");
        sendBroadcast(intent);
    }

    /**
     * 判断是否正在播放音乐
     *
     * @return 正在播放音乐返回true，否则返回false
     */
    public boolean isPlaying() {
        return sPlayer.isPlaying();
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 获取正在播放的音乐文件路径
     *
     * @return 正在播放的音乐文件路径
     */
    public String getPlayingPath() {
        return playingPath;
    }

    /**
     * 获取播放的歌曲
     */
    public Song getPlayingSong() {
        return playingSong;
    }

    /**
     * 获取正在播放的歌曲的封面
     */
    public Bitmap getCover() {
        if (sPlayer.isPlaying() || isPaused) {
            return sCover;
        }
        return null;
    }

    /**
     * 加载正在播放的歌曲的封面
     */
    private void loadCover() {
        Bitmap bitmap = Utils.getCover(playingSong);
    }

    /**
     * 获取歌曲播放进度的百分比
     *
     * @return
     */
    public float getProgressPercentage() {
        if (sPlayer != null && (sPlayer.isPlaying() || isPaused)) {
            return sPlayer.getCurrentPosition() / (float) sPlayer.getDuration();
        }
        return 0f;
    }

    /**
     * 获取当前播放进度
     *
     * @return
     */
    public float getProgress() {
        if (sPlayer != null && (sPlayer.isPlaying() || isPaused)) {
            return sPlayer.getCurrentPosition();
        }
        return 0f;
    }

    /**
     * 获取歌曲的总长度
     *
     * @return
     */
    public float getDuration() {
        if (sPlayer != null && (sPlayer.isPlaying() || isPaused)) {
            return sPlayer.getDuration();
        }
        return 0f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放MediaPlayer
        sPlayer.release();
    }

    /**
     * 设置播放进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (sPlayer != null) {
            sPlayer.seekTo(progress);
        }
    }

    public class PlayMusicBinder extends Binder {
        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }
}
