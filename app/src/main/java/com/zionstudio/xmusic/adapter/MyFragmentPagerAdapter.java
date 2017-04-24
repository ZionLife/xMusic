package com.zionstudio.xmusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager mFM;
    private ArrayList<Fragment> mList;

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        mFM = fm;
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
