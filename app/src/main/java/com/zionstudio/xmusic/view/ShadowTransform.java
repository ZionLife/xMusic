package com.zionstudio.xmusic.view;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class ShadowTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();


        return null;
    }

    @Override
    public String key() {
        return "shadow";
    }
}
