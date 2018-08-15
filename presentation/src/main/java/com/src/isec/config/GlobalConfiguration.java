package com.src.isec.config;

import android.app.Application;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.src.isec.BuildConfig;
import com.src.isec.base.delegate.AppLifecycles;
import com.src.isec.data.network.AddHeaderInterceptor;
import com.src.isec.data.network.GlobalHttpHandler;
import com.src.isec.data.network.SignRequestInterceptor;
import com.src.isec.data.network.log.RequestInterceptor;
import com.src.isec.di.module.AppModule;
import com.src.isec.di.module.ClientModule;
import com.src.isec.di.module.GlobalConfigModule;
import com.src.isec.integration.ConfigModule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive
 * @class 全局配置信息容器的实现
 * @time 2018/3/20 9:03
 * @change
 * @chang time
 * @class describe
 */

public class GlobalConfiguration implements ConfigModule {
    private static final int TIME_OUT = 10;

    /**
     * @param builder 通过建造者模式设置配置信息
     * @author liujiancheng
     * @time 2018/3/19  13:42
     * @describe 配置全局信息和自定义参数的入口
     */
    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        //Release 时,让框架不再打印 Http 请求和响应的信息
        if (!BuildConfig.DEBUG) {
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE);
        }
        //根据版本状态选择主机地址
        builder.baseurl(BuildConfig.DOMAIN).globalHttpHandler(GlobalHttpHandler.EMPTY)
                .gsonConfiguration(new AppModule.GsonConfiguration() {


                    @Override
                    public void configGson(Context context, GsonBuilder builder) {
                        builder.serializeNulls()//支持序列化null的参数
                                .enableComplexMapKeySerialization();//支持将序列化key为object的map,
                        // 默认只能序列化key为string的map
                    }
                }).retrofitConfiguration(new ClientModule.RetrofitConfiguration() {
            @Override
            public void configRetrofit(Context context, Retrofit.Builder builder) {
                //这里可以自己自定义配置Retrofit的参数,甚至你可以替换系统配置好的okhttp对象


            }
        }).okhttpConfiguration(new ClientModule.OkhttpConfiguration() {
            @Override
            public void configOkhttp(Context context, OkHttpClient.Builder builder) {
                //这里可以自己自定义配置Okhttp的参数
                builder.addInterceptor(new AddHeaderInterceptor()).addInterceptor(new
                        SignRequestInterceptor())
                        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(TIME_OUT, TimeUnit.SECONDS);
            }
        });


    }

    /**
     * @param lifecycles 自定义的{@link AppLifecycles}实现
     * @author liujiancheng
     * @time 2018/3/19  13:47
     * @describe 加入自实现的Application代理
     */
    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {

        lifecycles.add(new InitAppLifecycle());

    }

    /**
     * @param lifecycles 自定义的Activity生命周期回调
     * @author liujiancheng
     * @time 2018/3/19  14:47
     * @describe 使用{@link Application.ActivityLifecycleCallbacks}在Activity的生命周期中注入一些操作
     */
    @Override
    public void injectActivityLifecycle(Context context, List<Application
            .ActivityLifecycleCallbacks> lifecycles) {

        lifecycles.add(new InitActivityLifecycleCallback());


    }
}
