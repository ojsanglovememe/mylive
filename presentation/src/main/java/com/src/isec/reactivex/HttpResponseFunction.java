package com.src.isec.reactivex;


import com.src.isec.data.exception.RequestFailException;
import com.src.isec.data.exception.TokenInvalidException;
import com.src.isec.domain.StateCode;
import com.src.isec.domain.entity.BaseResponse;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.reactivex.subscriber
 * @class 业务异常分发
 * @time 2018/3/27 0027 15:18
 * @change
 * @chang time
 * @class describe
 */

public class HttpResponseFunction<T> implements Function<BaseResponse<T>, T> {

    @Override
    public T apply(@NonNull BaseResponse<T> tResultModel) {

        //请求失败（根据服务器返回的状态码，直接抛出自定义异常）
        if (!tResultModel.isSuccess()) {
            switch (tResultModel.getErrorCode()) {
                case StateCode.REQUEST_WAS_ABORTED:  //请求失败
                    throw new RequestFailException(tResultModel.getMessage());
                case StateCode.TOKEN_INVALID://Token失效
                    throw new TokenInvalidException(tResultModel.getMessage());
                default:
                    throw new RequestFailException(tResultModel.getMessage());
            }

        }
        return tResultModel.getData();
    }
}
