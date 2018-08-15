package com.src.isec.data.exception.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ErrorIntDef.NETWORK, ErrorIntDef.INTERNAL, ErrorIntDef.SERVER, ErrorIntDef.NONETWORK,
        ErrorIntDef.UNKNOW,
        ErrorIntDef.INVALID, ErrorIntDef.REQUEST})
@Retention(RetentionPolicy.SOURCE)
public @interface ErrorIntDef {

    int NETWORK = 1001;//net not response or timeout etc 无网、超时等
    int INTERNAL = 1002;//json parse error etc 一般是内部数据解析异常，譬如json解析异常
    int SERVER = 1003;//404.500 ..etc
    int NONETWORK = 1004;//无网络
    int UNKNOW = 1005;
    int INVALID = 1006;//request success but no need//token异常等自定义异常，可自行配置
    int REQUEST = 1007;//业务异常，如密码错误等

}
