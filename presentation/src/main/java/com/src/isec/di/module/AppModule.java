package com.src.isec.di.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.src.isec.data.network.IRepositoryManager;
import com.src.isec.data.network.RepositoryManager;
import com.src.isec.integration.ActivityLifecycle;
import com.src.isec.integration.lifecycle.ActivityLifecycleForRxLifecycle;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.di.module
 * @class 全局依赖容器
 * @time 2018/3/19 16:15
 * @change
 * @chang time
 * @class describe
 */
@Module
public abstract class AppModule {

    /**
     * @param configuration 定制化Gson接口
     * @author liujiancheng
     * @time 2018/3/19  16:22
     * @describe 提供Gson实例
     */
    @Singleton
    @Provides
    static Gson provideGson(Application application, @Nullable GsonConfiguration configuration) {
        GsonBuilder builder = new GsonBuilder();
        if (configuration != null)
            configuration.configGson(application, builder);
        return builder.create();
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  16:23
     * @describe 提供资源管理实例
     */
    @Binds
    abstract IRepositoryManager bindRepositoryManager(RepositoryManager repositoryManager);

    @Binds
    @Named("ActivityLifecycle")
    abstract Application.ActivityLifecycleCallbacks bindActivityLifecycle(ActivityLifecycle
                                                                                  activityLifecycle);

    @Binds
    @Named("ActivityLifecycleForRxLifecycle")
    abstract Application.ActivityLifecycleCallbacks bindActivityLifecycleForRxLifecycle
            (ActivityLifecycleForRxLifecycle activityLifecycleForRxLifecycle);


    public interface GsonConfiguration {
        void configGson(Context context, GsonBuilder builder);
    }
}
