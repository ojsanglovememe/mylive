package com.src.isec.mvp.presenter;

import android.content.Context;

import com.src.isec.mvp.view.IView;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter
 * @class 定义Presenter基础规则
 * @time 2018/3/19 15:49
 * @change
 * @chang time
 * @class describe
 */

public interface IPresenter<V extends IView> {

    /**
     * 做一些初始化操作
     */
    /**
     * @author liujiancheng
     * @time 2018/3/19  16:05
     * @describe 进行初始化操作
     * 注意：与View相关的操作请勿在此函数内执行
     */
    void onStart();


    /**
     * @author liujiancheng
     * @time 2018/3/19  16:05
     * @describe 资源释放
     */
    void onDestroy();

    /**
     * @author liujiancheng
     * @time 2018/3/20  15:57
     * @describe 关联View和上下文环境
     */
    void attachViewAndContext(V iView, Context context);
}
