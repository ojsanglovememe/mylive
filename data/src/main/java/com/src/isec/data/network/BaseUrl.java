package com.src.isec.data.network;

import okhttp3.HttpUrl;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.network
 * @class 针对于 BaseUrl 在 App 启动时不能确定,需要请求服务器接口动态获取的应用场景
 * @time 2018/3/19 17:55
 * @change
 * @chang time
 * @class describe
 */

public interface BaseUrl {
    /**
     * @author liujiancheng
     * @time 2018/3/19  17:55
     * @describe 在调用 Retrofit API 接口之前,使用 Okhttp 或其他方式,请求到正确的 BaseUrl 并通过此方法返回
     */
    HttpUrl url();
}
