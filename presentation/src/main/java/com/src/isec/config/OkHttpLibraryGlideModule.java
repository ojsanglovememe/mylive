package com.src.isec.config;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.LibraryGlideModule;

import java.io.InputStream;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.config
 * @class 自定义GlideModule
 * 使用okhttp进行Gilde的网络底层库
 * @time 2018/4/12 18:36
 * @change
 * @chang time
 * @class describe
 */
@GlideModule
public class OkHttpLibraryGlideModule extends LibraryGlideModule {


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull
            Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
