package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.playlist.Playlist;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class MusicFgPlaylistAdapter extends RecyclerView.Adapter<MusicFgPlaylistAdapter.PlaylistViewHolder> {
    private Context mContext;
    private ArrayList<Playlist> mList;

    public MusicFgPlaylistAdapter(Context context, ArrayList<Playlist> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PlaylistViewHolder holder = new PlaylistViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_playlist_musicfg, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        Playlist list = mList.get(position);
        //加载歌单封面图片
        Picasso.with(mContext)
                .load(list.coverImgUrl)
                .into(holder.iv);

        holder.tvName.setText(list.name);
        holder.tvCount.setText(list.trackCount + "首，已下载10首");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvName;
        TextView tvCount;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_cover);
            tvName = (TextView) itemView.findViewById(R.id.tv_playlist_name);
            tvCount = (TextView) itemView.findViewById(R.id.tv_download_count);
        }
    }
}
