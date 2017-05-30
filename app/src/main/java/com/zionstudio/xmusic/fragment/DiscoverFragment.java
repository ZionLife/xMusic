package com.zionstudio.xmusic.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.BannersAdapter;
import com.zionstudio.xmusic.model.Banner;
import com.zionstudio.xmusic.model.BannerJson;
import com.zionstudio.xmusic.util.UrlUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class DiscoverFragment extends Fragment {
    private static final String TAG = "DiscoverFragment";
    @BindView(R.id.vp_discover_fg)
    ViewPager mVpDiscoverFg;
    Unbinder unbinder;
    @BindView(R.id.indicator_discover_fg)
    MagicIndicator mIndicatorDiscoverFg;
    private List<Banner> mBanners = new ArrayList<>();
    private BannersAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateDiscoverFragmentView");
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initView();
        return view;
    }

    private void initView() {
        mAdapter = new BannersAdapter(getContext(), mBanners);
        mVpDiscoverFg.setAdapter(mAdapter);
//        mCircleNavigator = new CircleNavigator(getContext());
//        mCircleNavigator.setCircleColor(getResources().getColor(R.color.colorPrimary));
//        mCircleNavigator.setFollowTouch(false);
//        mIndicatorDiscoverFg.setNavigator(mCircleNavigator);
//        ViewPagerHelper.bind(mIndicatorDiscoverFg, mVpDiscoverFg);
    }

    private void initData() {
        getBanner();
    }

    private void getBanner() {
        Request request = CommonRequest.createGetRequest(UrlUtils.BANNER, null);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj, String cookie) {
                BannerJson bannerJson = (BannerJson) responseObj;
                if (bannerJson.code == 200) {
                    Log.e(TAG, "请求Banner成功");
                    mBanners.clear();
                    mBanners.addAll(bannerJson.banners);
                    mAdapter.notifyDataSetChanged();
                    //通知Indicator更新数据
                    CircleNavigator circleNavigator = new CircleNavigator(getContext());
                    circleNavigator.setCircleColor(getResources().getColor(R.color.colorPrimary));
                    circleNavigator.setCircleCount(mBanners.size());
                    circleNavigator.notifyDataSetChanged();
                    circleNavigator.setFollowTouch(false);
                    mIndicatorDiscoverFg.setNavigator(circleNavigator);
                    ViewPagerHelper.bind(mIndicatorDiscoverFg, mVpDiscoverFg);
//                    mBanners = bannerJson.banners;
                }
            }

            @Override
            public void onFailure(Object responseObj) {

            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, BannerJson.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
