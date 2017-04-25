package com.zionstudio.videoapp.okhttp.listener;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public interface DisposeDataListener {
    public void onSuccess(Object responseObj, String cookie);

    public void onFailure(Object responseObj);
}
