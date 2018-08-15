package com.src.isec.mvp.view;

import com.src.isec.domain.entity.UserEntity;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view
 * @class 手机号登录
 * @time 2018/4/11 0011 11:08
 * @change
 * @chang time
 * @class describe
 */

public interface ILoginView extends IView {


    void setEnableSend(boolean isEnalbe);

    void setSendTime(String time);

    void onLoginSuccess(UserEntity userEntity);

}
