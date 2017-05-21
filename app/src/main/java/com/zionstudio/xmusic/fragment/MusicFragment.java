package com.zionstudio.xmusic.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.videoapp.okhttp.request.RequestParams;
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.activity.LocalSongsActivity;
import com.zionstudio.xmusic.activity.PlaylistDetailActivity;
import com.zionstudio.xmusic.activity.RecentlyPlayedActivity;
import com.zionstudio.xmusic.adapter.MusicFgItemAdapter;
import com.zionstudio.xmusic.adapter.MusicFgPlaylistAdapter;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.playlist.MyPlaylist;
import com.zionstudio.xmusic.model.playlist.Playlist;
import com.zionstudio.xmusic.util.UrlUtils;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.DividerDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class MusicFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.rv_item_musicfg)
    RecyclerView mRvItemMusicfg;
    @BindView(R.id.rv_mylist_musicfg)
    RecyclerView mRvMylistMusicfg;
    @BindView(R.id.iv_arr)
    ImageView mIvArr;
    @BindView(R.id.tv_playlist_title)
    TextView mTvPlaylistTitle;
    @BindView(R.id.iv_playlist_setting)
    ImageView mIvPlaylistSetting;
    @BindView(R.id.srl_musicfg)
    SwipeRefreshLayout mSrlMusicfg;

    private boolean arrIsDown;
    @BindView(R.id.rl_playlist)
    RelativeLayout mRlPlaylist;
    private MusicFgItemAdapter mItemAdapter;
    private MusicFgPlaylistAdapter mPlaylistAdapter;
    private static final String[] mItemTitle = new String[]{"本地音乐", "最近播放", "下载管理", "我的收藏"};
    private static final int[] mItemIcon = new int[]{R.drawable.music_icn_local, R.drawable.music_icn_recent, R.drawable.music_icn_dld, R.drawable.music_icn_artist};
    private static final String TAG = "MusicFragment";
    private static ArrayList<Playlist> mPlaylist = new ArrayList<Playlist>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateMusicFragmentView");
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initData() {
        getMyPlaylist();
    }

    /**
     * 获取我的歌单
     */
    private void getMyPlaylist() {
        //获取我的歌单
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", MyApplication.getMyApplication().mUserInfo.account.id);
        RequestParams params = new RequestParams(map);
        Request request = CommonRequest.createGetRequest(UrlUtils.PLAYLIST, params);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj, String cookie) {
                MyPlaylist list = (MyPlaylist) responseObj;
                if (list.code.equals("200")) {
                    //更新歌单
                    mPlaylist.clear();
                    mPlaylist.addAll(list.playlist);
                    mPlaylistAdapter.notifyDataSetChanged();
                    mSrlMusicfg.setRefreshing(false);
                } else {
                    mSrlMusicfg.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Object responseObj) {
                mSrlMusicfg.setRefreshing(false);
            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, MyPlaylist.class));
    }

    private void initView() {
        //给RvItemMusicfg设置参数
        mItemAdapter = new MusicFgItemAdapter(this.getContext(), mItemTitle, mItemIcon, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MusicFragment.this.getContext(), LocalSongsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MusicFragment.this.getContext(), RecentlyPlayedActivity.class));
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mRvItemMusicfg.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRvItemMusicfg.addItemDecoration(new DividerDecoration(this.getContext(), 0, Utils.MUSIC_FG_DIVIDER_TYPE));
        mRvItemMusicfg.setAdapter(mItemAdapter);

        //给RvMylistMusicfg设置参数
        mPlaylistAdapter = new MusicFgPlaylistAdapter(MusicFragment.this.getContext(), mPlaylist, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.makeToast("点击了歌单的：" + position);
                Intent intent = new Intent(MusicFragment.this.getContext(), PlaylistDetailActivity.class);
                intent.putExtra("id", mPlaylist.get(position).id);
                ImageView iv = (ImageView) view.findViewById(R.id.iv_cover);
                iv.setDrawingCacheEnabled(true);
                Bitmap cover = iv.getDrawingCache();
                intent.putExtra("cover", cover);
                startActivity(intent);
                iv.setDrawingCacheEnabled(false);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Utils.makeToast("长按了歌单的：" + position);
            }
        });

        mRvMylistMusicfg.setAdapter(mPlaylistAdapter);
        mRvMylistMusicfg.setLayoutManager(new LinearLayoutManager(MusicFragment.this.getContext()));
        mRvMylistMusicfg.addItemDecoration(new DividerDecoration(this.getContext(), 0, Utils.MUSIC_FG_PLAYLIST_DIVIDER_TYPE));

        //给我的歌单的菜单设置参数，初始时默认显示歌单的RecyclerView
        mIvArr.setRotation(90);
        arrIsDown = true;
        mRvMylistMusicfg.setVisibility(View.VISIBLE);

        mRlPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueAnimator arrAnim;
                if (arrIsDown) {
                    Log.e(TAG, "ROTATION before:" + mIvArr.getRotation() + "arrIsDown:" + arrIsDown);
                    arrAnim = ObjectAnimator.ofFloat(mIvArr, "rotation", mIvArr.getRotation(), 0);
                    //隐藏我的歌单
                    mRvMylistMusicfg.setVisibility(View.GONE);
                    arrIsDown = false;
                } else {
                    Log.e(TAG, "ROTATION before:" + mIvArr.getRotation() + "arrIsDown:" + arrIsDown);
                    arrAnim = ObjectAnimator.ofFloat(mIvArr, "rotation", mIvArr.getRotation(), 90);
                    //显示我的歌单
                    mRvMylistMusicfg.setVisibility(View.VISIBLE);
                    arrIsDown = true;
                }
                arrAnim.setDuration(200);
                arrAnim.start();
            }
        });

        mIvPlaylistSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.makeToast("点击了设置");
            }
        });

        //设置下拉刷新监听器
        mSrlMusicfg.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyPlaylist();
            }
        });
        mSrlMusicfg.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
