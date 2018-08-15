package com.src.isec.mvp.presenter.implement;

import com.blankj.utilcode.util.LogUtils;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.interactor.ExitRoomUseCase;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IPublishView;
import com.src.isec.reactivex.HttpResponseFunction;
import com.src.isec.reactivex.HttpResponseSubscriber;
import com.src.isec.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

@ActivityScope
public class PublisherPresenter extends BasePresenter<IPublishView> {

    private ExitRoomUseCase exitRoomUseCase;

    @Inject
    PublisherPresenter(ExitRoomUseCase exitRoomUseCase) {
        super();
        this.exitRoomUseCase = exitRoomUseCase;

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * 主播退出房间
     * @param roomNum
     */
    public void exitRoom(String roomNum) {
        exitRoomUseCase.execute(ExitRoomUseCase.builderParams().onLive().onRole()
                .onRoomNum(roomNum).onTxToken(UserInfoManager.getInstance().getTXToken()))
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .map(new HttpResponseFunction())
                .subscribeOn(Schedulers.io())
                .subscribe(new HttpResponseSubscriber(mContext, mRootView) {
                    @Override
                    protected void onCall(Object o) {
                        LogUtils.i("主播退出房间。。。。");
                    }
                });

    }

}
