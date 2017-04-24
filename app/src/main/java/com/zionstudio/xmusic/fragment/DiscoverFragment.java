package com.zionstudio.xmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zionstudio.xmusic.R;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class DiscoverFragment extends Fragment {
    private static final String TAG = "DiscoverFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateDiscoverFragmentView");
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        init();
        return view;
    }

    private void init() {

    }


}
