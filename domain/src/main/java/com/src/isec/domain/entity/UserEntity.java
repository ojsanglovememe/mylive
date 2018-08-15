package com.src.isec.domain.entity;

import java.io.Serializable;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class describe
 * @time 2018/3/21 11:40
 * @change
 * @chang time
 * @class describe
 */

public class UserEntity implements Serializable{

    private  String id;
    private  String token;
    private  String mobile;
    private  String nickname;
    private  String avatar;
    private  String sex;
    private  String wechat;
    private  String qq;
    private  String sina;
    private  String type;
    private  String status;
    private  String create_time;
    private  String modify_time;
    private  String number;
    private  String userSig;
    private  String txtoken;


    public String getTxtoken() {
        return txtoken == null ? "" : txtoken;
    }

    public void setTxtoken(String txtoken) {
        this.txtoken = txtoken;
    }

    private int follow = 0;
    private int fans = 0;

    public UserEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getSina() {
        return sina;
    }

    public void setSina(String sina) {
        this.sina = sina;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }


    public int getAttention() {
        return follow;
    }

    public void setAttention(int attention) {
        this.follow = attention;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }
}
