package com.src.isec.mvp.view;

import com.src.isec.domain.entity.TabTagEntity;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view
 * @class 底部首页
 * @time 2018/4/27 0027 15:23
 * @change
 * @chang time
 * @class describe
 */

public interface IHomeView extends IView{

    void onDataSuccess(List<TabTagEntity> list);
}
