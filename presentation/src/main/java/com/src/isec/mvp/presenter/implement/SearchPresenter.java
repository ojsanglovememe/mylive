package com.src.isec.mvp.presenter.implement;


import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.domain.entity.TypeLiveEntity;
import com.src.isec.domain.interactor.AttentionUseCase;
import com.src.isec.domain.interactor.LiveListUseCase;
import com.src.isec.domain.interactor.SearchListUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.ISearchView;
import com.src.isec.reactivex.HttpResponseSubscriber;

import java.util.List;

import javax.inject.Inject;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class 首页搜索
 * @time 2018/3/20 15:41
 * @change
 * @chang time
 * @class describe
 */
@ActivityScope
public class SearchPresenter extends BasePresenter<ISearchView> {

    private LiveListUseCase mLiveListUseCase;
    private SearchListUseCase mSearchListUseCase;
    private AttentionUseCase mAttentionUseCase;

    @Inject
    public SearchPresenter(LiveListUseCase liveListUseCase, SearchListUseCase searchListUseCase, AttentionUseCase attentionUseCase) {
        super();
        this.mLiveListUseCase = liveListUseCase;
        this.mSearchListUseCase = searchListUseCase;
        this.mAttentionUseCase = attentionUseCase;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {

    }

    public void getHotList(){
        addMapSubscription(mLiveListUseCase.execute(LiveListUseCase.builderParams().type(Constants.LIVE_TYPE_RECOMMEND).limit(Constants.PAGE_NUM).useId(UserInfoManager.getInstance().getUserId())),
                new HttpResponseSubscriber<TypeLiveEntity>(mContext, mRootView, LoadingTypeIntDef.RENDERING){
                    @Override
                    protected void onCall(TypeLiveEntity item) {
                        mRootView.onHotDataSuccess(item.getList());
                    }
                });
    }

    public void getSearchList(String key){
        addMapSubscription(mSearchListUseCase.execute(SearchListUseCase.builderParams().key(key)),
                new HttpResponseSubscriber<List<LiveEntity>>(mContext, mRootView, LoadingTypeIntDef.RENDERING){
                    @Override
                    protected void onCall(List<LiveEntity> list) {
                        mRootView.onSearchDataSuccess(key, list);
                    }
                });
    }

    public void getAttention(String id){
        addMapSubscription(mAttentionUseCase.execute(AttentionUseCase.builderParams().id(id)),
                new HttpResponseSubscriber<Object>(mContext, mRootView){

                    @Override
                    protected void onCall(Object o) {
                        mRootView.onAttentionCallback(o);
                    }
                });
    }
}
