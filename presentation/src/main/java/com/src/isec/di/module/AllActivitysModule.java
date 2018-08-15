package com.src.isec.di.module;


import com.src.isec.di.component.BaseActivityComponent;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.mvp.view.implement.activity.AttentionFansActivity;
import com.src.isec.mvp.view.implement.activity.ChangePhoneActivity;
import com.src.isec.mvp.view.implement.activity.ChangePhoneSubmitActivity;
import com.src.isec.mvp.view.implement.activity.EditNickNameActivity;
import com.src.isec.mvp.view.implement.activity.EditPersonalInfoActivity;
import com.src.isec.mvp.view.implement.activity.LivePlayerActivity;
import com.src.isec.mvp.view.implement.activity.LoginActivity;
import com.src.isec.mvp.view.implement.activity.LoginModeActivity;
import com.src.isec.mvp.view.implement.activity.MainActivity;
import com.src.isec.mvp.view.implement.activity.PublishSettingActivity;
import com.src.isec.mvp.view.implement.activity.PublisherActivity;
import com.src.isec.mvp.view.implement.activity.RealNameAuthActivity;
import com.src.isec.mvp.view.implement.activity.RealNameAuthSubmitActivity;
import com.src.isec.mvp.view.implement.activity.SearchActivity;
import com.src.isec.mvp.view.implement.activity.SettingActivity;
import com.src.isec.mvp.view.implement.activity.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.di.module
 * @class ActivityModule注入容器的集合
 * 所有Activity的Module可以在此类声明注入,Moudle提供的Provides必须为静态方法
 * @time 2018/3/19 9:08
 * @change
 * @chang time
 * @class describe
 */
@Module(subcomponents = {BaseActivityComponent.class})
public abstract class AllActivitysModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract SplashActivity contributeSplashActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract LoginModeActivity contributeLoginModeActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract PublishSettingActivity contributePublishSettingActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract PublisherActivity contributePublishActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract RealNameAuthActivity contributeRealNameAuthActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract LivePlayerActivity contributeLivePlayerActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract RealNameAuthSubmitActivity contributeRealNameAuthSubmitActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract EditPersonalInfoActivity contributeEditPersonalInfoActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract EditNickNameActivity contributeEditNickNameActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract ChangePhoneActivity contributeChangePhoneActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract ChangePhoneSubmitActivity contributeChangePhoneSubmitActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SettingActivity contributeSettingActivityActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract AttentionFansActivity contributeAttentionFansActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SearchActivity contributeSearchActivityInjector();
}
