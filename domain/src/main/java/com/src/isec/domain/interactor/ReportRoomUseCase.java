package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.ReportRoomEnity;
import com.src.isec.domain.repository.IUserRepository;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author wj
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class describe
 * @time 2018/4/20 16:51
 * @change
 * @chang time
 * @class describe  上报
 */
public class ReportRoomUseCase extends UseCase<BaseResponse<ReportRoomEnity>, ReportRoomUseCase.Params> {
    private final IUserRepository mIUserRepository;

    @Inject
    public ReportRoomUseCase(IUserRepository mIUserRepository) {
        this.mIUserRepository = mIUserRepository;
    }

    public static Params builderParams() {
        return new Params();
    }

    @Override
    protected Observable<BaseResponse<ReportRoomEnity>> buildUseCaseObservable(Params params) {
        return mIUserRepository.reportRoom(params.params);
    }


    /**
     * title' =>"天王盖地虎",    //String 标题
     * 'type' =>"live",    //String 房间类型
     * 'roomnum' =>12,   //Integer 房间id
     * 'groupid' =>"12",   //String 群组id
     * 'cover' =>"http://t2.hddhhn.com/uploads/allimg/150505/4_05051J9516141.jpg",      //String 群封面地址
     * 'appid' =>1256439922,      //Integer appid
     * 'device' =>0,     //Integer 0 IOS 1Android 2 PC
     * 'videotype' =>0,   //Integer 0 摄像头 1 屏幕分享
     * 'userid' =>6,   //用户id
     * 'lbs'   =>array(
     * 'longitude' =>1465463854.312,   //经度
     * 'latitude' =>1465463854.21321,   //纬度
     */

    public static final class Params extends BaseParams {
        public Params onDefault() {
            params.put("appid", "1256439922");
            params.put("device", "1");
            params.put("type", "live");
            return this;
        }

        public Params onCity(String city){
            params.put("city",city);
            return this;
        }

        public Params onTitle(String Title) {
            params.put("title", Title);
            return this;
        }

        public Params onTxToken(String txToken) {
            params.put("token", txToken);
            return this;
        }

        public Params onRoomnum(String roomnum) {
            params.put("roomnum", roomnum);
            return this;
        }

        public Params onGroupid(String groupid) {
            params.put("groupid", groupid);
            return this;
        }

        public Params onCover(String cover) {
            params.put("cover", cover);
            return this;
        }

        public Params onVideotype(String videotype) {
            params.put("videotype", videotype);
            return this;
        }

        public Params onUserid(String userid) {
            params.put("userid", userid);
            return this;
        }

        public Params onLongitude(String longitude) {
            params.put("longitude", longitude);
            return this;
        }

        public Params onLatitude(String latitude) {
            params.put("latitude", latitude);
            return this;
        }

    }


}
