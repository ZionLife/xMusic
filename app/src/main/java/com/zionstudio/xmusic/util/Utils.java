package com.zionstudio.xmusic.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import com.zionstudio.xmusic.activity.LoginActivity;
import com.zionstudio.xmusic.activity.MainActivity;
import com.zionstudio.xmusic.MyApplication;
import com.zionstudio.xmusic.model.Song;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class Utils {
    //定义RecyclerView的Divider的类型常量
    public static final int MUSIC_FG_DIVIDER_TYPE = 0;
    public static final int MUSIC_FG_PLAYLIST_DIVIDER_TYPE = 1;
    public static final int LOCALSONGS_ACTIVITY_DIVIDER_TYPE = 2;
    private static final String TAG = "Utils";

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 创建并显示Toast
     *
     * @param str
     */
    public static void makeToast(String str) {
        Toast.makeText(MyApplication.sContext, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转到登录界面
     *
     * @param context
     */
    public static void skipToLoginActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    /**
     * 跳转到主界面
     *
     * @param context
     */
    public static void skipToMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     *
     * @param object 待加密的转换为String的对象
     * @return String   加密后的String
     */
    public static String Object2String(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用Base64解密String，返回Object对象
     *
     * @param objectString 待解密的String
     * @return object      解密后的object
     */
    public static Object String2Object(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 搜索本地音乐
     */
    public static List<Song> getAllMediaList(Context context) {
        Cursor cursor = null;
        List<Song> mediaList = new ArrayList<Song>();
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
            if (cursor == null) {
                Log.e(TAG, "查询cursor为null");
                return mediaList;
            }
            int count = cursor.getCount();
            if (count <= 0) {
                Log.e(TAG, "查询结果条数为0");
                return mediaList;
            }
            mediaList = new ArrayList<Song>();
            Song s = null;
            while (cursor.moveToNext()) {
                s = new Song();
                s.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                s.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                s.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                //检查是否会小于1分钟或者小于1Mb，且过滤掉.flac文件（系统MediaPlayer对.flac支持不好，以后用开源库替换）
                if ((s.duration / 60 <= 0) || (s.size < 1024 * 1024) || s.path.contains(".flac")) {
                    continue;
                }

                s.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                s.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                s.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                s.albums = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                s.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                s.type = Constants.TYPE_LOCAL;
                mediaList.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaList;
    }

    /**
     * 获取屏幕宽
     */
    public static int getScreenWidth() {
        return MyApplication.sContext.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高
     */
    public static int getScreenHeight() {
        return MyApplication.sContext.getResources().getDisplayMetrics().heightPixels;
    }
}