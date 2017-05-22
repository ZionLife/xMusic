package com.zionstudio.xmusic.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.model.playlist.Song;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/5/13 0013.
 */

public class BitmapUtils {

    /**
     * 按比例缩放图片
     *
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


    /**
     * 获取音乐专辑封面
     */
    public static Bitmap getCover(Song s) {
        MediaMetadataRetriever retriever;
        Bitmap bitmap = null;
        if (s != null) {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(s.url);
            byte[] cover = retriever.getEmbeddedPicture();
            if (cover != null) {
                bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            }
            retriever.release();
        }
        return bitmap;
    }

    /**
     * 获取音乐专辑封面的byte数组，便于压缩
     */
    public static byte[] getCoverByteArray(Song s) {
        byte[] bytes = null;
        if (s.type == Constants.TYPE_LOCAL) {
            MediaMetadataRetriever retriever;
            if (s != null) {
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(s.url);
                bytes = retriever.getEmbeddedPicture();
                retriever.release();
            }
        }
        return bytes;
    }

    /**
     * 计算图片的采样率
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfWidth / inSampleSize > reqWidth) && halfHeight / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 将Bitmap转换成字节数组
     *
     * @param bitmap
     * @param format
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(format, 100, bos);
        byte[] result = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取Bitmap的大小
     *
     * @param bitmap
     * @return
     */
    public static long getBitmapsize(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 根据字节数组创建符合期望长宽的压缩后的Bitmap
     *
     * @param bytes
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampleBitmapFromBytes(byte[] bytes, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //无需透明通道
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 根据资源文件创建符合期望长宽的压缩后的Bitmap
     *
     * @param res
     * @param resID
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resID, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resID, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resID, options);
    }
}
