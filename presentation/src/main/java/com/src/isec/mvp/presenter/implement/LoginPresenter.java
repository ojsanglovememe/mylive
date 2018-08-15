package com.src.isec.mvp.presenter.implement;


import android.widget.EditText;

import com.src.isec.R;
import com.src.isec.reactivex.HttpResponseFunction;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.domain.entity.VerificationCodeEntity;
import com.src.isec.domain.interactor.PhoneLoginUseCase;
import com.src.isec.domain.interactor.SendCodeUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.ILoginView;
import com.src.isec.reactivex.HttpResponseSubscriber;
import com.src.isec.utils.RxLifecycleUtils;
import com.src.isec.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class 登录选择模式Presenter
 * @time 2018/3/20 16:41
 * @change
 * @chang time
 * @class describe
 */
@ActivityScope
public class LoginPresenter extends BasePresenter<ILoginView> {

    private SendCodeUseCase mSendCodeUseCase;
    private PhoneLoginUseCase mPhoneLoginUseCase;

    @Inject
    public LoginPresenter(SendCodeUseCase sendCodeUseCase, PhoneLoginUseCase phoneLoginUseCase) {
        super();
        mSendCodeUseCase = sendCodeUseCase;
        mPhoneLoginUseCase = phoneLoginUseCase;

    }

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {

    }

    /**
     * @author liujiancheng
     * @time 2018/4/18  17:07
     * @describe 发送验证码
     */
    public void senCode(int second, String mobile, EditText etCode) {
        mRootView.setEnableSend(false);
        mSendCodeUseCase.execute(SendCodeUseCase.builderParams().mobile(mobile))
                .map(new HttpResponseFunction<VerificationCodeEntity>())
                .subscribeOn(Schedulers.io())
                //切换到主线程显示Toast
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<VerificationCodeEntity>() {
                    @Override
                    public void accept(VerificationCodeEntity verificationCodeEntity) throws
                            Exception {
                        etCode.setText(verificationCodeEntity.code);
                        ToastUtil.show(R.string.phone_code_send_success);
                    }
                })
                //切换到工作线程进行计时操作
                .observeOn(Schedulers.io())
                .flatMap(new Function<VerificationCodeEntity,
                        ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(VerificationCodeEntity o) throws Exception {
                        return Observable.interval(1, TimeUnit.SECONDS).take
                                (second);
                    }
                }).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long o) throws Exception {
                return second - (o + 1);
            }
        }).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResponseSubscriber<Long>(mContext) {
                    @Override
                    protected void onCall(Long aLong) {

                        if (aLong == 0) {
                            mRootView.setEnableSend(true);
                            mRootView.setSendTime(mContext.getString(R.string.btn_phone_code));
                        } else {
                            mRootView.setEnableSend(false);
                            mRootView.setSendTime(new StringBuilder(String.valueOf(aLong)).append("S")
                                    .toString());
                        }


                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        //当出现异常时，恢复发送按钮点击状态
                        mRootView.setEnableSend(true);
                        mRootView.setSendTime(mContext.getString(R.string.btn_phone_code));
                    }
        });


    }

    public void phoneLogin(String mobile, String code) {
        addMapSubscription(mPhoneLoginUseCase.execute(PhoneLoginUseCase.builderParams().type("mobile").mobile(mobile).code(code)),
                new HttpResponseSubscriber<UserEntity>(mContext, mRootView, LoadingTypeIntDef.HANDLE, mContext.getString(R.string.login_loading)) {

                    @Override
                    protected void onCall(UserEntity userEntity) {
                        mRootView.onLoginSuccess(userEntity);
                    }
                });
    }
}
