package com.src.isec.mvp.view;

import com.src.isec.domain.entity.UserEntity;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view
 * @class 个人中心
 * @time 2018/4/12 0012 09:50
 * @change
 * @chang time
 * @class describe
 */

public interface IMyView extends IView {

    void onPersonalInfoSuccess(UserEntity userEntity);

}
