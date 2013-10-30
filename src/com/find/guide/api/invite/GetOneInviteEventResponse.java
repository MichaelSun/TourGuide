package com.find.guide.api.invite;

import com.find.guide.invite.InviteEvent;
import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

public class GetOneInviteEventResponse extends ResponseBase {

    public InviteEvent inviteEvent;

    @JsonCreator
    public GetOneInviteEventResponse(@JsonProperty("eventId") long eventId, @JsonProperty("userId") int userId,
            @JsonProperty("guideId") int guideId, @JsonProperty("eventType") int eventType,
            @JsonProperty("eventStatus") int eventStatus, @JsonProperty("startTime") long startTime,
            @JsonProperty("endTime") long endTime, @JsonProperty("scenic") String scenic,
            @JsonProperty("createTime") long createTime, @JsonProperty("satisfaction") int satisfaction,
            @JsonProperty("guideName") String guideName, @JsonProperty("guideHeadUrl") String guideHeadUrl,
            @JsonProperty("mobile") String mobile) {
        inviteEvent = new InviteEvent(eventId, userId, guideId, eventType, eventStatus, startTime, endTime, scenic,
                createTime, satisfaction, guideName, guideHeadUrl, mobile);
    }
}
