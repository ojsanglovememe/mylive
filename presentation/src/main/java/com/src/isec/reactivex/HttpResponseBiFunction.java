package com.src.isec.reactivex;

import com.src.isec.data.exception.RequestFailException;
import com.src.isec.data.exception.TokenInvalidException;
import com.src.isec.domain.StateCode;
import com.src.isec.domain.entity.BaseResponse;

import io.reactivex.functions.BiFunction;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.reactivex
 * @class 业务异常分发，转换为Observable,用于zip场景
 * @time 2018/4/27 12:54
 * @change
 * @chang time
 * @class describe
 */
public abstract class HttpResponseBiFunction<T1, T2, R> implements BiFunction<BaseResponse<T1>,BaseResponse<T2>,R> {



    @Override
    public R apply(BaseResponse<T1> t1BaseResponse, BaseResponse<T2>
            t2BaseResponse) {

        if (!t1BaseResponse.isSuccess()) {
            switch (t1BaseResponse.getErrorCode()) {
                case StateCode.REQUEST_WAS_ABORTED:  //请求失败
                    throw new RequestFailException(t1BaseResponse.getMessage());
                case StateCode.TOKEN_INVALID://Token失效
                    throw new TokenInvalidException(t1BaseResponse.getMessage());
                default:
                    throw new RequestFailException(t1BaseResponse.getMessage());
            }

        }


        if (!t2BaseResponse.isSuccess()) {
            switch (t2BaseResponse.getErrorCode()) {
                case StateCode.REQUEST_WAS_ABORTED:  //请求失败
                    throw new RequestFailException(t2BaseResponse.getMessage());
                case StateCode.TOKEN_INVALID://Token失效
                    throw new TokenInvalidException(t2BaseResponse.getMessage());
                default:
                    throw new RequestFailException(t2BaseResponse.getMessage());
            }

        }

        return onCall(t1BaseResponse,t2BaseResponse);



    }


    public abstract R onCall(BaseResponse<T1> t1BaseResponse,BaseResponse<T2> t2BaseResponse);
}
