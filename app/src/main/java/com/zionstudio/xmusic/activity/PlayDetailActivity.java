package com.zionstudio.xmusic.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.service.PlayMusicService;
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

    private ServiceConnection mConn;
    private PlayMusicService mService;

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
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        bindService(new Intent(this, PlayMusicService.class), mConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void initView() {
        super.initView();
        setUpPlayer();
    }

    /**
     * 构建唱片机，调整布局参数
     */
    private void setUpPlayer() {
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
    }

    public void startPlayer() {
        ObjectAnimator animStylus = ObjectAnimator.ofFloat(mIvStylus, "rotation", -30, 0);
        animStylus.setDuration(500);
        animStylus.setInterpolator(null);
        animStylus.start();
        animStylus.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(mMpv, "rotation", 0, 359);
                anim.setDuration(25000);
                anim.setInterpolator(null);
                anim.setRepeatCount(-1);
                anim.start();
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

    @OnClick({R.id.iv_playbutton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_playbutton:
                if (!mService.isPlaying()) {
                    startPlayer();
                    mIvPlaybutton.setImageResource(R.drawable.playing_icon);
                } else {
                    mIvPlaybutton.setImageResource(R.drawable.paused_icon);
                }
                break;
        }
    }
}
