package com.src.isec.mvp.view;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement
 * @class 更换手机
 * @time 2018/4/10 0010 11:02
 * @change
 * @chang time
 * @class describe
 */

public interface IChangePhoneView extends IView{

    void setEnableSend(boolean isEnable);

    void setSendTime(String time);

    default void onVerificationSuccess(Object o){}

    default void onBindingSuccess(Object o){}


}
