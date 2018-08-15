package com.src.isec.mvp.view;

import android.support.annotation.NonNull;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.view
 * @class 定义View的基础规则
 * @time 2018/3/19 15:54
 * @change
 * @chang time
 * @class describe
 */

public interface IView {

    /**
     * @author liujiancheng
     * @time 2018/3/20  15:30
     * @describe 显示加载对话框
     */
    void showLoading();

    /**
     * @author liujiancheng
     * @time 2018/4/19  12:01
     * @describe 显示加载对话框，自定义提示文字
     */
    void showLoading(String hint);


    /**
     * @author liujiancheng
     * @time 2018/3/20  15:30
     * @describe 隐藏加载对话框
     */
    void hideLoading();

    /**
     * @author liujiancheng
     * @time 2018/4/19  13:58
     * @describe 显示状态视图的等待状态
     */
    void showStateViewLoading();


    /**
     * @author liujiancheng
     * @time 2018/4/19  13:59
     * @describe 显示状态视图的正常状态
     */
    void showStateViewContent();





    /**
     * @author liujiancheng
     * @time 2018/4/19  14:03
     * @describe 显示下拉刷新状态
     */
    default void showRefreshLoading() {

    }


    /**
     * @author liujiancheng
     * @time 2018/4/19  14:03
     * @describe 隐藏下拉刷新状态
     */
    default void hideRefreshLoading() {

    }

    /**
     * @author liujiancheng
     * @time 2018/4/19  14:45
     * @describe 隐藏加载更多时的状态
     */
    default void hideRefreshLoadMore() {

    }

    /**
     * @author liujiancheng
     * @time 2018/3/20  15:31
     * @describe 显示信息
     */
    void showMessage(@NonNull String message);


    /**
     * @author liujiancheng
     * @time 2018/3/26  16:42
     * @describe 处理没有网络的状态
     */
    default void handleNoNetWork(String message) {
    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  16:42
     * @describe 处理网络异常的状态
     */
    default void handleNetWorkError(String message) {
    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  16:44
     * @describe 处理Token失效的状态
     */
    default void handleTokenInvalid(String message) {
    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  16:45
     * @describe 处理通用异常的状态
     */
    default void handleCurrencyError(String message) {
    }
}
