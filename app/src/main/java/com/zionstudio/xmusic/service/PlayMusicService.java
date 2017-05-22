package com.zionstudio.xmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.activity.PlayDetailActivity;
import com.zionstudio.xmusic.model.playlist.Song;
import com.zionstudio.xmusic.util.BitmapUtils;
import com.zionstudio.xmusic.util.Constants;
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

    private MyApplication mApplication = MyApplication.getMyApplication();

    private static boolean isStop = true;
    private static boolean isPaused = false;
    private static final String TAG = "PlayMusicService";
    private static MediaPlayer sPlayer;
    private static String playingPath = "";
    private Song playingSong;
    private final IBinder mBinder = new PlayMusicBinder();
    private static Bitmap sCover;

    private static final String NOTIFICATION_ACTION = "com.zionstudio.xmusic.notification";
    private final int PLAY_BUTTON = 0;
    private final int NEXT_BUTTON = 1;
    private final int EXIT_BUTTON = 2;
    private NotificationReceiver mReceiver = null;
    private final int NOTIFICATION_ID = 1025; //Notification的ID
    private NotificationManager mManager;
    private byte[] mCoverBytes;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (sPlayer == null) {
            sPlayer = new MediaPlayer();
            //注册播放完成的监听器
            sPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playingPath = "";
                    if (mApplication.mPlayingIndex < mApplication.mPlayingList.size() - 1) {
                        mApplication.mPlayingIndex++;
                        PlayMusicService.this.playMusic(mApplication.mPlayingList.get(mApplication.mPlayingIndex));
                    } else {
                        Intent intent = new Intent("com.zionstudio.xmusic.playstate");
                        intent.putExtra("type", "end");
                        sendBroadcast(intent);
                    }
                }
            });
            //注册缓冲进度监听器
            sPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    //当播放网络歌曲时，歌曲加载过程中会回调这个方法
//                    Log.e(TAG, "进度: " + percent + "%");
                }
            });

            //
            sPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG, "on Prepared");
                    sPlayer.start();
                    if (sCover != null) {
                        sCover.recycle();
                    }
                    sCover = null;
                    isPaused = false;
                    loadCoverBytes();
                    Intent intent = new Intent("com.zionstudio.xmusic.playstate");
                    intent.putExtra("type", "start");
                    sendBroadcast(intent);
                    sendNotification();
                }
            });
        }
        mReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter(NOTIFICATION_ACTION);
        registerReceiver(mReceiver, filter);
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
        String path = song.url;
        if (!sPlayer.isPlaying() || !path.equals(playingPath)) {
            sPlayer.reset();
            try {
                sPlayer.setDataSource(path);
                //异步进行prepare,减少主界面的卡顿
                sPlayer.prepareAsync();
                sPlayer.setLooping(false);

                playingSong = song;
                playingPath = path;
            } catch (IOException e) {
                e.printStackTrace();
                Intent intent = new Intent("com.zionstudio.xmusic.playstate");
                intent.putExtra("type", "end");
                sendBroadcast(intent);
            }
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.view_notification);
        Bitmap bitmap;
        if (isPlaying() || isPaused()) {
            //加载封面图片
            byte[] bytes = BitmapUtils.getCoverByteArray(playingSong);
            if (bytes != null) {
                bitmap = BitmapUtils.decodeSampleBitmapFromBytes(bytes, Utils.dp2px(this, 68), Utils.dp2px(this, 68));
            } else {
                bitmap = BitmapUtils.decodeSampleBitmapFromResource(getResources(), R.drawable.cover_square, Utils.dp2px(this, 68), Utils.dp2px(this, 68));
            }
            //设置歌曲名和演唱者
            views.setTextViewText(R.id.tv_title_notification, playingSong.title);
            views.setViewVisibility(R.id.tv_artist_notification, View.VISIBLE);
            views.setTextViewText(R.id.tv_artist_notification, playingSong.artist);
        } else {
            //当未播放时，重置通知栏样式
            bitmap = BitmapUtils.decodeSampleBitmapFromResource(getResources(), R.drawable.cover_square, Utils.dp2px(this, 68), Utils.dp2px(this, 68));
            views.setTextViewText(R.id.tv_title_notification, "当前未播放音乐");
            views.setViewVisibility(R.id.tv_artist_notification, View.GONE);
        }
        //设置播放按钮图标
        if (isPlaying()) {
            views.setImageViewResource(R.id.iv_playbutton_notification, R.drawable.a_2);
        } else {
            views.setImageViewResource(R.id.iv_playbutton_notification, R.drawable.a_4);
        }
        views.setImageViewBitmap(R.id.iv_cover_notification, bitmap);
        Notification notification;
        //给按钮设置事件
        Intent btnIntent = new Intent(NOTIFICATION_ACTION);
        //暂停/播放按钮事件
        btnIntent.putExtra("ButtonID", PLAY_BUTTON);
        PendingIntent playBtnIntent = PendingIntent.getBroadcast(this, 0, btnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.iv_playbutton_notification, playBtnIntent);
        //下一首事件
        btnIntent.putExtra("ButtonID", NEXT_BUTTON);
        PendingIntent nextBtnIntent = PendingIntent.getBroadcast(this, 1, btnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.iv_nextsong_notification, nextBtnIntent);
        //退出程序事件
        btnIntent.putExtra("ButtonID", EXIT_BUTTON);
        PendingIntent exitBtnIntent = PendingIntent.getBroadcast(this, 2, btnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.iv_close_notification, exitBtnIntent);
        //设置点击进入播放详情页
        Intent intent = new Intent(this, PlayDetailActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContent(views)
                .setWhen(System.currentTimeMillis())
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher);
        notification = builder.build();
        //一直显示直到用户响应
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.contentIntent = pendingIntent;
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.notify(NOTIFICATION_ID, notification);
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
            sendNotification();
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
            sendNotification();
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
        sendNotification();
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
     * 获取正在播放的歌曲的封面的字节数组
     */
    public byte[] getCoverBytes() {
        if (sPlayer.isPlaying() || isPaused) {
            return mCoverBytes;
        }
        return null;
    }

    /**
     * 加载正在播放的歌曲的封面的字节数组
     */
    private void loadCoverBytes() {
//        Bitmap bitmap = BitmapUtils.getCover(playingSong);
        Log.e(TAG, "on load cover bytes");
        if (playingSong == null) {
            return;
        }
        if (playingSong.type == Constants.TYPE_LOCAL) {
            Log.e(TAG, "on load local cover bytes");
            mCoverBytes = BitmapUtils.getCoverByteArray(playingSong);
            Intent intent = new Intent("com.zionstudio.xmusic.playstate");
            intent.putExtra("type", "updatePlaybar");
            sendBroadcast(intent);

        } else {
            Log.e(TAG, "on load online cover bytes");
            Glide.with(this)
                    .asBitmap()
                    .load(playingSong.al.picUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            mCoverBytes = BitmapUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);
                            Log.e(TAG, "on load online cover bytes succeed" + "url: " + playingSong.al.picUrl);
                            Intent intent = new Intent("com.zionstudio.xmusic.playstate");
                            intent.putExtra("type", "updatePlaybar");
                            sendBroadcast(intent);
                        }
                    });
//            Picasso.with(this)
//                    .load(playingSong.al.picUrl)
//                    .into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            mCoverBytes = BitmapUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);
//                            Log.e(TAG, "on load online cover bytes succeed" + "url: " + playingSong.al.picUrl);
//                            Intent intent = new Intent("com.zionstudio.xmusic.playstate");
//                            intent.putExtra("type", "updatePlaybar");
//                            sendBroadcast(intent);
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable errorDrawable) {
//                            Log.e(TAG, "on load online cover bytes failed" + "url: " + playingSong.al.picUrl);
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//                            Log.e(TAG, "on prepare load online cover bytes" + "url: " + playingSong.al.picUrl);
//                        }
//                    });
        }
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

    /**
     * 播放下一首歌，如果存在的话
     */
    public void playNextSong() {
        if (mApplication.mPlayingIndex < mApplication.mPlayingList.size() - 1) {
            //如果还存在下一首，则播放
            mApplication.mPlayingIndex++;
            this.playMusic(mApplication.mPlayingList.get(mApplication.mPlayingIndex));
            sendNotification();
        } else {
            Utils.makeToast("下一首是不存在的");
        }
    }

    /**
     * 播放上一首歌，如果存在的话
     */
    public void playPrevSong() {
        if (mApplication.mPlayingIndex > 0) {
            //如果还存在上一首，则播放
            mApplication.mPlayingIndex--;
            this.playMusic(mApplication.mPlayingList.get(mApplication.mPlayingIndex));
            sendNotification();
        } else {
            Utils.makeToast("上一首是不存在的");
        }
    }

    public void playUrl(String url) {
        sPlayer.reset();
        try {
            sPlayer.setDataSource(url);
            sPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放MediaPlayer
        sPlayer.release();

//        //保存当前播放的歌曲的索引
//        SharedPreferences sp = getSharedPreferences("PlyaingInfo", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("PlayingIndex", mApplication.mPlayingIndex);
//        editor.commit();

        unregisterReceiver(mReceiver);
        mApplication = null;
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

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int btnID = intent.getIntExtra("ButtonID", -1);
            switch (btnID) {
                case PLAY_BUTTON:
                    if (PlayMusicService.this.isPlaying()) {
                        PlayMusicService.this.pauseMusic();
                    } else if (PlayMusicService.this.isPaused()) {
                        PlayMusicService.this.continueMusic();
                    }
                    break;
                case NEXT_BUTTON:
                    PlayMusicService.this.playNextSong();
                    break;
                case EXIT_BUTTON:
                    mManager.cancel(NOTIFICATION_ID);
                    mApplication.exitApplication();
                    break;
            }
        }
    }
}
