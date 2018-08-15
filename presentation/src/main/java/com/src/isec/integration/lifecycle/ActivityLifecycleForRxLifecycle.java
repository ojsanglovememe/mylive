package com.src.isec.integration.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.src.isec.base.BaseActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import io.reactivex.subjects.Subject;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.integration.lifecycle
 * @class Activity生命周期回调
 * 用于绑定RxJava生命周期
 * @time 2018/3/19 10:22
 * @change
 * @chang time
 * @class describe
 */
@Singleton
public class ActivityLifecycleForRxLifecycle implements Application.ActivityLifecycleCallbacks {

    @Inject
    Lazy<FragmentLifecycleForRxLifecycle> mFragmentLifecycle;

    @Inject
    public ActivityLifecycleForRxLifecycle() {
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  10:24
     * @describe 在Activity的onCreate回调中注册Fragment的生命周期回调
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.CREATE);
            boolean useFragment = activity instanceof BaseActivity ? ((BaseActivity) activity)
                    .useFragment() : true;

            if (activity instanceof FragmentActivity && useFragment) {
                ((FragmentActivity) activity).getSupportFragmentManager()
                        .registerFragmentLifecycleCallbacks(mFragmentLifecycle.get(), true);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.START);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.RESUME);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.PAUSE);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.STOP);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.DESTROY);
        }
    }

    /**
     * 从 {@link BaseActivity}
     * 中获得桥梁对象 {@code BehaviorSubject<ActivityEvent> mLifecycleSubject}
     */

    /**
     * @author liujiancheng
     * @time 2018/3/19  10:26
     * @describe 从 {@link com.src.isec.base.BaseActivity}
     * 中获得桥梁对象 {@code BehaviorSubject<ActivityEvent> mLifecycleSubject}
     */
    private Subject<ActivityEvent> obtainSubject(Activity activity) {
        return ((ActivityLifecycleable) activity).provideLifecycleSubject();
    }
}
