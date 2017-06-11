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

    int count = 3;

    private int BANNER_TYPE = 0;
    private int CATEGORY_TYPE = 1;
    private int RECOMMEND_PLAYLIST_TYPE = 2;
    private LayoutInflater mInflater;

    private List<Banner> mBanners;
    private List<Playlist> mRecommendPlaylists;
    private RecommendPlaylistAdapter mRecommendPlaylistAdapter;
    private BannersAdapter mBannersAdapter;
    private  static Runnable sRunnable = null;
    private static Handler sHandler = null;

    public DiscoverFgRvAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBanners = new ArrayList<>();
        mRecommendPlaylists = new ArrayList<>();
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
        } else if (viewType == RECOMMEND_PLAYLIST_TYPE) {
            View viewPlaylist = mInflater.inflate(R.layout.layout_recommend_playlist_discoverfg, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewPlaylist.getLayoutParams();
            params.setFullSpan(true);
            viewPlaylist.setLayoutParams(params);
            return new RecommendPlaylistHolder(viewPlaylist);
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
        } else if (holder instanceof RecommendPlaylistHolder && mRecommendPlaylists.size() != 0) {
            initRecommendPlaylist((RecommendPlaylistHolder) holder);
        }
    }

    private void initRecommendPlaylist(RecommendPlaylistHolder holder) {
        holder.rvRecommendPlaylist.setLayoutManager(new GridLayoutManager(mContext, 3));
        mRecommendPlaylistAdapter = new RecommendPlaylistAdapter(mContext, mRecommendPlaylists, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                intent.putExtra("id", mRecommendPlaylists.get(position).id);
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
        holder.rvRecommendPlaylist.addItemDecoration(new DividerGridItemDecoration(mContext));
        holder.rvRecommendPlaylist.setAdapter(mRecommendPlaylistAdapter);
    }

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

        }
        return super.getItemViewType(position);
    }

    public void setBannersData(List<Banner> datas) {
        Log.e(TAG, "setBannersData！");
        mBanners.clear();
        mBanners.addAll(datas);
        notifyDataSetChanged(); //此处待改。
//        mBannersAdapter.notifyDataSetChanged();
    }

    public void setRecommendPlaylists(List<Playlist> datas) {
        mRecommendPlaylists.clear();
        mRecommendPlaylists.addAll(datas);
        notifyDataSetChanged();
//        mRecommendPlaylistAdapter.notifyDataSetChanged();
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

    class RecommendPlaylistHolder extends RecyclerView.ViewHolder {
        RecyclerView rvRecommendPlaylist;
        TextView tvRecommendPlaylistTitle;

        public RecommendPlaylistHolder(View itemView) {
            super(itemView);
            rvRecommendPlaylist = (RecyclerView) itemView.findViewById(R.id.rv_recommend_playlist);
            tvRecommendPlaylistTitle = (TextView) itemView.findViewById(R.id.tv_recommend_playlist_title);
        }
    }
}
