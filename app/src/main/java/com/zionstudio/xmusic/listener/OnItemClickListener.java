package com.zionstudio.xmusic.listener;

import android.view.View;

/**
 * RecyclerView的item点击事件
 * Created by Administrator on 2017/4/26 0026.
 */

public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
