package com.find.guide.api;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NoNeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NoNeedTicket
@RestMethodUrl("user/register")
public class RegisterRequest extends PMRequestBase<RegisterResponse> {

    @RequiredParam("mobile")
    private String mobileNum;

    @RequiredParam("password")
    private String passwd;

    @RequiredParam("name")
    private String name;

    @RequiredParam("verifyCode")
    private String verifyCode;

    @RequiredParam("gender")
    private int gender;

    public RegisterRequest(String mobile, String passwd, String name, String verifyCode, int gender) {
        this.mobileNum = mobile;
        this.passwd = passwd;
        this.name = name;
        this.verifyCode = verifyCode;
        this.gender = gender;
    }

}
