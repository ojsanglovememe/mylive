package com.src.isec.data.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.src.isec.domain.entity.UserEntity;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.integration
 * @class 用户信息管理类，用于用户相关信息持久化和
 * @time 2018/4/21 11:26
 * @change
 * @chang time
 * @class describe
 */
public class UserInfoManager {

    private final String SPNAME = "user";


    private final String USERID = "userid";
    private final String MOBILE = "mobile";
    private final String NICKNAME = "nickname";
    private final String AVATAR = "avatar";
    private final String SEX = "sex";
    private final String TYPE = "type";
    private final String STATUS = "status";
    private final String CREATETIME = "create_time";
    private final String MODIFYTIME = "modify_time";
    private final String NUMBER = "number";
    private final String TOKEN = "token";
    private final String USERSIG = "userSig";
    private final String TXTOKEN = "txtoken";
    private final String FANS = "fans";
    private final String ATTENTION = "follow";
    private final String ISLOGIN = "islogin";
    /***
     * 是否实名认证
     */
    private final String IS_AUTH = "isAuth";

    private UserInfoManager() {

    }


    public static UserInfoManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  11:39
     * @describe 装载用户信息
     */
    public void loadUserInfo(UserEntity userEntity) {
        unInstallUserInfo();
        SPUtils.getInstance(SPNAME).put(USERID, userEntity.getId());
        SPUtils.getInstance(SPNAME).put(MOBILE, userEntity.getMobile());
        SPUtils.getInstance(SPNAME).put(NICKNAME, userEntity.getNickname());
        SPUtils.getInstance(SPNAME).put(AVATAR, userEntity.getAvatar());
        SPUtils.getInstance(SPNAME).put(SEX, userEntity.getSex());
        SPUtils.getInstance(SPNAME).put(TYPE, userEntity.getType());
        SPUtils.getInstance(SPNAME).put(CREATETIME, userEntity.getCreate_time());
        SPUtils.getInstance(SPNAME).put(MODIFYTIME, userEntity.getModify_time());
        SPUtils.getInstance(SPNAME).put(NUMBER, userEntity.getNumber());
        SPUtils.getInstance(SPNAME).put(TOKEN, userEntity.getToken());
        SPUtils.getInstance(SPNAME).put(USERSIG, userEntity.getUserSig());
        SPUtils.getInstance(SPNAME).put(TXTOKEN, userEntity.getTxtoken());
        SPUtils.getInstance(SPNAME).put(FANS, userEntity.getFans());
        SPUtils.getInstance(SPNAME).put(ATTENTION, userEntity.getAttention());
        SPUtils.getInstance(SPNAME).put(ISLOGIN, true);


    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  13:01
     * @describe 卸载用户信息
     */
    public void unInstallUserInfo() {
        SPUtils.getInstance(SPNAME).clear(true);
    }


    public String getUserId() {

        return SPUtils.getInstance(SPNAME).getString(USERID, "");
    }

    public String getMobile() {
        return SPUtils.getInstance(SPNAME).getString(MOBILE, "");
    }

    public void setMobile(String str) {
         SPUtils.getInstance(SPNAME).put(MOBILE, str);
    }

    public void setNickName(String str) {
        SPUtils.getInstance(SPNAME).put(NICKNAME, str);
    }

    public String getNickName() {
        return SPUtils.getInstance(SPNAME).getString(NICKNAME, "");
    }

    public String getAvatar() {
        return SPUtils.getInstance(SPNAME).getString(AVATAR, "");
    }

    public void setAvatar(String path) {
         SPUtils.getInstance(SPNAME).put(AVATAR, path);
    }

    public String getSex() {
        return SPUtils.getInstance(SPNAME).getString(SEX, "");
    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  13:09
     * @describe 是否是平台管理员
     */
    public boolean isPlatformManager() {
        return SPUtils.getInstance(SPNAME).getString(TYPE, "0").equalsIgnoreCase("1");

    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  13:10
     * @describe 是否禁播
     */
    public boolean isNoSowing() {
        return SPUtils.getInstance(SPNAME).getString(STATUS, "1").equalsIgnoreCase("0");
    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  13:11
     * @describe 直播间号
     */
    public String getNumber() {
        return SPUtils.getInstance(SPNAME).getString(NUMBER, "");
    }


    /**
     * @author liujiancheng
     * @time 2018/4/21  13:11
     * @describe 业务Token
     */
    public String getTOKEN() {
        return SPUtils.getInstance(SPNAME).getString(TOKEN, "");
    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  13:12
     * @describe IM登录签名
     */
    public String getUserSig() {
        return SPUtils.getInstance(SPNAME).getString(USERSIG, "");
    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  13:12
     * @describe 直播Token
     */
    public String getTXToken() {
        return SPUtils.getInstance(SPNAME).getString(TXTOKEN, "");
    }

    /***
     * 关注数
     * @return
     */
    public int getAttentionNum(){
        return SPUtils.getInstance(SPNAME).getInt(ATTENTION, 0);
    }

    public void setAttentionNum(int attentionNum){
        SPUtils.getInstance(SPNAME).put(ATTENTION, attentionNum);
    }

    /***
     * 粉丝数
     * @return
     */
    public int getFansNum(){
        return SPUtils.getInstance(SPNAME).getInt(FANS, 0);
    }

    public void setFansNum(int attentionNum){
        SPUtils.getInstance(SPNAME).put(FANS, attentionNum);
    }

    /***
     * 是否绑定手机号
     * @return
     */
    public boolean isBindingMobile(){
        return !TextUtils.isEmpty(SPUtils.getInstance(SPNAME).getString(MOBILE));
    }

    public boolean isLogin() {
        return SPUtils.getInstance(SPNAME).getBoolean(ISLOGIN, false);
    }

    public void setAuth(boolean isAuth){
        SPUtils.getInstance(SPNAME).put(IS_AUTH, isAuth);
    }

    public boolean isAuth(){
        return SPUtils.getInstance(SPNAME).getBoolean(IS_AUTH, false);
    }

    /**
     * @author liujiancheng
     * @time 2018/4/21  11:38
     * @describe 使用静态内部类实现单例
     */
    private static class LazyHolder {

        private static final UserInfoManager INSTANCE = new UserInfoManager();
    }
}
