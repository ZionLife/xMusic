package com.zionstudio.xmusic.activity;

import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.videoapp.okhttp.request.RequestParams;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.PlaylistDetailAdapter;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.playlist.Album;
import com.zionstudio.xmusic.model.playlist.Artist;
import com.zionstudio.xmusic.model.playlist.Data;
import com.zionstudio.xmusic.model.playlist.Song;
import com.zionstudio.xmusic.model.playlist.SongsUrl;
import com.zionstudio.xmusic.model.playlist.SongsDetail;
import com.zionstudio.xmusic.model.playlist.Playlist;
import com.zionstudio.xmusic.model.playlist.PlaylistDetail;
import com.zionstudio.xmusic.model.playlist.Privilege;
import com.zionstudio.xmusic.model.playlist.Track;
import com.zionstudio.xmusic.service.PlayMusicService;
import com.zionstudio.xmusic.util.Constants;
import com.zionstudio.xmusic.util.UrlUtils;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.DividerDecoration;

import net.qiujuer.genius.blur.StackBlur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/5/16 0016.
 */

public class PlaylistDetailActivity extends BasePlaybarActivity {
    private static final String TAG = "PlaylistDetailActivity";
    @BindView(R.id.rv_playlistdetail)
    RecyclerView mRvPlaylistdetail;
    @BindView(R.id.iv_back_playlistdetail)
    ImageView mIvBackPlaylistdetail;
    @BindView(R.id.tv_playlist)
    TextView mTvPlaylist;
    @BindView(R.id.iv_menu_playlistdetail)
    ImageView mIvMenuPlaylistdetail;
    @BindView(R.id.tb_playlistdetail)
    Toolbar mTbPlaylistdetail;

    private PlaylistDetail mPlaylistDetail;
    private Playlist mPlaylist;
    //    private List<Track> mTracks = new ArrayList<Track>();
    private List<Song> mSongs = new ArrayList<Song>();
    private PlaylistDetailAdapter mAdapter;
    private List<Privilege> mPrivileges = new ArrayList<Privilege>();
    private List<Data> mSongsUrl = new ArrayList<Data>();
    private SongsDetail mSongDetail;
    private int scrolledYSum = 0;
    private int headerHeight = 0;
    private int toolbarHeight = 0;
    private Drawable mToolbarBg;
    private Drawable mHeaderBg;
    private View mHeader;
    private ServiceConnection mConnection;
    private PlayMusicService mService;
    private Song mPlayingSong;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_playlistdetail;
    }

    @Override
    protected void initData() {
        super.initData();
        getPlaylistDetail();
    }

    @Override
    protected void initView() {
        super.initView();
        //给RecyclerView设置属性
        mAdapter = new PlaylistDetailAdapter(this, mSongs, mPrivileges, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //由于RecyclerView中的第一项是header，因此数据的index应该是position - 1
                if(sApplication.mPlayingList == null){
                    sApplication.mPlayingList = new ArrayList<>();
                }
                if (!sApplication.mPlayingList.containsAll(mSongs)) {
                    sApplication.mPlayingList.clear();
                    sApplication.mPlayingList.addAll(mSongs);
                }
                Song song = mSongs.get(position - 1);
                sService.playMusic(song);

                //获取歌曲详情
//                getSongDetail(song.id);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvPlaylistdetail.setAdapter(mAdapter);
        mRvPlaylistdetail.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setHeaderView(LayoutInflater.from(this).inflate(R.layout.view_header_playlistdetail, mRvPlaylistdetail, false));
        mRvPlaylistdetail.addItemDecoration(new DividerDecoration(this, 0, Utils.PLAYLISTDETAIL_ACTIVITY_DIVIDER_TYPE));

        //对RecyclerView设置滑动监听。根据滑动距离设置Toolbar背景的透明度
        mRvPlaylistdetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledYSum += dy;
                int delta = headerHeight - toolbarHeight;
//                Log.e(TAG, "Y轴滑动:" + scrolledYSum);
                if (scrolledYSum >= 0 && scrolledYSum < delta) {
                    int alpha = (int) ((scrolledYSum / (float) delta) * 255);
                    mToolbarBg.setAlpha(alpha);
                    mTbPlaylistdetail.setBackground(mToolbarBg);
                } else if (scrolledYSum > 0 && scrolledYSum >= delta && mToolbarBg.getAlpha() < 255) {
                    //有时手指滑动过快，导致scrolledYsum大于delta，但是透明度没有设置成255,因此加个判断
                    mToolbarBg.setAlpha(255);
                    mTbPlaylistdetail.setBackground(mToolbarBg);
                }
            }
        });

        final Bitmap cover = getIntent().getParcelableExtra("cover");
        mHeader = mAdapter.getHeaderView();
        if (mHeader != null) {
            mHeader.post(new Runnable() {
                @Override
                public void run() {
                    mHeaderBg = getBackgroundDrawable(cover, mHeader.getWidth(), mHeader.getHeight());
                    mHeader.setBackground(mHeaderBg);
                    ImageView iv = (ImageView) mHeader.findViewById(R.id.iv_cover_header_playlistdetail);
                    iv.setImageBitmap(cover);
                    //与mHeaderBg相同，但是为了避免更改mToolbarBg的透明度时影响到mHeaderBg,因此另外创建个Drawable对象。
                    mToolbarBg = getBackgroundDrawable(cover, mHeader.getWidth(), mHeader.getHeight());
                    //记录header高度
                    headerHeight = mHeader.getHeight();
                }
            });
        }

        //获取Toolbar高度
        mTbPlaylistdetail.post(new Runnable() {
            @Override
            public void run() {
                toolbarHeight = mTbPlaylistdetail.getHeight();
            }
        });

    }

//    /**
//     * 请求歌单所有歌曲的url
//     */
//    private void getSongsUrl() {
//        HashMap<String, String> map = new HashMap<>();
//        StringBuilder ids = new StringBuilder();
//        for (Song song : mSongs) {
//            ids.append("," + song.id);
//        }
//        //删除第一个逗号
//        ids.delete(0, 1);
//        map.put("id", ids.toString());
//        RequestParams params = new RequestParams(map);
//        Request request = CommonRequest.createGetRequest(UrlUtils.SONG_URL, params);
//        DisposeDataListener listener = new DisposeDataListener() {
//            @Override
//            public void onSuccess(Object responseObj, String cookie) {
//                //do something
//                SongsUrl songsUrl = (SongsUrl) responseObj;
//                if (songsUrl.code == 200) {
//                    mSongsUrl = songsUrl.data;
//                    Utils.makeToast("获取歌曲url成功");
//                }
//            }
//
//            @Override
//            public void onFailure(Object responseObj) {
//                Log.e(TAG, "请求歌曲url失败");
//            }
//        };
//        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, SongsUrl.class));
//    }


    /**
     * 请求歌单详情
     */
    private void getPlaylistDetail() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", getIntent().getStringExtra("id"));
        RequestParams params = new RequestParams(map);
        Request request = CommonRequest.createGetRequest(UrlUtils.PLAYLIST_DETAIL, params);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj, final String cookie) {
                Utils.makeToast("请求歌单详情成功");
                mPlaylistDetail = (PlaylistDetail) responseObj;
                //如果请求成功
                if (mPlaylistDetail.code == 200) {
                    mPlaylist = mPlaylistDetail.playlist;
                    mSongs.clear();
                    mSongs.addAll(mPlaylist.tracks);
//                    mTracks.clear();
//                    mTracks.addAll(mPlaylist.tracks);
                    formatSongs();
                    mPrivileges.clear();
                    mPrivileges.addAll(mPlaylistDetail.privileges);
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG, "设置数据成功");
                    //获取歌单歌曲的url
//                    getSongsUrl();
                }
            }

            @Override
            public void onFailure(Object responseObj) {
                Utils.makeToast("请求歌单详情失败");
            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, PlaylistDetail.class));
    }

    /**
     * 由于获取来的歌曲对象中的歌手和专辑是List，将其格式化成String
     */
    private void formatSongs() {
        for (Song song : mSongs) {
            //格式化歌手
            StringBuilder artisits = new StringBuilder();
            for (Artist ar : song.ar) {
                artisits.append("/" + ar.name);
            }
            artisits.delete(0, 1);
            //格式化专辑
            StringBuilder albums = new StringBuilder();
            for (Album al : song.al) {
                albums.append("/" + al.name);
            }
            albums.delete(0, 1);

            song.artist = artisits.toString();
            song.album = albums.toString();
            song.type = Constants.TYPE_ONLINE;
        }
    }

//    /**
//     * 请求歌曲详情
//     *
//     * @param id 歌曲id
//     */
//    private void getSongDetail(int id) {
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("ids", String.valueOf(id));
//        RequestParams params = new RequestParams(map);
//        final Request request = CommonRequest.createGetRequest(UrlUtils.SONG_DETAIL, params);
//        DisposeDataListener listener = new DisposeDataListener() {
//            @Override
//            public void onSuccess(Object responseObj, String cookie) {
//                SongsDetail songsDetail = (SongsDetail) responseObj;
//                if (songsDetail.code == 200) {
////                    mPlayingSong = songsDetail.songs.get(0);
//                    getSongUrl(mPlayingSong.id);
//                }
//            }
//
//            @Override
//            public void onFailure(Object responseObj) {
//
//            }
//        };
//        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, SongsDetail.class));
//    }

    /**
     //     * 获取单首歌曲的url
     //     *
     //     * @param id
     //     */
//    private void getSongUrl(int id) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("id", String.valueOf(id));
//        RequestParams params = new RequestParams(map);
//        Request request = CommonRequest.createGetRequest(UrlUtils.SONG_URL, params);
//        DisposeDataListener listener = new DisposeDataListener() {
//            @Override
//            public void onSuccess(Object responseObj, String cookie) {
//                //do something
//                SongsUrl songsUrl = (SongsUrl) responseObj;
//                if (songsUrl.code == 200) {
//                    mPlayingSong.url = songsUrl.data.get(0).url;
//                    mPlayingSong.type = Constants.TYPE_ONLINE;
//                    sService.playMusic(mPlayingSong);
//                    Utils.makeToast("获取歌曲url成功");
//                }
//            }
//
//            @Override
//            public void onFailure(Object responseObj) {
//                Log.e(TAG, "请求歌曲url失败");
//            }
//        };
//        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, SongsUrl.class));
//    }

    /**
     * 加载activity顶部的高斯模糊后的背景图片
     *
     * @param bitmap    源bitmap
     * @param reqWidth  期望宽度
     * @param reqHeight 期望高度
     * @return 高斯模糊后的Drawable对象
     */
    private Drawable getBackgroundDrawable(Bitmap bitmap, float reqWidth, float reqHeight) {
        final float aspectRatio = reqHeight / reqWidth;
        //图片高度无需变动，裁剪图片高，使高宽比与期望的一致即可
//        int cropBitmapWidth = (int) (aspectRatio * bitmap.getHeight());
        int cropBitmapHeight = (int) (aspectRatio * bitmap.getWidth());
//        //计算裁剪时，宽开始的坐标
//        int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);
        //计算裁剪时，高开始的坐标
        int cropBitmapHeightY = (bitmap.getHeight() - cropBitmapHeight) / 2;
        //获得裁剪后的Bitmap
//        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth, bitmap.getHeight());
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, 0, cropBitmapHeightY, bitmap.getWidth(), cropBitmapHeight);

        //先把裁剪后的图片进行压缩，压缩后的图片宽高比失调。但是把图片设置到View上之后，又会进行拉伸。
        // 因此实际显示的图片宽高比会和以前一样。但是通过createScaledBitmap把图片压缩了。这一点容易疑惑为什么按比例裁剪后又让比例失调。
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 35, bitmap.getHeight() / 35, false);

        //调用高斯模糊算法进行模糊
        final Bitmap blurBitmap = StackBlur.blur(scaledBitmap, 7, false);

        final Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        cropBitmap.recycle();
        scaledBitmap.recycle();
        System.gc();
        return foregroundDrawable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back_playlistdetail})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_back_playlistdetail:
                PlaylistDetailActivity.this.finish();
                break;
        }
    }
}
