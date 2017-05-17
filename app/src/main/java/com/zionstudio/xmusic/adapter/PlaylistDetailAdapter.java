package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.playlist.Artist;
import com.zionstudio.xmusic.model.playlist.Track;

import java.util.List;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.MyHolder> {
    private Context mContext;
    private List<Track> mData;

    public PlaylistDetailAdapter(Context context, List<Track> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder = new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Track track = mData.get(position);
        //演唱者
        String arAndAl = "";
        for (Artist artist : track.ar) {
            arAndAl += artist.name + "/";
        }
        holder.tvAlbum.setText(arAndAl.substring(0, arAndAl.length() - 1) + "-" + track.al.get(0).name);
        holder.tvCount.setText("" + (position + 1));
        holder.tvName.setText(track.name);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tvCount;
        TextView tvName;
        TextView tvAlbum;
        ImageView ivMenu;
        ImageView ivIcon;
        RelativeLayout rl;

        public MyHolder(View itemView) {
            super(itemView);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count_itemsong);
            tvCount.setVisibility(View.VISIBLE);
            tvName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvAlbum = (TextView) itemView.findViewById(R.id.tv_album_name);
            ivMenu = (ImageView) itemView.findViewById(R.id.iv_menu);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            rl = (RelativeLayout) itemView.findViewById(R.id.rl_localsongs);
        }
    }
}
