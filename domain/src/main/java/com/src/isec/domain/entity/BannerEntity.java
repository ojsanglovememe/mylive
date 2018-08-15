package com.src.isec.domain.entity;

import java.io.Serializable;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class 首页轮播图
 * @time 2018/4/27 0027 15:03
 * @change
 * @chang time
 * @class describe
 */

public class BannerEntity implements Serializable{

    private String id;
    private String title;
    private String image;
    private String url;
    private String description;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
