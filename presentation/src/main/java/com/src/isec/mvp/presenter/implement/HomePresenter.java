package com.src.isec.mvp.presenter.implement;


import com.src.isec.di.socpe.FragmentScope;
import com.src.isec.domain.entity.TabTagEntity;
import com.src.isec.domain.interactor.TabTagUseCase;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IHomeView;
import com.src.isec.reactivex.HttpResponseSubscriber;

import java.util.List;

import javax.inject.Inject;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class 底部首页Presenter
 * @time 2018/3/20 16:41
 * @change
 * @chang time
 * @class describe
 */
@FragmentScope
public class HomePresenter extends BasePresenter<IHomeView> {

    private TabTagUseCase mTabTagUseCase;


    @Inject
    public HomePresenter(TabTagUseCase tabTagUseCase) {
        super();
        this.mTabTagUseCase = tabTagUseCase;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    public void getTabTagList() {
        addMapSubscription(mTabTagUseCase.execute(TabTagUseCase.builderParams()),
                new HttpResponseSubscriber<List<TabTagEntity>>(mContext, mRootView) {

                    @Override
                    protected void onCall(List<TabTagEntity> list) {
                        mRootView.onDataSuccess(list);
                    }
                });
    }
}
