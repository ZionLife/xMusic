package com.zionstudio.xmusic.util;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.widget.Toast;


import com.zionstudio.xmusic.activity.LoginActivity;
import com.zionstudio.xmusic.activity.MainActivity;
import com.zionstudio.xmusic.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class Utils {
    //定义RecyclerView的Divider的类型常量
    public static final int MUSIC_FG_DIVIDER_TYPE = 0;
    public static final int MUSIC_FG_PLAYLIST_DIVIDER_TYPE = 1;
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
        Toast.makeText(MyApplication.mContext, str, Toast.LENGTH_SHORT).show();
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
}