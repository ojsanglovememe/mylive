package com.src.isec.data.exception.parser;


import com.src.isec.data.exception.RequestFailException;
import com.src.isec.data.exception.utils.ErrorIntDef;

class RequestFailExceptionParser extends ExceptionParser {

    /**
     * @author liujiancheng
     * @name IsecLive
     * @class name：com.src.isec.data.exception.parser
     * @class 业务异常的解析，如密码错误等
     * @time 2018/3/26 11:47
     * @change
     * @chang time
     * @class describe
     */
    @Override
    protected boolean handler(Throwable e, IHandler handler) {
        if (e != null) {
            if (RequestFailException.class.isAssignableFrom(e.getClass())) {
                handler.onHandler(ErrorIntDef.REQUEST, getMessageFromThrowable(ErrorIntDef.REQUEST,
                        e));
                return true;
            }
        }

        return false;
    }
}
