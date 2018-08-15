package com.src.isec.mvp.view;

import com.src.isec.domain.entity.LiveEntity;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement
 * @class 首页关注
 * @time 2018/4/10 0010 11:02
 * @change
 * @chang time
 * @class describe
 */

public interface IHomeAttentionView extends IView{

    void onDataSuccess(boolean isAttention, List<LiveEntity> list, boolean isRefresh);

    void onAttentionCallback(Object o);
}
