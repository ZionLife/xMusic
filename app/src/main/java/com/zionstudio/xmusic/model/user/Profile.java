package com.zionstudio.xmusic.model.user;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class Profile implements Serializable {
    public String province;
    public String backgroundUrl;       //背景图片地址
    public String avatarUrl;           //头像地址
    public String nickname;
    public String userId;
}
