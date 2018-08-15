package com.src.isec.data.exception.parser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.exceptions.CompositeException;


/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.exception.parser
 * @class 解析异常的执行类
 * @time 2018/3/26 11:26
 * @change
 * @chang time
 * @class describe
 */
public class ExceptionParseMgr {
    //当前执行类的单例对象
    private volatile static ExceptionParseMgr mInstance;

    //存放责任链中匹配的异常解析实例
    private List<ExceptionParser> parsers = new ArrayList<>();

    //是否只回调最后一个异常
    private boolean isOnlyOneCallback = true;


    public static ExceptionParseMgr Instance() {

        if (mInstance == null) {

            synchronized (ExceptionParseMgr.class) {


                if (mInstance == null) {

                    mInstance = new ExceptionParseMgr();
                }
            }

        }


        return mInstance;

    }


    ExceptionParseMgr() {

        resetParses();
        connectionParse();
    }

    /**
     * @author liujiancheng
     * @time 2018/3/26  11:30
     * @describe 重置异常责任链
     */
    private void resetParses() {
        parsers.clear();
        parsers.add(new NoNetWorkExceptionParser());
        parsers.add(new NetExceptionParser());
        parsers.add(new ServerExceptionParser());
        parsers.add(new TokenInvalidExceptionParser());
        parsers.add(new InternalExceptionParser());
        parsers.add(new RequestFailExceptionParser());
        parsers.add(new UnknowExceptionParser());//last item must be UnknowExceptionParser
    }

    /**
     * @author liujiancheng
     * @time 2018/3/26  11:30
     * @describe 配置异常责任链
     */
    private void connectionParse() {
        for (int i = 0; i < parsers.size(); i++) {

            final ExceptionParser item = parsers.get(i);

            if (i + 1 < parsers.size()) {
                item.setNextParser(parsers.get(i + 1));
            }
        }
    }

    /**
     * @author liujiancheng
     * @time 2018/3/26  11:32
     * @describe 解析异常
     */
    public void parseException(Throwable e, ExceptionParser.IHandler iHandler) {
        //对类的类型进行判断，如果是Rxjava抛出的异常则开始执行解析
        if (e != null && CompositeException.class.isAssignableFrom(e.getClass())) {
            //因为一个Rxjava是链式调用，可能会抛出多个异常，最终会生成了一个异常列表
            List<Throwable> tEExceptions = ((CompositeException) e).getExceptions();
            if (tEExceptions != null) {
                for (Throwable t : tEExceptions) {
                    parsers.get(0).handleException(t, iHandler);
                    if (isOnlyOneCallback) break;
                }
            }
        } else {
            //如果不是Rxjava抛出的异常，就表示只有一个异常
            parsers.get(0).handleException(e, iHandler);
        }
    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  11:35
     * @describe 如果设为true则仅回调Rxjava抛出的首个异常
     */
    public void isOnlyOneCallback(boolean isOnlyOneCallback) {
        this.isOnlyOneCallback = isOnlyOneCallback;
    }


}
