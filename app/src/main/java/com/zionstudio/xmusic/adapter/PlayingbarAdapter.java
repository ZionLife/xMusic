package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.playlist.Song;
import com.zionstudio.xmusic.util.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public class PlayingbarAdapter extends PagerAdapter {
    private Context mContext;
    private List<Song> mDatas;

    public PlayingbarAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_playingbar, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_cover_playing);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_playing);
        TextView tvArtists = (TextView) view.findViewById(R.id.tv_artist_playing);
        Song s = mDatas.get(position);
        tvArtists.setVisibility(View.VISIBLE);
        tvTitle.setText(s.name);
        tvArtists.setText(s.artist);
        Bitmap cover = null;
        byte[] coverBytes = s.coverBytes;
        if (coverBytes != null) {
            cover = BitmapUtils.decodeSampleBitmapFromBytes(coverBytes, iv.getWidth(), iv.getHeight());
        }
        if (cover != null) {
            iv.setImageBitmap(cover);
        } else {
            iv.setImageResource(R.drawable.default_cover);
        }

//        if (mDatas.get(position).type == Constants.TYPE_LOCAL) {
//            //假如是本地音乐
//
//        }
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setDatas(List<Song> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
    }
}
