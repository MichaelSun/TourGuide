package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("user/getUserInfo")
public class GetUserInfoRequest extends PMRequestBase<GetUserInfoResponse> {

    @RequiredParam("userId")
    private int userId;

    public GetUserInfoRequest(int userId) {
        this.userId = userId;
    }
}
