package com.src.isec.mvp.presenter.implement;

import android.net.Uri;

import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.CreateRoomEntity;
import com.src.isec.domain.entity.FileEntity;
import com.src.isec.domain.entity.ReportRoomEnity;
import com.src.isec.domain.interactor.CreateRoomUserCase;
import com.src.isec.domain.interactor.ReportRoomUseCase;
import com.src.isec.domain.interactor.UploadFileUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IPublishSettingView;
import com.src.isec.reactivex.HttpResponseBiFunction;
import com.src.isec.reactivex.HttpResponseFunction;
import com.src.isec.reactivex.HttpResponseObservable;
import com.src.isec.reactivex.HttpResponseSubscriber;
import com.src.isec.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@ActivityScope
public class PublishSettingPresenter extends BasePresenter<IPublishSettingView> {

    private UploadFileUseCase uploadFileUseCase;

    private CreateRoomUserCase createRoomUserCase;
    private ReportRoomUseCase reportRoomUseCase;
    private String roomNum;

    @Inject
    public PublishSettingPresenter(UploadFileUseCase uploadFileUseCase, CreateRoomUserCase
            createRoomUserCase, ReportRoomUseCase reportRoomUseCase) {
        super();
        this.uploadFileUseCase = uploadFileUseCase;
        this.createRoomUserCase = createRoomUserCase;
        this.reportRoomUseCase = reportRoomUseCase;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * 上传图片并创建房间
     */
    public void createRoom(Uri cover, String title, String latitude, String longitude, String
            location, String imGroupId) {
        Observable.zip(uploadFileUseCase.execute(UploadFileUseCase.builderParams().onFile(cover
                .getPath())), createRoomUserCase.execute(CreateRoomUserCase.builderParams()
                .onLive().
                        onTxToken(UserInfoManager.getInstance().getTXToken())), new
                HttpResponseBiFunction<FileEntity, CreateRoomEntity, String[]>() {
                    @Override
                    public String[] onCall(BaseResponse<FileEntity> fileEntityBaseResponse,
                                           BaseResponse<CreateRoomEntity>
                                                   createRoomEntityBaseResponse) {

                        roomNum = createRoomEntityBaseResponse.getData().getRoomnum();
                        String[] parmas = {fileEntityBaseResponse.getData().getUrl(),
                                createRoomEntityBaseResponse.getData()
                                        .getRoomnum()};

                        return parmas;
                    }
                }).flatMap(new Function<String[],
                ObservableSource<BaseResponse<ReportRoomEnity>>>() {
            @Override
            public ObservableSource<BaseResponse<ReportRoomEnity>> apply(String[] strings) throws
                    Exception {
                return reportRoomUseCase.execute(ReportRoomUseCase.builderParams().onDefault()
                        .onTxToken(UserInfoManager.getInstance().getTXToken())
                        .onTitle(title)
                        .onCover(strings[0])
                        .onCity(location)
                        .onRoomnum(strings[1])
                        .onGroupid(imGroupId)
                        .onUserid(UserInfoManager.getInstance().getUserId())
                        .onVideotype("0")
                        .onLatitude(latitude)
                        .onLongitude(longitude));
            }
        }).map(new HttpResponseFunction<ReportRoomEnity>())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new HttpResponseSubscriber<ReportRoomEnity>(mContext, mRootView, LoadingTypeIntDef.HANDLE) {


            @Override
            protected void onCall(ReportRoomEnity reportRoomEnity) {
                mRootView.onGetUrl(reportRoomEnity.getPush(), roomNum);

            }
        });


    }


    /**
     * 创建房间
     */
    public void createRoom(String cover, String title, String latitude, String longitude, String
            location, String imGroupId) {


        createRoomUserCase.execute(CreateRoomUserCase.builderParams().onLive().
                onTxToken(UserInfoManager.getInstance().getTXToken())).flatMap(new HttpResponseObservable<CreateRoomEntity, ReportRoomEnity>() {
            @Override
            public ObservableSource<BaseResponse<ReportRoomEnity>> onCall(BaseResponse
                                                                                  <CreateRoomEntity> createRoomEntityBaseResponse) {
                roomNum = createRoomEntityBaseResponse.getData().getRoomnum();
                return reportRoomUseCase.execute(ReportRoomUseCase.builderParams().onDefault()
                        .onTxToken(UserInfoManager.getInstance().getTXToken())
                        .onTitle(title)
                        .onCover(cover)
                        .onCity(location)
                        .onRoomnum(roomNum)
                        .onGroupid(imGroupId)
                        .onUserid(UserInfoManager.getInstance().getUserId())
                        .onVideotype("0")
                        .onLatitude(latitude)
                        .onLongitude(longitude));
            }
        })
                .map(new HttpResponseFunction<ReportRoomEnity>())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new HttpResponseSubscriber<ReportRoomEnity>(mContext, mRootView, LoadingTypeIntDef.HANDLE) {
            @Override
            protected void onCall(ReportRoomEnity reportRoomEnity) {
                mRootView.onGetUrl(reportRoomEnity.getPush(), roomNum);
            }
        });


    }

}