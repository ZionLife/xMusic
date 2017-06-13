package com.zionstudio.xmusic.model;

import com.zionstudio.xmusic.model.playlist.Playlist;

import java.util.List;

/**
 * Created by Administrator on 2017/6/13 0013.
 * 网友精选歌单接口返回的Json
 */

public class SelectedPlaylistsJson {
    public int code;
    public List<Playlist> playlists;
    public boolean more;
    public int total;
}
