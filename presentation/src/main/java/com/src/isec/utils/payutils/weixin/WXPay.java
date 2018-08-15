package com.src.isec.utils.payutils.weixin;

import android.content.Context;
import android.text.TextUtils;

import com.src.isec.utils.payutils.PayResultEntity;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付
 * Created by tsy on 16/6/1.
 */
public class WXPay {

    private static WXPay mWXPay;
    private IWXAPI mWXApi;
    private WXPayResultCallBack mCallback;

    public static final int NO_OR_LOW_WX = 1;   //未安装微信或微信版本过低
    public static final int ERROR_PAY_PARAM = 2;  //支付参数错误
    public static final int ERROR_PAY = 3;  //支付失败

    public interface WXPayResultCallBack {
        void onSuccess(); //支付成功

        void onError(int error_code);   //支付失败

        void onCancel();    //支付取消
    }

    public WXPay(Context context, String wx_appid) {
        mWXApi = WXAPIFactory.createWXAPI(context, null);
        mWXApi.registerApp(wx_appid);
    }

    public static void init(Context context, String wx_appid) {
        if (mWXPay == null) {
            mWXPay = new WXPay(context, wx_appid);
        }
    }

    public static WXPay getInstance() {
        return mWXPay;
    }

    public IWXAPI getWXApi() {
        return mWXApi;
    }

    /**
     * 发起微信支付
     */
    public void doPay(PayResultEntity entity, WXPayResultCallBack callback) {
        mCallback = callback;

        if (!check()) {
            if (mCallback != null) {
                mCallback.onError(NO_OR_LOW_WX);
            }
            return;
        }

        if (entity == null) {
            if (mCallback != null) {
                mCallback.onError(ERROR_PAY_PARAM);
            }
            return;
        }

        if (TextUtils.isEmpty(entity.appid) || TextUtils.isEmpty(entity.partnerid)
                || TextUtils.isEmpty(entity.prepayid) || TextUtils.isEmpty(entity.sign) ||
                TextUtils.isEmpty(entity.noncestr)) {
            if (mCallback != null) {
                mCallback.onError(ERROR_PAY_PARAM);
            }
            return;
        }
        PayReq req = new PayReq();
        req.appId = entity.appid;
        req.partnerId = entity.partnerid;
        req.prepayId = entity.prepayid;
        req.packageValue = entity.Package;
        req.nonceStr = entity.noncestr;
        req.timeStamp = entity.timestamp;
        req.sign = entity.sign;

//        String json = new Gson().toJson(req);
//        Logger.json(json);

        mWXApi.sendReq(req);
    }

    //支付回调响应
    public void onResp(int error_code) {
        if (mCallback == null) {
            return;
        }

        if (error_code == 0) {   //成功
            mCallback.onSuccess();
        } else if (error_code == -1) {   //错误
            mCallback.onError(ERROR_PAY);
        } else if (error_code == -2) {   //取消
            mCallback.onCancel();
        }

        mCallback = null;
    }

    //检测是否支持微信支付
    private boolean check() {
        return mWXApi.isWXAppInstalled() && mWXApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }
}
