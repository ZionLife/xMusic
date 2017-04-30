package com.zionstudio.xmusic.model.user;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class UserInfo implements Serializable {
    private static final long serialVersionUID = -9218878370081852774L;
    public String code;
    public Account account;
    public Profile profile;
    public ArrayList<Binding> bindings;
    private String cookies;

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getCookies() {
        return cookies;
    }
}
