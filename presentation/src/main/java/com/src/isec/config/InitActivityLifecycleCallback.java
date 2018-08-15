package com.src.isec.config;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import timber.log.Timber;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.config
 * @class Activity生命周期代理实现
 * 装载Toolbar
 * @time 2018/3/20 9:45
 * @change
 * @chang time
 * @class describe
 */

public class InitActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.w(activity.getClass().getSimpleName() + " - onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.w(activity.getClass().getSimpleName() + " - onActivityStarted");
        if (!activity.getIntent().getBooleanExtra("isInitToolbar", false)) {
            //由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
            //而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated
            // 中移动到 onActivityStarted 中执行
            activity.getIntent().putExtra("isInitToolbar", true);
            //这里全局给Activity设置toolbar
//            Toolbar toolbar = activity.findViewById(R.id.toolbar);
//            if (toolbar != null) {
//                if (activity instanceof BaseActivity) {
//                    ((BaseActivity) activity).setToolbar(toolbar);
//                    if (!((BaseActivity) activity).isCustomToolbar()) {
//                        ((BaseActivity) activity).setSupportActionBar(toolbar);
//                        ((BaseActivity) activity).getSupportActionBar()
//                                .setDisplayShowTitleEnabled(false);
//
//                        ToolbarHelper.addMiddleTitle(activity.getApplicationContext(), activity
//                                .getTitle(), toolbar);
//                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                activity.finish();
//                            }
//                        });
//
//                    }
//                } else {
//                    ((AppCompatActivity) activity).setSupportActionBar(toolbar);
//                    ((AppCompatActivity) activity).getSupportActionBar()
//                            .setDisplayShowTitleEnabled(false);
//                }
//            }

        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.w(activity.getClass().getSimpleName() + " - onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.w(activity.getClass().getSimpleName() + " - onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.w(activity.getClass().getSimpleName() + " - onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.w(activity.getClass().getSimpleName() + " - onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.w(activity.getClass().getSimpleName() + " - onActivityDestroyed");
    }
}
