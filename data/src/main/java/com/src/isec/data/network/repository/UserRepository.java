package com.src.isec.data.network.repository;

import com.src.isec.data.network.IRepositoryManager;
import com.src.isec.data.network.api.service.UserService;
import com.src.isec.data.utils.PictureCompressUtil;
import com.src.isec.data.utils.SignRequestUtil;
import com.src.isec.domain.entity.BannerEntity;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.CreateRoomEntity;
import com.src.isec.domain.entity.FileEntity;
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.domain.entity.ReportRoomEnity;
import com.src.isec.domain.entity.TabTagEntity;
import com.src.isec.domain.entity.TypeLiveEntity;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.domain.entity.VerificationCodeEntity;
import com.src.isec.domain.entity.WatcherEntity;
import com.src.isec.domain.repository.IUserRepository;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.network.repository
 * @class describe
 * @time 2018/3/21 14:44
 * @change
 * @chang time
 * @class describe
 */
@Singleton
public class UserRepository extends BaseRepository implements IUserRepository {


    @Inject
    public UserRepository(IRepositoryManager iRepositoryManager) {
        super(iRepositoryManager);
    }


    @Override
    public Observable<BaseResponse<VerificationCodeEntity>> sendCode(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).senCode(params);
    }

    @Override
    public Observable<BaseResponse<UserEntity>> phoneLogin(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).phoneLogin(params);
    }

    @Override
    public Observable<BaseResponse<UserEntity>> personalInfo(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).personalInfo(params);
    }

    @Override
    public Observable<BaseResponse<Object>> updatePersonalInfo(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).updatePersonalInfo(params);
    }

    @Override
    public Observable<BaseResponse<TypeLiveEntity>> liveList(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).liveList(params);
    }

    /***
     * 关注/取关主播
     * @param params
     * @return
     */
    @Override
    public Observable<BaseResponse<Object>> attention(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).attention(params);
    }

    @Override
    public Observable<BaseResponse<TypeLiveEntity>> attentionFansList(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).attentionFansList(params);
    }

    //创建直播间
    @Override
    public Observable<BaseResponse<CreateRoomEntity>> createRoom(Map<String, Object> params) {

        return mRepositoryManager.obtainRetrofitService(UserService.class).createRoom(params);
    }

    /**
     * 上报房间
     *
     * @param params
     * @return
     */
    @Override
    public Observable<BaseResponse<ReportRoomEnity>> reportRoom(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).reportRoom(params);
    }

    @Override
    public Observable<BaseResponse<FileEntity>> upload(String fileUri) {


        return Observable.just(fileUri).flatMap(new Function<String, ObservableSource<BaseResponse<FileEntity>>>() {
            @Override
            public ObservableSource<BaseResponse<FileEntity>> apply(String s) throws Exception {
                File file = PictureCompressUtil.compressPic(fileUri);

                String time = String.valueOf(System.currentTimeMillis() / 1000);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                MultipartBody.Part sign = MultipartBody.Part.createFormData("sign", SignRequestUtil.signDefualRequest(time));
                MultipartBody.Part signType = MultipartBody.Part.createFormData("signType", SignRequestUtil.SIGNTYPE);
                MultipartBody.Part t = MultipartBody.Part.createFormData("t", time);

                return mRepositoryManager.obtainRetrofitService(UserService.class).upload(filePart, sign, signType, t);
            }
        });


    }

    @Override
    public Observable<BaseResponse<Object>> enterLeaveRoom(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).enterLeaveRoom(params);
    }

    @Override
    public Observable<BaseResponse<WatcherEntity>> roomidList(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).roomidList(params);
    }


    /**
     * 主播退出房间
     * @param params
     * @return
     */
    @Override
    public Observable<BaseResponse> exitRoom(Map<String, Object> params) {

        return mRepositoryManager.obtainRetrofitService(UserService.class).exitRoom(params);
    }

    @Override
    public Observable<BaseResponse<List<LiveEntity>>> searchList(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).searchList(params);
    }

    @Override
    public Observable<BaseResponse<Object>> bindingMobile(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).bindingMobile(params);
    }

    @Override
    public Observable<BaseResponse<List<BannerEntity>>> bannerList(Map<String, Object> params) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).bannerList(params);
    }

    @Override
    public Observable<BaseResponse<List<TabTagEntity>>> tabTagList() {
        return mRepositoryManager.obtainRetrofitService(UserService.class).tabTagList();
    }

}
