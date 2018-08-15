package com.src.isec.data.network.repository;

import com.src.isec.data.network.IRepositoryManager;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.network.repository
 * @class Repository基类
 * 定义Repository基类基础规则
 * @time 2018/3/21 15:31
 * @change
 * @chang time
 * @class describe
 */

public class BaseRepository {

    //用于管理网络请求层, 以及数据缓存层
    protected IRepositoryManager mRepositoryManager;

    public BaseRepository(IRepositoryManager iRepositoryManager) {
        mRepositoryManager = iRepositoryManager;
    }

}
