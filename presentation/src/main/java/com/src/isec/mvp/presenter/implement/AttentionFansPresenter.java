package com.src.isec.mvp.presenter.implement;


import android.text.TextUtils;

import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.domain.entity.TypeLiveEntity;
import com.src.isec.domain.interactor.AttentionFansListUseCase;
import com.src.isec.domain.interactor.AttentionUseCase;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IAttentionFansView;
import com.src.isec.reactivex.HttpResponseSubscriber;

import javax.inject.Inject;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class 用户关注/粉丝列表Presenter
 * @time 2018/3/20 16:41
 * @change
 * @chang time
 * @class describe
 */
@ActivityScope
public class AttentionFansPresenter extends BasePresenter<IAttentionFansView> {

    private AttentionFansListUseCase mAttentionFansListUseCase;
    private AttentionUseCase mAttentionUseCase;

    @Inject
    public AttentionFansPresenter(AttentionFansListUseCase attentionFansListUseCase, AttentionUseCase attentionUseCase) {
        super();
        this.mAttentionFansListUseCase = attentionFansListUseCase;
        this.mAttentionUseCase = attentionUseCase;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {

    }

    public void getAttentionList(String type, @LoadingTypeIntDef int loadingTypeIntDef, boolean isRefresh, int page, long pageTime){
        addMapSubscription(mAttentionFansListUseCase.execute(AttentionFansListUseCase.builderParams().type(type).limit(Constants.PAGE_NUM).page(page).pageTime(pageTime).useId(UserInfoManager.getInstance().getUserId())),
                new HttpResponseSubscriber<TypeLiveEntity>(mContext, mRootView, loadingTypeIntDef){
                    @Override
                    protected void onCall(TypeLiveEntity item) {
                        mRootView.onDataSuccess(TextUtils.equals(Constants.LIVE_TYPE_RECOMMEND, item.getType()), item.getList(), isRefresh);
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
