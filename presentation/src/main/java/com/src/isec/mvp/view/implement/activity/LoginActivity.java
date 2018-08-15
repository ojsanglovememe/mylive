package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.mvp.presenter.implement.LoginPresenter;
import com.src.isec.mvp.view.ILoginView;
import com.src.isec.mvp.view.IView;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.LoginEvent;
import com.src.isec.utils.IMMgrUtil;
import com.src.isec.utils.RxLifecycleUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 手机号登录
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {


    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_login)
    TextView btnLogin;

    private final int SECOND = 60;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        setActivityTitle(R.string.title_login_phone);
    }

    @Override
    public void initListener() {
        super.initListener();

        RxTextView.textChanges(etPhone).debounce(300, TimeUnit.MILLISECONDS).map(new Function<CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence) throws Exception {
                return RegexUtils.isMobileExact(charSequence);
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) LoginActivity.this))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        tvCode.setEnabled(aBoolean);
                    }
                });

        Observable.combineLatest(RxTextView.textChanges(etPhone), RxTextView.textChanges(etCode),
                new BiFunction<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                        return RegexUtils.isMobileExact(charSequence) && !TextUtils.isEmpty(charSequence2);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) LoginActivity.this))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        btnLogin.setEnabled(aBoolean);
                    }
                });

    }

    @OnClick({R.id.tv_code, R.id.btn_login})
    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.tv_code:
                mPresenter.senCode(SECOND, etPhone.getText().toString().trim(), etCode);
                break;
            case R.id.btn_login:
                String mobile = etPhone.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                mPresenter.phoneLogin(mobile, code);
                break;
        }
    }

    @Override
    public void setEnableSend(boolean isEnable) {
        tvCode.setEnabled(isEnable);
    }

    @Override
    public void setSendTime(String time) {
        tvCode.setText(time);
    }

    @Override
    public void onLoginSuccess(UserEntity userEntity) {
        if (userEntity == null) {
            return;
        }
        UserInfoManager.getInstance().loadUserInfo(userEntity);
        IMMgrUtil.imLogin();
        RxBus.getInstance().send(new LoginEvent());
        startActivity(MainActivity.class);
        finish();
    }

}
