package com.find.guide.api.invite;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("inviteEvent/getOneInviteEvent")
public class GetOneInviteEventRequest extends PMRequestBase<GetOneInviteEventResponse> {

    @RequiredParam("eventId")
    private long eventId;

    public GetOneInviteEventRequest(long eventId) {
        super();
        this.eventId = eventId;
    }

}
