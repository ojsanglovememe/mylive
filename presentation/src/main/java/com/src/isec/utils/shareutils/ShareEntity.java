package com.src.isec.utils.shareutils;

import android.graphics.Bitmap;

/**
 * Created by Tracy on 2017/10/16.
 */

public class ShareEntity {

    private String title;
    private String summary;
    private String targetUrl;
    private String thumbUrlOrPath;
    private Bitmap thumb;

    private String url;

    public ShareEntity() {

    }

    public ShareEntity(String title, String summary, String targetUrl, String thumbUrlOrPath) {
        this.title = title;
        this.summary = summary;
        this.targetUrl = targetUrl;
        this.thumbUrlOrPath = thumbUrlOrPath;
    }

    public ShareEntity(String title, String summary, String targetUrl, Bitmap thumb) {
        this.title = title;
        this.summary = summary;
        this.targetUrl = targetUrl;
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getThumbUrlOrPath() {
        return thumbUrlOrPath;
    }

    public void setThumbUrlOrPath(String thumbUrlOrPath) {
        this.thumbUrlOrPath = thumbUrlOrPath;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
