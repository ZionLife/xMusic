package com.zionstudio.xmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.util.Utils;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

/**
 * 给RecyclerView画分割线，左边图片下方不画。
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Drawable mDivider;
    private int mOrientation;
    private int type;

    public static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public DividerDecoration(Context context, int orientation, int type) {
        this.mContext = context;
        this.mOrientation = orientation;
        this.type = type;
        final TypedArray ta = context.obtainStyledAttributes(ATTRS);
        this.mDivider = ta.getDrawable(0);
        ta.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //避免因为RecyclerView中没有item，而导致在drawHorizontalLine中findViewById得到null
        if (parent.getChildCount() != 0) {
            drawHorizontalLine(c, parent, state);
        }
    }

    private void drawHorizontalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = 0;
        switch (type) {
            case Utils.MUSIC_FG_DIVIDER_TYPE:
                //让divider的起始位置为TextView的起始位置
                TextView tvMusicFg = (TextView) parent.findViewById(R.id.tv_itemtitle_musicfg);
                left = (int) tvMusicFg.getX();
                break;
            case Utils.MUSIC_FG_PLAYLIST_DIVIDER_TYPE:
                LinearLayout ll = (LinearLayout) parent.findViewById(R.id.ll_playlist);
                left = (int) ll.getX();
                break;
            case Utils.LOCALSONGS_ACTIVITY_DIVIDER_TYPE:
                LinearLayout llLocalSongs = (LinearLayout) parent.findViewById(R.id.ll_itemsong);
                left = (int) llLocalSongs.getX();
                break;
            case Utils.PLAYLISTDETAIL_ACTIVITY_DIVIDER_TYPE:
                left = (int) mContext.getResources().getDimension(R.dimen.songLeftIconWidth);
                break;
        }

        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            //获取child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 绘制Music Fragment的divider
     *
     * @param c
     * @param parent
     * @param state
     */
    private void drawDivider(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (type == Utils.PLAYLISTDETAIL_ACTIVITY_DIVIDER_TYPE) {
            if (position == 0) {
                outRect.set(0, 0, 0, 0);
                return;
            }
        }
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}
