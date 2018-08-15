package com.src.isec.domain.repository;

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

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.domain.repository
 * @class describe
 * @time 2018/3/21 14:49
 * @change
 * @chang time
 * @class describe
 */

public interface IUserRepository {



    Observable<BaseResponse<VerificationCodeEntity>> sendCode(Map<String, Object> params);


    Observable<BaseResponse<UserEntity>> phoneLogin(Map<String, Object> params);

    Observable<BaseResponse<UserEntity>> personalInfo(Map<String, Object> params);

    /***
     * 修改用户信息
     * @param params
     * @return
     */
    Observable<BaseResponse<Object>> updatePersonalInfo(Map<String, Object> params);

    /***
     * @author huxiangliang
     * @describe 直播列表（热门直播，附近直播，关注直播）
     * @param params
     * @return
     */
    Observable<BaseResponse<TypeLiveEntity>> liveList(Map<String, Object> params);

    /***
     * 关注/取关主播
     * @param params
     * @return
     */
    Observable<BaseResponse<Object>> attention(Map<String, Object> params);

    /***
     * 获取用户关注/粉丝列表
     * @param params
     * @return
     */
    Observable<BaseResponse<TypeLiveEntity>> attentionFansList(Map<String, Object> params);

    /***
     * 首页搜索
     * @param params
     * @return
     */
    Observable<BaseResponse<List<LiveEntity>>> searchList(Map<String, Object> params);

    /***
     * 绑定手机号（更换手机、绑定新手机）
     * @param params
     * @return
     */
    Observable<BaseResponse<Object>> bindingMobile(Map<String, Object> params);

    /***
     * 首页轮播图
     * @param params
     * @return
     */
    Observable<BaseResponse<List<BannerEntity>>> bannerList(Map<String, Object> params);

    /***
     * 首页顶部标签
     * @return
     */
    Observable<BaseResponse<List<TabTagEntity>>> tabTagList();

    Observable<BaseResponse<CreateRoomEntity>> createRoom(Map<String,Object> params);

    Observable<BaseResponse<ReportRoomEnity>> reportRoom(Map<String,Object> params);

    Observable<BaseResponse<FileEntity>> upload(String uri);
    //主播退出房间
    Observable<BaseResponse> exitRoom(Map<String,Object> params);

    Observable<BaseResponse<Object>> enterLeaveRoom( Map<String, Object> params);


    Observable<BaseResponse<WatcherEntity>> roomidList(Map<String, Object> params);

}
