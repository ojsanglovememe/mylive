package com.src.isec.reactivex.rxbus.event;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.reactivex.rxbus.event
 * @class 修个个人信息事件
 * @time 2018/4/25 0025 11:00
 * @change
 * @chang time
 * @class describe
 */

public class UpdatePersonalInfoEvent {

    private String nickname;
    private String sex;
    private String avatar;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
