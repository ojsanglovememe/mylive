package com.src.isec.domain.entity;

import java.io.Serializable;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class 直播实体类
 * @time 2018/4/10 0010 11:37
 * @change
 * @chang time
 * @class describe
 */

public class LiveEntity implements Serializable{

    private String id;
    private String nickname;
    private String avatar;
    private String number;
    private String title;
    private String cover;

    private String play_url1;
    private String play_url2;
    private String play_url3;
    private String city;
    private String chat_room_id;

    /**直播间人数**/
    private int headcount = 0;
    /**是否已关注，0：未关注，1：已关注**/
    private int is_follow = 0;
    /**主播开播时的距离**/
    private double distance = 0;

    /**下播时间，如果正在直播，此字段为0**/
    private long finish_time = 0;
    /**开播时间，如果现在没有直播，此字段为上次开播时间**/
    private long create_time = 0;

    public LiveEntity() {
    }

    /***
     * 主播是否正在直播
     * @return
     */
    public boolean isOnline() {
        return finish_time == 0;
    }

    public String getId() {
        return id;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPlay_url1() {
        return play_url1;
    }

    public void setPlay_url1(String play_url1) {
        this.play_url1 = play_url1;
    }

    public String getPlay_url2() {
        return play_url2;
    }

    public void setPlay_url2(String play_url2) {
        this.play_url2 = play_url2;
    }

    public String getPlay_url3() {
        return play_url3;
    }

    public void setPlay_url3(String play_url3) {
        this.play_url3 = play_url3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getChat_room_id() {
        return chat_room_id;
    }

    public void setChat_room_id(String chat_room_id) {
        this.chat_room_id = chat_room_id;
    }

    public int getHeadCount() {
        return headcount;
    }

    public boolean isFollow() {
        return is_follow == 1;
    }

    public void setFollow(boolean isFollow){
        is_follow = isFollow ? 1 : 0;
    }

    public double getDistance() {
        return distance;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }



    public void setId(String id) {
        this.id = id;
    }
}
