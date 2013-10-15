package com.find.guide.model;

import java.io.Serializable;

import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

/**
 * 游客信息
 * 
 */
public class Tourist implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3092975145083360198L;

    public static final int USER_TYPE_TOURIST = 0;
    public static final int USER_TYPE_TOURGUIDE = 1;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("gender")
    private int gender;

    @JsonProperty("usrType")
    private int userType;

    @JsonProperty("headUrl")
    private String headUrl;

    @JsonCreator
    public Tourist(int userId, String userName, String mobile, int gender, int userType, String headUrl) {
        this.userId = userId;
        this.userName = userName;
        this.mobile = mobile;
        this.gender = gender;
        this.userType = userType;
        this.headUrl = headUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

}
