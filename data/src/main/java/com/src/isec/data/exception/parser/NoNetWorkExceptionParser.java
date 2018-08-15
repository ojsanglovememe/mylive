package com.src.isec.data.exception.parser;

import com.src.isec.data.exception.NoNetWorkException;
import com.src.isec.data.exception.utils.ErrorIntDef;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception.parser
 * @class 无网络异常解析
 * @time 2018/3/26 14:07
 * @change
 * @chang time
 * @class describe
 */

public class NoNetWorkExceptionParser extends ExceptionParser {


    @Override
    protected boolean handler(Throwable e, IHandler handler) {
        if (e != null) {
            if (
                //进行异常匹配,如果匹配失败则转移到下一个异常类型
                    NoNetWorkException.class.isAssignableFrom(e.getClass())) {
                handler.onHandler(ErrorIntDef.NONETWORK, getMessageFromThrowable(ErrorIntDef
                        .NONETWORK, e));
                return true;
            }
        }
        return false;

    }
}
