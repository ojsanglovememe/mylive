package com.src.isec.data.exception.parser;

import com.src.isec.data.exception.TokenInvalidException;
import com.src.isec.data.exception.utils.ErrorIntDef;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception.parser
 * @class Token失效异常的解析
 * @time 2018/3/26 15:54
 * @change
 * @chang time
 * @class describe
 */
class TokenInvalidExceptionParser extends ExceptionParser {

    @Override
    protected boolean handler(Throwable e, IHandler handler) {
        if (e != null) {
            if (TokenInvalidException.class.isAssignableFrom(e.getClass())) {
                handler.onHandler(ErrorIntDef.INVALID, getMessageFromThrowable(ErrorIntDef.INVALID,
                        e));
                return true;
            }
        }

        return false;
    }
}
