package com.src.isec.mvp.presenter.implement;


import com.src.isec.R;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.domain.interactor.PhoneLoginUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.ILoginModeView;
import com.src.isec.reactivex.HttpResponseSubscriber;

import javax.inject.Inject;

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
public class LoginModePresenter extends BasePresenter<ILoginModeView> {

    private PhoneLoginUseCase mPhoneLoginUseCase;

    @Inject
    public LoginModePresenter(PhoneLoginUseCase phoneLoginUseCase) {
        super();
        this.mPhoneLoginUseCase = phoneLoginUseCase;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    public void thirdLogin(String type, String openId) {
        addMapSubscription(mPhoneLoginUseCase.execute(PhoneLoginUseCase.builderParams().type(type).openId(openId)),
                new HttpResponseSubscriber<UserEntity>(mContext, mRootView, LoadingTypeIntDef.HANDLE, mContext.getString(R.string.login_loading)) {

                    @Override
                    protected void onCall(UserEntity userEntity) {
                        mRootView.onLoginSuccess(userEntity);
                    }
                });
    }
}
