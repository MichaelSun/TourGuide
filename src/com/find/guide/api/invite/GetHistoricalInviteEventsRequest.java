package com.find.guide.api.invite;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("inviteEvent/getHistoricalInviteEvents")
public class GetHistoricalInviteEventsRequest extends PMRequestBase<GetHistoricalInviteEventsResponse> {

    @RequiredParam("start")
    private int start;

    @RequiredParam("rows")
    private int rows;

    public GetHistoricalInviteEventsRequest(int start, int rows) {
        this.start = start;
        this.rows = rows;
    }

}
