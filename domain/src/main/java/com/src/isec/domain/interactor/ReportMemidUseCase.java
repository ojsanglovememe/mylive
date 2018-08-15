package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class 进入/退出房间上报
 * @time 2018/4/25 11:32
 * @change
 * @chang time
 * @class describe
 */

public class ReportMemidUseCase extends UseCase<BaseResponse<Object>,ReportMemidUseCase.Params>{



    private IUserRepository mIUserRepository;


    @Inject
    public ReportMemidUseCase(IUserRepository userRepository){

        mIUserRepository=userRepository;
    }



    /**
     * @author huxiangliang
     * @time 2018/4/18  14:35
     * @describe 构建入参
     */
    public static Params builderParams() {
        return new Params();
    }

    @Override
    protected Observable<BaseResponse<Object>> buildUseCaseObservable(Params params) {
        return mIUserRepository.enterLeaveRoom(params.params);
    }

    /**
     * @author huxiangliang
     * @time 2018/4/18  14:33
     * @describe 使用建造者模式配置入参
     */
    public static final class Params extends BaseParams {



        public Params roomnum(String roomnum) {
            params.put("roomnum", roomnum);
            return this;
        }

        /**
         *  @author liujiancheng
         *  @time 2018/4/25  11:42
         *  @describe 角色为观众
         */
        public Params isWatcher() {
            params.put("role", "0");
            return this;
        }

        /**
         *  @author liujiancheng
         *  @time 2018/4/25  11:42
         *  @describe 角色为主播
         */
        public Params isAnchor() {
            params.put("role", "1");
            return this;
        }
        public Params inRoom() {
            params.put("operate", "0");
            return this;
        }

        public Params outRoom() {
            params.put("operate", "1");
            return this;
        }

        public Params addTxToken(String token){
            params.put("token",token);
            return this;
        }

    }
}
