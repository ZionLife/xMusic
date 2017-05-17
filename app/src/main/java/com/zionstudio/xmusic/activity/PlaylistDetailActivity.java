package com.zionstudio.xmusic.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.videoapp.okhttp.request.RequestParams;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.PlaylistDetailAdapter;
import com.zionstudio.xmusic.model.playlist.Playlist;
import com.zionstudio.xmusic.model.playlist.PlaylistDetail;
import com.zionstudio.xmusic.model.playlist.Track;
import com.zionstudio.xmusic.util.UrlUtils;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.DividerDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/5/16 0016.
 */

public class PlaylistDetailActivity extends BasePlaybarActivity {
    private static final String TAG = "PlaylistDetailActivity";
    @BindView(R.id.rv_playlistdetail)
    RecyclerView mRvPlaylistdetail;

    private PlaylistDetail mPlaylistDetail;
    private Playlist mPlaylist;
    private List<Track> mTracks = new ArrayList<Track>();
    private PlaylistDetailAdapter mAdapter;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_playlistdetail2;
    }

    @Override
    protected void initView() {
        super.initView();
        mAdapter = new PlaylistDetailAdapter(this, mTracks);
        mRvPlaylistdetail.setAdapter(mAdapter);
        mRvPlaylistdetail.setLayoutManager(new LinearLayoutManager(this));
        mRvPlaylistdetail.addItemDecoration(new DividerDecoration(this, 0, Utils.LOCALSONGS_ACTIVITY_DIVIDER_TYPE));
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
            public void onSuccess(Object responseObj, String cookie) {
                Log.e(TAG, "请求歌单详情成功");
                mPlaylistDetail = (PlaylistDetail) responseObj;
                //如果请求成功
                if (mPlaylistDetail.code == 200) {
                    mPlaylist = mPlaylistDetail.playlist;
                    mTracks.clear();
                    mTracks.addAll(mPlaylist.tracks);
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG, "设置数据成功");
                }
            }

            @Override
            public void onFailure(Object responseObj) {
                Log.e(TAG, "请求歌单详情失败");
            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, PlaylistDetail.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
