package com.duoec.commons.pojo.house;

/**
 * Created by ycoe on 16/7/5.
 */
public class Property {
    /**
     * 物业名称
     */
    private String propertyName;
    /**
     * 物业费
     */
    private Double propertyFee;
    /**
     * 产权年限
     */
    private Integer propertyRights;
    /**
     * 装修情况
     */
    private String propertyDecorate;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Double getPropertyFee() {
        return propertyFee;
    }

    public void setPropertyFee(Double propertyFee) {
        this.propertyFee = propertyFee;
    }

    public Integer getPropertyRights() {
        return propertyRights;
    }

    public void setPropertyRights(Integer propertyRights) {
        this.propertyRights = propertyRights;
    }

    public String getPropertyDecorate() {
        return propertyDecorate;
    }

    public void setPropertyDecorate(String propertyDecorate) {
        this.propertyDecorate = propertyDecorate;
    }
}
