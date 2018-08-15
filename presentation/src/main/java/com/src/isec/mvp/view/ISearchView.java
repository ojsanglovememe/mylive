package com.src.isec.mvp.view;

import com.src.isec.domain.entity.LiveEntity;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view
 * @class 首页搜索
 * @time 2018/4/25 0025 11:39
 * @change
 * @chang time
 * @class describe
 */

public interface ISearchView extends IView{

    void onSearchDataSuccess(String key, List<LiveEntity> list);

    void onHotDataSuccess(List<LiveEntity> list);

    void onAttentionCallback(Object o);

}
