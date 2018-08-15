package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.CreateRoomEntity;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author wj
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class describe
 * @time 2018/4/20 15:53
 * @change
 * @chang time
 * @class describe 创建直播间
 */
public class CreateRoomUserCase extends UseCase<BaseResponse<CreateRoomEntity>, CreateRoomUserCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public CreateRoomUserCase(IUserRepository mIUserRepository) {
        this.mIUserRepository = mIUserRepository;
    }


    public static Params builderParams() {
        return new Params();
    }

    @Override
    protected Observable<BaseResponse<CreateRoomEntity>> buildUseCaseObservable(Params params) {
        return  mIUserRepository.createRoom(params.params);
    }

    public static final class Params extends BaseParams {

        public Params onTxToken(String txToken) {
            params.put("token",txToken);
            return this;
        }

        public Params onLive(){
            params.put("type","live");
            return this;
        }

    }

}
