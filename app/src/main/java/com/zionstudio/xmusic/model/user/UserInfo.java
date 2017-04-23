package com.zionstudio.xmusic.model.user;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class UserInfo implements Serializable {
    public Account account;
    public Profile profile;
    public ArrayList<Binding> bindings;
}
