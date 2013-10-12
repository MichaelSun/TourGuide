package com.find.guide.api;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NoNeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-12
 * Time: AM11:03
 * To change this template use File | Settings | File Templates.
 */

@NoNeedTicket
@RestMethodUrl("user/login")
public class LoginRequest extends PMRequestBase<LoginResponse> {

    @RequiredParam("mobile")
    private String mMobileNum;

    @RequiredParam("password")
    private String mPasswd;

    public LoginRequest(String mobile, String passwd) {
        mMobileNum = mobile;
        mPasswd = passwd;
    }
}
