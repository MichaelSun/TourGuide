package com.find.guide.api.push;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("push/bindDevice")
public class BindDeviceRequest extends PMRequestBase<BindDeviceResponse> {

    // android把channelId和userId拼接起来，中间用“@#$”分隔
    @RequiredParam("deviceToken")
    private String deviceToken;

    // 1 为android,2 为ios
    @RequiredParam("deviceType")
    private int deviceType;

    public BindDeviceRequest(String pushChannelId, String pushUserId) {
        deviceToken = pushChannelId + "@#$" + pushUserId;
        deviceType = 1;
    }

}
