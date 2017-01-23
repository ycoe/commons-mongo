package com.duoec.commons.pojo.house;

/**
 * Created by ycoe on 16/7/5.
 */
public class HookInfo {
    /**
     * 房多多id
     */
    private Integer fddId;
    /**
     * 搜房id
     */
    private Integer souFunId;
    /**
     * 吉屋id
     */
    private Integer iwjwId;
    /**
     * 安居客id
     */
    private Integer anjukeId;

    /**
     * 原料库唯一id
     */
    private String materialId;

    /**
     * cms唯一id
     */
    private Long cmsId;

    /**
     * WaterfallId
     */
    private Long waterfallId;

    public Integer getFddId() {
        return fddId;
    }

    public void setFddId(Integer fddId) {
        this.fddId = fddId;
    }

    public Integer getSouFunId() {
        return souFunId;
    }

    public void setSouFunId(Integer souFunId) {
        this.souFunId = souFunId;
    }

    public Integer getIwjwId() {
        return iwjwId;
    }

    public void setIwjwId(Integer iwjwId) {
        this.iwjwId = iwjwId;
    }

    public Integer getAnjukeId() {
        return anjukeId;
    }

    public void setAnjukeId(Integer anjukeId) {
        this.anjukeId = anjukeId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public Long getCmsId() {
        return cmsId;
    }

    public void setCmsId(Long cmsId) {
        this.cmsId = cmsId;
    }

    public Long getWaterfallId() {
        return waterfallId;
    }

    public void setWaterfallId(Long waterfallId) {
        this.waterfallId = waterfallId;
    }
}
