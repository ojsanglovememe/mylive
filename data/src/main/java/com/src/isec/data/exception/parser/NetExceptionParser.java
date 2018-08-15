package com.src.isec.data.exception.parser;

import android.text.TextUtils;

import com.src.isec.data.exception.utils.Cons;
import com.src.isec.data.exception.utils.ErrorIntDef;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception.parser
 * @class 网络连接异常的解析
 * @time 2018/3/26 11:47
 * @change
 * @chang time
 * @class describe
 */
class NetExceptionParser extends ExceptionParser {


    @Override
    protected boolean handler(Throwable e, IHandler handler) {
        if (e != null) {
            if (!TextUtils.isEmpty(e.getMessage())) {
                if (Cons.IO_EXCEPTION.equalsIgnoreCase(e.getMessage()) || Cons.SOCKET_EXCEPTION
                        .equalsIgnoreCase(e.getMessage())) {
                    //取消请求捕获
                    return true;
                }
            }
            if (UnknownHostException.class.isAssignableFrom(e.getClass()) ||
                    SocketException.class.isAssignableFrom(e.getClass()) ||
                    SocketTimeoutException.class.isAssignableFrom(e.getClass())
                    || SSLHandshakeException.class.isAssignableFrom(e.getClass())) {
                handler.onHandler(ErrorIntDef.NETWORK, getMessageFromThrowable(ErrorIntDef
                        .NETWORK, e));
                return true;
            }
        }
        return false;
    }
}

