package com.zionstudio.xmusic.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Administrator on 2017/5/13 0013.
 */

public class BitmapUtils {

    /**
     * 按比例缩放图片
     * @param bitmap
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getScaleBitmap(Bitmap bitmap, float reqWidth, float reqHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratioWidth = reqWidth / (float) width;
        float ratioHeight = reqHeight / (float) height;
        Matrix matrix = new Matrix();
        matrix.setScale(ratioWidth, ratioHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (bitmap != newBitmap) {
            bitmap.recycle();
        }
        return newBitmap;
    }
}
