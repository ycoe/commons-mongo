package com.duoec.commons.pojo.house;

import java.util.List;

/**
 * Created by ycoe on 16/7/5.
 */
public class HouseBasicInfo {

    private String houseName;

    private List<String> houseAlias;

    private String houseLogo;

    private Integer housePrice;

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public List<String> getHouseAlias() {
        return houseAlias;
    }

    public void setHouseAlias(List<String> houseAlias) {
        this.houseAlias = houseAlias;
    }

    public String getHouseLogo() {
        return houseLogo;
    }

    public void setHouseLogo(String houseLogo) {
        this.houseLogo = houseLogo;
    }

    public Integer getHousePrice() {
        return housePrice;
    }

    public void setHousePrice(Integer housePrice) {
        this.housePrice = housePrice;
    }
}
