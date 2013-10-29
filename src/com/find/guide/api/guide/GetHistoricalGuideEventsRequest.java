package com.find.guide.api.guide;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.OptionalParam;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("guideEvent/getHistoricalGuideEvents")
public class GetHistoricalGuideEventsRequest extends PMRequestBase<GetHistoricalGuideEventsResponse> {

    @RequiredParam("start")
    private int start;

    @RequiredParam("rows")
    private int rows;

    @RequiredParam("userId")
    private int userId;

    public GetHistoricalGuideEventsRequest(int userId, int start, int rows) {
        this.userId = userId;
        this.start = start;
        this.rows = rows;
    }

}
