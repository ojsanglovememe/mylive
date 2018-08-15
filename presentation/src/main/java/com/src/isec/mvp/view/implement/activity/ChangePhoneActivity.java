package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.mvp.presenter.implement.ChangePhonePresenter;
import com.src.isec.mvp.view.IChangePhoneView;
import com.src.isec.mvp.view.IView;
import com.src.isec.utils.RxLifecycleUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 更换手机号，输入旧手机
 * @time 2018/4/18 0010 16:45
 * @change
 * @chang time
 * @class describe
 */

public class ChangePhoneActivity extends BaseActivity<ChangePhonePresenter> implements IChangePhoneView {

    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.tv_btn_step)
    TextView tvBtnStep;

    private final int SECOND = 60;

    private String mobile;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_phone;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        mobile = UserInfoManager.getInstance().getMobile();
        setActivityTitle(R.string.change_binding_phone);
        tvPhone.setText(mobile);
    }

    @Override
    public void initListener() {
        super.initListener();
        RxTextView.textChanges(etCode).debounce(600 , TimeUnit.MILLISECONDS ).map(new Function<CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence) throws Exception {
                return !TextUtils.isEmpty(charSequence);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) ChangePhoneActivity.this))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        tvBtnStep.setEnabled(aBoolean);
                    }
                });
    }

    @OnClick({R.id.tv_code, R.id.tv_btn_step})
    public void onClickEvent(View v){
        switch (v.getId()){
            case R.id.tv_code:
                mPresenter.senCode(SECOND, mobile, etCode);
                break;
            case R.id.tv_btn_step:
                mPresenter.verificationMobile(mobile, etCode.getText().toString().trim());
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
    public void onVerificationSuccess(Object o) {
        startActivity(ChangePhoneSubmitActivity.class);
    }

}
