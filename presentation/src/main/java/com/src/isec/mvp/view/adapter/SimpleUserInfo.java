package com.src.isec.mvp.view.adapter;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 模拟用户信息类
 * @time 2018/4/13 9:36
 * @change
 * @chang time
 * @class describe
 */

public class SimpleUserInfo {
    public String userid;
    public String nickname;
    public String headpic;
    public String laction;

    public SimpleUserInfo(){}

    public SimpleUserInfo(String userid, String nickname, String headpic) {
        this.userid = userid;
        this.nickname = nickname;
        this.headpic = headpic;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }
}
