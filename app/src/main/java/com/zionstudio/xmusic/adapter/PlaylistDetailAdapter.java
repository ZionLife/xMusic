package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.playlist.Song;
import com.zionstudio.xmusic.model.playlist.Artist;
import com.zionstudio.xmusic.model.playlist.Privilege;
import com.zionstudio.xmusic.model.playlist.Track;

import java.util.List;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.MyHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;
    private List<Track> mData;
    private List<Privilege> mPrivileges;
    private View mHeaderView = null;
    private OnItemClickListener mListener = null;

    public PlaylistDetailAdapter(Context context, List<Track> tracks, List<Privilege> privileges, OnItemClickListener l) {
        mContext = context;
        mData = tracks;
        mPrivileges = privileges;
        mListener = l;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new MyHolder(mHeaderView);
        }
        MyHolder holder = new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        final int pos = getRealPosition(holder);

        Track track = mData.get(pos);
        //演唱者
        String arAndAl = "";
        for (Artist artist : track.ar) {
            arAndAl += artist.name + "/";
        }
        holder.tvAlbum.setText(arAndAl.substring(0, arAndAl.length() - 1) + "-" + track.al.get(0).name);
        holder.tvCount.setText("" + (pos + 1));
        holder.tvName.setText(track.name);
        holder.ivLocal.setVisibility(View.GONE);

        //判断是否是本地歌曲，是的话就设置本地icon
        for (Song song : MyApplication.getMyApplication().mLocalSongs) {
            if (song.title.equals(track.name)) {
                holder.ivLocal.setVisibility(View.VISIBLE);
            }
        }
        if (track.mv != 0) {
            holder.ivMv.setVisibility(View.VISIBLE);
        } else {
            holder.ivMv.setVisibility(View.GONE);
        }
        if (mPrivileges.get(pos).maxbr >= 999000) {
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.ivIcon.setImageResource(R.drawable.sq_icon);
        } else {
            holder.ivIcon.setVisibility(View.GONE);
        }

        //注册点击监听事件
        if (mListener != null) {
            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    private int getRealPosition(MyHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mData.size() : mData.size() + 1;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tvCount;
        TextView tvName;
        TextView tvAlbum;
        ImageView ivMenu;
        ImageView ivIcon;
        ImageView ivLocal;
        LinearLayout rl;
        ImageView ivMv;
        LinearLayout ll;

        public MyHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            ll = (LinearLayout) itemView.findViewById(R.id.ll_itemsong);
            ll.setPadding(0, 0, 0, 0);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count_itemsong);
            tvCount.setVisibility(View.VISIBLE);
            ivMv = (ImageView) itemView.findViewById(R.id.iv_mv);
            ivLocal = (ImageView) itemView.findViewById(R.id.iv_isLocal);
            tvName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvAlbum = (TextView) itemView.findViewById(R.id.tv_album_name);
            ivMenu = (ImageView) itemView.findViewById(R.id.iv_menu);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            rl = (LinearLayout) itemView.findViewById(R.id.rl_localsongs);
        }
    }
}
