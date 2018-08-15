package com.src.isec.reactivex;

import com.src.isec.data.exception.RequestFailException;
import com.src.isec.data.exception.TokenInvalidException;
import com.src.isec.domain.StateCode;
import com.src.isec.domain.entity.BaseResponse;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.reactivex
 * @class 业务异常分发，转换为Observable
 * @time 2018/4/24 10:46
 * @change
 * @chang time
 * @class describe
 */
public abstract class HttpResponseObservable<T,R> implements Function<BaseResponse<T>,
        ObservableSource<BaseResponse<R>>> {


    @Override
    public ObservableSource<BaseResponse<R>> apply(BaseResponse<T> tBaseResponse) throws Exception {
        //请求失败（根据服务器返回的状态码，直接抛出自定义异常）
        if (!tBaseResponse.isSuccess()) {
            switch (tBaseResponse.getErrorCode()) {
                case StateCode.REQUEST_WAS_ABORTED:  //请求失败
                    throw new RequestFailException(tBaseResponse.getMessage());
                case StateCode.TOKEN_INVALID://Token失效
                    throw new TokenInvalidException(tBaseResponse.getMessage());
                default:
                    throw new RequestFailException(tBaseResponse.getMessage());
            }

        }

        return onCall(tBaseResponse);
    }


    public abstract ObservableSource<BaseResponse<R>> onCall(BaseResponse<T> tBaseResponse);
}
