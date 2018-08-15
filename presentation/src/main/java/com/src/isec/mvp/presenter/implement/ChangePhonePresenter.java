package com.src.isec.mvp.presenter.implement;


import android.widget.EditText;

import com.src.isec.R;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.entity.VerificationCodeEntity;
import com.src.isec.domain.interactor.BindingMobileUseCase;
import com.src.isec.domain.interactor.SendCodeUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IChangePhoneView;
import com.src.isec.reactivex.HttpResponseFunction;
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
 * @class 更换手机Presenter
 * @time 2018/3/20 16:41
 * @change
 * @chang time
 * @class describe
 */
@ActivityScope
public class ChangePhonePresenter extends BasePresenter<IChangePhoneView> {

    private SendCodeUseCase mSendCodeUseCase;
    private BindingMobileUseCase mBindingMobileUseCase;

    @Inject
    public ChangePhonePresenter(SendCodeUseCase sendCodeUseCase, BindingMobileUseCase bindingMobileUseCase) {
        super();
        this.mSendCodeUseCase = sendCodeUseCase;
        this.mBindingMobileUseCase = bindingMobileUseCase;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    public void senCode(int second, String mobile, EditText etCode) {
        mRootView.setEnableSend(false);
        mSendCodeUseCase.execute(SendCodeUseCase.builderParams().mobile(mobile))
                .map(new HttpResponseFunction<VerificationCodeEntity>())
                .subscribeOn(Schedulers.io())
                //切换到主线程显示Toast
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<VerificationCodeEntity>() {
                    @Override
                    public void accept(VerificationCodeEntity verificationCodeEntity) throws Exception {
                        etCode.setText(verificationCodeEntity.code);
                        ToastUtil.show(R.string.phone_code_send_success);
                    }
                })
                //切换到工作线程进行计时操作
                .observeOn(Schedulers.io())
                .flatMap(new Function<VerificationCodeEntity, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(VerificationCodeEntity o) throws Exception {
                        return Observable.interval(1, TimeUnit.SECONDS).take(second);
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
                        mRootView.setEnableSend(aLong == 0);
                        mRootView.setSendTime(aLong == 0 ? mContext.getString(R.string.btn_phone_code) : new StringBuilder(String.valueOf(aLong)).append("S").toString());
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

    /***
     * 验证原手机号
     * @param mobile
     * @param code
     */
    public void verificationMobile(String mobile, String code){
        addMapSubscription(mBindingMobileUseCase.execute(BindingMobileUseCase.builderParams().type("check").mobile(mobile).code(code)),
                new HttpResponseSubscriber<Object>(mContext, mRootView, LoadingTypeIntDef.HANDLE){

                    @Override
                    protected void onCall(Object o) {
                        mRootView.onVerificationSuccess(o);
                    }
                });
    }

    public void bindingMobile(String mobile, String code){
        addMapSubscription(mBindingMobileUseCase.execute(BindingMobileUseCase.builderParams().type("bind").mobile(mobile).code(code)),
                new HttpResponseSubscriber<Object>(mContext, mRootView, LoadingTypeIntDef.HANDLE){

                    @Override
                    protected void onCall(Object o) {
                        mRootView.onBindingSuccess(o);
                    }
                });
    }
}
