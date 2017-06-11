package com.zionstudio.xmusic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.BannersAdapter;
import com.zionstudio.xmusic.adapter.RecommendPlaylistAdapter;
import com.zionstudio.xmusic.model.Banner;
import com.zionstudio.xmusic.model.BannerJson;
import com.zionstudio.xmusic.model.PersonalizedJson;
import com.zionstudio.xmusic.model.playlist.Playlist;
import com.zionstudio.xmusic.util.UrlUtils;
import com.zionstudio.xmusic.util.Utils;

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
    private final List<Banner> mBanners = new ArrayList<>();
    private final List<Playlist> mPersonalizedPlaylist = new ArrayList<>();
    @BindView(R.id.imageView2)
    ImageView mImageView2;
    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.rv_list_discoveryfg)
    RecyclerView mRvListDiscoveryfg;
    private BannersAdapter mBannerAdapter;
    private RecommendPlaylistAdapter mPersonalizedAdapter;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_scrollview, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initView();
        return view;
    }

    private void initView() {
        //给Banner设置Adapter
        mBannerAdapter = new BannersAdapter(getContext(), mBanners);
        mVpDiscoverFg.setAdapter(mBannerAdapter);

        //给推荐歌单设置Adapter
        mPersonalizedAdapter = new RecommendPlaylistAdapter(getContext(), mPersonalizedPlaylist);
        mRvListDiscoveryfg.setAdapter(mPersonalizedAdapter);
        mRvListDiscoveryfg.setLayoutManager(new GridLayoutManager(getContext(), 3));

    }

    private void initData() {
        mRunnable = new Runnable() {
            @Override
            public void run() {

                if (mBanners.size() != 0) {
//                    int currentPos = mVpDiscoverFg.getCurrentItem();
//                    if (currentPos == mBanners.size() - 1) {
//                        mVpDiscoverFg.setCurrentItem(0);
//                    } else {
//                        mVpDiscoverFg.setCurrentItem(currentPos + 1);
//                    }
                    mVpDiscoverFg.setCurrentItem((mVpDiscoverFg.getCurrentItem() + 1) % mBanners.size());
                }
                mHandler.postDelayed(mRunnable, 1000);
            }
        };
        getBanner();
        getList();
    }

    private void getList() {
        Request request = CommonRequest.createGetRequest(UrlUtils.RECOMMEND_RESOURCE, null);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                PersonalizedJson personalizedJson = (PersonalizedJson) responseObj;
                if (personalizedJson.code == 200) {
                    mPersonalizedPlaylist.clear();
                    mPersonalizedPlaylist.addAll(personalizedJson.result);
                    mPersonalizedAdapter.notifyDataSetChanged();
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

    private void getBanner() {
        Request request = CommonRequest.createGetRequest(UrlUtils.BANNER, null);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BannerJson bannerJson = (BannerJson) responseObj;
                if (bannerJson.code == 200) {
                    Log.e(TAG, "请求Banner成功");
                    mBanners.clear();
                    mBanners.addAll(bannerJson.banners);
                    mBannerAdapter.notifyDataSetChanged();
                    //通知Indicator更新数据
                    CircleNavigator circleNavigator = new CircleNavigator(getContext());
                    circleNavigator.setCircleColor(getResources().getColor(R.color.colorPrimary));
                    circleNavigator.setCircleCount(mBanners.size());
                    circleNavigator.notifyDataSetChanged();
                    circleNavigator.setFollowTouch(false);
                    mIndicatorDiscoverFg.setNavigator(circleNavigator);
                    ViewPagerHelper.bind(mIndicatorDiscoverFg, mVpDiscoverFg);
                    mHandler.postDelayed(mRunnable, 1000);
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
