package com.find.guide.api.guide;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("guideEvent/accept")
public class GuideAcceptRequest extends PMRequestBase<GuideAcceptResponse> {

    @RequiredParam("eventId")
    private long eventId;

    @RequiredParam("userId")
    private int userId;

    public GuideAcceptRequest(long eventId, int userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

}
