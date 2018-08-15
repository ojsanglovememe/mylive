package com.src.isec.domain.interactor;

import io.reactivex.Observable;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.domain
 * @class 用例基础类
 * 定义usecase规则
 * @time 2018/3/13 17:58
 * @change
 * @chang time
 * @class describe
 */
public abstract class UseCase<T, Params> {

    /**
     * @author liujiancheng
     * @time 2018/3/21  16:11
     * @describe 构建被观察者
     * 子类中需要实现此方法
     */
    protected abstract Observable<T> buildUseCaseObservable(Params params);

    /**
     * @author liujiancheng
     * @time 2018/3/21  16:35
     * @describe 执行用例/有参
     */
    public Observable<T> execute(Params params) {
        return this.buildUseCaseObservable(params);
    }

    /**
     * @author liujiancheng
     * @time 2018/3/21  16:35
     * @describe 执行用例/无参
     */
    public Observable<T> execute() {
        return this.buildUseCaseObservable(null);
    }




}
