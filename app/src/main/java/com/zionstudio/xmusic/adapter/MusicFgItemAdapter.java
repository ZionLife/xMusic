package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.zionstudio.xmusic.R;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class MusicFgItemAdapter extends RecyclerView.Adapter<MusicFgItemAdapter.MyViewHolder> {
    private Context mContext = null;
    private String[] mTitle = null;
    private int[] mIcon = null;

    public MusicFgItemAdapter(Context context, String[] title, int[] icon) {
        mContext = context;
        mTitle = title;
        mIcon = icon;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musicfg, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv.setImageResource(mIcon[position]);
        holder.tvTitle.setText(mTitle[position]);

    }


    @Override
    public int getItemCount() {
        return mTitle.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvTitle;
        TextView tvCount;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_itemicon_musicfg);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_itemtitle_musicfg);
            tvCount = (TextView) itemView.findViewById(R.id.tv_itemcount_musicfg);
        }
    }
}
