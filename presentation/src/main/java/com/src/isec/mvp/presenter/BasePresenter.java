package com.src.isec.mvp.presenter;

import android.content.Context;

import com.src.isec.reactivex.HttpResponseFunction;
import com.src.isec.mvp.view.IView;
import com.src.isec.utils.RxLifecycleUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter
 * @class Presenter基类
 * @time 2018/3/20 15:33
 * @change
 * @chang time
 * @class describe
 */

public abstract class BasePresenter<V extends IView> implements IPresenter<V> {
    protected final String TAG = this.getClass().getSimpleName();

    protected V mRootView;

    protected Context mContext;

    public BasePresenter() {
        onStart();
    }

    @Override
    public void attachViewAndContext(V iView, Context context) {
        mRootView = iView;
        mContext = context;
    }

    public void addMapSubscription(Observable apiObservable, Observer observer) {
        apiObservable.map(new HttpResponseFunction())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
