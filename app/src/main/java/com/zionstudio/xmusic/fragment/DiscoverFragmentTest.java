package com.zionstudio.xmusic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.DiscoverFgRvAdapter;
import com.zionstudio.xmusic.model.Banner;
import com.zionstudio.xmusic.model.BannerJson;
import com.zionstudio.xmusic.model.PersonalizedJson;
import com.zionstudio.xmusic.util.UrlUtils;
import com.zionstudio.xmusic.util.Utils;

import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class DiscoverFragmentTest extends BaseFragment {
    private final String TAG = "DiscoverFragment";
    @BindView(R.id.rv_content_discoverfg)
    RecyclerView mRvContentDiscoverfg;
    Unbinder unbinder;
    private DiscoverFgRvAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_test, container, false);
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
        getList();
    }

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

    private void getList() {
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
