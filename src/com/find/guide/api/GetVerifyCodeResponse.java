package com.find.guide.api;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetVerifyCodeResponse extends ResponseBase{

    public static final int SUCCESS = 0;
    
    @JsonProperty("result")
    public int result;
    
}
