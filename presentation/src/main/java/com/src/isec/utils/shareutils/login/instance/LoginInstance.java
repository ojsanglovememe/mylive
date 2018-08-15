package com.src.isec.utils.shareutils.login.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.src.isec.utils.shareutils.login.LoginListener;
import com.src.isec.utils.shareutils.login.result.BaseToken;


/**
 * Created by shaohui on 2016/12/1.
 */

public abstract class LoginInstance {

    public LoginInstance(Activity activity, LoginListener listener, boolean fetchUserInfo) {

    }

    public abstract void doLogin(Activity activity, LoginListener listener, boolean fetchUserInfo);

    public abstract void fetchUserInfo(BaseToken token);

    public abstract void handleResult(int requestCode, int resultCode, Intent data);

    public abstract boolean isInstall(Context context);

    public abstract void recycle();
}
