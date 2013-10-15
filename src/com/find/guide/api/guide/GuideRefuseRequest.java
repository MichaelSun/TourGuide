package com.find.guide.api.guide;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("guideEvent/refuse")
public class GuideRefuseRequest extends PMRequestBase<GuideRefuseResponse> {

    @RequiredParam("eventId")
    private long eventId;

    @RequiredParam("guideId")
    private int guideId;

    public GuideRefuseRequest(long eventId, int guideId) {
        this.eventId = eventId;
        this.guideId = guideId;
    }

}
