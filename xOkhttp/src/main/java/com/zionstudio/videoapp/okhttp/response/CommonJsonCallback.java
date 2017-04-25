package com.zionstudio.videoapp.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.zionstudio.videoapp.okhttp.exception.OkHttpException;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class CommonJsonCallback implements Callback {
    protected final String RESULT_CODE = "ecode";
    protected final int RESULT_COADE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";
    protected final String COOKIE_STORE = "Set-Cookie";

    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private Handler mDeliveryHandler;
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandler handler) {
        this.mListener = handler.mListener;
        this.mClass = handler.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void onFailure(final Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        final String cookie = response.header("Set-Cookie", "");
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result, cookie);
            }
        });
    }

    private void handleResponse(Object result, String cookie) {
        if (result == null || result.toString().trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }

        try {
            if (mClass == null) {
                mListener.onSuccess(result, cookie);
            } else {
                String resultStr = result.toString();
                Object obj = JSON.parseObject(resultStr, mClass);
                if (obj != null) {
                    mListener.onSuccess(obj, cookie);
                } else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                }
            }
        } catch (Exception e) {
            mListener.onFailure(new OkHttpException(OTHER_ERROR, e.getMessage()));
            e.printStackTrace();
        }
    }
}
