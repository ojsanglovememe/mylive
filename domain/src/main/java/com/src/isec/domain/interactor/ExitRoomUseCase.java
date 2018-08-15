package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author wj
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class describe
 * @time 2018/4/25 10:05
 * @change
 * @chang time
 * @class describe 主播端退出房间
 */
public class ExitRoomUseCase extends UseCase<BaseResponse, ExitRoomUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public ExitRoomUseCase(IUserRepository mIUserRepository) {
        this.mIUserRepository = mIUserRepository;
    }


    public static ExitRoomUseCase.Params builderParams() {
        return new ExitRoomUseCase.Params();
    }
    @Override
    protected Observable<BaseResponse> buildUseCaseObservable(Params params) {
        return mIUserRepository.exitRoom(params.params);
    }

    public static final class Params extends BaseParams {

        public Params onTxToken(String txToken) {
            params.put("token", txToken);
            return this;
        }

        public Params onLive() {
            params.put("type", "live");
            return this;
        }

        public Params onRoomNum(String roomNum) {
            params.put("roomnum", roomNum);
            return this;
        }

        public Params onRole() {
            params.put("role", "1");
            return this;
        }

    }
}
