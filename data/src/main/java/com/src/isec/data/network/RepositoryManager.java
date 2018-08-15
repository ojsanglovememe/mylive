package com.src.isec.data.network;

import com.src.isec.data.cache.Cache;
import com.src.isec.data.cache.CacheType;
import com.src.isec.domain.utils.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import retrofit2.Retrofit;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.integration
 * @class 资源管理的实现
 * @time 2018/3/19 14:01
 * @change
 * @chang time
 * @class describe
 */
@Singleton
public class RepositoryManager implements IRepositoryManager {

    @Inject
    Lazy<Retrofit> mRetrofit;

    @Inject
    Cache.Factory mCachefactory;
    private Cache<String, Object> mRetrofitServiceCache;

    @Inject
    public RepositoryManager() {
    }

    @Override
    public synchronized <T> T obtainRetrofitService(Class<T> service) {

        if (mRetrofitServiceCache == null)
            mRetrofitServiceCache = mCachefactory.build(CacheType.RETROFIT_SERVICE_CACHE);
        Preconditions.checkNotNull(mRetrofitServiceCache, "Cannot return null from a Cache" +
                ".Factory#build(int) method");
        T retrofitService = (T) mRetrofitServiceCache.get(service.getCanonicalName());
        if (retrofitService == null) {
            retrofitService = mRetrofit.get().create(service);
            mRetrofitServiceCache.put(service.getCanonicalName(), retrofitService);
        }
        return retrofitService;


    }


}
