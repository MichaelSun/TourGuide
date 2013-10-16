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

    private int userId;

    private String userName;

    private String mobile;

    private int gender;

    private int userType;

    private String headUrl;

    @JsonCreator
    public Tourist(@JsonProperty("userId") int userId, @JsonProperty("userName") String userName,
            @JsonProperty("mobile") String mobile, @JsonProperty("gender") int gender,
            @JsonProperty("userType") int userType, @JsonProperty("headUrl") String headUrl) {
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
