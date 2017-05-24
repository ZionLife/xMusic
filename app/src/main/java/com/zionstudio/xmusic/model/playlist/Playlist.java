package com.zionstudio.xmusic.model.playlist;

import com.zionstudio.xmusic.model.user.Profile;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class Playlist {
    public String playCount;
    public String coverImgId;
    public String coverImgUrl;
    public String name;
    public String id;
    public int trackCount;

    public Profile creator;
    public ArrayList<Song> tracks;
    public String picUrl;
}
