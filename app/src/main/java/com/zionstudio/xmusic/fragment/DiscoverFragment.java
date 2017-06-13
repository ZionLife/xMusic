package com.zionstudio.xmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.DiscoverFgRvAdapter;
import com.zionstudio.xmusic.model.BannerJson;
import com.zionstudio.xmusic.model.PersonalizedJson;
import com.zionstudio.xmusic.model.SelectedPlaylistsJson;
import com.zionstudio.xmusic.util.UrlUtils;
import com.zionstudio.xmusic.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class DiscoverFragment extends BaseFragment {
    private final String TAG = "DiscoverFragment";
    @BindView(R.id.rv_content_discoverfg)
    RecyclerView mRvContentDiscoverfg;
    Unbinder unbinder;
    private DiscoverFgRvAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initView();
        return view;
    }

    private void initView() {
        mAdapter = new DiscoverFgRvAdapter(getContext());
        mRvContentDiscoverfg.setAdapter(mAdapter);
//        mRvContentDiscoverfg.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRvContentDiscoverfg.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void initData() {
        getBanner();
        getPersonalizedList();
        getSelectedPlaylist();
    }

    /**
     * 获取网友精选歌单
     */
    private void getSelectedPlaylist() {
        final Request request = CommonRequest.createGetRequest(UrlUtils.SELECTED_PLAYLIST, null);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                SelectedPlaylistsJson result = (SelectedPlaylistsJson) responseObj;
                if (result.code == 200) {
                    mAdapter.setSelectedPlaylists(result.playlists);
                }
            }

            @Override
            public void onFailure(Object responseObj) {

            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, SelectedPlaylistsJson.class));
    }

    /**
     * 获取Banners数据
     */
    private void getBanner() {
        Request request = CommonRequest.createGetRequest(UrlUtils.BANNER, null);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BannerJson bannerJson = (BannerJson) responseObj;
                if (bannerJson.code == 200) {
                    Log.e(TAG, "获取Banner成功！");
                    mAdapter.setBannersData(bannerJson.banners);
                }
            }

            @Override
            public void onFailure(Object responseObj) {

            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, BannerJson.class));
    }

    /**
     * 获取个人推荐歌单
     */
    private void getPersonalizedList() {
        Request request = CommonRequest.createGetRequest(UrlUtils.RECOMMEND_RESOURCE, null);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                PersonalizedJson personalizedJson = (PersonalizedJson) responseObj;
                if (personalizedJson.code == 200) {
                    mAdapter.setRecommendPlaylists(personalizedJson.result);
                } else {
                    Utils.makeToast("请求失败");
                }
            }

            @Override
            public void onFailure(Object responseObj) {
                Utils.makeToast("请求失败");
            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, PersonalizedJson.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
