package com.zionstudio.xmusic.model.playlist;

import com.zionstudio.xmusic.model.playlist.Album;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class Song implements Serializable {
    private static final long seriaVersionUID = 1L;
    public int type;    //区分本地歌曲和在线歌曲
    public int id;
    //    public String title;
    public int duration;
    public long size;

    //歌曲名称
    public String name;
    //专辑列表
    public List<Album> al;
    //歌手列表
    public List<Artist> ar;
    public String url;
    //经格式化后的歌手名（可能有多个歌手）
    public String artist;
    //经格式化后的专辑名
    public String album;
    //mv
    public long mv;
    //专辑封面图片地址
    public String picUrl;
    public byte[] coverBytes;
}
