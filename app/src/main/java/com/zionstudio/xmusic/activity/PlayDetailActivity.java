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
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.playlist.Song;
import com.zionstudio.xmusic.service.PlayMusicService;
import com.zionstudio.xmusic.util.BitmapUtils;
import com.zionstudio.xmusic.util.Constants;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.BackgroundAnimationLinearLayout;
import com.zionstudio.xmusic.view.MyPlayerView;

import net.qiujuer.genius.blur.StackBlur;

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
    private static final int STYLUS_ANIM_DURATION_READY_PLAY = 300;
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
    private Bitmap mCover;
    private byte[] mCoverBytes;

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
        mTvTitlePlaydetail.setText(s.name);
        mTvArtistPlaydetail.setText(s.artist);
        if (mCover != null) {
            mCover.recycle();
        }
        Song playingSong = mService.getPlayingSong();
        mCoverBytes = mService.getCoverBytes();
        if (mCoverBytes != null) {
            mCover = BitmapUtils.decodeSampleBitmapFromBytes(mCoverBytes, (int) ((2 / 3f) * mMpv.getCDSize()), (int) ((2 / 3f) * mMpv.getCDSize()));
        } else {
            mCover = null;
        }
        //设置经过高斯模糊后的背景
        setBackgroundDrawable();
        //设置专辑封面
        mMpv.setCover(mCover);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Drawable d = getBackgroundDrawable();
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
     * 获取播放详情页的经过模糊处理的背景图片，具体解析看注释
     *
     * @return
     */
    private Drawable getBackgroundDrawable() {
        //专辑一般是正方形，因此按屏幕宽高比来裁剪图片，防止图片被拉伸。
        final float aspectRatio = (float) (Utils.getScreenWidth() * 1.0 / Utils.getScreenHeight());
        Bitmap bitmap;
        if (mCoverBytes != null) {
            bitmap = BitmapUtils.decodeSampleBitmapFromBytes(mCoverBytes, Utils.getScreenWidth(), Utils.getScreenHeight());
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cover);
        }
        //图片高度无需变动，裁剪图片宽，使宽高比与屏幕宽高比一致即可
        int cropBitmapWidth = (int) (aspectRatio * bitmap.getHeight());
        //计算裁剪时，宽开始的坐标
        int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);
        //获得裁剪后的Bitmap
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth, bitmap.getHeight());

        //先把裁剪后的图片进行压缩，压缩后的图片宽高比失调。但是把图片设置到View上之后，又会进行拉伸。
        // 因此实际显示的图片宽高比会和以前一样。但是通过createScaledBitmap把图片压缩了。这一点容易疑惑为什么按比例裁剪后又让比例失调。
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap.getHeight() / 50, false);

        //调用高斯模糊算法进行模糊
        final Bitmap blurBitmap = StackBlur.blur(scaledBitmap, 7, false);

        final Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        cropBitmap.recycle();
        scaledBitmap.recycle();
        System.gc();
        return foregroundDrawable;
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
                animStylus.setDuration(STYLUS_ANIM_DURATION_READY_PLAY);
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
                        mService.continueMusic();
                    } else if (mService.isPlaying()) {
                        mService.pauseMusic();
                    }
                }
                break;
            case R.id.iv_back_playdetail:
                this.finish();
                break;
            case R.id.iv_presong:
                mService.playPrevSong();
                break;
            case R.id.iv_nextsong:
                mService.playNextSong();
                break;
        }
    }

    class PlayStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            switch (type) {
                case "start":
                    if (mCDRotation != null && mCDRotation.isRunning()) {
                        mCDRotation.pause();
                    }
                    updateState();
                    break;
                case "paused":
                    needUpdateSeekBar = false;
                    mIvPlaybutton.setClickable(false);
                    hangPlayerAnim(PlayMusicService.PAUSE_MUSIC);
                    mIvPlaybutton.setImageResource(R.drawable.paused_icon);
                    break;
                case "continue":
                    needUpdateSeekBar = true;
                    mHandler.post(mRunnable);
                    mIvPlaybutton.setImageResource(R.drawable.playing_icon);
                    mIvPlaybutton.setClickable(false);
                    startPlayerAnim(STYLUS_ANIM_DURATION_READY_PLAY);
                    break;
                case "stop":
                    updateState();
                    break;
                case "end":
                    //没有歌曲播放了
                    resetState();
                    break;
                case "updatePlaybar":
                    updateState();
                    break;
            }
        }
    }
}
