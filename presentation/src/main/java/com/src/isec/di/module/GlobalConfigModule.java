package com.src.isec.di.module;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.src.isec.data.cache.Cache;
import com.src.isec.data.cache.CacheType;
import com.src.isec.data.cache.LruCache;
import com.src.isec.data.network.BaseUrl;
import com.src.isec.data.network.GlobalHttpHandler;
import com.src.isec.data.network.log.DefaultFormatPrinter;
import com.src.isec.data.network.log.FormatPrinter;
import com.src.isec.data.network.log.RequestInterceptor;
import com.src.isec.domain.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.di.module
 * @class 全局配置信息的注入容器
 * 通过建造者模式，向框架注入外包配置的自定义参数
 * @time 2018/3/19 13:36
 * @change
 * @chang time
 * @class describe
 */
@Module
public class GlobalConfigModule {
    private HttpUrl mApiUrl;
    private BaseUrl mBaseUrl;
    private GlobalHttpHandler mHandler;
    private List<Interceptor> mInterceptors;
    private ClientModule.RetrofitConfiguration mRetrofitConfiguration;
    private ClientModule.OkhttpConfiguration mOkhttpConfiguration;
    private AppModule.GsonConfiguration mGsonConfiguration;
    private RequestInterceptor.Level mPrintHttpLogLevel;
    private FormatPrinter mFormatPrinter;
    private Cache.Factory mCacheFactory;


    private GlobalConfigModule(Builder builder) {
        this.mApiUrl = builder.apiUrl;
        this.mBaseUrl = builder.baseUrl;
        this.mHandler = builder.handler;
        this.mInterceptors = builder.interceptors;
        this.mRetrofitConfiguration = builder.retrofitConfiguration;
        this.mOkhttpConfiguration = builder.okhttpConfiguration;
        this.mGsonConfiguration = builder.gsonConfiguration;
        this.mPrintHttpLogLevel = builder.printHttpLogLevel;
        this.mFormatPrinter = builder.formatPrinter;
        this.mCacheFactory = builder.cacheFactory;

    }


    @Singleton
    @Provides
    @Nullable
    List<Interceptor> provideInterceptors() {
        return mInterceptors;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  18:01
     * @describe 提供 BaseUrl
     */
    @Singleton
    @Provides
    HttpUrl provideBaseUrl() {
        if (mBaseUrl != null) {
            HttpUrl httpUrl = mBaseUrl.url();
            if (httpUrl != null) {
                return httpUrl;
            }
        }
        // TODO: 2018/3/19 提供默认的Retrofit url地址
        return mApiUrl == null ? HttpUrl.parse("https://api.github.com/") : mApiUrl;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  18:02
     * @describe 提供处理 Http 请求和响应结果的处理类
     */
    @Singleton
    @Provides
    @Nullable
    GlobalHttpHandler provideGlobalHttpHandler() {
        return mHandler;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  18:03
     * @describe 提供Retrofit自定义信息配置实例
     */
    @Singleton
    @Provides
    @Nullable
    ClientModule.RetrofitConfiguration provideRetrofitConfiguration() {
        return mRetrofitConfiguration;
    }

    /**
     * @author liujiancheng
     * @time 2018/3/19  18:06
     * @describe 提供OkHttp自定义信息配置的实例
     */
    @Singleton
    @Provides
    @Nullable
    ClientModule.OkhttpConfiguration provideOkhttpConfiguration() {
        return mOkhttpConfiguration;
    }

    /**
     * @author liujiancheng
     * @time 2018/3/19  18:07
     * @describe 提供Gson自定义呢信息配置的实例
     */
    @Singleton
    @Provides
    @Nullable
    AppModule.GsonConfiguration provideGsonConfiguration() {
        return mGsonConfiguration;
    }

    /**
     * @author liujiancheng
     * @time 2018/3/19  18:07
     * @describe 提供自定义HttpLog日志打印等级
     */
    @Singleton
    @Provides
    RequestInterceptor.Level providePrintHttpLogLevel() {
        return mPrintHttpLogLevel == null ? RequestInterceptor.Level.ALL : mPrintHttpLogLevel;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  18:08
     * @describe 提供自定义log日志打印样式
     */
    @Singleton
    @Provides
    FormatPrinter provideFormatPrinter() {
        return mFormatPrinter == null ? new DefaultFormatPrinter() : mFormatPrinter;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  18:09
     * @describe 提供Cache工厂实例
     */
    @Singleton
    @Provides
    Cache.Factory provideCacheFactory(Application application) {
        return mCacheFactory == null ? new Cache.Factory() {
            @NonNull
            @Override
            public Cache build(CacheType type) {
                //若想自定义 LruCache 的 size, 或者不想使用 LruCache, 想使用自己自定义的策略
                //并使用 GlobalConfigModule.Builder#cacheFactory() 扩展
                return new LruCache(type.calculateCacheSize(application));
            }
        } : mCacheFactory;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private HttpUrl apiUrl;
        private BaseUrl baseUrl;
        private GlobalHttpHandler handler;
        private List<Interceptor> interceptors;
        private ClientModule.RetrofitConfiguration retrofitConfiguration;
        private ClientModule.OkhttpConfiguration okhttpConfiguration;
        private AppModule.GsonConfiguration gsonConfiguration;
        private RequestInterceptor.Level printHttpLogLevel;
        private FormatPrinter formatPrinter;
        private Cache.Factory cacheFactory;


        private Builder() {
        }

        public Builder baseurl(String baseUrl) {//基础url
            if (TextUtils.isEmpty(baseUrl)) {
                throw new NullPointerException("BaseUrl can not be empty");
            }
            this.apiUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Builder baseurl(BaseUrl baseUrl) {
            this.baseUrl = Preconditions.checkNotNull(baseUrl, BaseUrl.class.getCanonicalName() +
                    "can not be null.");
            return this;
        }


        public Builder globalHttpHandler(GlobalHttpHandler handler) {//用来处理http响应结果
            this.handler = handler;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {//动态添加任意个interceptor
            if (interceptors == null)
                interceptors = new ArrayList<>();
            this.interceptors.add(interceptor);
            return this;
        }


        public Builder retrofitConfiguration(ClientModule.RetrofitConfiguration
                                                     retrofitConfiguration) {
            this.retrofitConfiguration = retrofitConfiguration;
            return this;
        }

        public Builder okhttpConfiguration(ClientModule.OkhttpConfiguration okhttpConfiguration) {
            this.okhttpConfiguration = okhttpConfiguration;
            return this;
        }


        public Builder gsonConfiguration(AppModule.GsonConfiguration gsonConfiguration) {
            this.gsonConfiguration = gsonConfiguration;
            return this;
        }

        public Builder printHttpLogLevel(RequestInterceptor.Level printHttpLogLevel) {//是否让框架打印
            // Http 的请求和响应信息
            this.printHttpLogLevel = Preconditions.checkNotNull(printHttpLogLevel, "The " +
                    "printHttpLogLevel can not be null, use RequestInterceptor.Level.NONE instead" +
                    ".");
            return this;
        }

        public Builder formatPrinter(FormatPrinter formatPrinter) {
            this.formatPrinter = Preconditions.checkNotNull(formatPrinter, FormatPrinter.class
                    .getCanonicalName() + "can not be null.");
            return this;
        }

        public Builder cacheFactory(Cache.Factory cacheFactory) {
            this.cacheFactory = cacheFactory;
            return this;
        }

        public GlobalConfigModule build() {
            return new GlobalConfigModule(this);
        }
    }

}
