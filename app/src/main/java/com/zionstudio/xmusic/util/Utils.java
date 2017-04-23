package com.zionstudio.xmusic.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zionstudio.xmusic.LoginActivity;
import com.zionstudio.xmusic.activity.MainActivity;
import com.zionstudio.xmusic.MyApplication;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class Utils {
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
}
