package com.src.isec.di.component;

import com.src.isec.base.BaseActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.di.component
 * @class BaseActivity的注入桥梁
 * 每一个继承BaseActivity的Activity，都共享同一个SubComponent
 * @time 2018/3/19 9:05
 * @change
 * @chang time
 * @class describe
 */
@Subcomponent(modules = AndroidInjectionModule.class)
public interface BaseActivityComponent extends AndroidInjector<BaseActivity> {


    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<BaseActivity> {
    }

}
