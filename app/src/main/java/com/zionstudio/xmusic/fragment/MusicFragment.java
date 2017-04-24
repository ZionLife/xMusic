package com.zionstudio.xmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zionstudio.xmusic.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class MusicFragment extends Fragment {
    private static final String TAG = "MusicFragment";
    @BindView(R.id.lv_musicfg)
    ListView mLvMusicfg;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateMusicFragmentView");
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        init();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void init() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
