package com.zionstudio.xmusic.model.playlist;

import com.zionstudio.xmusic.model.playlist.Album;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class Song implements Serializable {
    private static final long seriaVersionUID = 1L;
    public int type;
    public int id;
    public String title;
    public String display_name;
    public String url;
    public int duration;
    public String albums;
    public String artist;
    public String singer;
    public String coverPath;
    public long size;

    //在线歌曲
    public String name;
    public Album al;
}
