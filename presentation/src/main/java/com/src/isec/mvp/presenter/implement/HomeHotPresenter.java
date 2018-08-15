package com.src.isec.mvp.presenter.implement;

import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.FragmentScope;
import com.src.isec.domain.entity.BannerEntity;
import com.src.isec.domain.entity.TypeLiveEntity;
import com.src.isec.domain.interactor.BannerUseCase;
import com.src.isec.domain.interactor.LiveListUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IHomeHotView;
import com.src.isec.reactivex.HttpResponseSubscriber;

import java.util.List;

import javax.inject.Inject;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.presenter.implement
 * @class describe
 * @time 2018/3/27 0027 15:40
 * @change
 * @chang time
 * @class describe
 */
@FragmentScope
public class HomeHotPresenter extends BasePresenter<IHomeHotView> {

    private BannerUseCase mBannerUseCase;
    private LiveListUseCase mLiveListUseCase;

    @Inject
    public HomeHotPresenter(LiveListUseCase liveListUseCase, BannerUseCase bannerUseCase) {
        super();
        this.mLiveListUseCase = liveListUseCase;
        this.mBannerUseCase = bannerUseCase;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    /***
     * 热门直播
     * @param loadingTypeIntDef
     * @param isRefresh
     * @param page
     * @param pageTime
     */
    public void getHotLiveList(@LoadingTypeIntDef int loadingTypeIntDef, boolean isRefresh, int page, long pageTime) {
        addMapSubscription(mLiveListUseCase.execute(LiveListUseCase.builderParams().type(Constants.LIVE_TYPE_HOT_STR).limit(Constants.PAGE_NUM).page(page).pageTime(pageTime)
                        .useId(UserInfoManager.getInstance().getUserId())),
                new HttpResponseSubscriber<TypeLiveEntity>(mContext, mRootView,
                        loadingTypeIntDef) {
                    @Override
                    protected void onCall(TypeLiveEntity item) {
                        mRootView.onDataSuccess(item.getList(), isRefresh);
                    }
                });
    }

    /***
     * 附近直播
     * @param loadingTypeIntDef
     * @param isRefresh
     * @param page
     * @param pageTime
     * @param lat
     * @param lng
     */
    public void getNearbyLiveList(@LoadingTypeIntDef int loadingTypeIntDef, boolean isRefresh, int page, long pageTime, double lat, double lng) {
        addMapSubscription(mLiveListUseCase.execute(LiveListUseCase.builderParams().type(Constants.LIVE_TYPE_NEARBY_STR).limit(Constants.PAGE_NUM).page(page).pageTime(pageTime)
                        .useId(UserInfoManager.getInstance().getUserId()).lat(lat).lng(lng)),
                new HttpResponseSubscriber<TypeLiveEntity>(mContext, mRootView,
                        loadingTypeIntDef) {
                    @Override
                    protected void onCall(TypeLiveEntity item) {
                        mRootView.onDataSuccess(item.getList(), isRefresh);
                    }
                });
    }

    public void getBannerList(String id){
        addMapSubscription(mBannerUseCase.execute(BannerUseCase.builderParams().tag(id)),
                new HttpResponseSubscriber<List<BannerEntity>>(mContext, mRootView){

                    @Override
                    protected void onCall(List<BannerEntity> bannerEntities) {
                        mRootView.onBannerSuccess(bannerEntities);
                    }
                });
    }


}
