package com.zionstudio.xmusic.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zionstudio.xmusic.model.Banner;

import java.util.List;

/**
 * Created by Administrator on 2017/5/25 0025.
 */

public class BannersAdapter extends PagerAdapter {
    private Context mContext;
    private List<Banner> mData;

    public BannersAdapter(Context context, List<Banner> banners) {
        mContext = context;
        mData = banners;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String imageUrl = mData.get(position).pic;
        ImageView iv = new ImageView(mContext);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext)
                .load(imageUrl)
                .into(iv);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
