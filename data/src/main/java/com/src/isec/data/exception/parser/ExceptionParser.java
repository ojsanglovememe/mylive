package com.src.isec.data.exception.parser;


import com.src.isec.data.exception.utils.ErrorIntDef;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception.parser
 * @class 解析异常的基础类
 * 定义了解析异常的基础规则
 * 通过责任链模式进行异常匹配
 * @time 2018/3/26 11:07
 * @change
 * @chang time
 * @class describe
 */
public abstract class ExceptionParser {
    //下一个匹配的异常解析类
    private ExceptionParser nextParser;

    /**
     * @author liujiancheng
     * @time 2018/3/26  11:09
     * @describe 处理和转移异常
     * 如果不匹配则转移到下一个异常类型
     */
    final void handleException(Throwable e, IHandler handler) {
        //every time check [e] or [e.getCause()]
        if (handler(e, handler) || handler(e != null ? e.getCause() : null, handler)) {
            return;
        }
        next(e, handler);
    }

    /**
     * @author liujiancheng
     * @time 2018/3/26  11:21
     * @describe 转移到下一个异常类型
     */
    private void next(Throwable e, IHandler handler) {
        if (getNextParser() != null) getNextParser().handleException(e, handler);
    }

    /**
     * @author liujiancheng
     * @time 2018/3/26  11:21
     * @describe 处理异常
     * 处理不成功则返回false，则转移给下一个异常类型
     * 由子类实现
     */
    protected abstract boolean handler(Throwable e, IHandler handler);

    private ExceptionParser getNextParser() {
        return nextParser;
    }

    final void setNextParser(ExceptionParser nextParser) {
        this.nextParser = nextParser;
    }

    /**
     * @author liujiancheng
     * @time 2018/3/26  11:23
     * @describe 处理异常接口
     * 由实际处理异常的地方传入该接口的实现
     */
    public interface IHandler {
        void onHandler(@ErrorIntDef int errorIntDef, String message);
    }

    /**
     * @param errorIntDef 异常类型
     * @param e           异常实例
     * @author liujiancheng
     * @time 2018/3/26  11:24
     * @describe 定义来自异常的描述信息
     */
    protected String getMessageFromThrowable(@ErrorIntDef int errorIntDef, Throwable e) {

        String message;

        switch (errorIntDef) {
            case ErrorIntDef.NONETWORK:
                message = "没有网络";
                break;
            case ErrorIntDef.NETWORK:
                message = "网络异常";
                break;
            case ErrorIntDef.SERVER:
                message = "服务异常";
                break;
            case ErrorIntDef.INTERNAL:
                message = "解析异常";
                break;
            case ErrorIntDef.INVALID:
                message = e.getMessage();

                break;
            case ErrorIntDef.REQUEST:
                message = e.getMessage();
                break;
            default:
                message = "未知异常";
                break;
        }
        return message;
    }


}
