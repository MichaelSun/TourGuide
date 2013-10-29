package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("user/changeUserInfo")
public class ChangeUserInfoRequest extends PMRequestBase<ChangeUserInfoResponse> {

    @RequiredParam("userName")
    private String userName;

    @RequiredParam("gender")
    private int gender;

    @RequiredParam("headUrl")
    private String headUrl;

    public ChangeUserInfoRequest(String userName, int gender, String headUrl) {
        this.userName = userName;
        this.gender = gender;
        this.headUrl = headUrl;
    }

}
