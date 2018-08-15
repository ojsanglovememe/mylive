package com.src.isec.data.exception;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception
 * @class 登录令牌失效异常
 * @time 2018/3/26 14:56
 * @change
 * @chang time
 * @class describe
 */

public class TokenInvalidException extends RuntimeException {

    public TokenInvalidException(String message) {
        super(message);
    }

}
