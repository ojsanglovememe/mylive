package com.src.isec.integration;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.src.isec.base.BaseActivity;
import com.src.isec.di.Injectable;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.integration
 * @class Activity生命周期回调
 * 用于DaggerAndroid提前注入依赖
 * @time 2018/3/19 9:28
 * @change
 * @chang time
 * @class describe
 */
@Singleton
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {


    @Inject
    public ActivityLifecycle() {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (activity instanceof Injectable)
            handleActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * @param activity
     * @author liujiancheng
     * @time 2018/3/19  12:50
     * @describe 通过Activity生命周期回调，提前介入注入步骤
     * 不需要在BaseActivity中定义注入流程
     */
    private void handleActivity(Activity activity) {

        AndroidInjection.inject(activity);


        boolean useFragment = activity instanceof BaseActivity ? ((BaseActivity) activity)
                .useFragment() : true;

        if (activity instanceof FragmentActivity && useFragment) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(
                            new FragmentManager.FragmentLifecycleCallbacks() {
                                @Override
                                public void onFragmentAttached(FragmentManager fm, Fragment f,
                                                               Context context) {
                                    super.onFragmentAttached(fm, f, context);

                                    if (f instanceof Injectable) {
                                        AndroidSupportInjection.inject(f);
                                    }
                                }
                            }, true);
        }
    }
}
