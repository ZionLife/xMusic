package com.zionstudio.xmusic.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import butterknife.OnClick;

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
    @BindView(R.id.ll_playbar)
    LinearLayout mLlPlaybar;

    private PlayStateReceiver mReceiver;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean needUpdateProgress = false;
    private ServiceConnection mConn = null;

    protected void initData() {

        //初始化ServiceConnection
        if (sConnection == null) {
            sConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (sService == null) {
                        sService = ((PlayMusicService.PlayMusicBinder) service).getService();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    sService = null;
                }
            };
            mConn = sConnection;
        }
        bindService(new Intent(this, PlayMusicService.class), sConnection, BIND_AUTO_CREATE);
        //注册广播接收者
        mReceiver = new PlayStateReceiver();
        IntentFilter filter = new IntentFilter("com.zionstudio.xmusic.playstate");
        registerReceiver(mReceiver, filter);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgress();
                if (needUpdateProgress) {
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    @Override
    protected void initView() {
        super.initView();
        sPlayingBar = this.getLayoutInflater().inflate(R.layout.view_playingbar, null, false);
    }

    /**
     * 更新进度条
     */
    private void updateProgress() {
        if (sService == null) {
            return;
        }
        if (mRpPlaybutton != null) {
            if (sService.isPlaying()) {
                mRpPlaybutton.setState(RoundProgress.PLAYING_STATE);
//            needUpdateProgress = true;
            } else {
                mRpPlaybutton.setState(RoundProgress.PAUSED_STATE);
//            needUpdateProgress = false;
            }
            mRpPlaybutton.setProgress(sService.getProgress());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //activity每次可见时都更新状态栏
        updatePlayingBar();
        mHandler.post(mRunnable);
        //如果正在播放音乐则需要循环更新进度，置needUpdateProgress为true
        if (sService != null && sService.isPlaying()) {
            needUpdateProgress = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        needUpdateProgress = false;
    }

    /**
     * 更新状态栏
     */
    protected void updatePlayingBar() {
        if (sService != null) {
            if (sService.isPlaying()) {
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
            updateProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        if (mConn == sConnection) {
            Log.e(TAG, "equal");
        } else {
            Log.e(TAG, "not equal");
        }
        unbindService(sConnection);
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.rl_playbutton, R.id.ll_playbar})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_playbutton:
                if (sService != null && sService.isPlaying()) {
                    sService.pauseMusic();
                } else if (sService.isPaused()) {
                    sService.continueMusic();
                }
                break;
            case R.id.ll_playbar:
                startActivity(new Intent(BasePlayMusicActivity.this, PlayDetailActivity.class));
        }
    }

    class PlayStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            switch (type) {
                case "start":
                    BasePlayMusicActivity.this.updatePlayingBar();
                    mHandler.post(mRunnable);
                    needUpdateProgress = true;
                    break;
                case "paused":
                    BasePlayMusicActivity.this.updatePlayingBar();
                    needUpdateProgress = false;
                    break;
                case "continue":
                    BasePlayMusicActivity.this.updatePlayingBar();
                    needUpdateProgress = true;
                    mHandler.post(mRunnable);
                    break;
                case "stop":
                    break;
                case "end":
                    mTvTitlePlaying.setText("播放列表为空");
                    mTvArtistPlaying.setVisibility(View.GONE);
                    mIvCoverPlaying.setImageResource(R.drawable.default_cover);
                    needUpdateProgress = false;
                    break;
            }
        }
    }
}
