package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.playlist.Song;

import java.util.List;

/**
 * 本地音乐RecyclerView的Adapter
 * Created by Administrator on 2017/4/28 0028.
 */

public class LocalSongsAdapter extends RecyclerView.Adapter<LocalSongsAdapter.ViewHolder> {
    private Context mContext;
    private List<Song> mSongList;
    private static final String TAG = "LocalSongsAdapter";
    private OnItemClickListener mListener = null;

    public LocalSongsAdapter(Context context, List<Song> list, OnItemClickListener l) {
        mContext = context;
        mSongList = list;
        mListener = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Song s = mSongList.get(position);
        holder.tvName.setText(s.name);
        holder.tvAlbum.setText(s.artist + " - " + s.album);
        if (mListener != null) {
            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(holder.itemView, position);
                }
            });
        }
        holder.ivIcon.setVisibility(View.VISIBLE);
        if (s.size >= 1024 * 1024 * 15) {
            holder.ivIcon.setImageResource(R.drawable.sq_icon);
        } else if (s.size >= 1024 * 1024 * 8) {
            holder.ivIcon.setImageResource(R.drawable.hq_icon);
        } else {
            holder.ivIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAlbum;
        ImageView ivIsLocal;
        ImageView ivMenu;
        ImageView ivIcon;
        LinearLayout rl;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvAlbum = (TextView) itemView.findViewById(R.id.tv_album_name);
            ivIsLocal = (ImageView) itemView.findViewById(R.id.iv_isLocal);
            ivIsLocal.setVisibility(View.VISIBLE);
            ivMenu = (ImageView) itemView.findViewById(R.id.iv_menu);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            rl = (LinearLayout) itemView.findViewById(R.id.rl_localsongs);
        }
    }
}
