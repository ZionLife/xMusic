package com.zionstudio.xmusic.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * 实现Picasso的接口，将加载的图片显示成圆形
 * Created by Administrator on 2017/4/23 0023.
 */

public class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        //根据源图片按参数复制一个Bitmap
        Bitmap squareBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squareBitmap != source) {
            source.recycle();   //允许回收bitmap
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        //创建着色器BitmapShader，设置模式为CLAMP，即拉伸。
        BitmapShader shader = new BitmapShader(squareBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);

        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squareBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
