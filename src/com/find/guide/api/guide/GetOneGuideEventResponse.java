package com.find.guide.api.guide;

import com.find.guide.model.GuideEvent;
import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetOneGuideEventResponse extends ResponseBase {

    public GuideEvent guideEvent;

    public GetOneGuideEventResponse(@JsonProperty("guideId") int guideId, @JsonProperty("eventId") long eventId,
            @JsonProperty("userId") int userId, @JsonProperty("eventStatus") int eventStatus,
            @JsonProperty("satisfaction") int satisfaction, @JsonProperty("createTime") long createTime,
            @JsonProperty("eventType") int eventType) {
        guideEvent = new GuideEvent(guideId, eventId, userId, eventStatus, satisfaction, createTime, eventType);
    }
}
