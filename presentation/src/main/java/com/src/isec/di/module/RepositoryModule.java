package com.src.isec.di.module;

import com.src.isec.data.network.repository.UserRepository;
import com.src.isec.domain.repository.IUserRepository;

import dagger.Module;
import dagger.Provides;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.di.module
 * @class Repository资源仓库的依赖容器集合
 * 在 includes后面添加子Module
 * @time 2018/3/21 下午10:30
 * @change
 * @chang time
 * @class describe
 */

@Module
public class RepositoryModule {


    @Provides
    static IUserRepository provideIUserRepository(UserRepository userRepository) {

        return userRepository;
    }

}
