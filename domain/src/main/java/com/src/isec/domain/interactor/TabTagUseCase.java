package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.TabTagEntity;
import com.src.isec.domain.repository.IUserRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author huxiangliang
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 首页顶部标签
 * @time 2018/4/27 14:48
 * @change
 * @chang time
 * @class describe
 */
public class TabTagUseCase extends UseCase<BaseResponse<List<TabTagEntity>>, TabTagUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public TabTagUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<List<TabTagEntity>>> buildUseCaseObservable(Params params) {
        return mIUserRepository.tabTagList();
    }

    /**
     * @author huxiangliang
     * @time 2018/4/23  14:35
     * @describe 构建入参
     */
    public static Params builderParams() {
        return new Params();
    }

    /**
     * @author huxiangliang
     * @time 2018/4/23  14:33
     * @describe 使用建造者模式配置入参
     */
    public static final class Params extends BaseParams {
        public Params id(String id) {
            params.put("id", id);
            return this;
        }

    }
}
