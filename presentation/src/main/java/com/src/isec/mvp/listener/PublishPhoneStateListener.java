package com.src.isec.mvp.listener;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.tencent.rtmp.TXLivePusher;

import java.lang.ref.WeakReference;

/**
 * @name
 * @class name：
 * @class describe
 * @author wj
 * @time
 * @change
 * @chang time
 * @class describe  来电显示的监听
 */
public class PublishPhoneStateListener extends PhoneStateListener {

    WeakReference<TXLivePusher> mPusher;

    public PublishPhoneStateListener(TXLivePusher pusher) {
        mPusher = new WeakReference<TXLivePusher>(pusher);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        TXLivePusher pusher = mPusher.get();
        switch (state) {
            //电话等待接听
            case TelephonyManager.CALL_STATE_RINGING:
                if (pusher != null) pusher.pausePusher();
                break;
            //电话接听
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (pusher != null) pusher.pausePusher();
                break;
            //电话挂机
            case TelephonyManager.CALL_STATE_IDLE:
                if (pusher != null) pusher.resumePusher();
                break;
        }
    }
}