package com.src.isec.di.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.src.isec.data.network.GlobalHttpHandler;
import com.src.isec.data.network.log.RequestInterceptor;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.di.module
 * @class 第三方库的实例容器
 * @time 2018/3/19 16:28
 * @change
 * @chang time
 * @class describe
 */
@Module
public abstract class ClientModule {


    /**
     * @author liujiancheng
     * @time 2018/3/19  16:34
     * @describe 提供Retrofit实例
     */
    @Singleton
    @Provides
    static Retrofit provideRetrofit(Application application, @Nullable RetrofitConfiguration
            configuration, Retrofit.Builder builder, OkHttpClient client
            , HttpUrl httpUrl, Gson gson) {
        builder.baseUrl(httpUrl)//域名
                .client(client);//设置okhttp
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用 Rxjava
                .addConverterFactory(GsonConverterFactory.create(gson));//使用 Gson
        if (configuration != null)
            configuration.configRetrofit(application, builder);
        return builder.build();
    }

    /**
     * @author liujiancheng
     * @time 2018/3/19  16:38
     * @describe 提供OkHttpClient实例
     */
    @Singleton
    @Provides
    static OkHttpClient provideClient(Application application, @Nullable OkhttpConfiguration
            configuration, OkHttpClient.Builder builder, Interceptor intercept
            ,  @Nullable GlobalHttpHandler handler) {
        builder.addNetworkInterceptor(intercept);

        if (handler != null)
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    return chain.proceed(handler.onHttpRequestBefore(chain, chain.request()));
                }
            });
//        if (interceptors != null) {//如果外部提供了interceptor的集合则遍历添加
//            for (Interceptor interceptor : interceptors) {
//                builder.addInterceptor(interceptor);
//            }
//        }

        if (configuration != null)
            configuration.configOkhttp(application, builder);
        return builder.build();
    }

    @Singleton
    @Provides
    static Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    static OkHttpClient.Builder provideClientBuilder() {
        return new OkHttpClient.Builder();
    }

    @Binds
    abstract Interceptor bindInterceptor(RequestInterceptor interceptor);

    public interface RetrofitConfiguration {
        void configRetrofit(Context context, Retrofit.Builder builder);
    }

    public interface OkhttpConfiguration {
        void configOkhttp(Context context, OkHttpClient.Builder builder);
    }

}
