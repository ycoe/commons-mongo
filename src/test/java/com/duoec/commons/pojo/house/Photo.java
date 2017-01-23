package com.duoec.commons.pojo.house;

/**
 * Created by ycoe on 16/7/5.
 */
public class Photo {

    private long id;
    /**
     * 图片id
     */
    private int photoId;

    /**
     * cms唯一图片id
     */
    private String cmsPhotoId;
    /**
     * 图片分类：101-效果图；102-实景图；103-规划图；104:户型图；105-配套图；107-户型样板间图；201-楼书；202-沙盘图
     */
    private int cateId;
    /**
     * 图片类型，和cateId对应
     */
//    private String photoType; //无需冗余这个值
    /**
     * 地址
     */
    private String url;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getCmsPhotoId() {
        return cmsPhotoId;
    }

    public void setCmsPhotoId(String cmsPhotoId) {
        this.cmsPhotoId = cmsPhotoId;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
