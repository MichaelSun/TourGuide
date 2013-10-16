package com.find.guide.model;

import java.io.Serializable;

public class CityItem implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8068199678640045124L;

    private String cityName;

    private String cityIndex;

    private int cityCode;

    public CityItem(String cityName, String cityIndex, int cityCode) {
        this.cityName = cityName;
        this.cityIndex = cityIndex;
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityIndex() {
        return cityIndex;
    }

    public void setCityIndex(String cityIndex) {
        this.cityIndex = cityIndex;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

}
