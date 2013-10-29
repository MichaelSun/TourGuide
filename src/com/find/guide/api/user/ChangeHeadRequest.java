package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("user/changeHeadUrl")
public class ChangeHeadRequest extends PMRequestBase<ChangeHeadResponse> {

    @RequiredParam("headUrl")
    private String headUrl;

    public ChangeHeadRequest(String headUrl) {
        this.headUrl = headUrl;
    }

}
