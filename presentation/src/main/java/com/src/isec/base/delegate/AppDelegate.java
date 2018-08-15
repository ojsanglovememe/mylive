package com.src.isec.base.delegate;

import android.app.Application;
import android.content.Context;

import com.src.isec.config.GlobalComponentCallbacks;
import com.src.isec.di.module.GlobalConfigModule;
import com.src.isec.integration.ConfigModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.base.delegate
 * @class Application的生命周期代理实现层
 * 因为 Java 只能单继承
 * 所以当遇到某些三方库需要继承于它的 Application 的时候,就只有自定义 Application 并继承于三方库的 Application
 * 这时就不用再继承 IsecApplication,只用在自定义Application中对应的生命周期调用AppDelegate对应的方法
 * @time 2018/3/19 14:39
 * @change
 * @chang time
 * @class describe
 */
public class AppDelegate implements AppLifecycles {

    private Application mApplication;

    @Inject
    @Named("ActivityLifecycle")
    protected Application.ActivityLifecycleCallbacks mActivityLifecycle;

    @Inject
    @Named("ActivityLifecycleForRxLifecycle")
    protected Application.ActivityLifecycleCallbacks mActivityLifecycleForRxLifecycle;

    private List<AppLifecycles> mAppLifecycles = new ArrayList<>();
    private List<Application.ActivityLifecycleCallbacks> mActivityLifecycles = new ArrayList<>();
    @Inject
    protected GlobalComponentCallbacks mComponentCallback;


    @Inject
    public AppDelegate() {
    }

    /**
     * @author liujiancheng
     * @time 2018/3/20  12:56
     * @describe 注入自定义的App和Activity生命周期
     */
    public void injectLifecycle(Context context, ConfigModule configModule) {

        //将自定义实现的 Application 的生命周期回调 (AppLifecycles) 存入 mAppLifecycles 集合 (此时还未注册回调)
        configModule.injectAppLifecycle(context, mAppLifecycles);

        //将自定义实现的 Activity 的生命周期回调 (ActivityLifecycleCallbacks) 存入 mActivityLifecycles 集合
        // (此时还未注册回调)
        configModule.injectActivityLifecycle(context, mActivityLifecycles);


    }

    /**
     * @author liujiancheng
     * @time 2018/3/19  14:41
     * @describe 代理 attachBaseContext 回调
     */
    @Override
    public void attachBaseContext(Context base) {
        //遍历 mAppLifecycles, 执行所有已注册的 AppLifecycles 的 attachBaseContext()方法
        for (AppLifecycles lifecycle : mAppLifecycles) {
            lifecycle.attachBaseContext(base);
        }
    }

    @Override
    public void onCreate(Application application) {
        this.mApplication = application;
        //注册Activity 生命周期回调 提前进行Activity/Fragment注入
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycle);

        //注册Activity 生命周期回调 绑定RxJava生命周期
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleForRxLifecycle);


        //注册扩展的 Activity 生命周期逻辑
        //每个 ConfigModule 的实现类可以声明多个 Activity 的生命周期回调
        //也可以有 N 个 ConfigModule 的实现类 (支持组件化项目 各个 Module 的各种独特需求)
        for (Application.ActivityLifecycleCallbacks lifecycle : mActivityLifecycles) {
            mApplication.registerActivityLifecycleCallbacks(lifecycle);
        }
        //注册回掉: 内存紧张时释放部分内存
        mApplication.registerComponentCallbacks(mComponentCallback);

        //执行扩展的 App onCreate 逻辑
        for (AppLifecycles lifecycle : mAppLifecycles) {
            lifecycle.onCreate(mApplication);
        }


    }

    @Override
    public void onTerminate(Application application) {
        if (mActivityLifecycle != null) {
            mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycle);
        }
        if (mActivityLifecycleForRxLifecycle != null) {
            mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycleForRxLifecycle);
        }
        if (mComponentCallback != null) {
            mApplication.unregisterComponentCallbacks(mComponentCallback);
        }

        if (mActivityLifecycles != null && mActivityLifecycles.size() > 0) {
            for (Application.ActivityLifecycleCallbacks lifecycle : mActivityLifecycles) {
                mApplication.unregisterActivityLifecycleCallbacks(lifecycle);
            }
        }
        if (mAppLifecycles != null && mAppLifecycles.size() > 0) {
            for (AppLifecycles lifecycle : mAppLifecycles) {
                lifecycle.onTerminate(mApplication);
            }
        }


        this.mActivityLifecycle = null;
        this.mActivityLifecycleForRxLifecycle = null;
        this.mActivityLifecycles = null;
        this.mComponentCallback = null;
        this.mAppLifecycles = null;
        this.mApplication = null;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  15:41
     * @describe 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     */
    private GlobalConfigModule getGlobalConfigModule(Context context, List<ConfigModule> modules) {

        GlobalConfigModule.Builder builder = GlobalConfigModule
                .builder();

        //遍历 ConfigModule 集合, 给全局配置 GlobalConfigModule 添加参数
        for (ConfigModule module : modules) {
            module.applyOptions(context, builder);
        }

        return builder.build();
    }


}
