package com.src.isec.mvp.presenter.implement;

import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.WatcherEntity;
import com.src.isec.domain.interactor.AttentionUseCase;
import com.src.isec.domain.interactor.GetRoomMemberUseCase;
import com.src.isec.domain.interactor.ReportMemidUseCase;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.ILivePlayerView;
import com.src.isec.reactivex.HttpResponseFunction;
import com.src.isec.reactivex.HttpResponseObservable;
import com.src.isec.reactivex.HttpResponseSubscriber;
import com.src.isec.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.mvp.presenter.implement
 * @class 看直播界面Presenter
 * @time 2018/4/11 17:15
 * @change
 * @chang time
 * @class describe
 */

public class LivePlayerPresenter extends BasePresenter<ILivePlayerView> {


    private ReportMemidUseCase mReportMemidUseCase;

    private GetRoomMemberUseCase mGetRoomMemberUseCase;

    private AttentionUseCase mAttentionUseCase;
    @Inject
    public LivePlayerPresenter(ReportMemidUseCase reportMemidUseCase,GetRoomMemberUseCase getRoomMemberUseCase, AttentionUseCase attentionUseCase){
        mReportMemidUseCase=reportMemidUseCase;
        mGetRoomMemberUseCase=getRoomMemberUseCase;
        mAttentionUseCase = attentionUseCase;

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }



    /**
     *  @author liujiancheng
     *  @time 2018/4/25  11:50
     *  @describe 加入房间上报
     */
    public void inJoinRoom(String roomId){

        mReportMemidUseCase.execute(ReportMemidUseCase.builderParams().roomnum(roomId).inRoom().isWatcher().addTxToken(UserInfoManager.getInstance().getTXToken())).flatMap(new HttpResponseObservable<Object,WatcherEntity>(){
            @Override
            public ObservableSource<BaseResponse<WatcherEntity>> onCall(BaseResponse<Object>
                                                                                objectBaseResponse) {
                return mGetRoomMemberUseCase.execute(GetRoomMemberUseCase.builderParams().roomnum(roomId).defultIndex().defultSize().addTxToken(UserInfoManager.getInstance().getTXToken()));
            }
        }).map(new HttpResponseFunction<WatcherEntity>()).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new HttpResponseSubscriber<WatcherEntity>(mContext) {


            @Override
            protected void onCall(WatcherEntity watcherEntity) {
                mRootView.getGroupMemberListSucc(watcherEntity.getIdlist());
            }

            @Override
            protected void onErrorWithCurrency(String message) {
                super.onErrorWithCurrency(message);
                mRootView.onJoinRequestFail(message);
            }

        });

    }

    public void quitRoom(String roomId){

        mReportMemidUseCase.execute(ReportMemidUseCase.builderParams().roomnum(roomId).outRoom().isWatcher().addTxToken(UserInfoManager.getInstance().getTXToken()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResponseSubscriber<Object>(mContext) {
                    @Override
                    protected void onCall(Object o) {
//                        ToastUtil.show("退出房间成功");
                    }
                });

    }

    public void setAttention(String id){
        addMapSubscription(mAttentionUseCase.execute(AttentionUseCase.builderParams().id(id)), new HttpResponseSubscriber<Object>(mContext) {

            @Override
            protected void onCall(Object o) {
                mRootView.onAttentionSuccess();
            }

            @Override
            protected void onErrorWithCurrency(String message) {
                super.onErrorWithCurrency(message);
                mRootView.onAttentionFailed();
            }
        });
    }

}
