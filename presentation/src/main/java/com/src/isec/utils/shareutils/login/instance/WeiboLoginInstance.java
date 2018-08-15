package com.src.isec.utils.shareutils.login.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.src.isec.utils.shareutils.ShareLogger;
import com.src.isec.utils.shareutils.ShareManager;
import com.src.isec.utils.shareutils.login.LoginListener;
import com.src.isec.utils.shareutils.login.LoginPlatform;
import com.src.isec.utils.shareutils.login.LoginResult;
import com.src.isec.utils.shareutils.login.result.BaseToken;
import com.src.isec.utils.shareutils.login.result.WeiboToken;
import com.src.isec.utils.shareutils.login.result.WeiboUser;

import org.json.JSONObject;
import org.reactivestreams.Subscription;

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

public class WeiboLoginInstance extends LoginInstance {

    private static final String USER_INFO = "https://api.weibo.com/2/users/show.json";

    private SsoHandler mSsoHandler;

    private LoginListener mLoginListener;

    public WeiboLoginInstance(Activity activity, LoginListener listener, boolean fetchUserInfo) {
        super(activity, listener, fetchUserInfo);
        AuthInfo authInfo = new AuthInfo(activity, ShareManager.CONFIG.getWeiboId(),
                ShareManager.CONFIG.getWeiboRedirectUrl(), ShareManager.CONFIG.getWeiboScope());
        mSsoHandler = new SsoHandler(activity, authInfo);
        mLoginListener = listener;
    }

    @Override
    public void doLogin(Activity activity, final LoginListener listener,
                        final boolean fetchUserInfo) {
        mSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
                WeiboToken weiboToken = WeiboToken.parse(accessToken);
                if (fetchUserInfo) {
                    listener.beforeFetchUserInfo(weiboToken);
                    fetchUserInfo(weiboToken);
                } else {
                    listener.loginSuccess(new LoginResult(LoginPlatform.WEIBO, weiboToken));
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                ShareLogger.i(ShareLogger.INFO.WEIBO_AUTH_ERROR);
                listener.loginFailure(e);
            }

            @Override
            public void onCancel() {
                ShareLogger.i(ShareLogger.INFO.AUTH_CANCEL);
                listener.loginCancel();
            }
        });
    }

    @Override
    public void fetchUserInfo(final BaseToken token) {

        Flowable.create(new FlowableOnSubscribe<WeiboUser>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<WeiboUser> weiboUserEmitter) throws Exception {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(buildUserInfoUrl(token, USER_INFO)).build();
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                WeiboUser user = WeiboUser.parse(jsonObject);
                weiboUserEmitter.onNext(user);
                weiboUserEmitter.onComplete();
            }
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<WeiboUser>() {

                    private Subscription s;

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        this.s = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(WeiboUser weiboUserEmitter) {
                        mLoginListener.loginSuccess(new LoginResult(LoginPlatform.WEIBO, token, weiboUserEmitter));

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginListener.loginFailure(new Exception(e));
                        if (this.s != null) {
                            this.s.cancel();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (this.s != null) {
                            this.s.cancel();
                        }
                    }
                });

    }

    private String buildUserInfoUrl(BaseToken token, String baseUrl) {
        return baseUrl + "?access_token=" + token.getAccessToken() + "&uid=" + token.getOpenid();
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    public boolean isInstall(Context context) {
        IWeiboShareAPI shareAPI =
                WeiboShareSDK.createWeiboAPI(context, ShareManager.CONFIG.getWeiboId());
        return shareAPI.isWeiboAppInstalled();
    }

    @Override
    public void recycle() {
        mSsoHandler = null;
        mLoginListener = null;
    }
}
