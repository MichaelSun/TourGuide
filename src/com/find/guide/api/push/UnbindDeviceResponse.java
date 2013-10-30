package com.find.guide.api.push;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class UnbindDeviceResponse extends ResponseBase {

    @JsonProperty("result")
    public int result;

}
