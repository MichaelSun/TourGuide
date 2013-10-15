package com.find.guide.api.guide;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GuideRefuseResponse extends ResponseBase{

    @JsonProperty("result")
    public int result;
}
