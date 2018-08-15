package com.src.isec.domain.entity;

import com.src.isec.domain.StateCode;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class describe
 * @time 2018/3/27 0027 14:36
 * @change
 * @chang time
 * @class describe
 */

public class BaseResponse<T> {

    private String msg = "";
    private int code;

    private T data;

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public int getErrorCode() {
        return code;
    }

    public void setErrorCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == StateCode.REQUEST_SUCCESS;
    }
}
