package com.src.isec.data.exception;

/**
 * @author huxiangliang
 * @name IsecLive
 * @class name：com.src.isec.data.exception
 * @class 请求服务器失败（返回失败状态码）
 * @time 2018/3/26 14:56
 * @change
 * @chang time
 * @class describe
 */

public class RequestFailException extends RuntimeException {

    public RequestFailException(String message) {
        super(message);
    }

}
