package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("user/changeLocation")
public class ChangeLocationRequest extends PMRequestBase<ChangeLocationResponse> {

    @RequiredParam("location")
    private String location;

    public ChangeLocationRequest(String location) {
        this.location = location;
    }
}
