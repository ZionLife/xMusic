package com.zionstudio.xmusic.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.adapter.LocalSongsAdapter;
import com.zionstudio.xmusic.model.Song;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class LocalSongsActivity extends BaseActivity {
    private static List<Song> mLocalSongs = new ArrayList<Song>();
    private static LocalSongsAdapter mAdapter = null;
    @BindView(R.id.iv_back_localsongs)
    ImageView mIvBackLocalsongs;
    @BindView(R.id.rv_localsongs)
    RecyclerView mRvLocalsongs;
    @BindView(R.id.et_search_localsongs)
    EditText mEtSearchLocalsongs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localsongs);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        String where = "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0";
        mLocalSongs.clear();
        mLocalSongs.addAll(Utils.getAllMediaList(this, where));
    }

    private void initView() {
        //实现状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //设置返回按钮监听事件
        mIvBackLocalsongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalSongsActivity.this.finish();
            }
        });

        //给RecyclerView设置Adapter
        mAdapter = new LocalSongsAdapter(this, mLocalSongs);
        mRvLocalsongs.setAdapter(mAdapter);
        mRvLocalsongs.setLayoutManager(new LinearLayoutManager(this));
        mRvLocalsongs.addItemDecoration(new DividerDecoration(this, 0, Utils.LOCALSONGS_ACTIVITY_DIVIDER_TYPE));
    }

}
