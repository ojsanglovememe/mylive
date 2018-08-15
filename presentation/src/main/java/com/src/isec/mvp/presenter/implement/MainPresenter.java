package com.src.isec.mvp.presenter.implement;


import com.src.isec.di.socpe.ActivityScope;
import com.src.isec.mvp.presenter.BasePresenter;
import com.src.isec.mvp.view.IMainView;

import javax.inject.Inject;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.presenter.implement
 * @class describe
 * @time 2018/3/20 15:41
 * @change
 * @chang time
 * @class describe
 */
@ActivityScope
public class MainPresenter extends BasePresenter<IMainView> {

    @Inject
    public MainPresenter() {
        super();
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {

    }
}
