package com.src.isec.data.exception.parser;

import com.src.isec.data.exception.utils.ErrorIntDef;

import retrofit2.HttpException;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception.parser
 * @class 服务端异常的解析
 * @time 2018/3/26 11:54
 * @change
 * @chang time
 * @class describe
 */
class ServerExceptionParser extends ExceptionParser {

    @Override
    protected boolean handler(Throwable e, IHandler handler) {
        if (e != null) {
            if (HttpException.class.isAssignableFrom(e.getClass())) {
                handler.onHandler(ErrorIntDef.SERVER, getMessageFromThrowable(ErrorIntDef.SERVER,
                        e));
                return true;
            }
        }

        return false;
    }
}
