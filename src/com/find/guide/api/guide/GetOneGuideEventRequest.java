package com.find.guide.api.guide;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("guideEvent/getOneGuideEvent")
public class GetOneGuideEventRequest extends PMRequestBase<GetOneGuideEventResponse> {

    @RequiredParam("eventId")
    private long eventId;

    public GetOneGuideEventRequest(long eventId) {
        this.eventId = eventId;
    }
}
