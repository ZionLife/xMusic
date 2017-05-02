package com.zionstudio.xmusic.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.service.PlayMusicService;
import com.zionstudio.xmusic.view.RoundProgress;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Base activity for all activities playing music.
 * Created by Administrator on 2017/5/1 0001.
 */

public abstract class BasePlayMusicActivity extends BaseActivity {
    protected static ServiceConnection sConnection;
    protected static PlayMusicService sService;
    protected Song playingSong;
    protected static View sPlayingBar;
    private static final String TAG = "BasePlayMusicActivity";
    private static TimerTask sTimerTask;
    private static Timer sTimer;

    @BindView(R.id.iv_cover_playing)
    ImageView mIvCoverPlaying;
    @BindView(R.id.tv_title_playing)
    TextView mTvTitlePlaying;
    @BindView(R.id.tv_artist_playing)
    TextView mTvArtistPlaying;
    @BindView(R.id.iv_playlistbutton)
    ImageView mIvPlaylistbutton;
    @BindView(R.id.rp_playbutton)
    RoundProgress mRpPlaybutton;
    @BindView(R.id.rl_playbutton)
    RelativeLayout mRlPlaybutton;

    protected void initData() {
        //初始化ServiceConnection
        sConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                sService = ((PlayMusicService.PlayMusicBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                sService = null;
            }
        };
        //绑定服务
        bindService(new Intent(this, PlayMusicService.class), sConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void initView() {
        super.initView();
        sPlayingBar = this.getLayoutInflater().inflate(R.layout.view_playingbar, null, false);
        mRlPlaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sService != null && sService.isPlaying()) {
                    sService.pauseMusic();
                    updateProgress();

                } else if (sService.isPaused()) {
                    sService.startMusic();
                    updateProgress();
                }
            }
        });
        sTimerTask = new TimerTask() {
            @Override
            public void run() {
                updateProgress();
            }
        };
        sTimer = new Timer();
        sTimer.scheduleAtFixedRate(sTimerTask, 0, 1000);
    }

    /**
     * 更新进度条
     */
    private void updateProgress() {
        if (sService == null) {
            return;
        }
        if (sService.isPlaying()) {
            mRpPlaybutton.setState(RoundProgress.PLAYING_STATE);
            mRpPlaybutton.setProgress(sService.getProgress());
        } else if (sService.isPaused()) {
            mRpPlaybutton.setState(RoundProgress.PAUSED_STATE);
            mRpPlaybutton.setProgress(sService.getProgress());
        } else {
            mRpPlaybutton.setState(RoundProgress.PAUSED_STATE);
            mRpPlaybutton.setProgress(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //activity每次可见时都更新状态栏
        updatePlayingBar();
    }

    /**
     * 更新状态栏
     */
    protected void updatePlayingBar() {
        updateProgress();
        if (sService != null && (sService.isPlaying() || sService.isPaused())) {
            Song s = sService.getPlayingSong();
            //给状态栏设置歌曲名和歌手
            mTvTitlePlaying.setText(s.title);
            mTvArtistPlaying.setVisibility(View.VISIBLE);
            mTvArtistPlaying.setText(s.artist);
            //获取专辑封面并设置到状态栏
            Bitmap cover = sService.getCover();
            if (cover != null) {
                mIvCoverPlaying.setImageBitmap(cover);
            } else {
                mIvCoverPlaying.setImageResource(R.drawable.default_cover);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        unbindService(sConnection);
        Log.e(TAG, "on Destroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
