package com.src.isec.mvp.presenter.implement;

import android.text.TextUtils;

import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.FragmentScope;
import com.src.isec.domain.entity.TypeLiveEntity;
import com.src.isec.domain.interactor.AttentionUseCase;
import com.src.isec.domain.interactor.LiveListUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IHomeAttentionView;
import com.src.isec.reactivex.HttpResponseSubscriber;

import javax.inject.Inject;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.presenter.implement
 * @class 首页关注
 * @time 2018/3/27 0027 15:40
 * @change
 * @chang time
 * @class describe
 */
@FragmentScope
public class HomeAttentionPresenter extends BasePresenter<IHomeAttentionView> {

    private LiveListUseCase mLiveListUseCase;
    private AttentionUseCase mAttentionUseCase;

    @Inject
    public HomeAttentionPresenter(LiveListUseCase liveListUseCase, AttentionUseCase attentionUseCase) {
        super();
        this.mLiveListUseCase = liveListUseCase;
        this.mAttentionUseCase = attentionUseCase;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    public void getAttentionList(@LoadingTypeIntDef int loadingTypeIntDef, boolean isRefresh, int page, long pageTime){
        addMapSubscription(mLiveListUseCase.execute(LiveListUseCase.builderParams().type(Constants.LIVE_TYPE_FOLLOW).limit(Constants.PAGE_NUM).page(page).pageTime(pageTime).useId(UserInfoManager.getInstance().getUserId())),
                new HttpResponseSubscriber<TypeLiveEntity>(mContext, mRootView, loadingTypeIntDef){
                    @Override
                    protected void onCall(TypeLiveEntity item) {
                        mRootView.onDataSuccess(TextUtils.equals(Constants.LIVE_TYPE_FOLLOW, item.getType()), item.getList(), isRefresh);
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
