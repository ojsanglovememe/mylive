package com.src.isec.mvp.view;

import com.src.isec.domain.entity.LiveEntity;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement
 * @class 用户关注/粉丝列表
 * @time 2018/4/24 0010 10:02
 * @change
 * @chang time
 * @class describe
 */

public interface IAttentionFansView extends IView{

    void onDataSuccess(boolean isRecommend, List<LiveEntity> list, boolean isRefresh);

    void onAttentionCallback(Object o);

}
