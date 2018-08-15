package com.src.isec.mvp.view;

import com.src.isec.domain.entity.BannerEntity;
import com.src.isec.domain.entity.LiveEntity;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement
 * @class describe
 * @time 2018/4/10 0010 11:02
 * @change
 * @chang time
 * @class describe
 */

public interface IHomeHotView extends IView{

    void onBannerSuccess(List<BannerEntity> list);

    void onDataSuccess(List<LiveEntity> list, boolean isRefresh);
}
