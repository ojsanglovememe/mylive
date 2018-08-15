package com.src.isec.reactivex.rxbus.event;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.reactivex.rxbus.event
 * @class 登录事件
 * @time 2018/4/21 0021 11:20
 * @change
 * @chang time
 * @class describe
 */

public class LoginEvent {

    private boolean isLogin = true;

    public LoginEvent() {
    }

    public LoginEvent(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
