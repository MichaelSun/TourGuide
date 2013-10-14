package com.find.guide.api.invite;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("inviteEvent/inviteAll")
public class InviteAllRequest extends PMRequestBase<InviteAllResponse> {

    @RequiredParam("scenic")
    private String scenic;

    @RequiredParam("startTime")
    private long startTime;

    @RequiredParam("endTime")
    private long endTime;

    @RequiredParam("location")
    private String location;

    @RequiredParam("gender")
    private int gender;

    public InviteAllRequest(String scenic, long startTime, long endTime, String location, int gender) {
        super();
        this.scenic = scenic;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.gender = gender;
    }

}
