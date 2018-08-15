package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.WatcherEntity;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 获取房间观众列表
 * @time 2018/4/25 11:58
 * @change
 * @chang time
 * @class describe
 */

public class GetRoomMemberUseCase extends UseCase<BaseResponse<WatcherEntity>,GetRoomMemberUseCase.Params>{


    private IUserRepository mIUserRepository;

    @Inject
    public GetRoomMemberUseCase(IUserRepository userRepository){
        mIUserRepository=userRepository;

    }



    public static Params builderParams() {
        return new Params();
    }

    @Override
    protected Observable<BaseResponse<WatcherEntity>> buildUseCaseObservable(Params params) {
        return mIUserRepository.roomidList(params.params);
    }

    public static final class Params extends BaseParams {

        public Params roomnum(String roomnum) {
            params.put("roomnum", roomnum);
            return this;
        }


        public Params defultIndex() {
            params.put("index", "0");
            return this;
        }
        public Params index(String index) {
            params.put("index", index);
            return this;
        }

        public Params defultSize() {
            params.put("size", "100");
            return this;
        }
        public Params size(String size) {
            params.put("size", size);
            return this;
        }

        public Params addTxToken(String token){
            params.put("token",token);
            return this;
        }

    }
}
