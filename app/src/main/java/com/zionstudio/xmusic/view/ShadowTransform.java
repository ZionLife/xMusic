package com.zionstudio.xmusic.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class ShadowTransform implements Transformation {
    private static int brightness;

    /**
     * @param b 图片亮度值, -255 至 255。原图片为0. 大于0调亮，小于0调暗
     */
    public ShadowTransform(int b) {
        brightness = b;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        canvas.drawBitmap(source, 0, 0, paint);
        source.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "shadow";
    }
}
