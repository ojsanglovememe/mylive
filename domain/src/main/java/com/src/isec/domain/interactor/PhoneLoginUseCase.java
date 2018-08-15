package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

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
public class PhoneLoginUseCase extends UseCase<BaseResponse<UserEntity>, PhoneLoginUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public PhoneLoginUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<UserEntity>> buildUseCaseObservable(Params params) {
        return mIUserRepository.phoneLogin(params.params);
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

        public Params mobile(String mobile) {
            params.put("mobile", mobile);
            return this;
        }

        public Params code(String code) {
            params.put("code", code);
            return this;
        }

        public Params type(String type) {
            params.put("type", type);
            return this;
        }

        public Params openId(String openId) {
            params.put("openId", openId);
            return this;
        }

    }
}
