package com.zionstudio.xmusic.activity;

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
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.model.SongsDetail;
import com.zionstudio.xmusic.model.playlist.Playlist;
import com.zionstudio.xmusic.model.playlist.PlaylistDetail;
import com.zionstudio.xmusic.model.playlist.Privilege;
import com.zionstudio.xmusic.model.playlist.Track;
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
    private List<Track> mTracks = new ArrayList<Track>();
    private PlaylistDetailAdapter mAdapter;
    private List<Privilege> mPrivileges = new ArrayList<Privilege>();
    private List<Song> mSongs = new ArrayList<Song>();
    private SongsDetail mSongDetail;
    private int scrolledYSum = 0;
    private int headerHeight = 0;
    private int toolbarHeight = 0;
    private Drawable mToolbarBg;
    private Drawable mHeaderBg;
    private View mHeader;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_playlistdetail;
    }

    @Override
    protected void initView() {
        super.initView();
        //给RecyclerView设置属性
        mAdapter = new PlaylistDetailAdapter(this, mTracks, mPrivileges, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tv = (TextView) view.findViewById(R.id.tv_song_name);
                Utils.makeToast("点击了歌曲:" + tv.getText().toString());
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
                Log.e(TAG, "Y轴滑动:" + scrolledYSum);
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
                    //与mHeaderBg相同，但是为了避免更改mToolbarBg的透明度时影响到mHeaderBg,因此另外创建个。
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

    @Override
    protected void initData() {
        super.initData();
        getPlaylistDetail();
    }

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
                    mTracks.clear();
                    mTracks.addAll(mPlaylist.tracks);
                    mPrivileges.clear();
                    mPrivileges.addAll(mPlaylistDetail.privileges);
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG, "设置数据成功");

//                    //获取成功后，获取每个歌曲的详细信息（不包括歌曲url）
//                    for (Track track : mTracks) {
//                        getSongsDetail(track.id);
//                    }
                }
            }

            @Override
            public void onFailure(Object responseObj) {
                Utils.makeToast("请求歌单详情失败");
            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, PlaylistDetail.class));
    }

    private void getSongsDetail(int id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ids", String.valueOf(id));
        RequestParams params = new RequestParams(map);
        final Request request = CommonRequest.createGetRequest(UrlUtils.SONGS_DETAIL, params);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj, String cookie) {
                SongsDetail songsDetail = (SongsDetail) responseObj;
                if (songsDetail.code == 200) {
                    mSongs.add(songsDetail.songs.get(0));
                    Log.e(TAG, "请求歌曲详情成功");
                }
            }

            @Override
            public void onFailure(Object responseObj) {

            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, SongsDetail.class));
    }

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
        switch (view.getId()) {
            case R.id.iv_back_playlistdetail:
                PlaylistDetailActivity.this.finish();
                break;
        }
    }
}
