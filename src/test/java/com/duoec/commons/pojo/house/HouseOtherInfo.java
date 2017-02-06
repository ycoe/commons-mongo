package com.duoec.commons.pojo.house;

import com.duoec.commons.mongo.annotation.Ignore;

/**
 * Created by ycoe on 16/7/5.
 */
public class HouseOtherInfo {
    /**
     * 总建筑面积
     */
    private Double grossBuildingArea;
    /**
     * 占地面积
     */
    private Double netSiteArea;
    /**
     * 容积率
     */
    private Double housePlotRatio;
    /**
     * 绿化率
     */
    private Double houseCoveredRatio;
    /**
     * 规划户数
     */
    private Integer houseHouseholds;
    /**
     * 地上车位数
     */
    private Integer houseParkingOverground;
    /**
     * 地下车位数
     */
    private Integer houseParkingUnderground;
    /**
     * 车位比  计划户数/车位总数
     */
    private Double parkingRatio;

    /**
     * 车位描述
     */
    private String carport;
    /**
     * 物业公司
     */
    private String housePropertyCompany;
    /**
     * 开发商
     */
    private String houseDeveloper;
    /**
     * 预售许可证
     */
    private String presellLicence;
    /**
     * 销售代理
     */
    private String saleAgent;
    /**
     * 400电话,分机号
     */
    private Integer phone400;
    /**
     * 售楼处
     */
    private String saleOffice;
    /**
     * 描述
     */
    private String houseDescription;
    /**
     * 开盘时间
     */
    private Long openTime;
    /**
     * 认筹时间
     */
    private Long bookHouseTime;
    /**
     * 交房时间
     */
    private Long houseDeliverRoomDate;

    public Double getGrossBuildingArea() {
        return grossBuildingArea;
    }

    public void setGrossBuildingArea(Double grossBuildingArea) {
        this.grossBuildingArea = grossBuildingArea;
    }

    public Double getNetSiteArea() {
        return netSiteArea;
    }

    public void setNetSiteArea(Double netSiteArea) {
        this.netSiteArea = netSiteArea;
    }

    public Double getHousePlotRatio() {
        return housePlotRatio;
    }

    public void setHousePlotRatio(Double housePlotRatio) {
        this.housePlotRatio = housePlotRatio;
    }

    public Double getHouseCoveredRatio() {
        return houseCoveredRatio;
    }

    public void setHouseCoveredRatio(Double houseCoveredRatio) {
        this.houseCoveredRatio = houseCoveredRatio;
    }

    public Integer getHouseHouseholds() {
        return houseHouseholds;
    }

    public void setHouseHouseholds(Integer houseHouseholds) {
        this.houseHouseholds = houseHouseholds;
    }

    public Integer getHouseParkingOverground() {
        return houseParkingOverground;
    }

    public void setHouseParkingOverground(Integer houseParkingOverground) {
        this.houseParkingOverground = houseParkingOverground;
    }

    public Integer getHouseParkingUnderground() {
        return houseParkingUnderground;
    }

    public void setHouseParkingUnderground(Integer houseParkingUnderground) {
        this.houseParkingUnderground = houseParkingUnderground;
    }

    public Double getParkingRatio() {
        return parkingRatio;
    }

    public void setParkingRatio(Double parkingRatio) {
        this.parkingRatio = parkingRatio;
    }

    public String getCarport() {
        return carport;
    }

    public void setCarport(String carport) {
        this.carport = carport;
    }

    public String getHousePropertyCompany() {
        return housePropertyCompany;
    }

    public void setHousePropertyCompany(String housePropertyCompany) {
        this.housePropertyCompany = housePropertyCompany;
    }

    public String getHouseDeveloper() {
        return houseDeveloper;
    }

    public void setHouseDeveloper(String houseDeveloper) {
        this.houseDeveloper = houseDeveloper;
    }

    public String getPresellLicence() {
        return presellLicence;
    }

    public void setPresellLicence(String presellLicence) {
        this.presellLicence = presellLicence;
    }

    public String getSaleAgent() {
        return saleAgent;
    }

    public void setSaleAgent(String saleAgent) {
        this.saleAgent = saleAgent;
    }

    public Integer getPhone400() {
        return phone400;
    }

    public void setPhone400(Integer phone400) {
        this.phone400 = phone400;
    }

    public String getSaleOffice() {
        return saleOffice;
    }

    public void setSaleOffice(String saleOffice) {
        this.saleOffice = saleOffice;
    }

    public String getHouseDescription() {
        return houseDescription;
    }

    public void setHouseDescription(String houseDescription) {
        this.houseDescription = houseDescription;
    }

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public Long getBookHouseTime() {
        return bookHouseTime;
    }

    public void setBookHouseTime(Long bookHouseTime) {
        this.bookHouseTime = bookHouseTime;
    }

    public Long getHouseDeliverRoomDate() {
        return houseDeliverRoomDate;
    }

    public void setHouseDeliverRoomDate(Long houseDeliverRoomDate) {
        this.houseDeliverRoomDate = houseDeliverRoomDate;
    }
}
