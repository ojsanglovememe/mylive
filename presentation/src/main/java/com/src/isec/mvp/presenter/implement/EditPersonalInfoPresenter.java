package com.src.isec.mvp.presenter.implement;


import android.net.Uri;
import android.text.TextUtils;

import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.FileEntity;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.domain.interactor.PersonalInfoUseCase;
import com.src.isec.domain.interactor.UpdatePersonalInfoUseCase;
import com.src.isec.domain.interactor.UploadFileUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IEditPersonalInfoView;
import com.src.isec.reactivex.HttpResponseFunction;
import com.src.isec.reactivex.HttpResponseObservable;
import com.src.isec.reactivex.HttpResponseSubscriber;
import com.src.isec.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class 编辑个人资料Presenter
 * @time 2018/3/20 16:41
 * @change
 * @chang time
 * @class describe
 */
@ActivityScope
public class EditPersonalInfoPresenter extends BasePresenter<IEditPersonalInfoView> {

    private UploadFileUseCase mUploadFileUseCase;
    private PersonalInfoUseCase mPersonalInfoUseCase;
    private UpdatePersonalInfoUseCase mUpdatePersonalInfoUseCase;

    @Inject
    public EditPersonalInfoPresenter(PersonalInfoUseCase personalInfoUseCase, UpdatePersonalInfoUseCase updatePersonalInfoUseCase, UploadFileUseCase uploadFileUseCase) {
        super();
        this.mPersonalInfoUseCase = personalInfoUseCase;
        this.mUpdatePersonalInfoUseCase = updatePersonalInfoUseCase;
        this.mUploadFileUseCase = uploadFileUseCase;
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

    public void setNickName(String nickname){
        addMapSubscription(mUpdatePersonalInfoUseCase.execute(UpdatePersonalInfoUseCase.builderParams().nickname(nickname)),
                new HttpResponseSubscriber<Object>(mContext, mRootView, LoadingTypeIntDef.HANDLE){

                    @Override
                    protected void onCall(Object o) {
                        mRootView.onNicknameSuccess(nickname);
                    }
                });
    }

    public void setSex(String sex, String sexStr){
        addMapSubscription(mUpdatePersonalInfoUseCase.execute(UpdatePersonalInfoUseCase.builderParams().sex(sex)),
                new HttpResponseSubscriber<Object>(mContext, mRootView, LoadingTypeIntDef.HANDLE){

                    @Override
                    protected void onCall(Object o) {
                        mRootView.onSexSuccess(sexStr);
                    }
                });
    }

    public void uploadAvatar(Uri uri){

        String path = uri.getPath();

        if(TextUtils.isEmpty(path)){
            return;
        }
        mUploadFileUseCase.execute(UploadFileUseCase.builderParams().onFile(path))
                .flatMap(new HttpResponseObservable<FileEntity, Object>(){

                    @Override
                    public ObservableSource<BaseResponse<Object>> onCall(BaseResponse<FileEntity> fileEntityBaseResponse) {
                        String avatar = fileEntityBaseResponse.getData().getUrl();
                        return mUpdatePersonalInfoUseCase.execute(UpdatePersonalInfoUseCase.builderParams().avatar(avatar));
                    }
                }).map(new HttpResponseFunction<Object>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpResponseSubscriber<Object>(mContext, mRootView, LoadingTypeIntDef.HANDLE){

                    @Override
                    protected void onCall(Object o) {
                        mRootView.onAvatarSuccess(uri);
                    }
                });

    }
}
