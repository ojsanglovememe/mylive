package com.src.isec.domain.entity;

import java.io.Serializable;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class 首页顶部tab标签
 * @time 2018/4/27 0027 15:09
 * @change
 * @chang time
 * @class describe
 */

public class TabTagEntity implements Serializable{

    private String id;
    private String name;
    private String remark;
    private int post_count;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
