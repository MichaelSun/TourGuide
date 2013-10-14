package com.find.guide.api.user;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetVerifyCodeResponse extends ResponseBase{

    @JsonProperty("result")
    public int result;
    
}
