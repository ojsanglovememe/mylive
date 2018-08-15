package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.TypeLiveEntity;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

/**
 * @author huxiangliang
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 获取用户关注/粉丝列表
 * @time 2018/4/24 09:48
 * @change
 * @chang time
 * @class describe
 */
public class AttentionFansListUseCase extends UseCase<BaseResponse<TypeLiveEntity>, AttentionFansListUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public AttentionFansListUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<TypeLiveEntity>> buildUseCaseObservable(Params params) {
        return mIUserRepository.attentionFansList(params.params);
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

        public Params useId(@NonNull String id) {
            params.put("id", id);
            return this;
        }

        public Params type(@NonNull String type) {
            params.put("type", type);
            return this;
        }

        public Params page(int page) {
            params.put("page", page);
            return this;
        }

        public Params limit(int limit) {
            params.put("limit", limit);
            return this;
        }

        public Params pageTime(long pageTime) {
            params.put("pageTime", pageTime);
            return this;
        }

    }
}
