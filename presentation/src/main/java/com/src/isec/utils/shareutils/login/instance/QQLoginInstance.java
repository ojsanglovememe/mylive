package com.src.isec.utils.shareutils.login.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.src.isec.utils.shareutils.ShareLogger;
import com.src.isec.utils.shareutils.ShareManager;
import com.src.isec.utils.shareutils.login.LoginListener;
import com.src.isec.utils.shareutils.login.LoginPlatform;
import com.src.isec.utils.shareutils.login.LoginResult;
import com.src.isec.utils.shareutils.login.result.BaseToken;
import com.src.isec.utils.shareutils.login.result.QQToken;
import com.src.isec.utils.shareutils.login.result.QQUser;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by shaohui on 2016/12/1.
 */

public class QQLoginInstance extends LoginInstance {

    private static final String SCOPE = "get_simple_userinfo";

    private static final String URL = "https://graph.qq.com/user/get_user_info";

    private Tencent mTencent;

    private IUiListener mIUiListener;

    private LoginListener mLoginListener;

    public QQLoginInstance(Activity activity, final LoginListener listener,
                           final boolean fetchUserInfo) {
        super(activity, listener, fetchUserInfo);
        mTencent = Tencent.createInstance(ShareManager.CONFIG.getQqId(),
                activity.getApplicationContext());
        mLoginListener = listener;
        mIUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
               ShareLogger.i(ShareLogger.INFO.QQ_AUTH_SUCCESS);
                try {
                    QQToken token = QQToken.parse((JSONObject) o);
                    if (fetchUserInfo) {
                        listener.beforeFetchUserInfo(token);
                        fetchUserInfo(token);
                    } else {
                        listener.loginSuccess(new LoginResult(LoginPlatform.QQ, token));
                    }
                } catch (JSONException e) {
                   ShareLogger.i(ShareLogger.INFO.ILLEGAL_TOKEN);
                    mLoginListener.loginFailure(e);
                }
            }

            @Override
            public void onError(UiError uiError) {
               ShareLogger.i(ShareLogger.INFO.QQ_LOGIN_ERROR);
                listener.loginFailure(
                        new Exception("QQError: " + uiError.errorCode + uiError.errorDetail));
            }

            @Override
            public void onCancel() {
               ShareLogger.i(ShareLogger.INFO.AUTH_CANCEL);
                listener.loginCancel();
            }
        };
    }

    @Override
    public void doLogin(Activity activity, final LoginListener listener, boolean fetchUserInfo) {
        mTencent.login(activity, SCOPE, mIUiListener);
    }

    @Override
    public void fetchUserInfo(final BaseToken token) {

        Flowable.create(new FlowableOnSubscribe<QQUser>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<QQUser> qqUserEmitter) throws Exception {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(buildUserInfoUrl(token, URL)).build();
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                QQUser user = QQUser.parse(token.getOpenid(), jsonObject);
                qqUserEmitter.onNext(user);
                qqUserEmitter.onComplete();
            }
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<QQUser>() {

                    private Subscription sub;

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        sub = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(QQUser qqUser) {
                        mLoginListener.loginSuccess(new LoginResult(LoginPlatform.QQ, token, qqUser));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginListener.loginFailure(new Exception(e));
                        if(sub != null){
                            sub.cancel();
                        }

                    }

                    @Override
                    public void onComplete() {
                        if(sub != null){
                            sub.cancel();
                        }
                    }
                });

    }

    private String buildUserInfoUrl(BaseToken token, String base) {
        return base
                + "?access_token="
                + token.getAccessToken()
                + "&oauth_consumer_key="
                + ShareManager.CONFIG.getQqId()
                + "&openid="
                + token.getOpenid();
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        Tencent.handleResultData(data, mIUiListener);
    }

    @Override
    public boolean isInstall(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return false;
        }

        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfos) {
            if (TextUtils.equals(info.packageName.toLowerCase(), "com.tencent.mobileqq")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void recycle() {
        mTencent.releaseResource();
        mIUiListener = null;
        mLoginListener = null;
        mTencent = null;
    }
}
