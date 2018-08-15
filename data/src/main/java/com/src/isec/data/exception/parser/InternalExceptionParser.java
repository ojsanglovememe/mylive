package com.src.isec.data.exception.parser;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.src.isec.data.exception.utils.ErrorIntDef;

import org.json.JSONException;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception.parser
 * @class 序列化异常的解析
 * @time 2018/3/26 11:44
 * @change
 * @chang time
 * @class describe
 */
class InternalExceptionParser extends ExceptionParser {


    @Override
    protected boolean handler(Throwable e, IHandler handler) {
        if (e != null) {
            if (
                //进行异常匹配,如果匹配失败则转移到下一个异常类型
                    NumberFormatException.class.isAssignableFrom(e.getClass()) ||
                            JsonParseException.class.isAssignableFrom(e.getClass()) ||
                            JsonSyntaxException.class.isAssignableFrom(e.getClass()) ||
                            JSONException.class.isAssignableFrom(e.getClass())) {
                handler.onHandler(ErrorIntDef.INTERNAL, getMessageFromThrowable(ErrorIntDef
                        .INTERNAL, e));
                return true;
            }
        }
        return false;
    }


}
