package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.mvp.presenter.implement.LoginModePresenter;
import com.src.isec.mvp.view.ILoginModeView;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.LoginEvent;
import com.src.isec.utils.ToastUtil;
import com.src.isec.utils.shareutils.LoginUtil;
import com.src.isec.utils.shareutils.login.LoginListener;
import com.src.isec.utils.shareutils.login.LoginPlatform;
import com.src.isec.utils.shareutils.login.LoginResult;
import com.src.isec.utils.shareutils.login.result.BaseToken;
import com.src.isec.utils.shareutils.login.result.BaseUser;

import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 登录选择
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */
public class LoginModeActivity extends BaseActivity<LoginModePresenter> implements ILoginModeView {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_mode;
    }

    @Override
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.transparentBar().init();
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initListener() {
        super.initListener();
        RxBus.getInstance().register(LoginEvent.class, new Consumer<LoginEvent>() {
            @Override
            public void accept(LoginEvent loginEvent) throws Exception {
                finish();
            }
        }, this);
    }

    @OnClick({R.id.btn_wechat, R.id.btn_qq, R.id.btn_weibo, R.id.btn_phone})
    public void onClickEvent(View v){
        switch (v.getId()){
            case R.id.btn_wechat:
                break;
            case R.id.btn_qq:
                thirdLogin(LoginPlatform.QQ, "qq");
                break;
            case R.id.btn_weibo:
//                thirdLogin(LoginPlatform.WEIBO);
                break;
            case R.id.btn_phone:
                startActivity(LoginActivity.class);
                break;

        }
    }

    private void thirdLogin(final @LoginPlatform.Platform int platform, String type) {
        LoginUtil.login(this, platform, new LoginListener() {
            @Override
            public void loginSuccess(LoginResult result) {
                if(result == null){
                    ToastUtil.show(R.string.toast_login_fail);
                    return;
                }
//                String json = new Gson().toJson(result);
//                ToastUtil.show(json);
                BaseToken baseToken = result.getToken();
                BaseUser userInfo = result.getUserInfo();
                String openId = baseToken == null ? "" : baseToken.getOpenid();
                String nickname = userInfo == null ? "" : userInfo.getNickname();
                String avatar = userInfo == null ? "" : userInfo.getHeadImageUrlLarge();

                mPresenter.thirdLogin(type, openId);
            }

            @Override
            public void loginFailure(Exception e) {
                ToastUtil.show(R.string.toast_login_fail);
            }

            @Override
            public void loginCancel() {
                ToastUtil.show(R.string.toast_login_cancel);
            }
        });
    }

    @Override
    public void onLoginSuccess(UserEntity userEntity) {
        if(userEntity == null){
            return;
        }
        UserInfoManager.getInstance().loadUserInfo(userEntity);
        RxBus.getInstance().send(new LoginEvent());
        startActivity(MainActivity.class);
        finish();
    }
}
