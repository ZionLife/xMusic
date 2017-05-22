package com.zionstudio.xmusic.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.LocalSongsAdapter;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.playlist.Song;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class LocalSongsActivity extends BasePlaybarActivity {
    private static final String TAG = "LocalSongsActivity";
    private LocalSongsAdapter mAdapter = null;
    public List<Song> mLocalSongs = new ArrayList<Song>();
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
        //如果允许，则加载本地音乐
        if (readExternalPermission) {
            updateLocalMusic();
        }
    }

    private void updateLocalMusic() {
//        mLocalSongs.clear();
//        mLocalSongs.addAll(Utils.getAllMediaList(this));
        sApplication.mLocalSongs.clear();
        sApplication.mLocalSongs.addAll(Utils.getAllMediaList(this));
    }

    /**
     * 请求外部存储读权限
     */
    private void requestReadExternalPermission() {
        boolean result = true;
        String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = checkSelfPermission(permission[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permission, 321);
            } else {
//                updateLocalMusic();
                readExternalPermission = true;
            }
        } else {
//            updateLocalMusic();
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
//                updateLocalMusic();
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
        //给RecyclerView设置Adapter
        mAdapter = new LocalSongsAdapter(this, sApplication.mLocalSongs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Song s = sApplication.mLocalSongs.get(position);
                String path = s.url;
                if (sService != null) {
                    if (!sService.getPlayingPath().equals(path)) {
                        //开始播放音乐
                        sService.playMusic(s);
                    } else if (sService.isPaused()) {
                        sService.continueMusic();
                    }
                }
                //如果当前播放列表和本地音乐不同，则将本地音乐添加到当前播放列表中
                if (sApplication.mPlayingList != null
                        && (!sApplication.mPlayingList.containsAll(sApplication.mLocalSongs) || !sApplication.mLocalSongs.containsAll(sApplication.mPlayingList))) {
                    sApplication.mPlayingList.clear();
                    sApplication.mPlayingList.addAll(sApplication.mLocalSongs);
                }
                //记录下列表索引
                sApplication.mPlayingIndex = position;
                //添加进最近播放列表
                sApplication.mRecentlyPlayedList.add(s);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvLocalsongs.setAdapter(mAdapter);
        mRvLocalsongs.setLayoutManager(new LinearLayoutManager(this));
        mRvLocalsongs.addItemDecoration(new DividerDecoration(this, 0, Utils.LOCALSONGS_ACTIVITY_DIVIDER_TYPE));
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_localsongs;
    }

    @OnClick({R.id.iv_back_localsongs})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back_localsongs:
                LocalSongsActivity.this.finish();
                break;
        }
    }
}
