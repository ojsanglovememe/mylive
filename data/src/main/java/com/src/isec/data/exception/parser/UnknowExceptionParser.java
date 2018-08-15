package com.src.isec.data.exception.parser;


import com.src.isec.data.exception.utils.ErrorIntDef;

class UnknowExceptionParser extends ExceptionParser {

    /**
     * @author liujiancheng
     * @name IsecLive
     * @class name：com.src.isec.data.exception.parser
     * @class 未知异常的解析
     * 这里必须返回true,未知异常时责任链中最后一环
     * @time 2018/3/26 11:47
     * @change
     * @chang time
     * @class describe
     */
    @Override
    protected boolean handler(Throwable e, IHandler handler) {
        handler.onHandler(ErrorIntDef.UNKNOW, getMessageFromThrowable(ErrorIntDef.UNKNOW, e));
        return true;
    }
}
