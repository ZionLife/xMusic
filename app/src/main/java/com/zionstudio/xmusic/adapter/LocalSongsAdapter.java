package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地音乐RecyclerView的Adapter
 * Created by Administrator on 2017/4/28 0028.
 */

public class LocalSongsAdapter extends RecyclerView.Adapter<LocalSongsAdapter.ViewHolder> {
    private Context mContext;
    private List<Song> mSongList;
    private static final String TAG = "LocalSongsAdapter";

    public LocalSongsAdapter(Context context, List<Song> list) {
        mContext = context;
        mSongList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song s = mSongList.get(position);
        holder.tvName.setText(s.title);
        holder.tvAlbum.setText(s.artist + "-" + s.albums);
        Log.e(TAG, "onBindViewHolder: size=" + s.size / (1024 * 1024) + "; name=" + s.title);
        holder.ivIcon.setVisibility(View.VISIBLE);
        if (s.size >= 1024 * 1024 * 15) {
            holder.ivIcon.setImageResource(R.drawable.sq_icon);
            Log.e(TAG, "set SQ");
        } else if (s.size >= 1024 * 1024 * 8) {
            holder.ivIcon.setImageResource(R.drawable.hq_icon);
            Log.e(TAG, "set HQ");
        } else {
            holder.ivIcon.setVisibility(View.GONE);
            Log.e(TAG, "set GONE");
        }
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAlbum;
        ImageView ivMenu;
        ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvAlbum = (TextView) itemView.findViewById(R.id.tv_album_name);
            ivMenu = (ImageView) itemView.findViewById(R.id.iv_menu);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }
}
