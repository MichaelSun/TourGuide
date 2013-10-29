package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.OptionalParam;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("user/applyForGuide")
public class ApplyForGuideRequest extends PMRequestBase<ApplyForGuideResponse> {

    @OptionalParam("goodAtScenic")
    private String goodAtScenic;

    @RequiredParam("birthday")
    private long birthday;

    @RequiredParam("beGuideYear")
    private int beGuideYear;

    @OptionalParam("guideCardUrl")
    private String guideCardUrl;

    @RequiredParam("guideCardId")
    private String guideCardId;

    @RequiredParam("location")
    private String location;

    @RequiredParam("city")
    private int city;

    public ApplyForGuideRequest(String goodAtScenic, long birthday, int beGuideYear, String guideCardUrl,
            String guideCardId, String location, int city) {
        this.goodAtScenic = goodAtScenic;
        this.birthday = birthday;
        this.beGuideYear = beGuideYear;
        this.guideCardUrl = guideCardUrl;
        this.guideCardId = guideCardId;
        this.location = location;
        this.city = city;
    }

}
