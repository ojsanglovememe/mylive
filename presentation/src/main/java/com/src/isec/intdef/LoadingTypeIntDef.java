package com.src.isec.intdef;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.intdef
 * @class 网络加载状态类型，根据不同的类型显示不同的加载状态视图
 * @time 2018/4/19 14:19
 * @change
 * @chang time
 * @class describe
 */

@IntDef({LoadingTypeIntDef.HANDLE, LoadingTypeIntDef.RENDERING, LoadingTypeIntDef.LISTREFRESH,
        LoadingTypeIntDef.LISTLOADMORE})
@Retention(RetentionPolicy.SOURCE)
public @interface LoadingTypeIntDef {
    int RENDERING = 1001;//渲染数据时的网络加载状态类型
    int HANDLE = 1002;//处理数据时的网络加载状态类型
    int LISTREFRESH = 1003;//列表刷新数据时的网络加载状态类型
    int LISTLOADMORE = 1004;//列表加载更多数据时的网络加载状态类型
}
