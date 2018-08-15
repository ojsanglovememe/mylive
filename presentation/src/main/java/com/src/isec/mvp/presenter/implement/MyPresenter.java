package com.src.isec.mvp.presenter.implement;


import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.FragmentScope;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.domain.interactor.PersonalInfoUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IMyView;
import com.src.isec.reactivex.HttpResponseSubscriber;

import javax.inject.Inject;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class 个人中心Presenter
 * @time 2018/3/20 16:41
 * @change
 * @chang time
 * @class describe
 */
@FragmentScope
public class MyPresenter extends BasePresenter<IMyView> {

    private PersonalInfoUseCase mPersonalInfoUseCase;


    @Inject
    public MyPresenter(PersonalInfoUseCase personalInfoUseCase) {
        super();
        this.mPersonalInfoUseCase = personalInfoUseCase;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    public void onPersonalInfo() {
        addMapSubscription(mPersonalInfoUseCase.execute(PersonalInfoUseCase.builderParams()
                        .onUserId(UserInfoManager.getInstance().getUserId())),
                new HttpResponseSubscriber<UserEntity>(mContext, mRootView, LoadingTypeIntDef.RENDERING) {

                    @Override
                    protected void onCall(UserEntity userEntity) {
                        mRootView.onPersonalInfoSuccess(userEntity);
                    }
                });
    }
}
