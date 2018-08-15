package com.src.isec.domain.entity;

/**
 * @author wj
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class describe
 * @time 2018/4/20 16:49
 * @change
 * @chang time
 * @class describe 上报房间
 */
public class ReportRoomEnity {
    private String push;
    private String play1;
    private String play2;
    private String play3;

    public String getPush() {
        return push == null ? "" : push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getPlay1() {
        return play1 == null ? "" : play1;
    }

    public void setPlay1(String play1) {
        this.play1 = play1;
    }

    public String getPlay2() {
        return play2 == null ? "" : play2;
    }

    public void setPlay2(String play2) {
        this.play2 = play2;
    }

    public String getPlay3() {
        return play3 == null ? "" : play3;
    }

    public void setPlay3(String play3) {
        this.play3 = play3;
    }
}
