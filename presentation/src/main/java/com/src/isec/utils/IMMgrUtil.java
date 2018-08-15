package com.src.isec.utils;

import android.content.Context;

import com.src.isec.BuildConfig;
import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.tencent.TIMCallBack;
import com.tencent.TIMConnListener;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupSettings;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.TIMUserStatusListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


/**
 * @author sunmingchuan
 * @time 2018/4/9 10:53
 */

public class IMMgrUtil {

    //标志位确定SDK是否初始化，避免客户SDK未初始化的情况，实现可重入的init操作
    private static boolean isSDKInit = false;
    private static boolean isLogin = false;
    private static Observable mObservable = null;
    private static int repeatCount = 0;

    /**
     * IMSDK init操作
     * @param context application context
     */
    public static void init(final Context context) {

        if (isSDKInit)
            return;

        TIMManager.getInstance().setLogPrintEnable(BuildConfig.DEBUG);
        //禁用crash上报
        TIMManager.getInstance().disableCrashReport();
        //禁止服务器自动代替上报已读
        TIMManager.getInstance().disableAutoReport();
        TIMManager.getInstance().setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                Timber.d("conn success");
            }

            @Override
            public void onDisconnected(int i, String s) {
                Timber.d("disconn"+i+s);
                isLogin = false;
            }

            @Override
            public void onWifiNeedAuth(String s) {

            }
        });
        //初始化imsdk
        TIMManager.getInstance().init(context);
        //初始化群设置
        TIMManager.getInstance().initGroupSettings(new TIMGroupSettings());
        //注册sig失效监听回调
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                isLogin = false;
                ToastUtil.show("您的账号在另外一台设备上线！");
            }

            @Override
            public void onUserSigExpired() {
                isLogin = false;
            }
        });

        //禁用消息存储
        TIMManager.getInstance().disableStorage();

        isSDKInit = true;
    }

    /**
     * IM登录
     */
    public static void imLogin(){
        if(isLogin) {
            return;
        }
        String userSig = UserInfoManager.getInstance().getUserSig();
        TIMUser user = new TIMUser();
        user.setIdentifier(UserInfoManager.getInstance().getUserId());
        user.setAccountType(Constants.ACCOUNTTYPE);
        user.setAppIdAt3rd(String.valueOf(Constants.SDKAPPID));
        TIMManager.getInstance().login(Constants.SDKAPPID, user, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Timber.d("login failed"+i+s);
                if (repeatCount < Constants.IMLOGIN_COUNT) {
                    repeatCount++;
                    repeatLogin();
                }
            }

            @Override
            public void onSuccess() {
                //成功后重置
                repeatCount = 0;
                isLogin = true;
                mObservable = null;
                setNickName();
                Timber.d("login success");
            }
        });

    }

    /**
     * 登录失败时，延迟30秒尝试登录
     */
    private static void repeatLogin(){
        mObservable = Observable.timer(30, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        imLogin();
                    }
                });

    }

    /**
     * 登录成功后设置昵称
     */
    private static void setNickName(){
        TIMFriendshipManager.getInstance().setNickName(UserInfoManager.getInstance().getNickName(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Timber.d("setNickNameFailed"+i+s);
            }

            @Override
            public void onSuccess() {
                Timber.d("setNickName success");
            }
        });
    }

    /**
     * Application生命周期结束时登出
     */
    public static void logout(){
        if(isLogin) {
            TIMManager.getInstance().logout(new TIMCallBack() {
                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onSuccess() {
                    Timber.d("logout success");
                    isLogin = false;
                }
            });
        }
    }

    public static boolean getIMLoginState(){
        return isLogin;
    }


}
