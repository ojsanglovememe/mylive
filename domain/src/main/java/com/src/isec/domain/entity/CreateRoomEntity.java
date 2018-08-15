package com.src.isec.domain.entity;

/**
 * @author wj
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class describe
 * @time 2018/4/20 15:32
 * @change
 * @chang time
 * @class describe  创建房间拿到的参数
 */
public class CreateRoomEntity {
    private String roomnum;
    private String groupid;

    public CreateRoomEntity() {

    }

    public String getRoomnum() {
        return roomnum == null ? "" : roomnum;
    }

    public void setRoomnum(String roomnum) {
        this.roomnum = roomnum;
    }

    public String getGroupid() {
        return groupid == null ? "" : groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}
