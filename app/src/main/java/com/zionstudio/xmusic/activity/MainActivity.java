package com.zionstudio.xmusic.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.MyFragmentPagerAdapter;
import com.zionstudio.xmusic.fragment.DiscoverFragment;
import com.zionstudio.xmusic.fragment.MusicFragment;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.CircleTransform;
import com.zionstudio.xmusic.view.ShadowTransform;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BasePlaybarActivity {
    private static final String TAG = "MainActivity";
    private static ArrayList<Fragment> sFragmentList = new ArrayList<Fragment>();
    private static FragmentManager sFM = null;
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
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        Log.e(TAG, "MainActivity initView");
        //初始化Fragment和ViewPager
        MusicFragment musicFragment = new MusicFragment();
        DiscoverFragment discoverFragment = new DiscoverFragment();
        sFragmentList.add(musicFragment);
        sFragmentList.add(discoverFragment);
        sFM = getSupportFragmentManager();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(sFM, sFragmentList);
        mVpContent.setAdapter(adapter);
        showMusicFragment();
        //初始化Drawer
        setUpDrawer();
        //设置Listener
        mVpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 展示MusicFragment
     */
    private void showMusicFragment() {
        mIvToolbarMusic.setSelected(true);
        mIvToolbarDiscover.setSelected(false);
        mVpContent.setCurrentItem(FRAGMENT_MUSIC);
    }

    /**
     * 初始化Drawer
     */
    private void setUpDrawer() {
        //设置头像
        try {
            Picasso.with(this).load(sApplication.mUserInfo.profile.avatarUrl)
                    .transform(new CircleTransform())
                    .into(mSdvAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置背景，调整图片亮度
        Picasso.with(this).load(sApplication.mUserInfo.profile.backgroundUrl)
                .transform(new ShadowTransform(-80))
                .into(mIvBg);

        //设置昵称
        mTvNicknam.setText(sApplication.mUserInfo.profile.nickname);
    }


    /**
     * 展示DiscoverFragment
     */
    private void showDiscoverFragment() {
        mIvToolbarDiscover.setSelected(true);
        mIvToolbarMusic.setSelected(false);
        mVpContent.setCurrentItem(FRAGMENT_DISCOVER);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "MainActivity destroy");
    }

    /**
     * 按返回键不退出，实现在后台继续播放
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 各种按钮的点击事件
     *
     * @param v
     */
    @OnClick({R.id.iv_drawer, R.id.iv_toolbar_discover, R.id.iv_toolbar_music})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_drawer:
                mDl.openDrawer(mLlDrawer);
                break;
            case R.id.iv_toolbar_music:
                showMusicFragment();
                break;
            case R.id.iv_toolbar_discover:
                showDiscoverFragment();
                break;
        }
    }
}
