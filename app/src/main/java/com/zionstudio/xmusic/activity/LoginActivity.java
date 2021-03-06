package com.zionstudio.xmusic.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.zionstudio.videoapp.okhttp.CommonOkHttpClient;
import com.zionstudio.videoapp.okhttp.https.HttpsUtils;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.request.CommonRequest;
import com.zionstudio.videoapp.okhttp.request.RequestParams;
import com.zionstudio.xmusic.R;
import com.zionstudio.xmusic.model.user.UserInfo;
import com.zionstudio.xmusic.util.UrlUtils;
import com.zionstudio.xmusic.util.Utils;
import com.zionstudio.xmusic.view.LoginEt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import butterknife.BindView;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class LoginActivity extends BaseActivity {

    private static final int TIME_OUT = 30;
    private static final String TAG = "LoginActivity";
    private static int etHeight;
    private static int drawableDimension;
    private static String sPhone = null;
    private static String sPassword = null;
    private OkHttpClient mOkHttpClient;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.et_phone)
    LoginEt mEtPhone;
    @BindView(R.id.et_password)
    LoginEt mEtPassword;

    @Override
    protected void initView() {
        super.initView();
        if (sApplication.mUserInfo != null) {
            String str = sApplication.mUserInfo.account.userName;
            mEtPhone.setText(str.substring(str.indexOf("_") + 1, str.length()));
        }

        etHeight = mEtPassword.getHeight();
        drawableDimension = Utils.dp2px(this, 20);

        //给PhoneEditText设置图标
        final Drawable phoneDrawableLeft = getResources().getDrawable(R.drawable.phone_login, null);
        phoneDrawableLeft.setBounds(0, 0, drawableDimension, drawableDimension);
        final Drawable phoneDrawableRight = getResources().getDrawable(R.drawable.empty, null);
        phoneDrawableRight.setBounds(0, 0, drawableDimension, drawableDimension);

        mEtPhone.setCompoundDrawablePadding(Utils.dp2px(this, 5));

        Drawable passDrawableLeft = getResources().getDrawable(R.drawable.password_login, null);
        passDrawableLeft.setBounds(0, 0, drawableDimension, drawableDimension);
        Drawable passDrawableRigth = getResources().getDrawable(R.drawable.forgotpass, null);
        passDrawableRigth.setBounds(0, 0, 50, 50);
        mEtPassword.setCompoundDrawables(passDrawableLeft, null, passDrawableRigth, null);
        mEtPassword.setCompoundDrawablePadding(Utils.dp2px(this, 5));

        //设置对右边图片的监听
        mEtPhone.setDrawableRightListener(new LoginEt.DrawableListener() {
            @Override
            public void onDrawableClick() {
                mEtPhone.setText("");
            }
        });

        mEtPassword.setDrawableRightListener(new LoginEt.DrawableListener() {
            @Override
            public void onDrawableClick() {
                Utils.makeToast("稍后补充");
                mEtPassword.requestFocus();
            }
        });
        //设置焦点变化监听
        mEtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtPhone.setCompoundDrawables(phoneDrawableLeft, null, phoneDrawableRight, null);
                } else {
                    mEtPhone.setCompoundDrawables(phoneDrawableLeft, null, null, null);
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPhone = mEtPhone.getText().toString();
                sPassword = mEtPassword.getText().toString();
                if (!TextUtils.isEmpty(sPassword) && !TextUtils.isEmpty(sPhone)) {
                    checkAccount();
                } else {
                    Utils.makeToast("请输入用户名和密码");
                }
            }
        });

        //弹出键盘
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {

    }

    /**
     * 验证账户信息，并获取账户头像昵称等保存到sp中
     */
    private void checkAccount() {
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", sPhone);
        map.put("password", sPassword);
        RequestParams params = new RequestParams(map);
        Request request = CommonRequest.createGetRequest(UrlUtils.LOGIN, params);
        DisposeDataListener listener = new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                sApplication.mUserInfo = (UserInfo) responseObj;
                updateUserInfo();
                Utils.skipToMainActivity(LoginActivity.this);
                LoginActivity.this.finish();
            }

            @Override
            public void onFailure(Object responseObj) {
                Log.e(TAG, "onLoginFailure");
            }
        };
        CommonOkHttpClient.get(request, new DisposeDataHandler(listener, UserInfo.class));
    }

    /**
     * 将用户信息保存到SharedPreferences中
     */
    private void updateUserInfo() {
        UserInfo i = sApplication.mUserInfo;
        SharedPreferences userSP = getSharedPreferences("userSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSP.edit();
        //将Object转换成String输出
        editor.putString("userInfo", Utils.Object2String(i));
        editor.commit();
    }
}
