package com.src.isec.utils.payutils;

import java.io.Serializable;

/**
 * Created by Tracy on 2017/12/2.
 */

public class PayResultEntity implements Serializable {

    /***
     * 应用APPID
     */
    public String appid;

    /***
     * 商户号
     */
    public String partnerid;

    /***
     * nonce_str
     */
    public String noncestr;

    /***
     * 预支付交易会话标识
     */
    public String prepayid;

    /***
     * 签名
     */
    public String sign;

    public String timestamp;

    public String Package;

    /***
     * 订单号
     */
    public String ordernum;
    /***
     * 支付金额
     */
    public String amount;
    /***
     * 支付时间
     */
    public String payTime;
    /***
     * 支付类型
     */
    public String payType;


    /***
     * 支付宝请求参数
     */
    public String orderString;


}
