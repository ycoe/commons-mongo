package com.duoec.commons.pojo.house;

/**
 * Created by ycoe on 16/7/5.
 */
public class SurroundingFacility {
    /**
     * 公交
     */
    private String buses;
    /**
     * 生活配套
     */
    private String lifeSupport;
    /**
     * 学区配套
     */
    private String schoolSupport;
    /**
     * 交通出行
     */
    private String trafficSupport;
    /**
     * 医疗配套
     */
    private String medicalSupport;

    public String getBuses() {
        return buses;
    }

    public void setBuses(String buses) {
        this.buses = buses;
    }

    public String getLifeSupport() {
        return lifeSupport;
    }

    public void setLifeSupport(String lifeSupport) {
        this.lifeSupport = lifeSupport;
    }

    public String getSchoolSupport() {
        return schoolSupport;
    }

    public void setSchoolSupport(String schoolSupport) {
        this.schoolSupport = schoolSupport;
    }

    public String getTrafficSupport() {
        return trafficSupport;
    }

    public void setTrafficSupport(String trafficSupport) {
        this.trafficSupport = trafficSupport;
    }

    public String getMedicalSupport() {
        return medicalSupport;
    }

    public void setMedicalSupport(String medicalSupport) {
        this.medicalSupport = medicalSupport;
    }
}
