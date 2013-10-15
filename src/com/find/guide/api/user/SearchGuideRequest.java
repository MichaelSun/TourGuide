package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NoNeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NoNeedTicket
@RestMethodUrl("user/searchGuide")
public class SearchGuideRequest extends PMRequestBase<SearchGuideResponse> {

    @RequiredParam("city")
    private int city;

    @RequiredParam("gender")
    private int gender;

    @RequiredParam("scenic")
    private String scenic;

    @RequiredParam("start")
    private int start;

    @RequiredParam("rows")
    private int rows;

    public SearchGuideRequest(int city, int gender, String scenic, int start, int rows) {
        super();
        this.city = city;
        this.gender = gender;
        this.scenic = scenic;
        this.start = start;
        this.rows = rows;
    }

}
