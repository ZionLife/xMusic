package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.playlist.Playlist;

import java.util.List;

/**
 * Created by Administrator on 2017/6/8 0008.
 * DiscoverFragment里，推荐歌单的RecyclerView的Adapter
 */

public class RecommendPlaylistAdapter extends RecyclerView.Adapter<RecommendPlaylistAdapter.ViewHolder> {
    private Context mContext;
    private List<Playlist> mData;
    private OnItemClickListener mListener;

    public RecommendPlaylistAdapter(Context context, List<Playlist> playlists, OnItemClickListener l) {
        mContext = context;
        mData = playlists;
        mListener = l;
    }

    public RecommendPlaylistAdapter(Context context, List<Playlist> playlists) {
        mContext = context;
        mData = playlists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_playlist_discoverfg, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Playlist playlist = mData.get(position);
        Glide.with(mContext)
                .load(playlist.picUrl)
                .into(holder.iv);
        Glide.with(mContext)
                .load(playlist.picUrl)
                .into(holder.iv);

        holder.tvName.setText(playlist.name);
        if ((playlist.playCount / 10000) > 0) {
            holder.tvPlaycount.setText("" + playlist.playCount / 10000 + "万");
        } else {
            holder.tvPlaycount.setText("" + playlist.playCount);
        }

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder.ll, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll;
        ImageView iv;
        TextView tvName;
        TextView tvPlaycount;

        public ViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.ll_recommend);
            iv = (ImageView) itemView.findViewById(R.id.iv_bg);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPlaycount = (TextView) itemView.findViewById(R.id.tv_playcount);
        }
    }
}
