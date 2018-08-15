package com.src.isec.reactivex.rxbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.src.isec.integration.lifecycle.Lifecycleable;
import com.src.isec.utils.RxLifecycleUtils;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.reactivex
 * @class RxJava实现的事件总线, 基于RxRelay
 * @time 2018/4/19 16:59
 * @change
 * @chang time
 * @class describe
 */
public enum RxBus {

    INSTANCE;
    private Relay<Object> bus;


    //禁用构造方法
    RxBus() {
        bus = PublishRelay.create().toSerialized();
    }

    /**
     * @author liujiancheng
     * @time 2018/4/19  17:11
     * @describe 获取RxBus单例
     */
    public static RxBus getInstance() {
        return RxBus.INSTANCE;
    }

    /**
     * @author liujiancheng
     * @time 2018/4/19  17:07
     * @describe 发送事件
     */
    public void send(Object event) {
        bus.accept(event);
    }

    /**
     * @author liujiancheng
     * @time 2018/4/19  17:06
     * @describe 根据class类型生成接收事件的被观察者
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }


    public boolean hasObservers() {
        return bus.hasObservers();
    }

    /**
     * @author liujiancheng
     * @time 2018/4/19  17:08
     * @describe 一般情况下使用此注册事件方式，默认接收事件线程为主线程
     */
    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Lifecycleable
            lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                   Lifecycleable lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn
                        (scheduler).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                   Consumer onError,
                                   Action onComplete, Consumer onSubscribe, Lifecycleable
                                           lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn
                        (scheduler).subscribe(onNext, onError,
                        onComplete, onSubscribe);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                   Consumer onError,
                                   Action onComplete, Lifecycleable lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn(scheduler).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                   Consumer onError, Lifecycleable lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn(scheduler).subscribe(onNext, onError);
    }


    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError,
                                   Action onComplete, Consumer onSubscribe, Lifecycleable
                                           lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn(AndroidSchedulers.mainThread()).subscribe
                        (onNext, onError, onComplete, onSubscribe);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError,
                                   Action onComplete, Lifecycleable lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn(AndroidSchedulers.mainThread()).subscribe
                        (onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError,
                                   Lifecycleable lifecycleable) {
        return toObservable(eventType).compose(RxLifecycleUtils.bindToLifecycle(lifecycleable))
                .observeOn(AndroidSchedulers.mainThread()).subscribe
                        (onNext, onError);
    }


}
