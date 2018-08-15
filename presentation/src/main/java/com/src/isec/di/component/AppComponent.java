package com.src.isec.di.component;


import android.app.Application;

import com.google.gson.Gson;
import com.src.isec.data.cache.Cache;
import com.src.isec.data.network.IRepositoryManager;
import com.src.isec.IsecApplication;
import com.src.isec.di.module.AllActivitysModule;
import com.src.isec.di.module.AllFragmentsModule;
import com.src.isec.di.module.AppModule;
import com.src.isec.di.module.ClientModule;
import com.src.isec.di.module.GlobalConfigModule;
import com.src.isec.di.module.RepositoryModule;
import com.src.isec.integration.AppManager;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;
import okhttp3.OkHttpClient;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.di.component
 * @class Component注入桥梁基类
 * 为所有Component提供全局依赖
 * @time 2018/3/19 9:02
 * @change
 * @chang time
 * @class describe
 */
@Singleton
@Component(modules = {AndroidInjectionModule.class, AndroidSupportInjectionModule.class,
        AppModule.class, ClientModule.class, GlobalConfigModule.class, AllActivitysModule.class,
        AllFragmentsModule.class,
        RepositoryModule.class})
public interface AppComponent {
    Application application();

    //用于管理所有 activity
    AppManager appManager();

    //用于管理网络请求层,以及数据缓存层
    IRepositoryManager repositoryManager();

    OkHttpClient okHttpClient();


    //gson
    Gson gson();


    //用于创建框架所需缓存对象的工厂
    Cache.Factory cacheFactory();


    void inject(IsecApplication isecApplication);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        Builder globalConfigModule(GlobalConfigModule globalConfigModule);

        AppComponent build();
    }
}
