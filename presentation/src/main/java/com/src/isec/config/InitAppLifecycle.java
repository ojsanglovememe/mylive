package com.src.isec.config;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.blankj.utilcode.util.Utils;
import com.squareup.leakcanary.LeakCanary;
import com.src.isec.BuildConfig;
import com.src.isec.base.delegate.AppLifecycles;
import com.src.isec.data.utils.PictureCompressUtil;
import com.src.isec.utils.IMMgrUtil;
import com.src.isec.utils.ToastUtil;
import com.src.isec.utils.shareutils.ShareConfig;
import com.src.isec.utils.shareutils.ShareManager;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.config
 * @class App代理实现
 * 用于第三库的初始化工作以及dex分包策略
 * @time 2018/3/20 9:27
 * @change
 * @chang time
 * @class describe
 */

public class InitAppLifecycle implements AppLifecycles {
    @Override
    public void attachBaseContext(Context base) {
        MultiDex.install(base);  //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化

    }

    @Override
    public void onCreate(Application application) {

        if (BuildConfig.DEBUG) {
            //设置Timber的Debug模式
            Timber.plant(new Timber.DebugTree());
            //设置ButterKnife的Debug模式
            ButterKnife.setDebug(true);
        }

        //LeakCanary初始化
        // LeaKCanary在Gradle中使用的是release和debug分别进行的依赖，所以初始化过程中不用判断是否是debug模式
        if (LeakCanary.isInAnalyzerProcess(application)) {
            //判断是否是监测的进程
            return;
        }
        LeakCanary.install(application);
        com.blankj.utilcode.util.Utils.init(application);
        //初始化全局使用的Toast
        ToastUtil.init(application);
        initShareConfig();
        //初始化IMsdk
        IMMgrUtil.init(application.getApplicationContext());
        //初始化AndroidUtil
        Utils.init(application);
        //初始化图片压缩工具
        PictureCompressUtil.init(application);
    }

    @Override
    public void onTerminate(Application application) {
        //IM登出
        IMMgrUtil.logout();
    }



    /***
     * 初始化第三方分享、登录APP_ID
     */
    private void initShareConfig() {
        ShareConfig config = ShareConfig.instance()
                .qqId(Constants.QQ_APP_ID)
//                .wxId(Constant.WX_APP_ID)
                .weiboId(Constants.WEIBO_ID)
                .weiboRedirectUrl("https://api.weibo.com/oauth2/default.html")
//                .wxSecret(Constant.WX_APP_SECRET)
                .debug(true);
        ShareManager.init(config);
    }
}
