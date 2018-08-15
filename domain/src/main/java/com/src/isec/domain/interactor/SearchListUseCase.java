package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.domain.repository.IUserRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

/**
 * @author huxiangliang
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 首页搜索
 * @time 2018/4/17 17:48
 * @change
 * @chang time
 * @class describe
 */
public class SearchListUseCase extends UseCase<BaseResponse<List<LiveEntity>>, SearchListUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public SearchListUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<List<LiveEntity>>> buildUseCaseObservable(Params params) {
        return mIUserRepository.searchList(params.params);
    }

    /**
     * @author huxiangliang
     * @time 2018/4/18  14:35
     * @describe 构建入参
     */
    public static Params builderParams() {
        return new Params();
    }

    /**
     * @author huxiangliang
     * @time 2018/4/18  14:33
     * @describe 使用建造者模式配置入参
     */
    public static final class Params extends BaseParams {

        public Params key(@NonNull String key) {
            params.put("key", key);
            return this;
        }

    }
}
