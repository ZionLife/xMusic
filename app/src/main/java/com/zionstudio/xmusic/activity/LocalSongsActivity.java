package com.zionstudio.xmusic.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.LocalSongsAdapter;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.service.PlayMusicService;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class LocalSongsActivity extends BasePlayMusicActivity {
    private static final String TAG = "LocalSongsActivity";
    private static List<Song> sLocalSongs = new ArrayList<Song>();
    private static LocalSongsAdapter sAdapter = null;
    private static Bitmap sCover;
    MediaMetadataRetriever sRetriver = null;
    private boolean readExternalPermission = false;
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
    @BindView(R.id.iv_playlistbutton)
    ImageView mIvPlaylistbutton;

    @Override
    protected void initData() {
        super.initData();

        //查找本地音乐,针对6.0以上系统，先申请权限
        requestReadExternalPermission();
        //初始化ServiceConnection
        sConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e(TAG, "onServiceConnected");
                sService = ((PlayMusicService.PlayMusicBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisConnected");
                sService = null;
            }
        };

        //绑定服务
        bindService(new Intent(this, PlayMusicService.class), sConnection, BIND_AUTO_CREATE);
    }

    private void updateLocalMusic() {
        sLocalSongs.clear();
        sLocalSongs.addAll(Utils.getAllMediaList(this));

    }

    private void requestReadExternalPermission() {
        boolean result = true;
        String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = checkSelfPermission(permission[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permission, 321);
            } else {
                updateLocalMusic();
                readExternalPermission = true;
            }
        } else {
            updateLocalMusic();
            readExternalPermission = true;
        }
    }

    /**
     * 权限申请回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocalMusic();
                readExternalPermission = true;
            } else {
                Utils.makeToast("申请权限被拒绝，请前往设置中手动开启。");
                readExternalPermission = false;
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();
        //实现状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //给RecyclerView设置Adapter
        sAdapter = new LocalSongsAdapter(this, sLocalSongs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Song s = sLocalSongs.get(position);
                String path = s.path;
                if (sService != null) {
                    if (!sService.getPlayingPath().equals(path)) {
                        //开始播放音乐
                        sService.playMusic(s);
//                        //更新状态栏
//                        updatePlayingBar();
                    } else if (sService.isPaused()) {
                        sService.continueMusic();
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvLocalsongs.setAdapter(sAdapter);
        mRvLocalsongs.setLayoutManager(new LinearLayoutManager(this));
        mRvLocalsongs.addItemDecoration(new DividerDecoration(this, 0, Utils.LOCALSONGS_ACTIVITY_DIVIDER_TYPE));
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_localsongs;
    }

    @OnClick({R.id.iv_back_localsongs})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_localsongs:
                LocalSongsActivity.this.finish();
                break;
        }
    }
}
