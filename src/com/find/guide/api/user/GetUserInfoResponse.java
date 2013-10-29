package com.find.guide.api.user;

import com.find.guide.model.TourGuide;
import com.find.guide.model.Tourist;
import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

public class GetUserInfoResponse extends ResponseBase {

    static final int TYPE_TOURIST = 0;
    static final int TYPE_TOURGUIDE = 1;

    public Tourist tourist;

    @JsonCreator
    public GetUserInfoResponse(@JsonProperty("userId") int userId, @JsonProperty("userName") String userName,
            @JsonProperty("mobile") String mobile, @JsonProperty("gender") int gender,
            @JsonProperty("userType") int userType, @JsonProperty("headUrl") String headUrl,
            @JsonProperty("goodAtScenic") String goodAtScenic, @JsonProperty("birthday") long birthday,
            @JsonProperty("beGuideYear") int beGuideYear, @JsonProperty("guideCardUrl") String guideCardUrl,
            @JsonProperty("guideCardId") String guideCardId, @JsonProperty("location") String location,
            @JsonProperty("city") int city, @JsonProperty("evaluateScore") int evaluateScore,
            @JsonProperty("evaluateCount") int evaluateCount) {
        if (userType == TYPE_TOURGUIDE) {
            tourist = new TourGuide(userId, userName, mobile, gender, userType, headUrl, goodAtScenic, birthday,
                    beGuideYear, guideCardUrl, guideCardId, location, city, evaluateScore, evaluateCount);
        } else {
            tourist = new Tourist(userId, userName, mobile, gender, userType, headUrl);
        }
    }

}
