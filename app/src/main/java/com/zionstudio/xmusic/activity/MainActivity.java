package com.zionstudio.xmusic.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.MyFragmentPagerAdapter;
import com.zionstudio.xmusic.fragment.DiscoverFragment;
import com.zionstudio.xmusic.fragment.MusicFragment;
import com.zionstudio.xmusic.view.CircleTransform;
import com.zionstudio.xmusic.view.ShadowTransform;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private static FragmentManager mFM = null;
    private static final int FRAGMENT_MUSIC = 0;
    private static final int FRAGMENT_DISCOVER = 1;
    @BindView(R.id.rl_drawer_bg)
    RelativeLayout mRlDrawerBg;
    @BindView(R.id.sdv_avatar)
    ImageView mSdvAvatar;
    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.tv_nicknam)
    TextView mTvNicknam;
    @BindView(R.id.iv_toolbar_music)
    ImageView mIvToolbarMusic;
    @BindView(R.id.iv_toolbar_discover)
    ImageView mIvToolbarDiscover;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;
    @BindView(R.id.iv_drawer)
    ImageView mIvDrawer;
    @BindView(R.id.view_divider)
    View mViewDivider;
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    @BindView(R.id.tb)
    Toolbar mTb;
    @BindView(R.id.dl)
    DrawerLayout mDl;
    @BindView(R.id.ll_drawer)
    LinearLayout mLlDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.e(TAG, "TEST");
        initData();
        setUpDrawer();
        initView();
        initListener();
    }

    private void initListener() {
        //设置Drawer图标的点击事件
        mIvDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDl.openDrawer(mLlDrawer);
            }
        });

        mIvToolbarMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusicFragment();
            }
        });

        mIvToolbarDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiscoverFragment();
            }
        });

        mVpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "onPageSelected");
                switch (position) {
                    case FRAGMENT_MUSIC:
                        showMusicFragment();
                        break;
                    case FRAGMENT_DISCOVER:
                        showDiscoverFragment();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化Drawer
     */
    private void setUpDrawer() {
        //设置头像
        Picasso.with(this).load(MyApplication.sUserInfo.profile.avatarUrl)
                .transform(new CircleTransform())
                .into(mSdvAvatar);
        //设置背景，调整图片亮度
        Picasso.with(this).load(MyApplication.sUserInfo.profile.backgroundUrl)
                .transform(new ShadowTransform(-80))
                .into(mIvBg);

        //设置昵称
        mTvNicknam.setText(MyApplication.sUserInfo.profile.nickname);
    }

    private void initData() {
        //初始化Fragment和ViewPager
        MusicFragment musicFragment = new MusicFragment();
        DiscoverFragment discoverFragment = new DiscoverFragment();
        mFragmentList.add(musicFragment);
        mFragmentList.add(discoverFragment);
        mFM = getSupportFragmentManager();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(mFM, mFragmentList);
        mVpContent.setAdapter(adapter);
        showMusicFragment();
    }

    private void initView() {
        //实现状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void showMusicFragment() {
        mIvToolbarMusic.setSelected(true);
        mIvToolbarDiscover.setSelected(false);
        mVpContent.setCurrentItem(FRAGMENT_MUSIC);
    }

    private void showDiscoverFragment() {
        mIvToolbarDiscover.setSelected(true);
        mIvToolbarMusic.setSelected(false);
        mVpContent.setCurrentItem(FRAGMENT_DISCOVER);
    }
}
