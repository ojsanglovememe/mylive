package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BannerEntity;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.repository.IUserRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author huxiangliang
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 轮播图
 * @time 2018/4/23 17:48
 * @change
 * @chang time
 * @class describe
 */
public class BannerUseCase extends UseCase<BaseResponse<List<BannerEntity>>, BannerUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public BannerUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<List<BannerEntity>>> buildUseCaseObservable(Params params) {
        return mIUserRepository.bannerList(params.params);
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
        public Params tag(String tag) {
            params.put("tag", tag);
            return this;
        }

    }
}
