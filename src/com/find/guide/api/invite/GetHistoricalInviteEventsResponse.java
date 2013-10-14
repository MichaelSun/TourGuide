package com.find.guide.api.invite;

import java.util.List;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetHistoricalInviteEventsResponse extends ResponseBase{

    @JsonProperty("result")
    public List<InviteEvent> inviteEvents;
}
