package com.zionstudio.xmusic.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.service.PlayMusicService;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.BackgroundAnimationLinearLayout;
import com.zionstudio.xmusic.view.MyPlayerView;

import net.qiujuer.genius.blur.StackBlur;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zionstudio.xmusic.MyApplication.sPlayingIndex;
import static com.zionstudio.xmusic.MyApplication.sPlayingList;

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
    @BindView(R.id.ll_playdetail)
    BackgroundAnimationLinearLayout mLlPlaydetail;
    @BindView(R.id.iv_presong)
    ImageView mIvPresong;
    @BindView(R.id.iv_nextsong)
    ImageView mIvNextsong;
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
                mHandler.postDelayed(this, 1000);
            }
        }
    };
    private ObjectAnimator mCDRotation = null;
    private ObjectAnimator mAnimStylus = null;
    private PlayStateReceiver mReceiver = new PlayStateReceiver();

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
                    updateState();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        bindService(new Intent(this, PlayMusicService.class), mConn, BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter("com.zionstudio.xmusic.playstate");
        registerReceiver(mReceiver, filter);
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

    /**
     * 有音乐播放时，更新该页面
     */
    private void updateState() {
        Song s = mService.getPlayingSong();
        mTvTitlePlaydetail.setText(s.title);
        mTvArtistPlaydetail.setText(s.artist);
        Bitmap cover = Utils.getCover(mService.getPlayingSong());
        setBackgroundDrawable();
        //设置专辑封面
        mMpv.setCover(cover);
        updateDuration();
        updateProgress();
        if (mService.isPlaying()) {
            needUpdateSeekBar = true;
            mHandler.post(mRunnable);
            startPlayerAnim(STYLUS_ANIM_DURATION_ALREADY_PLAYED);
            mIvPlaybutton.setImageResource(R.drawable.playing_icon);
        } else {
            needUpdateSeekBar = false;
            mIvPlaybutton.setImageResource(R.drawable.paused_icon);
        }
    }

    /**
     * 歌曲播放结束且没有下一首歌曲时，重置该页面
     */
    public void resetState() {
        mIvPlaybutton.setImageResource(R.drawable.paused_icon);
        needUpdateSeekBar = false;
        mTvTitlePlaydetail.setText("云音乐");
        mTvArtistPlaydetail.setText("");
        mLlPlaydetail.setForeground(R.drawable.bg_detail);
        mTvDuration.setText("00:00");
        mTvProgress.setText("00:00");
        mLlPlaydetail.beginAnimation();
        hangPlayerAnim(PlayMusicService.END_MUSIC);
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


    public void setBackgroundDrawable() {
//        final float widthHeightSize = (float) (Utils.getScreenWidth() * 1.0 /Utils.getScreenHeight() * 1.0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap cover = Utils.getCover(mService.getPlayingSong());
                Log.e(TAG, "压缩前的大小：" + Utils.getBitmapsize(cover));
                if (cover == null) {
                    cover = BitmapFactory.decodeResource(getResources(), R.drawable.cover);
                }
                Bitmap blurBitmap = StackBlur.blurNativelyPixels(cover, 170, false);
                final Drawable d = new BitmapDrawable(blurBitmap);
                d.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLlPlaydetail.setForeground(d);
                        mLlPlaydetail.beginAnimation();
                    }
                });
            }
        }).start();
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

    /**
     * 暂停唱片机动画
     */
    private void hangPlayerAnim(final int type) {
        //让唱盘再转500毫秒后取消动画
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case PlayMusicService.END_MUSIC:
                        mCDRotation.cancel();
                        //将封面设置成初始
                        mMpv.setCover(null);
                        mMpv.setRotation(0);
                        break;
                    case PlayMusicService.PAUSE_MUSIC:
                        mCDRotation.pause();
                        break;
                }
                ObjectAnimator animStylus = ObjectAnimator.ofFloat(mIvStylus, "rotation", 0, -30);
                animStylus.setDuration(500);
                animStylus.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIvPlaybutton.setClickable(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animStylus.setInterpolator(null);
                animStylus.start();
            }
        }, 500);


    }

    /**
     * 开启播放状态的动画
     *
     * @param duration 设置duration参数是为了当第一次进入该activity时，如果是正在播放音乐的状态，则唱针无需动画，直接显示在唱盘上
     */
    private void startPlayerAnim(int duration) {
        mAnimStylus = ObjectAnimator.ofFloat(mIvStylus, "rotation", -30, 0);
        mAnimStylus.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIvPlaybutton.setClickable(true);
                if (mService != null && mService.isPlaying() && !mService.isPaused()) {
                    if (mCDRotation == null) {
                        mCDRotation = ObjectAnimator.ofFloat(mMpv, "rotation", mMpv.getRotation(), 360);
                        mCDRotation.setDuration(25000);
                        //匀速旋转
                        mCDRotation.setInterpolator(new LinearInterpolator());
                        mCDRotation.setRepeatCount(ObjectAnimator.INFINITE);
                        mMpv.setRotation(0);
                        mCDRotation.start();
                    } else if (mCDRotation.isPaused()) {
                        //从上次暂停的位置开始继续旋转
                        mCDRotation.resume();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimStylus.setInterpolator(new LinearInterpolator());
        mAnimStylus.setDuration(duration);
        mAnimStylus.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.post(mRunnable);
        if (mService != null && mService.isPlaying() && !mService.isPaused()) {
            needUpdateSeekBar = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        needUpdateSeekBar = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        needUpdateSeekBar = false;
        unbindService(mConn);
        unregisterReceiver(mReceiver);
    }

    @OnClick({R.id.iv_playbutton, R.id.iv_back_playdetail, R.id.iv_presong, R.id.iv_nextsong})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_playbutton:
                if (mService != null) {
                    if (mService.isPaused()) {

                        needUpdateSeekBar = true;
                        mHandler.post(mRunnable);
                        mIvPlaybutton.setImageResource(R.drawable.playing_icon);
                        mIvPlaybutton.setClickable(false);
                        startPlayerAnim(STYLUS_ANIM_DURATION_READY_PLAY);
                        mService.continueMusic();
                    } else if (mService.isPlaying()) {
                        needUpdateSeekBar = false;
                        mIvPlaybutton.setClickable(false);
                        hangPlayerAnim(PlayMusicService.PAUSE_MUSIC);
                        mService.pauseMusic();
                        mIvPlaybutton.setImageResource(R.drawable.paused_icon);
                    }
                }
                break;
            case R.id.iv_back_playdetail:
                this.finish();
                break;
            case R.id.iv_presong:

                if (sPlayingIndex > 0) {
                    //如果还存在上一首，则播放
                    sPlayingIndex--;
                    mService.playMusic(sPlayingList.get(sPlayingIndex));
                    if (mCDRotation != null) {
                        mCDRotation.cancel();
                    }
                    mCDRotation = null;
                    mMpv.setRotation(0);
                } else {
                    Utils.makeToast("上一首是不存在的");
                }
                break;
            case R.id.iv_nextsong:
                if (sPlayingIndex < sPlayingList.size() - 1) {
                    //如果还存在下一首，则播放
                    sPlayingIndex++;
                    mService.playMusic(sPlayingList.get(sPlayingIndex));
                    if (mCDRotation != null) {
                        mCDRotation.cancel();
                    }
                    mCDRotation = null;
                } else {
                    Utils.makeToast("下一首是不存在的");
                }
                break;
        }
    }

    class PlayStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            switch (type) {
                case "start":
                    updateState();
                    break;
                case "paused":
                    break;
                case "continue":
                    break;
                case "stop":
                    break;
                case "end":
                    if (sPlayingIndex < sPlayingList.size() - 1) {
                        sPlayingIndex++;
                        mService.playMusic(sPlayingList.get(sPlayingIndex));
                    } else {
                        resetState();
                    }
                    break;
            }
        }
    }
}
