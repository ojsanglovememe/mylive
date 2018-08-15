package com.src.isec.mvp.presenter.implement;


import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IRealNameAuthView;

import javax.inject.Inject;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class 实名认证Presenter
 * @time 2018/3/20 16:41
 * @change
 * @chang time
 * @class describe
 */
@ActivityScope
public class RealNameAuthPresenter extends BasePresenter<IRealNameAuthView> {



    @Inject
    public RealNameAuthPresenter() {
        super();

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }
}
