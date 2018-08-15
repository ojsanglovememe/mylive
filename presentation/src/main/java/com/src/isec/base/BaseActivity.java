package com.src.isec.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.nukc.stateview.StateView;
import com.gyf.barlibrary.ImmersionBar;
import com.src.isec.R;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.Injectable;
import com.src.isec.integration.lifecycle.ActivityLifecycleable;
import com.src.isec.mvp.presenter.IPresenter;
import com.src.isec.mvp.view.IView;
import com.src.isec.utils.ToastUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Calendar;

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
 * @class Activity基类
 * 完成presenter的注入、butterknife的绑定以及RxLifecycle的生命周期绑定
 * @time 2018/3/16 17:59
 * @change
 * @chang time
 * @class describe
 */

public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements
        ActivityLifecycleable, HasSupportFragmentInjector, Injectable, IView {

    protected String TAG;
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Unbinder mUnbinder;

    protected Activity mActivity;
    protected Context mContext;
    protected StateView mStateView;
    protected ImmersionBar mImmersionBar;

    /**
     * 网络加载等待框
     */
    private MaterialDialog mMaterialDialog;

    @Inject
    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null

    @Inject
    DispatchingAndroidInjector<android.support.v4.app.Fragment> fragmentInjector;

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

    /***
     * 分页页数
     */
    protected int page = 1;
    /***
     * 分页专用时间戳
     */
    protected long pageTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        TAG = getClass().getSimpleName();
        mActivity = this;
        mContext = this;
        setContentView(getLayoutId());
        pageTime = getPageTime();
        mUnbinder = ButterKnife.bind(this);
        //初始化沉浸式
        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }
        mStateView = injectStateView() != null ? StateView.inject(injectStateView()) : StateView.inject(this);
        if (injectStateView() != null) {
            mStateView.setRetryResource(R.layout.layout_error_view);
            mStateView.setEmptyResource(R.layout.layout_error_no_network);
        }
        if (mPresenter != null && this instanceof IView) {
            mPresenter.attachViewAndContext(this, this);
        }
        initialize(savedInstanceState);
        initData();
        initListener();
    }

    protected void initImmersionBar() {
        View topView = findViewById(R.id.title_top_view);
        if (topView == null) {
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
    protected View injectStateView() {
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

    public void initData() {
    }

    public void initListener() {
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
    public AndroidInjector<android.support.v4.app.Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
        if (mPresenter != null) {
            mPresenter.onDestroy();//释放资源
        }
        this.mPresenter = null;

        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    /***
     * 分页获取当前时间戳
     * @return
     */
    public long getPageTime(){
        return Calendar.getInstance().getTime().getTime();
    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtil.show(message);
    }

    public void showToast(@StringRes int resid) {
        ToastUtil.show(resid);
    }

    public void showToast(String msg) {
        ToastUtil.show(msg);
    }

    public void startActivity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
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
        titleBar.setDelegate(new TitleBarOnClickListener());
    }

    public void setActivityRightTitle(String title, String rightTitle, View.OnClickListener
            listener) {
        BGATitleBar titleBar = (BGATitleBar) findViewById(R.id.tb_title_bar);
        if (null == titleBar) {
            return;
        }
        titleBar.setLeftDrawable(R.drawable.ic_btn_back);
        titleBar.setTitleText(title);
        titleBar.setRightText(rightTitle);
        titleBar.setDelegate(new TitleBarOnClickListener(titleBar, listener));
    }

    public void setActivityRightTitle(@StringRes int titleResId, @StringRes int rightTextResId,
                                      View.OnClickListener listener) {
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

        public TitleBarOnClickListener(BGATitleBar titleBar, View.OnClickListener rightListener,
                                       View.OnClickListener rightListenerSec) {
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

    public boolean isLogin() {
        return UserInfoManager.getInstance().isLogin();
    }

    @Override
    public void showLoading() {
        if (mMaterialDialog == null) {
            mMaterialDialog = new MaterialDialog.Builder(this).progress(true, 0).build();
        }
        mMaterialDialog.setContent("");
        mMaterialDialog.show();

    }


    @Override
    public void showLoading(String hint) {
        if (mMaterialDialog == null) {
            mMaterialDialog = new MaterialDialog.Builder(this).progress(true, 0).build();
        }
        mMaterialDialog.setContent(hint);
        mMaterialDialog.show();
    }


    @Override
    public void hideLoading() {
        if (mMaterialDialog != null)
            mMaterialDialog.dismiss();
    }


    @Override
    public void showStateViewLoading() {
        if (mStateView != null)
            mStateView.showLoading();
    }


    @Override
    public void showStateViewContent() {
        if (mStateView != null)
            mStateView.showContent();
    }


    @Override
    public void handleNoNetWork(String message) {

        if (mStateView != null) {
            // TODO: 2018/4/19 设置无网络的layout布局
            mStateView.showRetry();
        }

    }

    @Override
    public void handleNetWorkError(String message) {
        if (mStateView != null) {
            // TODO: 2018/4/19 设置网络异常的layout布局
            mStateView.showRetry();
        }
    }

    @Override
    public void handleCurrencyError(String message) {
        if (mStateView != null) {
            // TODO: 2018/4/19 设置未知异常的layout布局
            mStateView.showRetry();
        }
    }


    @Override
    public void handleTokenInvalid(String message) {
        // TODO: 2018/4/19 token失效后统一挑战login登录页面
    }
}
