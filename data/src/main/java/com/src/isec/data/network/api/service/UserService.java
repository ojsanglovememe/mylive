package com.src.isec.data.network.api.service;


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

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;


/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.network.api.service
 * @class 业务API
 * @time 2018/3/21 10:53
 * @change
 * @chang time
 * @class describe
 */

public interface UserService {


    @POST("tool/sendCode")
    Observable<BaseResponse<VerificationCodeEntity>> senCode(@Body Map<String, Object> params);

    @POST("user")
    Observable<BaseResponse<UserEntity>> phoneLogin(@Body Map<String, Object> params);

    @GET("user")
    Observable<BaseResponse<UserEntity>> personalInfo(@QueryMap Map<String, Object> params);

    //创建房间
    @POST("live/sxb?svc=live&cmd=create")
    Observable<BaseResponse<CreateRoomEntity>> createRoom(@Body Map<String, Object> params);

    /***
     * @author huxiangliang
     * @describe 直播列表（热门直播，附近直播，关注直播）
     * @param params
     * @return
     */
    @GET("streamer")
    Observable<BaseResponse<TypeLiveEntity>> liveList(@QueryMap Map<String, Object> params);

    //上报房间
    @POST("live/sxb?svc=live&cmd=reportroom")
    Observable<BaseResponse<ReportRoomEnity>> reportRoom(@Body Map<String, Object> params);


    //上传图片
    @Multipart
    @POST("tool/upload")
    Observable<BaseResponse<FileEntity>> upload(@Part MultipartBody.Part file,@Part MultipartBody.Part sign,@Part MultipartBody.Part signType,@Part MultipartBody.Part t);


    /***
     * 关注/取关主播
     * @param params
     * @return
     */
    @PUT("streamer")
    Observable<BaseResponse<Object>> attention(@QueryMap Map<String, Object> params);

    /***
     * 获取用户关注/粉丝列表
     * @param params
     * @return
     */
    @GET("user/getSocial")
    Observable<BaseResponse<TypeLiveEntity>> attentionFansList(@QueryMap Map<String, Object> params);



    @POST("live/sxb?svc=live&cmd=exitroom")
    Observable<BaseResponse> exitRoom(@Body Map<String, Object> params);



    /**
     * 观众进入和退出直播间
     */
    @POST("live/sxb?svc=live&cmd=reportmemid")
    Observable<BaseResponse<Object>> enterLeaveRoom(@Body Map<String, Object> params);

    /**
     * 拉取直播间观众列表
     * @param params
     * @return
     */
    @POST("live/sxb?svc=live&cmd=roomidlist")
    Observable<BaseResponse<WatcherEntity>> roomidList(@Body Map<String, Object> params);



    /***
     * 修改用户信息
     * @param params
     * @return
     */
    @PUT("user")
    Observable<BaseResponse<Object>> updatePersonalInfo(@QueryMap Map<String, Object> params);

    /***
     * 首页搜索
     * @param params
     * @return
     */
    @GET("streamer/search")
    Observable<BaseResponse<List<LiveEntity>>> searchList(@QueryMap Map<String, Object> params);


    /***
     * 绑定手机号（更换手机、绑定新手机）
     * @param params
     * @return
     */
    @POST("tool/checkCode")
    Observable<BaseResponse<Object>> bindingMobile(@Body Map<String, Object> params);

    /***
     * 首页轮播图
     * @param params
     * @return
     */
    @GET("tool/getSlide")
    Observable<BaseResponse<List<BannerEntity>>> bannerList(@QueryMap Map<String, Object> params);

    /****
     * 首页顶部标签
     * @return
     */
    @GET("tool/getTag")
    Observable<BaseResponse<List<TabTagEntity>>> tabTagList();

}
