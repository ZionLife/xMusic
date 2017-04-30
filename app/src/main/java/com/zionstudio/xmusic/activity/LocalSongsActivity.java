package com.zionstudio.xmusic.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.LocalSongsAdapter;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.service.PlayMusicService;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.DividerDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class LocalSongsActivity extends BaseActivity {
    private static final String TAG = "LocalSongsActivity";
    private static List<Song> mLocalSongs = new ArrayList<Song>();
    private static LocalSongsAdapter mAdapter = null;
    private ServiceConnection mConnection;
    private PlayMusicService mBoundService;
    private static Bitmap mCover;
    MediaMetadataRetriever mRetriver = null;
    @BindView(R.id.iv_back_localsongs)
    ImageView mIvBackLocalsongs;
    @BindView(R.id.rv_localsongs)
    RecyclerView mRvLocalsongs;
    @BindView(R.id.et_search_localsongs)
    EditText mEtSearchLocalsongs;
    @BindView(R.id.iv_cover_playing)
    ImageView mIvCoverPlaying;
    @BindView(R.id.tv_title_playing)
    TextView mTvTitlePlaying;
    @BindView(R.id.tv_artist_playing)
    TextView mTvArtistPlaying;
    @BindView(R.id.iv_playbutton)
    ImageView mIvPlaybutton;
    @BindView(R.id.iv_playlistbutton)
    ImageView mIvPlaylistbutton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localsongs);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        String where = "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0";
        mLocalSongs.clear();
        mLocalSongs.addAll(Utils.getAllMediaList(this, where));

        //初始化ServiceConnection
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e(TAG, "onServiceConnected");
                mBoundService = ((PlayMusicService.PlayMusicBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisConnected");
                mBoundService = null;
            }
        };

        //绑定服务
        bindService(new Intent(this, PlayMusicService.class), mConnection, BIND_AUTO_CREATE);
    }

    private void initView() {
        //实现状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

//        //设置返回按钮监听事件
//        mIvBackLocalsongs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        //给RecyclerView设置Adapter
        mAdapter = new LocalSongsAdapter(this, mLocalSongs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.makeToast("开始播放:" + mLocalSongs.get(position).title);
                Song s = mLocalSongs.get(position);
                String path = s.path;
                if (!mBoundService.getPlayingPath().equals(path)) {
                    //获取专辑封面并设置到状态栏
                    Bitmap cover = getSongImage(path);
                    if (cover != null) {
                        mIvCoverPlaying.setImageBitmap(cover);
                    } else {
                        mIvCoverPlaying.setImageResource(R.mipmap.ic_launcher);
                    }
                    //给状态栏设置歌曲名和歌手
                    mTvTitlePlaying.setText(s.title);
                    mTvArtistPlaying.setText(s.artist);
                    //开始播放音乐
                    mBoundService.playMusic(path);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvLocalsongs.setAdapter(mAdapter);
        mRvLocalsongs.setLayoutManager(new LinearLayoutManager(this));
        mRvLocalsongs.addItemDecoration(new DividerDecoration(this, 0, Utils.LOCALSONGS_ACTIVITY_DIVIDER_TYPE));
    }

    /**
     * 获取歌曲的专辑图片
     *
     * @param path 歌曲路径
     */
    private Bitmap getSongImage(String path) {
        File f = new File(path);
        mRetriver = new MediaMetadataRetriever();
        mRetriver.setDataSource(path);
        byte[] cover = mRetriver.getEmbeddedPicture();
        if (cover != null) {
            mCover = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            mRetriver.release();
            return mCover;
        }
        return null;
    }

    private void playSong(String path) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除服务的绑定
        unbindService(mConnection);
    }

    @OnClick({R.id.iv_back_localsongs, R.id.iv_playbutton})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_localsongs:
                LocalSongsActivity.this.finish();
                break;
            case R.id.iv_playbutton:
                Log.e(TAG, "111");
                if (mBoundService.isPlaying()) {
                    mBoundService.pauseMusic();
                } else {
                    mBoundService.startMusic();
                }
                break;
        }
    }
}
