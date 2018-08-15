package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.VerificationCodeEntity;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 发送手机验证码
 * @time 2018/4/17 17:48
 * @change
 * @chang time
 * @class describe
 */
public class SendCodeUseCase extends UseCase<BaseResponse<VerificationCodeEntity>, SendCodeUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public SendCodeUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<VerificationCodeEntity>> buildUseCaseObservable(Params params) {
        return mIUserRepository.sendCode(params.params);
    }

    /**
     * @author liujiancheng
     * @time 2018/4/18  14:35
     * @describe 构建入参
     */
    public static Params builderParams() {
        return new Params();
    }

    /**
     * @author liujiancheng
     * @time 2018/4/18  14:33
     * @describe 使用建造者模式配置入参
     */
    public static final class Params extends BaseParams {
        public Params mobile(String mobile) {
            params.put("mobile", mobile);
            return this;
        }

    }
}
