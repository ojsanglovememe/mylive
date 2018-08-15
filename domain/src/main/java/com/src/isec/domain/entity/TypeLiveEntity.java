package com.src.isec.domain.entity;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class describe
 * @time 2018/4/23 0023 15:46
 * @change
 * @chang time
 * @class describe
 */

public class TypeLiveEntity {

    private String type;
    private List<LiveEntity> list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LiveEntity> getList() {
        return list;
    }

    public void setList(List<LiveEntity> list) {
        this.list = list;
    }
}
