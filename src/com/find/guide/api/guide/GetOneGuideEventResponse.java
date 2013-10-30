package com.find.guide.api.guide;

import com.find.guide.guide.GuideEvent;
import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetOneGuideEventResponse extends ResponseBase {

    public GuideEvent guideEvent;

    public GetOneGuideEventResponse(@JsonProperty("guideId") int guideId, @JsonProperty("eventId") long eventId,
            @JsonProperty("userId") int userId, @JsonProperty("eventStatus") int eventStatus,
            @JsonProperty("satisfaction") int satisfaction, @JsonProperty("createTime") long createTime,
            @JsonProperty("eventType") int eventType, @JsonProperty("startTime") long startTime,
            @JsonProperty("endTime") long endTime, @JsonProperty("scenic") String scenic,
            @JsonProperty("userName") String userName, @JsonProperty("userHeadUrl") String userHeadUrl,
            @JsonProperty("mobile") String mobile) {
        guideEvent = new GuideEvent(guideId, eventId, userId, eventStatus, satisfaction, createTime, eventType,
                startTime, endTime, scenic, userName, userHeadUrl, mobile);
    }
}
