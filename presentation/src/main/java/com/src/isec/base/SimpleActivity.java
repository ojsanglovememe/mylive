package com.src.isec.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.src.isec.R;
import com.src.isec.di.Injectable;
import com.src.isec.integration.lifecycle.ActivityLifecycleable;
import com.src.isec.utils.ToastUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bingoogolapple.titlebar.BGATitleBar;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.base
 * @class 简易Activity基类
 * 使用场景为非MVP模式
 * @time 2018/4/12 9:48
 * @change
 * @chang time
 * @class describe
 */
public abstract class SimpleActivity extends AppCompatActivity implements
        ActivityLifecycleable,HasSupportFragmentInjector,Injectable {

    protected String TAG ;
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Unbinder mUnbinder;

    protected Context mContext;
    protected ImmersionBar mImmersionBar;


    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    /**
     * @author liujiancheng
     * @time 2018/3/19  16:03
     * @describe 用于Rxjava绑定生命周期
     */
    @NonNull
    @Override
    public Subject<ActivityEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState){
        TAG = getClass().getSimpleName();
        mContext = this;
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        //初始化沉浸式
        if (isImmersionBarEnabled()){
            initImmersionBar();
        }
        initialize(savedInstanceState);
    }

    protected void initImmersionBar() {

        View topView = findViewById(R.id.title_top_view);
        if(topView == null){
            //在BaseActivity里初始化
            mImmersionBar = ImmersionBar.with(this);
            mImmersionBar.init();
        } else {
            mImmersionBar = ImmersionBar.with(this);
            mImmersionBar.statusBarView(topView).init();
        }

    }

    /**
     * @author liujiancheng
     * @time 2018/3/19  16:03
     * @describe 初始化 View
     */

    protected abstract @LayoutRes
    int getLayoutId();

    /**
     * @author huxiangliang
     * @time 2018/4/4  17:02
     * @describe 注入页面多状态布局（content，loading，empty，error）
     */
    protected View injectStateView(){
        return null;
    }

    /**
     * @author liujiancheng
     * @time 2018/3/19  16:02
     * @describe 初始化数据
     * 等同onCreate()
     */
    protected abstract void initialize(@Nullable Bundle savedInstanceState);

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }


    /**
     * @return
     * @author liujiancheng
     * @time 2018/3/19  16:00
     * @describe 这个 Activity 是否会使用 Fragment,框架会根据这个属性判断是否注册
     * {@link FragmentManager.FragmentLifecycleCallbacks}
     * 如果返回{@code false},那意味着这个 Activity 不需要绑定 Fragment,那你再在这个 Activity 中绑定继承于
     * {@link BaseFragment} 的 Fragment 将不起任何作用
     * @see com.src.isec.integration.ActivityLifecycle Fragment生命周期的注册过程
     */
    public boolean useFragment() {

        return true;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
        if (mImmersionBar != null){
            mImmersionBar.destroy();
        }
    }


    /**
     * @author liujiancheng
     * @time 2018/3/20  10:46
     * @describe 当前Activity是否需要自定义Toolbart
     * 默认为flase
     */
    public boolean isCustomToolbar() {


        return false;
    }


    public void startActivity(Class<? extends BaseActivity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
    }

    public void showToast(@StringRes int resid) {
        ToastUtil.show(resid);
    }

    public void showToast(String msg) {
        ToastUtil.show(msg);
    }

    public void setActivityTitle(@StringRes int titleResId) {
        setActivityTitle(getString(titleResId));
    }

    public void setActivityTitle(String title) {
        BGATitleBar titleBar = (BGATitleBar) findViewById(R.id.tb_title_bar);
        if (null == titleBar) {
            return;
        }
        titleBar.setLeftDrawable(R.drawable.ic_btn_back);
        titleBar.setTitleText(title);
        titleBar.setDelegate(new SimpleActivity.TitleBarOnClickListener());
    }

    public void setActivityRightTitle(String title, String rightTitle, View.OnClickListener listener) {
        BGATitleBar titleBar = (BGATitleBar) findViewById(R.id.tb_title_bar);
        if (null == titleBar) {
            return;
        }
        titleBar.setLeftDrawable(R.drawable.ic_btn_back);
        titleBar.setTitleText(title);
        titleBar.setRightText(rightTitle);
        titleBar.setDelegate(new TitleBarOnClickListener(titleBar, listener));
    }

    public void setActivityRightTitle(@StringRes int titleResId, @StringRes int rightTextResId, View.OnClickListener listener) {
        setActivityRightTitle(getString(titleResId), getString(rightTextResId), listener);
    }

    private class TitleBarOnClickListener implements BGATitleBar.Delegate {

        private BGATitleBar titleBar;
        private View.OnClickListener rightListener;
        private View.OnClickListener rightListenerSec;

        public TitleBarOnClickListener() {
        }

        public TitleBarOnClickListener(BGATitleBar titleBar, View.OnClickListener rightListener) {
            this.titleBar = titleBar;
            this.rightListener = rightListener;
        }

        public TitleBarOnClickListener(BGATitleBar titleBar, View.OnClickListener rightListener, View.OnClickListener rightListenerSec) {
            this.titleBar = titleBar;
            this.rightListener = rightListener;
            this.rightListenerSec = rightListenerSec;
        }

        @Override
        public void onClickLeftCtv() {
            onBackPressed();
        }

        @Override
        public void onClickTitleCtv() {

        }

        @Override
        public void onClickRightCtv() {
            if (this.rightListener == null || this.titleBar == null) {
                return;
            }
            this.rightListener.onClick(titleBar.getRightCtv());
        }

        @Override
        public void onClickRightSecondaryCtv() {
            if (this.rightListenerSec == null || this.titleBar == null) {
                return;
            }
            this.rightListenerSec.onClick(titleBar.getRightSecondaryCtv());
        }
    }


}
