package com.src.isec.utils.shareutils.login.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.src.isec.utils.shareutils.ShareLogger;
import com.src.isec.utils.shareutils.ShareManager;
import com.src.isec.utils.shareutils.login.LoginListener;
import com.src.isec.utils.shareutils.login.LoginPlatform;
import com.src.isec.utils.shareutils.login.LoginResult;
import com.src.isec.utils.shareutils.login.result.BaseToken;
import com.src.isec.utils.shareutils.login.result.WxToken;
import com.src.isec.utils.shareutils.login.result.WxUser;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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

public class WxLoginInstance extends LoginInstance {

    public static final String SCOPE_USER_INFO = "snsapi_userinfo";
    private static final String SCOPE_BASE = "snsapi_base";

    private static final String BASE_URL = "https://api.weixin.qq.com/sns/";

    private IWXAPI mIWXAPI;

    private LoginListener mLoginListener;

    private OkHttpClient mClient;

    private boolean fetchUserInfo;

    public WxLoginInstance(Activity activity, LoginListener listener, boolean fetchUserInfo) {
        super(activity, listener, fetchUserInfo);
        mLoginListener = listener;
        mIWXAPI = WXAPIFactory.createWXAPI(activity, ShareManager.CONFIG.getWxId());
        mClient = new OkHttpClient();
        this.fetchUserInfo = fetchUserInfo;
    }

    @Override
    public void doLogin(Activity activity, LoginListener listener, boolean fetchUserInfo) {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = SCOPE_USER_INFO;
        req.state = String.valueOf(System.currentTimeMillis());
        mIWXAPI.sendReq(req);
    }

    private void getToken(final String code) {

        Flowable.create(new FlowableOnSubscribe<WxToken>() {

            @Override
            public void subscribe(@NonNull FlowableEmitter<WxToken> wxTokenEmitter) throws Exception {
                Request request = new Request.Builder().url(buildTokenUrl(code)).build();
                Response response = mClient.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                WxToken token = WxToken.parse(jsonObject);
                wxTokenEmitter.onNext(token);
                wxTokenEmitter.onComplete();
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<WxToken>() {

                    private Subscription s;

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        this.s = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(WxToken wxToken) {
                        if (fetchUserInfo) {
                            mLoginListener.beforeFetchUserInfo(wxToken);
                            fetchUserInfo(wxToken);
                        } else {
                            mLoginListener.loginSuccess(new LoginResult(LoginPlatform.WX, wxToken));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginListener.loginFailure(new Exception(e.getMessage()));
                        if(this.s != null){
                            this.s.cancel();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(this.s != null){
                            this.s.cancel();
                        }
                    }
                });


    }

    @Override
    public void fetchUserInfo(final BaseToken token) {

        Flowable.create(new FlowableOnSubscribe<WxUser>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<WxUser> wxUserEmitter) throws Exception {
                Request request = new Request.Builder().url(buildUserInfoUrl(token)).build();
                Response response = mClient.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                WxUser user = WxUser.parse(jsonObject);
                wxUserEmitter.onNext(user);
                wxUserEmitter.onComplete();
            }
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<WxUser>() {
                    private Subscription s;
                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        this.s = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(WxUser wxUser) {
                        mLoginListener.loginSuccess(new LoginResult(LoginPlatform.WX, token, wxUser));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginListener.loginFailure(new Exception(e));
                        if(this.s != null){
                            this.s.cancel();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(this.s != null){
                            this.s.cancel();
                        }
                    }
                });
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        mIWXAPI.handleIntent(data, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {
            }

            @Override
            public void onResp(BaseResp baseResp) {
//                String json = new Gson().toJson(baseResp);
//                Logger.json(json);
                if (baseResp instanceof SendAuth.Resp && baseResp.getType() == 1) {
                    SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                    switch (resp.errCode) {
                        case BaseResp.ErrCode.ERR_OK:
                            getToken(resp.code);
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            mLoginListener.loginCancel();
                            break;
                        case BaseResp.ErrCode.ERR_SENT_FAILED:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_SENT_FAILED));
                            break;
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_UNSUPPORT));
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_AUTH_DENIED));
                            break;
                        default:
                            mLoginListener.loginFailure(new Exception(ShareLogger.INFO.WX_ERR_AUTH_ERROR));
                    }
                }
            }
        });
    }

    @Override
    public boolean isInstall(Context context) {
        return mIWXAPI.isWXAppInstalled();
    }

    @Override
    public void recycle() {
        if (mIWXAPI != null) {
            mIWXAPI.detach();
        }
    }

    private String buildTokenUrl(String code) {
        return BASE_URL
                + "oauth2/access_token?appid="
                + ShareManager.CONFIG.getWxId()
                + "&secret="
                + ShareManager.CONFIG.getWxSecret()
                + "&code="
                + code
                + "&grant_type=authorization_code";
    }

    private String buildUserInfoUrl(BaseToken token) {
        return BASE_URL
                + "userinfo?access_token="
                + token.getAccessToken()
                + "&openid="
                + token.getOpenid();
    }
}
