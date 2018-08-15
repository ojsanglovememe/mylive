package com.src.isec.base.delegate;

import android.app.Application;
import android.content.Context;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.base.delegate
 * @class Application的生命周期代理
 * @time 2018/3/19 13:45
 * @change
 * @chang time
 * @class describe
 */

public interface AppLifecycles {

    void attachBaseContext(Context base);

    void onCreate(Application application);

    void onTerminate(Application application);
}
