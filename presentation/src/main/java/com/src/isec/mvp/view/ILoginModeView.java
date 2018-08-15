package com.src.isec.mvp.view;

import com.src.isec.domain.entity.UserEntity;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view
 * @class 登录选择模式
 * @time 2018/4/11 0011 09:56
 * @change
 * @chang time
 * @class describe
 */

public interface ILoginModeView extends IView{

    void onLoginSuccess(UserEntity userEntity);
}
