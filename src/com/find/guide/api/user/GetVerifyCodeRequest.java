package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NoNeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NoNeedTicket
@RestMethodUrl("user/getVerifyCode")
public class GetVerifyCodeRequest extends PMRequestBase<GetVerifyCodeResponse> {

    @RequiredParam("mobile")
    private String mobile;

    public GetVerifyCodeRequest(String mobile) {
        this.mobile = mobile;
    }

}
