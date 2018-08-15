package com.src.isec.mvp.view;

import android.net.Uri;

import com.src.isec.domain.entity.UserEntity;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view
 * @class 编辑个人资料
 * @time 2018/4/17 0017 15:38
 * @change
 * @chang time
 * @class describe
 */

public interface IEditPersonalInfoView extends IView{

    default void onPersonalInfoSuccess(UserEntity userEntity){}

    default void onNicknameSuccess(String nickname){}

    default void onSexSuccess(String sex){}

    default void onAvatarSuccess(Uri uri){}
}
