package com.find.guide.api.invite;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("inviteEvent/invite")
public class InviteRequest extends PMRequestBase<InviteResponse> {

    @RequiredParam("guideId")
    private int guideId;

    @RequiredParam("scenic")
    private String scenic;

    @RequiredParam("startTime")
    private long startTime;

    @RequiredParam("endTime")
    private long endTime;

    public InviteRequest(int guideId, String scenic, long startTime, long endTime) {
        this.guideId = guideId;
        this.scenic = scenic;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
