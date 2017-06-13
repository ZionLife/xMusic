package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.activity.PlaylistDetailActivity;
import com.zionstudio.xmusic.listener.OnItemClickListener;
import com.zionstudio.xmusic.model.Banner;
import com.zionstudio.xmusic.model.playlist.Playlist;
import com.zionstudio.xmusic.view.DividerGridItemDecoration;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/10 0010.
 * Discover Fragment页面的最外层RecyclerView的Adapter
 */

public class DiscoverFgRvAdapter extends RecyclerView.Adapter {
    private final String TAG = "DiscoverFgRvAdapter";
    private Context mContext;

    int count = 5;

    private int BANNER_TYPE = 0;
    private int CATEGORY_TYPE = 1;
    private int RECOMMEND_PLAYLIST_TYPE = 2;
    private int SELECTED_PLAYLIST_TYPE = 3;
    private int BOTTOMLINE_TYPE = -1;

    private LayoutInflater mInflater;

    private List<Banner> mBanners;
    private List<Playlist> mRecommendPlaylists;
    private List<Playlist> mSelectedPlaylists;
    private RecommendPlaylistAdapter mPlaylistAdapter;
    private BannersAdapter mBannersAdapter;
    private static Runnable sRunnable = null;
    private static Handler sHandler = null;

    public DiscoverFgRvAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBanners = new ArrayList<>();
        mRecommendPlaylists = new ArrayList<>();
        mSelectedPlaylists = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder！");
        if (viewType == BANNER_TYPE) {
            View viewBanner = mInflater.inflate(R.layout.layout_banner_discoverfg, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewBanner.getLayoutParams();
            params.setFullSpan(true);
            viewBanner.setLayoutParams(params);
            return new BannerHolder(viewBanner);
        } else if (viewType == CATEGORY_TYPE) {
            View viewCategory = mInflater.inflate(R.layout.layout_category_discoverfg, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewCategory.getLayoutParams();
            params.setFullSpan(true);
            viewCategory.setLayoutParams(params);
            return new CategoryHolder(viewCategory);
        } else if (viewType == RECOMMEND_PLAYLIST_TYPE || viewType == SELECTED_PLAYLIST_TYPE) {
            View viewPlaylist = mInflater.inflate(R.layout.layout_playlist_discoverfg, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewPlaylist.getLayoutParams();
            params.setFullSpan(true);
            viewPlaylist.setLayoutParams(params);
            return new PlaylistHolder(viewPlaylist);
        } else if (viewType == BOTTOMLINE_TYPE) {
            View viewBottomLine = mInflater.inflate(R.layout.layout_bottomline, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewBottomLine.getLayoutParams();
            params.setFullSpan(true);
            viewBottomLine.setLayoutParams(params);
            return new BottomLineHolder(viewBottomLine);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder！");
        //待加判断是否第一次初始
        if (holder instanceof BannerHolder && mBanners.size() != 0) {
            initBanner((BannerHolder) holder);
        } else if (holder instanceof CategoryHolder) {
            //待添加监听事件
        } else if (holder instanceof PlaylistHolder && getItemViewType(position) == RECOMMEND_PLAYLIST_TYPE && mRecommendPlaylists.size() != 0) {
            ((PlaylistHolder) holder).tvPlaylistTitle.setText("推荐歌单");
            initPlaylist((PlaylistHolder) holder, mRecommendPlaylists);
        } else if (holder instanceof PlaylistHolder && getItemViewType(position) == SELECTED_PLAYLIST_TYPE && mSelectedPlaylists.size() != 0) {
            ((PlaylistHolder) holder).tvPlaylistTitle.setText("热门精选");
            initPlaylist((PlaylistHolder) holder, mSelectedPlaylists);
        } else if (holder instanceof BottomLineHolder) {
            ((BottomLineHolder) holder).tvHint.setText("我是有底线的");
        }
    }

    /**
     * 初始化歌单RecyclerView
     *
     * @param holder
     * @param datas
     */
    private void initPlaylist(PlaylistHolder holder, final List<Playlist> datas) {
        holder.rvPlaylist.setLayoutManager(new GridLayoutManager(mContext, 3));
        mPlaylistAdapter = new RecommendPlaylistAdapter(mContext, datas, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                intent.putExtra("id", datas.get(position).id);
                ImageView iv = (ImageView) view.findViewById(R.id.iv_bg);
                iv.setDrawingCacheEnabled(true);
                Bitmap cover = iv.getDrawingCache();
                intent.putExtra("cover", cover);
                mContext.startActivity(intent);
                iv.setDrawingCacheEnabled(false);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        holder.rvPlaylist.addItemDecoration(new DividerGridItemDecoration(mContext));
        holder.rvPlaylist.setAdapter(mPlaylistAdapter);
    }

    /**
     * 初始化Banner
     *
     * @param holder
     */
    private void initBanner(final BannerHolder holder) {
        Log.e(TAG, "initBanner！");
        mBannersAdapter = new BannersAdapter(mContext, mBanners);
        holder.vp.setAdapter(mBannersAdapter);
        CircleNavigator circleNavigator = new CircleNavigator(mContext);
        circleNavigator.setCircleColor(mContext.getResources().getColor(R.color.colorPrimary));
        circleNavigator.setCircleCount(mBanners.size());
        circleNavigator.notifyDataSetChanged();
        circleNavigator.setFollowTouch(false);
        holder.indicator.setNavigator(circleNavigator);
        ViewPagerHelper.bind(holder.indicator, holder.vp);
        if (sHandler == null) {
            sHandler = new Handler();
            sRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mBanners.size() != 0) {
                        holder.vp.setCurrentItem((holder.vp.getCurrentItem() + 1) % mBanners.size());
                    }
                    sHandler.postDelayed(sRunnable, 5000);
                }
            };
            sHandler.postDelayed(sRunnable, 5000);
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {

        switch (position) {
            case 0:
                return BANNER_TYPE;
            case 1:
                return CATEGORY_TYPE;
            case 2:
                return RECOMMEND_PLAYLIST_TYPE;
            case 3:
                return SELECTED_PLAYLIST_TYPE;
            case 4:
                return BOTTOMLINE_TYPE;

        }
        return super.getItemViewType(position);
    }

    /**
     * 由DiscoverFragment获取到Banners数据之后设置进来
     *
     * @param datas
     */
    public void setBannersData(List<Banner> datas) {
        Log.e(TAG, "setBannersData！");
        mBanners.clear();
        mBanners.addAll(datas);
        notifyDataSetChanged(); //此处待改。
//        mBannersAdapter.notifyDataSetChanged();
    }

    /**
     * 设置个人推荐歌单数据
     *
     * @param datas
     */
    public void setRecommendPlaylists(List<Playlist> datas) {
        mRecommendPlaylists.clear();
        mRecommendPlaylists.addAll(datas);
        notifyDataSetChanged();
//        mPlaylistAdapter.notifyDataSetChanged();
    }

    /**
     * 设置网友精选歌单数据
     *
     * @param datas
     */
    public void setSelectedPlaylists(List<Playlist> datas) {
        mSelectedPlaylists.clear();
        mSelectedPlaylists.addAll(datas);
        notifyDataSetChanged();
    }

    //顶部Banner的ViewHolder
    class BannerHolder extends RecyclerView.ViewHolder {
        ViewPager vp;
        RelativeLayout rl;
        MagicIndicator indicator;

        public BannerHolder(View itemView) {
            super(itemView);
            vp = (ViewPager) itemView.findViewById(R.id.vp_discover_fg);
            rl = (RelativeLayout) itemView.findViewById(R.id.rl_banner_discoverfg);
            indicator = (MagicIndicator) itemView.findViewById(R.id.indicator_discover_fg);
        }
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        LinearLayout llFm;
        LinearLayout llRecommend;
        LinearLayout llTopList;

        public CategoryHolder(View itemView) {
            super(itemView);
            llFm = (LinearLayout) itemView.findViewById(R.id.ll_personalfm);
            llRecommend = (LinearLayout) itemView.findViewById(R.id.ll_recommend);
            llTopList = (LinearLayout) itemView.findViewById(R.id.ll_toplist);
        }
    }

    class PlaylistHolder extends RecyclerView.ViewHolder {
        RecyclerView rvPlaylist;
        TextView tvPlaylistTitle;

        public PlaylistHolder(View itemView) {
            super(itemView);
            rvPlaylist = (RecyclerView) itemView.findViewById(R.id.rv_recommend_playlist);
            tvPlaylistTitle = (TextView) itemView.findViewById(R.id.tv_recommend_playlist_title);
        }
    }

    class BottomLineHolder extends RecyclerView.ViewHolder {
        TextView tvHint;

        public BottomLineHolder(View itemView) {
            super(itemView);
            tvHint = (TextView) itemView.findViewById(R.id.tv_hint);
        }
    }
}
