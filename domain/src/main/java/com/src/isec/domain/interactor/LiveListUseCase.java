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
 * @class 手机号登录
 * @time 2018/4/17 17:48
 * @change
 * @chang time
 * @class describe
 */
public class LiveListUseCase extends UseCase<BaseResponse<TypeLiveEntity>, LiveListUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public LiveListUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<TypeLiveEntity>> buildUseCaseObservable(Params params) {
        return mIUserRepository.liveList(params.params);
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

        public Params useId(String id) {
            params.put("id", id);
            return this;
        }

        public Params lng(double lng) {
            params.put("lng", lng);
            return this;
        }

        public Params lat(double lat) {
            params.put("lat", lat);
            return this;
        }

    }
}
