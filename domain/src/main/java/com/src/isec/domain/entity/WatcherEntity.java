package com.src.isec.domain.entity;

import java.util.List;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class 直播间观众信息
 * @time 2018/4/25 10:38
 * @change
 * @chang time
 * @class describe
 */

public class WatcherEntity {

    private String total;
    private List<WatchItem> idlist;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<WatchItem> getIdlist() {
        return idlist;
    }

    public void setIdlist(List<WatchItem> idlist) {
        this.idlist = idlist;
    }

    public class WatchItem{

        private String id;
        private String role;
        private String avatar;
        private String nickname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
