package com.src.isec.integration;

import android.app.Application;
import android.content.Context;

import com.src.isec.base.delegate.AppLifecycles;
import com.src.isec.di.module.GlobalConfigModule;

import java.util.List;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.integration
 * @class 全局信息配置容器
 * 定义配置信息规则
 * @time 2018/3/19 13:32
 * @change
 * @chang time
 * @class describe
 */

public interface ConfigModule {


    /**
     * @author liujiancheng
     * @time 2018/3/19  13:42
     * @describe 配置全局信息和自定义参数的入口
     * @param builder 通过建造者模式设置配置信息
     */
    void applyOptions(Context context, GlobalConfigModule.Builder builder);


    /**
     *  @author liujiancheng
     *  @time 2018/3/19  13:47
     *  @describe  加入自实现的Application代理
     *  @param lifecycles 自定义的{@link AppLifecycles}实现
     *
     */
    void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles);





    /**
     *  @author liujiancheng
     *  @time 2018/3/19  14:47
     *  @describe 使用{@link Application.ActivityLifecycleCallbacks}在Activity的生命周期中注入一些操作
     *  @param lifecycles 自定义的Activity生命周期回调
     */
    void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks>
            lifecycles);
}
