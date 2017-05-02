package com.zionstudio.xmusic.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.util.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class PlayMusicService extends Service {
    public static final int PLAY_MUSIC = 0;
    public static final int PAUSE_MUSIC = 1;
    public static final int STOP_MUSIC = 2;

    private static boolean isStop = true;
    private static boolean isPaused = false;
    private static final String TAG = "PlayMusicService";
    private static MediaPlayer sPlayer;
    private static String playingPath = "";
    private Song playingSong;
    private final IBinder sBinder = new PlayMusicBinder();
    private static Bitmap sCover;
    private MediaMetadataRetriever sRetriver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "on PlayMusicService create");
        if (sPlayer == null) {
            sPlayer = new MediaPlayer();
            sPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
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
            } catch (IOException e) {
                e.printStackTrace();
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
        }
    }

    /**
     * 停止播放音乐
     */
    public void stopMusic() {
        if (sPlayer.isPlaying()) {
            sPlayer.stop();
        }
    }

    /**
     * 继续播放音乐
     */
    public void startMusic() {
        sPlayer.start();
        isPaused = false;
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
        File f = new File(playingPath);
        sRetriver = new MediaMetadataRetriever();
        sRetriver.setDataSource(playingPath);
        byte[] cover = sRetriver.getEmbeddedPicture();
        if (cover != null) {
            sCover = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            sRetriver.release();
        }
    }

    public float getProgress() {
        if (sPlayer != null && (sPlayer.isPlaying() || isPaused)) {
            return sPlayer.getCurrentPosition() / (float) sPlayer.getDuration();
        }
        return 0f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放MediaPlayer
        sPlayer.release();
    }

    public class PlayMusicBinder extends Binder {
        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }
}
