package com.zionstudio.videoapp.okhttp;


import com.zionstudio.videoapp.okhttp.https.HttpsUtils;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataHandler;
import com.zionstudio.videoapp.okhttp.listener.DisposeDataListener;
import com.zionstudio.videoapp.okhttp.response.CommonJsonCallback;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/3/12 0012.
 */

public class CommonOkHttpClient {
    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;

    static {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        okHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.followSslRedirects(true);

        okHttpClientBuilder.sslSocketFactory(HttpsUtils.initSSLSocketFactory(), HttpsUtils.initTrustManager());
        mOkHttpClient = okHttpClientBuilder.build();
    }

    public static Call sendRequest(Request request, Callback commonCallback) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(commonCallback);
        return call;
    }

    public static Call get(Request request, DisposeDataHandler handler) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handler));
        return call;
    }

    public static Call post(Request request, DisposeDataHandler handler) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handler));
        return call;
    }
}
