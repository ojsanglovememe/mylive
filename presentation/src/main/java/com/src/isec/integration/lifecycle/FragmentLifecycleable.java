package com.src.isec.integration.lifecycle;

import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.integration.lifecycle
 * @class Fragment实现此接口，用于与Rxjava的生命周期绑定
 * @time 2018/3/19 9:36
 * @change
 * @chang time
 * @class describe
 */

public interface FragmentLifecycleable extends Lifecycleable<FragmentEvent> {
}
