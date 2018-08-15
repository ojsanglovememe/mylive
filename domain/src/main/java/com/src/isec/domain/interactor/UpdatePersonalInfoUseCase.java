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
 * @class 修改个人信息资料
 * @time 2018/4/17 17:48
 * @change
 * @chang time
 * @class describe
 */
public class UpdatePersonalInfoUseCase extends UseCase<BaseResponse<Object>, UpdatePersonalInfoUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public UpdatePersonalInfoUseCase(IUserRepository iUserRepository) {
        mIUserRepository = iUserRepository;
    }

    @Override
    protected Observable<BaseResponse<Object>> buildUseCaseObservable(Params params) {
        return mIUserRepository.updatePersonalInfo(params.params);
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

        public Params nickname(String nickname) {
            params.put("nickname", nickname);
            return this;
        }

        public Params sex(String sex) {
            params.put("sex", sex);
            return this;
        }

        public Params avatar(String avatar) {
            params.put("avatar", avatar);
            return this;
        }

    }
}
