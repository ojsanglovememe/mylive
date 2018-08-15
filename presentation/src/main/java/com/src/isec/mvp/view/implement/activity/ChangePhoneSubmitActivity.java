package com.src.isec.mvp.view.implement.activity;

import android.content.Intent;
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
import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.mvp.presenter.implement.ChangePhonePresenter;
import com.src.isec.mvp.view.IChangePhoneView;
import com.src.isec.mvp.view.IView;
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
 * @class 更换手机号，输入新手机
 * @time 2018/4/18 0010 16:45
 * @change
 * @chang time
 * @class describe
 */

public class ChangePhoneSubmitActivity extends BaseActivity<ChangePhonePresenter> implements IChangePhoneView {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.tv_btn_change)
    TextView tvBtnChange;
    @BindView(R.id.tv_change_phone_tips)
    TextView tvChangePhone;

    private final int SECOND = 60;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_phone_submit;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        setActivityTitle(R.string.change_binding_phone);
        tvChangePhone.setVisibility(getIntent().getBooleanExtra(Constants.BINDING_MOBILE, false) ? View.INVISIBLE : View.VISIBLE);
        tvBtnChange.setText(getIntent().getBooleanExtra(Constants.BINDING_MOBILE, false) ? R.string.binding_phone : R.string.btn_change);
    }


    @Override
    public void initListener() {
        super.initListener();

        RxTextView.textChanges(etPhone).debounce(600 , TimeUnit.MILLISECONDS ).map(new Function<CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence) throws Exception {
                return RegexUtils.isMobileExact(charSequence);
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) ChangePhoneSubmitActivity.this))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        tvCode.setEnabled(aBoolean);
                    }
                });

        Observable.combineLatest(RxTextView.textChanges(etPhone), RxTextView.textChanges(etCode), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                return RegexUtils.isMobileExact(charSequence) && !TextUtils.isEmpty(charSequence2);
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) ChangePhoneSubmitActivity.this))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        tvBtnChange.setEnabled(aBoolean);
                    }
                });
    }

    @OnClick({R.id.tv_code, R.id.tv_btn_change})
    public void onClickEvent(View v) {
        switch (v.getId()){
            case R.id.tv_code:
                mPresenter.senCode(SECOND, etPhone.getText().toString().trim(), etCode);
                break;
            case R.id.tv_btn_change:
                mPresenter.bindingMobile(etPhone.getText().toString().trim(), etCode.getText().toString().trim());
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
    public void onBindingSuccess(Object o) {
        showToast(R.string.binding_success);
        UserInfoManager.getInstance().setMobile(etPhone.getText().toString().trim());
        Intent intent = new Intent(mContext, SettingActivity.class);
        intent.putExtra(Constants.LOGIN_MOBILE, etPhone.getText().toString().trim());
        startActivity(intent);
    }
}
