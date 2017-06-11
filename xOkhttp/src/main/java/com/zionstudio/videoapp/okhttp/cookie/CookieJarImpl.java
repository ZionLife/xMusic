package com.zionstudio.videoapp.okhttp.cookie;

import android.util.Log;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {
    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) new IllegalArgumentException("cookieStore can not be null.");
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.add(url, cookies);
        Log.e("CookieJarImpl", "url:" + url + " save from response" + cookieStore.get(url).toString());
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        Log.e("CookieJarImpl", "url:" + url + " load for request:" + cookieStore.get(url).toString());
        return cookieStore.get(url);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
