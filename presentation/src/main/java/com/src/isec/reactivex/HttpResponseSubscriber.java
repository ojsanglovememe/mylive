package com.src.isec.reactivex;


import android.content.Context;
import android.text.TextUtils;

import com.src.isec.data.exception.NoNetWorkException;
import com.src.isec.data.exception.parser.ExceptionParseMgr;
import com.src.isec.data.exception.parser.ExceptionParser;
import com.src.isec.data.exception.utils.ErrorIntDef;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.view.IView;
import com.src.isec.utils.NetWorkUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.reactivex.subscriber
 * @class 订阅网络观察者的基类
 * 定义了异常捕获的基础规则
 * @time 2018/3/24 17:04
 * @change
 * @chang time
 * @class describe
 */

public abstract class HttpResponseSubscriber<T> implements Observer<T> {


    private Context mContext;

    private IView mIView;

    private @LoadingTypeIntDef
    int mLoadingType;
    /**
     * 对话框提示语
     */
    private String mLoadHint;

    public HttpResponseSubscriber(Context context) {
        mContext = context.getApplicationContext();
    }

    public HttpResponseSubscriber(Context context, IView iView) {
        mContext = context.getApplicationContext();
        mIView = iView;
    }

    public HttpResponseSubscriber(Context context, IView iView, @LoadingTypeIntDef int loadingTypeIntDef) {
        mContext = context.getApplicationContext();
        mIView = iView;
        mLoadingType = loadingTypeIntDef;
    }

    public HttpResponseSubscriber(Context context, IView iView, @LoadingTypeIntDef int loadingTypeIntDef, String loadHint) {
        mContext = context.getApplicationContext();
        mIView = iView;
        mLoadingType = loadingTypeIntDef;
        mLoadHint = loadHint;
    }

    @Override
    public void onSubscribe(Disposable d) {
        // 判断网络状态 如果没有连接网络或者网络不可用则取消此次订阅
        if (!NetWorkUtil.isConnected(mContext) || !NetWorkUtil.isAvailable(mContext)) {
            //调用Error回调，传入无网络异常实例
            onError(new NoNetWorkException());
            //取消订阅
            if (!d.isDisposed())
                d.dispose();
            return;
        }

        loadingStatus();
    }


    @Override
    public void onError(Throwable t) {
        Timber.e(t);
        ExceptionParseMgr.Instance().parseException(t, new ExceptionParser.IHandler() {
            @Override
            public void onHandler(int errorIntDef, String message) {

                switch (errorIntDef) {
                    case ErrorIntDef.NONETWORK:
                        onErrorWithNoNetWork(message);
                        break;
                    case ErrorIntDef.NETWORK:
                        onErrorWithNetError(message);
                        break;
                    case ErrorIntDef.INVALID:
                        onErrorWithTokenInvalid(message);
                        break;
                    case ErrorIntDef.REQUEST:
                    case ErrorIntDef.SERVER:
                    case ErrorIntDef.INTERNAL:
                    case ErrorIntDef.UNKNOW:
                        onErrorWithCurrency(message);
                        break;
                }

            }
        });


    }


    @Override
    public void onComplete() {
        resetLoadingStatus();
    }


    @Override
    public void onNext(T t) {
        onCall(t);
    }

    protected abstract void onCall(T t);

    /**
     * @author liujiancheng
     * @time 2018/3/26  15:10
     * @describe 结束请求等待状态
     * 一般此处是隐藏LoadingDialog
     * 子类重写此方法来自定义结束请求等待状态的操作
     */
    protected void resetLoadingStatus() {
        if (mIView != null) {
            switch (mLoadingType) {
                case LoadingTypeIntDef.HANDLE:
                    mIView.hideLoading();
                    break;

                case LoadingTypeIntDef.RENDERING:
                    mIView.showStateViewContent();
                    break;

                case LoadingTypeIntDef.LISTREFRESH:
                    mIView.hideRefreshLoading();
                    break;

                case LoadingTypeIntDef.LISTLOADMORE:
                    mIView.hideRefreshLoadMore();
                    break;
            }
        }

    }

    protected void loadingStatus() {
        if (mIView != null) {
            switch (mLoadingType) {
                case LoadingTypeIntDef.HANDLE:
                    if (TextUtils.isEmpty(mLoadHint)) {
                        mIView.showLoading();
                    } else {
                        mIView.showLoading(mLoadHint);
                    }
                    break;

                case LoadingTypeIntDef.RENDERING:
                    mIView.showStateViewLoading();
                    break;

                case LoadingTypeIntDef.LISTREFRESH:
                    mIView.showRefreshLoading();
                    break;
            }
        }
    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  15:09
     * @describe 当前无网络
     * 子类可重写此方法来自定义无网络状态的处理
     */
    protected void onErrorWithNoNetWork(String message) {

        if (mIView != null) {
            switch (mLoadingType) {
                case LoadingTypeIntDef.HANDLE:
                    mIView.showMessage(message);
                    break;

                case LoadingTypeIntDef.RENDERING:
                    mIView.handleNoNetWork(message);
                    break;

                case LoadingTypeIntDef.LISTREFRESH:
                    mIView.hideRefreshLoading();
                    mIView.showMessage(message);
                    break;

                case LoadingTypeIntDef.LISTLOADMORE:
                    mIView.hideRefreshLoadMore();
                    mIView.showMessage(message);
                    break;
            }
        }


    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  15:20
     * @describe 登录令牌失效
     * 如果当前接口是私有信息，则需要实重写此方法
     * 子类可重写此方法来自定义Token失效状态的处理
     */
    protected void onErrorWithTokenInvalid(String message) {
        if (mIView != null){
            switch (mLoadingType) {
                case LoadingTypeIntDef.HANDLE:
                    mIView.hideLoading();
                    break;

                case LoadingTypeIntDef.RENDERING:
                    mIView.handleNetWorkError(message);
                    break;

                case LoadingTypeIntDef.LISTREFRESH:
                    mIView.hideRefreshLoading();
                    break;

                case LoadingTypeIntDef.LISTLOADMORE:
                    mIView.hideRefreshLoadMore();
                    break;
            }

            mIView.handleTokenInvalid(message);
        }

    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  15:31
     * @describe 网络异常
     * 请求超时、证书错误等状况
     * 子类可重写此方法来自定义网络异常状态的处理
     */
    protected void onErrorWithNetError(String message) {

        if (mIView != null) {
            switch (mLoadingType) {
                case LoadingTypeIntDef.HANDLE:
                    mIView.hideLoading();
                    mIView.showMessage(message);
                    break;

                case LoadingTypeIntDef.RENDERING:
                    mIView.handleNetWorkError(message);
                    break;

                case LoadingTypeIntDef.LISTREFRESH:
                    mIView.hideRefreshLoading();
                    mIView.showMessage(message);
                    break;

                case LoadingTypeIntDef.LISTLOADMORE:
                    mIView.hideRefreshLoadMore();
                    mIView.showMessage(message);
                    break;
            }
        }
    }


    /**
     * @author liujiancheng
     * @time 2018/3/26  15:40
     * @describe 通用异常
     * 解析异常、服务端异常
     * 子类可重写此方法来自定义通用异常状态的处理
     */
    protected void onErrorWithCurrency(String message) {
        if (mIView != null) {
            switch (mLoadingType) {
                case LoadingTypeIntDef.HANDLE:
                    mIView.hideLoading();
                    mIView.showMessage(message);
                    break;

                case LoadingTypeIntDef.RENDERING:
                    mIView.handleCurrencyError(message);
                    break;

                case LoadingTypeIntDef.LISTREFRESH:
                    mIView.hideRefreshLoading();
                    mIView.showMessage(message);
                    break;

                case LoadingTypeIntDef.LISTLOADMORE:
                    mIView.hideRefreshLoadMore();
                    mIView.showMessage(message);
                    break;
            }
        }
    }


}
