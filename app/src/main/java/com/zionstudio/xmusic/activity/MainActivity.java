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
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.MyFragmentPagerAdapter;
import com.zionstudio.xmusic.fragment.DiscoverFragment;
import com.zionstudio.xmusic.fragment.MusicFragment;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.CircleTransform;
import com.zionstudio.xmusic.view.ShadowTransform;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zionstudio.xmusic.MyApplication.sPlayingIndex;
import static com.zionstudio.xmusic.MyApplication.sPlayingList;
import static com.zionstudio.xmusic.MyApplication.sRecentlyPlayedList;

public class MainActivity extends BasePlayMusicActivity {
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
        SharedPreferences sp = getSharedPreferences("PlayingInfo", Context.MODE_PRIVATE);
        String playingList = sp.getString("PlayingList", null);
        if (playingList != null) {
            sPlayingList = (List<Song>) Utils.String2Object(playingList);
        } else {
            sPlayingList = new ArrayList<Song>();
        }
        sPlayingIndex = sp.getInt("PlayingIndex", -1);
        String recentlyPlayedList = sp.getString("RecentlyPlayedList", null);
        if (recentlyPlayedList != null) {
            sRecentlyPlayedList = (List<Song>) Utils.String2Object(recentlyPlayedList);
        } else {
            sRecentlyPlayedList = new ArrayList<Song>();
        }
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
            Picasso.with(this).load(MyApplication.sUserInfo.profile.avatarUrl)
                    .transform(new CircleTransform())
                    .into(mSdvAvatar);
        } catch (Exception e) {
            e.printStackTrace();
            if (MyApplication.sUserInfo == null) {
                Log.e(TAG, "sUserInfo == null");
            }
        }
        //设置背景，调整图片亮度
        Picasso.with(this).load(MyApplication.sUserInfo.profile.backgroundUrl)
                .transform(new ShadowTransform(-80))
                .into(mIvBg);

        //设置昵称
        mTvNicknam.setText(MyApplication.sUserInfo.profile.nickname);
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
        //将播放列表和最近播放歌曲相关信息保存到SP中
        SharedPreferences sp = getSharedPreferences("PlayingInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("PlayingList", Utils.Object2String(sPlayingList));
        editor.putInt("PlayingIndex", sPlayingIndex);
        editor.putString("RecentlyPlayedList", Utils.Object2String(sRecentlyPlayedList == null ? new ArrayList<Song>() : sRecentlyPlayedList));
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
