package com.find.guide.api.guide;

import com.plugin.internet.core.json.JsonProperty;

public class TourGuideView {

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
