package com.zionstudio.xmusic.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.service.PlayMusicService;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.MyPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class PlayDetailActivity extends BaseActivity {
    @BindView(R.id.iv_back_playdetail)
    ImageView mIvBackPlaydetail;
    @BindView(R.id.tv_title_playdetail)
    TextView mTvTitlePlaydetail;
    @BindView(R.id.tv_artist_playdetail)
    TextView mTvArtistPlaydetail;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.toolbar_detail)
    Toolbar mToolbarDetail;
    @BindView(R.id.view)
    View mView;
    @BindView(R.id.iv_stylus)
    ImageView mIvStylus;
    @BindView(R.id.mpv)
    MyPlayerView mMpv;
    @BindView(R.id.iv_playbutton)
    ImageView mIvPlaybutton;
    @BindView(R.id.seekBar)
    SeekBar mSeekBar;
    //若打开该页面时，正在播放音乐，则唱针动画时间为0，直接旋转CD，否则唱针动画时长为500.
    private static final int STYLUS_ANIM_DURATION_ALREADY_PLAYED = 0;
    private static final int STYLUS_ANIM_DURATION_READY_PLAY = 500;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;

    private static final String TAG = "PlayDetailActivity";
    private ServiceConnection mConn;
    private PlayMusicService mService;
    private boolean needUpdateSeekBar = false;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mService != null) {
                updateProgress();
            }
            if (needUpdateSeekBar) {
                Log.e(TAG, "更新进度条");
                mHandler.postDelayed(this, 1000);
            }
        }
    };
    private ObjectAnimator mRotation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_playdetail;
    }

    @Override
    protected void initData() {
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((PlayMusicService.PlayMusicBinder) service).getService();
                //与服务建立连接后，更新View
                if (mService != null && (mService.isPlaying() || mService.isPaused())) {
                    Song s = mService.getPlayingSong();
                    mTvTitlePlaydetail.setText(s.title);
                    mTvArtistPlaydetail.setText(s.artist);
                    Bitmap cover = Utils.getCover(mService.getPlayingSong());
                    //设置专辑封面
                    mMpv.setCover(cover);
                    updateDuration();
                    updateProgress();
//                    float duration = mService.getDuration();
//                    int minutes = (int) (duration / (1000 * 60));
//                    int seconds = (int) ((duration / 1000) % 60);
//                    //设置
//                    mTvDuration.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
//                    mSeekBar.setMax((int) duration);
//                    mSeekBar.setProgress((int) mService.getProgress());
                    if (mService.isPlaying()) {
                        needUpdateSeekBar = true;
                        mHandler.post(mRunnable);
                        startPlayerAnim(STYLUS_ANIM_DURATION_ALREADY_PLAYED);
                        mIvPlaybutton.setImageResource(R.drawable.playing_icon);
                    } else {
                        mIvPlaybutton.setImageResource(R.drawable.paused_icon);
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        bindService(new Intent(this, PlayMusicService.class), mConn, BIND_AUTO_CREATE);
    }

    private void updateProgress() {
        mSeekBar.setProgress((int) mService.getProgress());
        float progress = mService.getProgress();
        int minutes = (int) (progress / (1000 * 60));
        int seconds = (int) ((progress / 1000) % 60);
        mTvProgress.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    }

    private void updateDuration() {
        float duration = mService.getDuration();
        int minutes = (int) (duration / (1000 * 60));
        int seconds = (int) ((duration / 1000) % 60);
        //设置
        mTvDuration.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
        mSeekBar.setMax((int) duration);
    }

    @Override
    protected void initView() {
        super.initView();
        mSeekBar.setProgress(0);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mService != null && (mService.isPlaying() || mService.isPaused())) {
                    mService.setProgress(seekBar.getProgress());
                    updateProgress();
                } else {
                    mSeekBar.setProgress(0);
                }
            }
        });
        initPlayer();
    }

    /**
     * 构建唱片机，调整布局参数
     */
    private void initPlayer() {
        Bitmap stylus = BitmapFactory.decodeResource(getResources(), R.drawable.stylus);
        //设置唱针的参数
        mIvStylus = (ImageView) findViewById(R.id.iv_stylus);
        mIvStylus.setPivotX(0);
        mIvStylus.setPivotY(0);


//        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(mIvStylus.getLayoutParams());
//        lp1.setMargins(stylus.getWidth() / 2, 0, 0, 0);
//        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        mIvStylus.setLayoutParams(lp1);
        mIvStylus.setRotation(-30);

        //设置CD布局的参数
        mMpv = (MyPlayerView) findViewById(R.id.mpv);
        int ivHeight = stylus.getHeight();
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mMpv.getLayoutParams());
        lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp2.setMargins(0, ivHeight * 2 / 3, 0, 0);
        mMpv.setLayoutParams(lp2);
        stylus.recycle();
    }

    private void hangPlayerAnim() {
        //让唱盘再转1000毫秒后取消动画
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 1000);
        mRotation.pause();

        ObjectAnimator animStylus = ObjectAnimator.ofFloat(mIvStylus, "rotation", 0, -30);
        animStylus.setDuration(500);
        animStylus.setInterpolator(null);
        animStylus.start();
    }

    /**
     * 开启播放状态的动画
     *
     * @param duration 设置duration参数是为了当第一次进入该activity时，如果是正在播放音乐的状态，则唱针无需动画，直接显示在唱盘上
     */
    private void startPlayerAnim(int duration) {

        final ObjectAnimator animStylus = ObjectAnimator.ofFloat(mIvStylus, "rotation", -30, 0);
        animStylus.setDuration(duration);
        animStylus.setInterpolator(null);
        animStylus.start();
        animStylus.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mService != null && mService.isPlaying() && !mService.isPaused()) {
                    if (mRotation == null) {
                        mRotation = ObjectAnimator.ofFloat(mMpv, "rotation", 0, 360);
                        mRotation.setDuration(25000);
                        mRotation.setInterpolator(null);
                        mRotation.setRepeatCount(ObjectAnimator.INFINITE);
                    }
                    mRotation.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
    }

    @OnClick({R.id.iv_playbutton, R.id.iv_back_playdetail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_playbutton:
                if (mService != null) {
                    if (mService.isPaused()) {
                        mService.continueMusic();
                        needUpdateSeekBar = true;
                        mHandler.post(mRunnable);
                        mIvPlaybutton.setImageResource(R.drawable.playing_icon);
                        startPlayerAnim(STYLUS_ANIM_DURATION_READY_PLAY);
                    } else if (mService.isPlaying()) {
                        needUpdateSeekBar = false;
                        hangPlayerAnim();
                        mService.pauseMusic();
                        mIvPlaybutton.setImageResource(R.drawable.paused_icon);
                    }
                }
                break;
            case R.id.iv_back_playdetail:
                this.finish();
                break;
        }
    }
}
