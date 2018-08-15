package com.src.isec.data.network;

import com.google.gson.Gson;
import com.src.isec.data.utils.SignRequestUtil;

import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.network
 * @class 通过拦截器统一对接口入参进行签名
 * @time 2018/4/17 9:33
 * @change
 * @chang time
 * @class describe
 */
public class SignRequestInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        //获取到方法
        String method = original.method();

        String time = String.valueOf(System.currentTimeMillis() / 1000);

        //get请求的封装
        if (method.equals("GET") || method.equals("PUT")) {
            //获取到请求地址api
            HttpUrl originalHttpUrl = original.url();
            //通过请求地址(最初始的请求地址)获取到参数列表
            Set<String> parameterNames = originalHttpUrl.queryParameterNames();
            TreeMap<String, String> treeMap = new TreeMap<>();
            for (String key : parameterNames) {
                treeMap.put(key, originalHttpUrl.queryParameter(key));
            }
            treeMap.put("appKey", SignRequestUtil.SIGNKEY);
            treeMap.put("t", time);
            String sign = SignRequestUtil.signRequest(treeMap, "utf-8");
            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("t", time)
                    .addQueryParameter("sign", sign)
                    .addQueryParameter("signType", SignRequestUtil.SIGNTYPE)
                    .build();
            Request request = original.newBuilder()
                    .url(url).build();
            return chain.proceed(request);


        } else if (method.equals("POST")) {

            RequestBody requestBody = original.body();

            if(requestBody instanceof MultipartBody){
                            return chain.proceed(original);
            }else {
                TreeMap<String, String> treeMap;
                Gson gson = new Gson();
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                String oldParamsJson = buffer.readUtf8();
                treeMap = gson.fromJson(oldParamsJson, TreeMap.class);  //原始参数
                treeMap.put("appKey", SignRequestUtil.SIGNKEY);
                treeMap.put("t", time);
                String sign = SignRequestUtil.signRequest(treeMap, "utf-8");
                treeMap.put("sign", sign);
                treeMap.put("signType", SignRequestUtil.SIGNTYPE);
                treeMap.remove("appKey");
                String newJsonParams = gson.toJson(treeMap);  //装换成json字符串
                Request request = original.newBuilder().post(RequestBody.create(MediaType.parse
                        ("application/json; charset=UTF-8"), newJsonParams)).build();
                return chain.proceed(request);
            }







        }

        return chain.proceed(original);
    }
}
