package com.find.guide.api.push;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("push/unbindDevice")
public class UnbindDeviceRequest extends PMRequestBase<UnbindDeviceResponse> {

    public UnbindDeviceRequest() {

    }

}
