package com.src.isec.mvp.view;

import com.src.isec.domain.entity.WatcherEntity;

import java.util.List;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.mvp.view
 * @class describe
 * @time 2018/4/11 17:11
 * @change
 * @chang time
 * @class describe
 */

public interface ILivePlayerView extends IView {

    void getGroupMemberListSucc(List<WatcherEntity.WatchItem> watcherEntityList);


    void onJoinRequestFail(String msg);

    void onAttentionSuccess();

    void onAttentionFailed();

}
