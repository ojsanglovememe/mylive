package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author huxiangliang
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 绑定手机号（更换手机、绑定新手机）
 * @time 2018/4/26 11:00
 * @change
 * @chang time
 * @class describe
 */
public class BindingMobileUseCase extends UseCase<BaseResponse<Object>, BindingMobileUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public BindingMobileUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<Object>> buildUseCaseObservable(Params params) {
        return mIUserRepository.bindingMobile(params.params);
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

    }
}
