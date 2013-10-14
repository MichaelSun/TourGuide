package com.find.guide.api.user;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetUserInfoResponse extends ResponseBase {
    
    public static final int TYPE_TOURIST = 0;
    public static final int TYPE_TOURGUIDE = 1;

    @JsonProperty("userId")
    public int userId;

    @JsonProperty("userName")
    public String userName;

    @JsonProperty("mobile")
    public String mobile;

    @JsonProperty("gender")
    public int gender;

    @JsonProperty("userType")
    public int userType;

    @JsonProperty("headUrl")
    public String headUrl;

    @JsonProperty("goodAtScenic")
    public String goodAtScenic;

    @JsonProperty("birthday")
    public long birthday;

    @JsonProperty("beGuideYear")
    public int beGuideYear;

    @JsonProperty("guideCardUrl")
    public String guideCardUrl;

    @JsonProperty("guideCardId")
    public String guideCardId;

    @JsonProperty("location")
    public String location;

    @JsonProperty("city")
    public int city;

}
