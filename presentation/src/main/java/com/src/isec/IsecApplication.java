package com.src.isec;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.src.isec.base.delegate.AppDelegate;
import com.src.isec.config.GlobalConfiguration;
import com.src.isec.di.component.DaggerAppComponent;
import com.src.isec.di.module.GlobalConfigModule;
import com.src.isec.integration.ConfigModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.base
 * @class 自定义Application
 * 绑定App代理
 * 注册Activity和Fragment生命周期回调
 * 进行全局配置信息的初始化
 * @time 2018/3/19 9:15
 * @change
 * @chang time
 * @class describe
 */

public class IsecApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    /**
     * 绑定Application生命周期
     */
    @Inject
    AppDelegate mAppDelegate;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //初始化全局信息配置容器
        ConfigModule configModule = new GlobalConfiguration();
        DaggerAppComponent.builder().application(this).globalConfigModule(getGlobalConfigModule
                (this.getBaseContext(), configModule)).build().inject(this);
        mAppDelegate.injectLifecycle(this, configModule);
        mAppDelegate.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppDelegate.onCreate(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        mAppDelegate.onTerminate(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/21  9:03
     * @describe 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     */
    private GlobalConfigModule getGlobalConfigModule(Context context, ConfigModule module) {
        GlobalConfigModule.Builder builder = GlobalConfigModule
                .builder();
        module.applyOptions(context, builder);
        return builder.build();
    }

}
