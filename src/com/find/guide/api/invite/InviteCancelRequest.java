package com.find.guide.api.invite;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("inviteEvent/cancel")
public class InviteCancelRequest extends PMRequestBase<InviteCancelResponse> {

    @RequiredParam("eventId")
    private long eventId;

    @RequiredParam("guideId")
    private int guideId;

    public InviteCancelRequest(long eventId, int guideId) {
        this.eventId = eventId;
        this.guideId = guideId;
    }

}
