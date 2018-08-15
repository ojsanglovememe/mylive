package com.src.isec.data.network;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.src.isec.data.utils.UserInfoManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.network
 * @class 通过拦截器全局添加Http请求头
 * @time 2018/4/16 17:52
 * @change
 * @chang time
 * @class describe
 */
public class AddHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = UserInfoManager.getInstance().getTOKEN();
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("XX-Version", AppUtils.getAppVersionName())
                .header("XX-Device-Id", DeviceUtils.getAndroidID())
                .header("XX-Device-Type", "android")
                .header("XX-Token", token)
                .build();

        return chain.proceed(request);
    }
}
