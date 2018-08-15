package com.src.isec.domain;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.domain
 * @class 存放状态码
 * @time 2018/4/18 16:53
 * @change
 * @chang time
 * @class describe
 */
public interface StateCode {

    int REQUEST_SUCCESS = 100001;//请求成功
    int REQUEST_WAS_ABORTED = 100002;//请求失败
    int PARAMETES_EMPTY = 100003;//参数为空
    int TOKEN_INVALID = 100004;//token失效
}
