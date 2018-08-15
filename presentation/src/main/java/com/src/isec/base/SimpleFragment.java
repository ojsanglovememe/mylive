package com.src.isec.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.Injectable;
import com.src.isec.integration.lifecycle.FragmentLifecycleable;
import com.src.isec.utils.ToastUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.base
 * @class 简易Fragment基类
 * 使用场景为非MVP模式
 * @time 2018/3/19 16:01
 * @change
 * @chang time
 * @class describe
 */

public abstract class SimpleFragment extends Fragment implements
        FragmentLifecycleable ,Injectable {

    protected String TAG ;
    private final BehaviorSubject<FragmentEvent> mLifecycleSubject = BehaviorSubject.create();
    private Unbinder mUnbinder;

    protected Context mContext;
    protected Activity mActivity;

    /**
     * 是否对用户可见
     */
    protected boolean mIsVisible;
    /**
     * 是否加载完成
     * 当执行完onViewCreated方法后即为true
     */
    protected boolean mIsPrepare;

    /**
     * 是否加载沉浸栏
     * 当执行完onViewCreated方法后即为true
     */
    protected boolean mIsImmersion;

    protected ImmersionBar mImmersionBar;

    @NonNull
    @Override
    public Subject<FragmentEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getLayoutView() != null) {
            return getLayoutView();
        } else {
            return inflater.inflate(getLayoutId(), container, false);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TAG = getClass().getSimpleName();

        //绑定butterknife
        if (view != null){
            mUnbinder = ButterKnife.bind(this, view);
        }
        if (isLazyLoad()) {
            mIsPrepare = true;
            mIsImmersion = true;
            onLazyLoad();
        } else {
            initData();
            if (isImmersionBarEnabled())
                initImmersionBar();
        }

        initView(view, savedInstanceState);
        getBundle(getArguments());

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }

    /**
     * 是否懒加载
     *
     * @return the boolean
     */
    protected boolean isLazyLoad() {
        return true;
    }

    /**
     * 是否在Fragment使用沉浸式
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 用户可见时执行的操作
     */
    protected void onVisible() {
        onLazyLoad();
    }

    private void onLazyLoad() {
        if (mIsVisible && mIsPrepare) {
            mIsPrepare = false;
            initData();
        }
        if (mIsVisible && mIsImmersion && isImmersionBarEnabled()) {
            initImmersionBar();
        }
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.keyboardEnable(true).navigationBarWithKitkatEnable(false).init();
    }

    /**
     * 用户不可见执行
     */
    protected void onInvisible() {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            try {
                mUnbinder.unbind();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                //fix Bindings already cleared
                Timber.w(TAG + ":onDestroyView: " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        this.mUnbinder = null;

        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }

    public abstract @LayoutRes
    int getLayoutId();

    public View getLayoutView() {
        return null;
    }

    /**
     * @author liujiancheng
     * @time 2018/3/21  9:44
     * @describe 初始化 View
     */
    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    /**
     * @author huxiangliang
     * @time 2018/4/2  11:00
     * @describe 得到Activity传进来的值
     */
    public void getBundle(Bundle bundle) {

    }

    /**
     * @author liujiancheng
     * @time 2018/3/21  9:45
     * @describe Fragment与寄生的容器进行通信的函数
     * 如 Activity 想让自己持有的某个 Fragment 对象执行一些方法,就重写此方法
     * 统一传 {@link Message}, 通过 what 字段来区分不同的方法, 在 {@link #communicationFragment(Message)}
     * 方法中就可以 {@code switch} 做不同的操作, 这样就可以用统一的入口方法做多个不同的操作, 可以起到分发的作用
     * 使用时注意Fragment的生命周期,如果用到Presenter注意Presenter的注入时间，避免空指针
     */
    protected void communicationFragment(Message message) {

    }

    public void showToast(String msg) {
        ToastUtil.show(msg);
    }

    public boolean isLogin(){
        return UserInfoManager.getInstance().isLogin();
    }

    public void startActivity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(mContext, tarActivity);
        startActivity(intent);
    }

}
