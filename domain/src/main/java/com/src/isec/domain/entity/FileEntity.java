package com.src.isec.domain.entity;

/**
 * @author wj
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class describe
 * @time 2018/4/23 11:15
 * @change
 * @chang time
 * @class describe 上传文件返回的url
 */
public class FileEntity {

    private String url;

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
