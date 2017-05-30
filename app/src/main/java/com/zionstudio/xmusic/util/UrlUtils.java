package com.zionstudio.xmusic.util;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class UrlUtils {
    //    public static final String SERVER_HOST = "http://119.29.119.123:3000";
    public static final String SERVER_HOST = "http://192.168.199.209:3000";

    //登陆接口
    public static final String LOGIN = SERVER_HOST + "/login/cellphone";
    //获取用户歌单
    public static final String PLAYLIST = SERVER_HOST + "/user/playlist";
    //获取歌单详情
    public static final String PLAYLIST_DETAIL = SERVER_HOST + "/playlist/detail";
    //获取歌曲详情（不包含url）
    public static final String SONG_DETAIL = SERVER_HOST + "/song/detail";
    //获取歌曲url
    public static final String SONG_URL = SERVER_HOST + "/music/url";
    //获取banner
    public static final String BANNER = SERVER_HOST + "/banner";
}
