package com.src.isec.di.module;

import com.src.isec.di.component.BaseFragmentComponent;
import com.src.isec.di.socpe.FragmentScope;
import com.src.isec.mvp.view.implement.fragment.HomeAttentionFragment;
import com.src.isec.mvp.view.implement.fragment.HomeFragment;
import com.src.isec.mvp.view.implement.fragment.HomeHotFragment;
import com.src.isec.mvp.view.implement.fragment.MyFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.di.module
 * @class FragmentModule注入容器的集合
 * 所有Activity的Module可以在此类声明注入,Moudle提供的Provides必须为静态方法
 * @time 2018/4/10 14:15
 * @change
 * @chang time
 * @class describe
 */
@Module(subcomponents = {BaseFragmentComponent.class})
public abstract class AllFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract HomeHotFragment contributeHomeHotFragmentInjector();

    @FragmentScope
    @ContributesAndroidInjector
    abstract HomeAttentionFragment contributeHomeAttentionFragmentInjector();

    @FragmentScope
    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeHomeFragmentInjector();

    @FragmentScope
    @ContributesAndroidInjector
    abstract MyFragment contributeMyFragmentInjector();

}
