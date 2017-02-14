package com.duoec.commons.pojo.house;

import com.duoec.commons.mongo.annotation.AutoIncrement;
import com.duoec.commons.mongo.annotation.Ignore;

import java.util.List;

/**
 * Created by ycoe on 16/7/5.
 */
public class House {
    /**
     * 主键
     */
    @AutoIncrement(start = 10000)
    private long id;

    private Location location;

    /**
     * 推广城市
     */
    private List<Integer> promotionCityIds;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 推荐理由
     */
    private String recommendReason;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long updateTime;

    /**
     * 基本信息
     */
    private HouseBasicInfo basicInfo;

    /**
     * 扩展信息
     */
    private HouseOtherInfo otherInfo;

    /**
     * 周边配套
     */
    private SurroundingFacility surroundingFacility;

    /**
     * 物业信息
     */
    private List<Property> properties;

    /**
     * 相册
     */
    private List<Photo> photos;

    /**
     * 勾子信息
     */
    private HookInfo hookInfo;

    /**
     * 标识(hot / ...)
     * 不需要单独标识出来!
     */
//    private Flag flags;

    /**
     * 推送到waterfall时间
     */
    private Long exportTime;

    /**
     * 导出到waterfall的状态
     * 0 : 刚导入到cms 1: 推到cms的新版本未推送到waterfall 2:cms和waterfall一致
     */
    private int dataStatus;

    /**
     * 数据完善度得分
     */
    private Integer score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Integer> getPromotionCityIds() {
        return promotionCityIds;
    }

    public void setPromotionCityIds(List<Integer> promotionCityIds) {
        this.promotionCityIds = promotionCityIds;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getRecommendReason() {
        return recommendReason;
    }

    public void setRecommendReason(String recommendReason) {
        this.recommendReason = recommendReason;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public HouseBasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(HouseBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public HouseOtherInfo getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(HouseOtherInfo otherInfo) {
        this.otherInfo = otherInfo;
    }

    public SurroundingFacility getSurroundingFacility() {
        return surroundingFacility;
    }

    public void setSurroundingFacility(SurroundingFacility surroundingFacility) {
        this.surroundingFacility = surroundingFacility;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public HookInfo getHookInfo() {
        return hookInfo;
    }

    public void setHookInfo(HookInfo hookInfo) {
        this.hookInfo = hookInfo;
    }

    public Long getExportTime() {
        return exportTime;
    }

    public void setExportTime(Long exportTime) {
        this.exportTime = exportTime;
    }

    public int getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(int dataStatus) {
        this.dataStatus = dataStatus;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
