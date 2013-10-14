package com.find.guide.api.guide;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.OptionalTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@OptionalTicket
@RestMethodUrl("user/getNearByGuide")
public class GetNearByGuideRequest extends PMRequestBase<GetNearByGuideResponse> {

    @RequiredParam("location")
    private String location;

    @RequiredParam("dist")
    private double dist;

    @RequiredParam("start")
    private int start;

    @RequiredParam("rows")
    private int rows;

    public GetNearByGuideRequest(String location, double dist, int start, int rows) {
        this.location = location;
        this.dist = dist;
        this.start = start;
        this.rows = rows;
    }

}
